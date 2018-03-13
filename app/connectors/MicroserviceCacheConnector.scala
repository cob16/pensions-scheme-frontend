/*
 * Copyright 2018 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package connectors

import com.google.inject.Inject
import config.FrontendAppConfig
import identifiers.TypedIdentifier
import play.api.http.Status._
import play.api.libs.json._
import play.api.libs.ws.WSClient
import uk.gov.hmrc.crypto.{ApplicationCrypto, Crypted, PlainText}
import uk.gov.hmrc.http._
import utils.{Cleanup, UserAnswers}

import scala.concurrent.{ExecutionContext, Future}

class MicroserviceCacheConnector @Inject() (
                                             config: FrontendAppConfig,
                                             http: WSClient,
                                             crypto: ApplicationCrypto
                                           ) extends DataCacheConnector {

  private def url(id: String) = s"${config.pensionsSchemeUrl}/pensions-scheme/journey-cache/scheme/$id"

  override def save[A, I <: TypedIdentifier[A]](cacheId: String, id: I, value: A)
                                               (implicit
                                                fmt: Format[A],
                                                cleanup: Cleanup[I],
                                                ec: ExecutionContext,
                                                hc: HeaderCarrier
                                               ): Future[JsValue] = {

    fetch(cacheId).flatMap {
      json =>
        UserAnswers(json.getOrElse(Json.obj())).set(id)(value) match {
          case JsSuccess(UserAnswers(updatedJson), _) =>

            val decrypted = PlainText(Json.stringify(updatedJson))
            val encrypted = crypto.JsonCrypto.encrypt(decrypted)

            http.url(url(cacheId))
              .withHeaders(hc.headers: _*)
              .post(encrypted.value).flatMap {
                response =>
                  response.status match {
                    case OK =>
                      Future.successful(updatedJson)
                    case _ =>
                      Future.failed(new HttpException(response.body, response.status))
                  }
              }
          case JsError(errors) =>
            Future.failed(JsResultException(errors))
        }
    }
  }

  override def fetch(id: String)(implicit
                                 ec: ExecutionContext,
                                 hc: HeaderCarrier
                                ): Future[Option[JsValue]] = {

    http.url(url(id))
      .withHeaders(hc.headers: _*)
      .get().flatMap {
        response =>
          response.status match {
            case NOT_FOUND =>
              Future.successful(None)
            case OK =>
              val decrypted = crypto.JsonCrypto.decrypt(Crypted(response.body))
              Future.successful(Some(Json.parse(decrypted.value)))
            case _ =>
              Future.failed(new HttpException(response.body, response.status))
          }
    }
  }
}