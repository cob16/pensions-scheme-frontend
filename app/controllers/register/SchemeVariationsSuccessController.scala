/*
 * Copyright 2019 HM Revenue & Customs
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

package controllers.register

import config.FrontendAppConfig
import connectors.UpdateSchemeCacheConnector
import controllers.Retrievals
import controllers.actions._
import javax.inject.Inject
import models.UpdateMode
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import views.html.register.schemeVariationsSuccess

import scala.concurrent.ExecutionContext

class SchemeVariationsSuccessController @Inject()(appConfig: FrontendAppConfig,
                                                  override val messagesApi: MessagesApi,
                                                  cacheConnector: UpdateSchemeCacheConnector,
                                                  authenticate: AuthAction,
                                                  getData: DataRetrievalAction)
                                                 (implicit val ec: ExecutionContext) extends FrontendController with I18nSupport with Retrievals {

  def onPageLoad(srn: String): Action[AnyContent] = (authenticate andThen getData(UpdateMode, Some(srn))).async {
    implicit request =>
      val schemeName = existingSchemeName
      cacheConnector.removeAll(srn).map { _ =>
        Ok(
          schemeVariationsSuccess(
            appConfig, schemeName, Some(srn)
          )
        )
      }
  }
}