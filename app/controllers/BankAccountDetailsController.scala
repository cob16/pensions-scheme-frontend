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

package controllers

import config.FrontendAppConfig
import connectors.UserAnswersCacheConnector
import controllers.actions._
import forms.BankAccountDetailsFormProvider
import identifiers.{BankAccountDetailsId, SchemeNameId}
import javax.inject.Inject
import models.Mode
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import utils.annotations.AboutBankDetails
import utils.{Navigator, UserAnswers}
import views.html.bankAccountDetails

import scala.concurrent.{ExecutionContext, Future}

class BankAccountDetailsController @Inject()(appConfig: FrontendAppConfig,
                                             override val messagesApi: MessagesApi,
                                             dataCacheConnector: UserAnswersCacheConnector,
                                             @AboutBankDetails navigator: Navigator,
                                             authenticate: AuthAction,
                                             getData: DataRetrievalAction,
                                             requireData: DataRequiredAction,
                                             formProvider: BankAccountDetailsFormProvider
                                            )(implicit val ec: ExecutionContext) extends FrontendController with I18nSupport with Retrievals {

  private val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (authenticate andThen getData andThen requireData).async {
    implicit request =>
      SchemeNameId.retrieve.right.map { schemeName =>
        val preparedForm = request.userAnswers.get(BankAccountDetailsId) match {
          case None => form
          case Some(value) => form.fill(value)
        }
        Future.successful(Ok(bankAccountDetails(appConfig, preparedForm, mode, schemeName)))
      }
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (authenticate andThen getData andThen requireData).async {
    implicit request =>
      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          SchemeNameId.retrieve.right.map { schemeName =>
            Future.successful(BadRequest(bankAccountDetails(appConfig, formWithErrors, mode, schemeName)))
          },
        value =>
          dataCacheConnector.save(request.externalId, BankAccountDetailsId, value).map(cacheMap =>
            Redirect(navigator.nextPage(BankAccountDetailsId, mode, UserAnswers(cacheMap))))
      )
  }
}