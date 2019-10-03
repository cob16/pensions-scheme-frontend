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

package navigators.establishers.individual

import com.google.inject.Inject
import connectors.UserAnswersCacheConnector
import controllers.register.establishers.individual.routes._
import controllers.routes.{AnyMoreChangesController, SessionExpiredController}
import identifiers.Identifier
import identifiers.register.establishers.IsEstablisherNewId
import identifiers.register.establishers.individual._
import models.Mode._
import models._
import navigators.AbstractNavigator
import play.api.mvc.Call
import utils.UserAnswers

class EstablishersIndividualAddressNavigator @Inject()(val dataCacheConnector: UserAnswersCacheConnector) extends AbstractNavigator {

  import  EstablishersIndividualAddressNavigator._

  private def normalAndCheckModeRoutes(mode: SubscriptionMode, ua: UserAnswers, srn: Option[String]): PartialFunction[Identifier, Call] = {
    case PostCodeLookupId(index)                => AddressListController.onPageLoad(mode, index, None)
    case AddressListId(index)                   => AddressController.onPageLoad(mode, index, None)
    case AddressId(index) if mode == NormalMode => AddressYearsController.onPageLoad(mode, index, None)
    case AddressId(index)                       => CheckYourAnswersAddressController.onPageLoad(journeyMode(mode), index, None)
    case AddressYearsId(index)                  => establisherAddressYearsRoutes(mode, ua, index, None)
    case PreviousPostCodeLookupId(index)        => PreviousAddressListController.onPageLoad(mode, index, None)
    case PreviousAddressListId(index)           => PreviousAddressController.onPageLoad(mode, index, None)
    case PreviousAddressId(index)               => CheckYourAnswersAddressController.onPageLoad(journeyMode(mode), index, None)
  }

  //scalastyle:off cyclomatic.complexity
  private def updateModeRoutes(mode: VarianceMode, ua: UserAnswers, srn: Option[String]): PartialFunction[Identifier, Call] = {
    case PostCodeLookupId(index)                      => AddressListController.onPageLoad(mode, index, srn)
    case AddressListId(index)                         => AddressController.onPageLoad(mode, index, srn)
    case AddressId(index) if mode == UpdateMode       => AddressYearsController.onPageLoad(mode, index, srn)
    case AddressId(index)                             => establisherAddressRoute(ua, mode, index, srn)
    case AddressYearsId(index)                        => establisherAddressYearsRoutes(mode, ua, index, srn)
    case PreviousPostCodeLookupId(index)              => PreviousAddressListController.onPageLoad(mode, index, srn)
    case PreviousAddressListId(index)                 => PreviousAddressController.onPageLoad(mode, index, srn)
    case id@IndividualConfirmPreviousAddressId(index) => booleanNav(id, ua, moreChanges(srn), previousAddressLookup(mode, index, srn))
    case PreviousAddressId(index)                     => cyaOrMoreChanges(ua, journeyMode(mode), index, srn)
  }
  //scalastyle:on cyclomatic.complexity

  override protected def routeMap(from: NavigateFrom): Option[NavigateTo] =
    navigateTo(normalAndCheckModeRoutes(NormalMode, from.userAnswers, None), from.id)

  override protected def editRouteMap(from: NavigateFrom): Option[NavigateTo] =
    navigateTo(normalAndCheckModeRoutes(CheckMode, from.userAnswers, None), from.id)

  override protected def updateRouteMap(from: NavigateFrom, srn: Option[String]): Option[NavigateTo] =
    navigateTo(updateModeRoutes(UpdateMode, from.userAnswers, srn), from.id)

  override protected def checkUpdateRouteMap(from: NavigateFrom, srn: Option[String]): Option[NavigateTo] =
    navigateTo(updateModeRoutes(CheckUpdateMode, from.userAnswers, srn), from.id)
}

object EstablishersIndividualAddressNavigator {
  private def moreChanges(srn: Option[String]): Call = AnyMoreChangesController.onPageLoad(srn)
  private def previousAddressLookup(mode: Mode, index: Index, srn: Option[String]): Call =
    PreviousAddressPostCodeLookupController.onPageLoad(mode, index, srn)

  private def cyaOrMoreChanges(ua: UserAnswers, mode: Mode, index: Int, srn: Option[String]): Call =
    ua.get(IsEstablisherNewId(index)) match {
      case Some(true) => CheckYourAnswersAddressController.onPageLoad(mode, index, srn)
      case _ => moreChanges(srn)
    }

  private def establisherAddressRoute(ua: UserAnswers, mode: Mode, index: Int, srn: Option[String]): Call = {
    ua.get(IsEstablisherNewId(index)) match {
      case Some(true) => CheckYourAnswersAddressController.onPageLoad(journeyMode(mode), index, srn)
      case _ => IndividualConfirmPreviousAddressController.onPageLoad(index, srn)
    }
  }

  private def establisherAddressYearsRoutes(mode: Mode, ua: UserAnswers, index: Int, srn: Option[String]): Call =
    ua.get(AddressYearsId(index)) match {
      case Some(AddressYears.OverAYear) => CheckYourAnswersAddressController.onPageLoad(journeyMode(mode), index, srn)
      case Some(AddressYears.UnderAYear) => previousAddressLookup(mode, index, srn)
      case _ => SessionExpiredController.onPageLoad()
    }
}


