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

package controllers.register.trustees.company

import config.FrontendAppConfig
import controllers.VatVariationsController
import controllers.actions._
import forms.VatVariationsFormProvider
import identifiers.register.trustees.company.{CompanyDetailsId, CompanyVatVariationsId}
import javax.inject.Inject
import models.{Index, Mode}
import navigators.Navigator
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent}
import services.UserAnswersService
import utils.annotations.TrusteesCompany
import viewmodels.{Message, VatViewModel}

import scala.concurrent.ExecutionContext

class CompanyVatVariationsController @Inject()(
                                                override val appConfig: FrontendAppConfig,
                                                override val messagesApi: MessagesApi,
                                                override val userAnswersService: UserAnswersService,
                                                 override val navigator: Navigator,
                                                authenticate: AuthAction,
                                                getData: DataRetrievalAction,
                                                allowAccess: AllowAccessActionProvider,
                                                requireData: DataRequiredAction,
                                                formProvider: VatVariationsFormProvider
                                              )(implicit val ec: ExecutionContext) extends VatVariationsController {

  private def form(companyName: String) = formProvider(companyName)

  private def viewModel(mode: Mode, index: Index, srn: Option[String], companyName: String): VatViewModel = {
    VatViewModel(
      postCall = routes.CompanyVatVariationsController.onSubmit(mode, index, srn),
      title = Message("messages__vatVariations__company_title"),
      heading = Message("messages__vatVariations__heading", companyName),
      hint = Message("messages__vatVariations__hint", companyName),
      subHeading = None,
      srn = srn
    )
  }

  def onPageLoad(mode: Mode, index: Index, srn: Option[String]): Action[AnyContent] =
    (authenticate andThen getData(mode, srn) andThen allowAccess(srn) andThen requireData).async {
      implicit request =>
        CompanyDetailsId(index).retrieve.right.map { details =>
          val companyName = details.companyName
          get(CompanyVatVariationsId(index), viewModel(mode, index, srn, companyName), form(companyName))
        }
    }

  def onSubmit(mode: Mode, index: Index, srn: Option[String]): Action[AnyContent] = (authenticate andThen getData(mode, srn) andThen requireData).async {
    implicit request =>
      CompanyDetailsId(index).retrieve.right.map { details =>
        val companyName = details.companyName
        post(CompanyVatVariationsId(index), mode, viewModel(mode, index, srn, companyName), form(companyName))
      }
  }
}
