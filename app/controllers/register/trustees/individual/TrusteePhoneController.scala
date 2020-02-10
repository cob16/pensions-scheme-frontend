/*
 * Copyright 2020 HM Revenue & Customs
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

package controllers.register.trustees.individual

import config.FrontendAppConfig
import controllers.PhoneNumberController
import controllers.actions._
import forms.PhoneFormProvider
import identifiers.register.trustees.individual.{TrusteeNameId, TrusteePhoneId}
import javax.inject.Inject
import models.{Index, Mode}
import navigators.Navigator
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserAnswersService
import viewmodels.{CommonFormWithHintViewModel, Message}
import views.html.phoneNumber

import scala.concurrent.ExecutionContext

class TrusteePhoneController @Inject()(
                                        val appConfig: FrontendAppConfig,
                                        override val messagesApi: MessagesApi,
                                        authenticate: AuthAction,
                                        getData: DataRetrievalAction,
                                        override val userAnswersService: UserAnswersService,
                                        allowAccess: AllowAccessActionProvider,
                                        requireData: DataRequiredAction,
                                        val navigator: Navigator,
                                        formProvider: PhoneFormProvider,
                                        val controllerComponents: MessagesControllerComponents,
                                        val view: phoneNumber
                                      )(implicit val ec: ExecutionContext) extends PhoneNumberController with I18nSupport {


  protected val form: Form[String] = formProvider()

  private def viewModel(mode: Mode, srn: Option[String], index: Index): Retrieval[CommonFormWithHintViewModel] =
    Retrieval {
      implicit request =>
        TrusteeNameId(index).retrieve.right.map {
          details =>
            CommonFormWithHintViewModel(
              routes.TrusteePhoneController.onSubmit(mode, index, srn),
              Message("messages__enterPhoneNumber", Message("messages__theIndividual").resolve),
              Message("messages__enterPhoneNumber", details.fullName),
              Some(Message("messages__contact_details__hint", details.fullName)),
              srn = srn
            )
        }
    }

  def onPageLoad(mode: Mode, index: Index, srn: Option[String]): Action[AnyContent] =
    (authenticate andThen getData(mode, srn) andThen allowAccess(srn) andThen requireData).async {
      implicit request =>
        viewModel(mode, srn, index).retrieve.right.map {
          vm =>
            get(TrusteePhoneId(index), form, vm)
        }
    }

  def onSubmit(mode: Mode, index: Index, srn: Option[String]): Action[AnyContent] =
    (authenticate andThen getData(mode, srn) andThen allowAccess(srn) andThen requireData).async {
      implicit request =>
        viewModel(mode, srn, index).retrieve.right.map {
          vm =>
            post(TrusteePhoneId(index), mode, form, vm)
        }
    }

}
