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

package controllers.register.establishers.company.director

import audit.AuditService
import config.FrontendAppConfig
import controllers.Retrievals
import controllers.actions._
import controllers.address.ManualAddressController
import forms.address.AddressFormProvider
import identifiers.register.establishers.company.director.{DirectorDetailsId, DirectorPreviousAddressId, DirectorPreviousAddressListId, DirectorPreviousAddressPostcodeLookupId}
import javax.inject.Inject
import models.address.Address
import models.{Index, Mode}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import services.UserAnswersService
import utils.annotations.EstablishersCompanyDirector
import utils.{CountryOptions, Navigator}
import viewmodels.Message
import viewmodels.address.ManualAddressViewModel

class DirectorPreviousAddressController @Inject()(
                                                   val appConfig: FrontendAppConfig,
                                                   val messagesApi: MessagesApi,
                                                   val userAnswersService: UserAnswersService,
                                                   @EstablishersCompanyDirector val navigator: Navigator,
                                                   authenticate: AuthAction,
                                                   getData: DataRetrievalAction,
                                                   allowAccess: AllowAccessActionProvider,
                                                   requireData: DataRequiredAction,
                                                   val formProvider: AddressFormProvider,
                                                   val countryOptions: CountryOptions,
                                                   val auditService: AuditService
                                                 ) extends ManualAddressController with I18nSupport with Retrievals {

  protected val form: Form[Address] = formProvider()
  private[controllers] val postCall = routes.DirectorPreviousAddressController.onSubmit _
  private[controllers] val title: Message = "messages__companyDirectorAddress__title"
  private[controllers] val heading: Message = "messages__companyDirectorAddress__heading"

  def onPageLoad(mode: Mode, establisherIndex: Index, directorIndex: Index, srn: Option[String]): Action[AnyContent] =
    (authenticate andThen getData(mode, srn) andThen allowAccess(srn) andThen requireData).async {
      implicit request =>
        viewmodel(mode: Mode, establisherIndex: Index, directorIndex: Index, srn).retrieve.right.map {
          vm =>
            get(DirectorPreviousAddressId(establisherIndex, directorIndex), DirectorPreviousAddressListId(establisherIndex, directorIndex), vm)
        }
    }

  private def viewmodel(mode: Mode, establisherIndex: Index, directorIndex: Index, srn: Option[String]): Retrieval[ManualAddressViewModel] =
    Retrieval {
      implicit request =>
        DirectorDetailsId(establisherIndex, directorIndex).retrieve.right.map {
          director =>
            ManualAddressViewModel(
              postCall(mode, establisherIndex, directorIndex, srn),
              countryOptions.options,
              title = Message(title),
              heading = Message(heading),
              secondaryHeader = Some(director.fullName),
              srn = srn
            )
        }
    }

  def onSubmit(mode: Mode, establisherIndex: Index, directorIndex: Index, srn: Option[String]): Action[AnyContent] = (authenticate andThen getData(mode, srn) andThen requireData).async {
    implicit request =>
      viewmodel(mode: Mode, establisherIndex: Index, directorIndex: Index, srn).retrieve.right.map {
        vm =>
          post(
            DirectorPreviousAddressId(establisherIndex, directorIndex),
            DirectorPreviousAddressListId(establisherIndex, directorIndex),
            vm,
            mode,
            context(vm),
            DirectorPreviousAddressPostcodeLookupId(establisherIndex, directorIndex)
          )
      }
  }

  private def context(viewModel: ManualAddressViewModel): String = {
    viewModel.secondaryHeader match {
      case Some(name) => s"Company Director Previous Address: $name"
      case _ => "Company Director Previous Address"
    }
  }

}
