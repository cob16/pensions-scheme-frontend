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

package controllers.register.establishers.company

import config.FrontendAppConfig
import controllers.VatController
import controllers.actions._
import forms.VatFormProvider
import identifiers.register.establishers.company.{CompanyDetailsId, CompanyVatId}
import javax.inject.Inject
import models.{Index, Mode}
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent}
import services.UserAnswersService
import utils.Navigator
import utils.annotations.EstablishersCompany
import viewmodels.{Message, VatViewModel}


class CompanyVatController @Inject()(
                                          override val appConfig: FrontendAppConfig,
                                          override val messagesApi: MessagesApi,
                                          override val userAnswersService: UserAnswersService,
                                          @EstablishersCompany override val navigator: Navigator,
                                          authenticate: AuthAction,
                                          getData: DataRetrievalAction,
                                          requireData: DataRequiredAction,
                                          formProvider: VatFormProvider
                                        ) extends VatController {

  private def viewmodel(mode: Mode, index: Index, srn: Option[String]): Retrieval[VatViewModel] =
    Retrieval {
      implicit request =>
        CompanyDetailsId(index).retrieve.right.map {
          details =>
            VatViewModel(
              postCall = routes.CompanyVatController.onSubmit(mode, index, srn),
              title = Message("messages__companyVat__title"),
              heading = Message("messages__companyVat__heading", details.companyName),
              hint = Message("messages__common__company_vat__hint", details.companyName),
              subHeading = None,
              srn = srn
            )
        }
    }

  private val form = formProvider("messages__companyVat__error__required")

  def onPageLoad(mode: Mode, index: Index, srn: Option[String]): Action[AnyContent] =
    (authenticate andThen getData(mode, srn) andThen requireData).async {
    implicit request =>
      viewmodel(mode, index, srn).retrieve.right.map {
        vm =>
          get(CompanyVatId(index), form, vm)
      }
  }

  def onSubmit(mode: Mode, index: Index, srn: Option[String]): Action[AnyContent] =
    (authenticate andThen getData(mode, srn) andThen requireData).async {
    implicit request =>
      viewmodel(mode, index, srn).retrieve.right.map {
        vm =>
          post(CompanyVatId(index), mode, form, vm)
      }
  }
}