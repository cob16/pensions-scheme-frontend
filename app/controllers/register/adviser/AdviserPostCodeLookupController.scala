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

package controllers.register.adviser

import javax.inject.Inject

import config.FrontendAppConfig
import connectors.{AddressLookupConnector, DataCacheConnector}
import controllers.actions.{AuthAction, DataRequiredAction, DataRetrievalAction}
import controllers.address.PostcodeLookupController
import forms.address.PostCodeLookupFormProvider
import identifiers.register.adviser.{AdviserAddressPostCodeLookupId, AdviserDetailsId}
import models.{Index, Mode}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent}
import utils.Navigator
import viewmodels.Message
import viewmodels.address.PostcodeLookupViewModel

class AdviserPostCodeLookupController @Inject() (
                                                  override val appConfig: FrontendAppConfig,
                                                  override val messagesApi: MessagesApi,
                                                  override val cacheConnector: DataCacheConnector,
                                                  override val addressLookupConnector: AddressLookupConnector,
                                                  override val navigator: Navigator,
                                                  authenticate: AuthAction,
                                                  getData: DataRetrievalAction,
                                                  requireData: DataRequiredAction,
                                                  formProvider: PostCodeLookupFormProvider
                                                ) extends PostcodeLookupController {

  private val title: Message = "messages__adviserPostcodeLookupAddress__title"
  private val heading: Message = "messages__adviserPostcodeLookupAddress__heading"
  private val invalidPostcode: Message = "messages__error__postcode_invalid"
  private val noResults: Message = "messages__error__postcode_no_results"

  protected val form: Form[String] = formProvider()

  private def viewmodel(mode: Mode): Retrieval[PostcodeLookupViewModel] =
    Retrieval {
      implicit request =>
        AdviserDetailsId.retrieve.right.map {
          details =>
            PostcodeLookupViewModel(
              routes.AdviserPostCodeLookupController.onSubmit(mode),
              routes.AdviserAddressController.onPageLoad(mode),
              title = Message(title),
              heading = Message(heading),
              subHeading = Some(Message("messages__adviserPostcodeLookupAddress__secondary")),
              enterPostcode=Message("messages__adviserPostcodeLookupAddress__enterPostcode")
            )
        }
    }

  def onPageLoad(mode: Mode): Action[AnyContent] =
    (authenticate andThen getData andThen requireData).async {
      implicit request =>
        viewmodel(mode).retrieve.right map get
    }

  def onSubmit(mode: Mode): Action[AnyContent] =
    (authenticate andThen getData andThen requireData).async {
      implicit request =>
        viewmodel(mode).retrieve.right.map {
          vm =>
            post(AdviserAddressPostCodeLookupId, vm, mode)
        }
    }
}
