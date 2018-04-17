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

package controllers.register.trustees.individual

import javax.inject.Inject

import config.FrontendAppConfig
import connectors.DataCacheConnector
import controllers.Retrievals
import controllers.actions.{AuthAction, DataRequiredAction, DataRetrievalAction}
import controllers.address.AddressListController
import identifiers.register.trustees.individual._
import models.{Index, Mode}
import models.requests.DataRequest
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, Result}
import utils.Navigator
import viewmodels.Message
import viewmodels.address.AddressListViewModel

import scala.concurrent.Future

class IndividualAddressListController @Inject()(override val appConfig: FrontendAppConfig,
                                             override val messagesApi: MessagesApi,
                                             override val cacheConnector: DataCacheConnector,
                                             override val navigator: Navigator,
                                             authenticate: AuthAction,
                                             getData: DataRetrievalAction,
                                             requireData: DataRequiredAction) extends AddressListController with Retrievals  {

  def onPageLoad(mode: Mode, index: Index): Action[AnyContent] = (authenticate andThen getData andThen requireData).async {
    implicit request =>
       viewmodel(mode, index).right.map(get)
  }

  def onSubmit(mode: Mode, index: Index): Action[AnyContent] = (authenticate andThen getData andThen requireData).async {
    implicit request =>
      viewmodel(mode, index).right.map{
        vm =>
          post(vm, IndividualAddressListId(index), TrusteeAddressId(index), mode)
      }
  }

  private def viewmodel(mode: Mode, index: Index)(implicit request: DataRequest[AnyContent]): Either[Future[Result], AddressListViewModel] = {
    (TrusteeDetailsId(index) and IndividualPostCodeLookupId(index)).retrieve.right.map{
      case trusteeDetails ~ addresses => AddressListViewModel(
        postCall = routes.IndividualAddressListController.onSubmit(mode, index),
        manualInputCall = routes.TrusteeAddressController.onPageLoad(mode, index),
        addresses = addresses,
        subHeading = Some(Message(trusteeDetails.fullName))
      )
    }.left.map(_ =>
      Future.successful(Redirect(routes.IndividualPostCodeLookupController.onPageLoad(mode,index))))
  }
}