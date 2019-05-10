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

package navigators

import com.google.inject.{Inject, Singleton}
import config.FrontendAppConfig
import connectors.UserAnswersCacheConnector
import identifiers.register.establishers.{IsEstablisherCompleteId, IsEstablisherNewId}
import identifiers.register.establishers.IsEstablisherNewId
import identifiers.register.establishers.individual._
import models.{AddressYears, CheckMode, CheckUpdateMode, Index, Mode, NormalMode, UpdateMode}
import models.Mode.journeyMode
import utils.{Navigator, UserAnswers}

@Singleton
class EstablishersIndividualNavigator @Inject()(
                                                 appConfig: FrontendAppConfig,
                                                 val dataCacheConnector: UserAnswersCacheConnector
                                               ) extends Navigator {

  override protected def routeMap(from: NavigateFrom): Option[NavigateTo] = routes(from, NormalMode, None)

  override protected def updateRouteMap(from: NavigateFrom, srn: Option[String]): Option[NavigateTo] = routes(from, UpdateMode, srn)

  override protected def editRouteMap(from: NavigateFrom): Option[NavigateTo] = editRoutes(from, CheckMode, None)

  override protected def checkUpdateRouteMap(from: NavigateFrom, srn: Option[String]): Option[NavigateTo] = editRoutes(from, CheckUpdateMode, srn)

  private def checkYourAnswers(index: Int, mode: Mode, srn: Option[String]): Option[NavigateTo] =
    NavigateTo.dontSave(controllers.register.establishers.individual.routes.CheckYourAnswersController.onPageLoad(mode, index, srn))

  private def anyMoreChanges(srn: Option[String]): Option[NavigateTo] =
    NavigateTo.dontSave(controllers.routes.AnyMoreChangesController.onPageLoad(srn))

  private def addressYearsRoutes(index: Int, mode: Mode, srn: Option[String])(answers: UserAnswers): Option[NavigateTo] = {
    answers.get(AddressYearsId(index)) match {
      case Some(AddressYears.UnderAYear) =>
        NavigateTo.dontSave(controllers.register.establishers.individual.routes.PreviousAddressPostCodeLookupController.onPageLoad(mode, index, srn))
      case Some(AddressYears.OverAYear) =>
        NavigateTo.dontSave(controllers.register.establishers.individual.routes.ContactDetailsController.onPageLoad(mode, index, srn))
      case None =>
        NavigateTo.dontSave(controllers.routes.SessionExpiredController.onPageLoad())
    }
  }

  //scalastyle:off cyclomatic.complexity
  protected def routes(from: NavigateFrom, mode: Mode, srn: Option[String]): Option[NavigateTo] = {
    from.id match {
      case EstablisherDetailsId(index) =>
        NavigateTo.dontSave(controllers.register.establishers.individual.routes.EstablisherNinoController.onPageLoad(mode, index, srn))

      case EstablisherNinoId(index) =>
        NavigateTo.dontSave(controllers.register.establishers.individual.routes.UniqueTaxReferenceController.onPageLoad(mode, index, srn))

      case UniqueTaxReferenceId(index) =>
        NavigateTo.dontSave(controllers.register.establishers.individual.routes.PostCodeLookupController.onPageLoad(mode, index, srn))

      case PostCodeLookupId(index) =>
        NavigateTo.dontSave(controllers.register.establishers.individual.routes.AddressListController.onPageLoad(mode, index, srn))

      case AddressListId(index) =>
        NavigateTo.dontSave(controllers.register.establishers.individual.routes.AddressController.onPageLoad(mode, index, srn))

      case AddressId(index) =>
        NavigateTo.dontSave(controllers.register.establishers.individual.routes.AddressYearsController.onPageLoad(mode, index, srn))

      case AddressYearsId(index) =>
        addressYearsRoutes(index, mode, srn)(from.userAnswers)

      case PreviousPostCodeLookupId(index) =>
        NavigateTo.dontSave(controllers.register.establishers.individual.routes.PreviousAddressListController.onPageLoad(mode, index, srn))

      case PreviousAddressListId(index) =>
        NavigateTo.dontSave(controllers.register.establishers.individual.routes.PreviousAddressController.onPageLoad(mode, index, srn))

      case PreviousAddressId(index) =>
        NavigateTo.dontSave(controllers.register.establishers.individual.routes.ContactDetailsController.onPageLoad(mode, index, srn))

      case ContactDetailsId(index) =>
        checkYourAnswers(index, mode, srn)

      case CheckYourAnswersId =>
        if (mode == CheckMode || mode == NormalMode) {
          NavigateTo.dontSave(controllers.register.establishers.routes.AddEstablisherController.onPageLoad(mode, srn))
        } else {
          anyMoreChanges(srn)
        }
    }
  }

  private def exitMiniJourney(index: Index, mode: Mode, srn: Option[String], answers: UserAnswers): Option[NavigateTo] =
    if(mode == CheckMode || mode == NormalMode){
      checkYourAnswers(index, journeyMode(mode), srn)
    } else {
      if(answers.get(IsEstablisherCompleteId(index)).getOrElse(false)) anyMoreChanges(srn)
      else checkYourAnswers(index, journeyMode(mode), srn)
    }

  private def addressYearsEditRoutes(index: Int, mode: Mode, srn: Option[String])(answers: UserAnswers): Option[NavigateTo] = {
    answers.get(AddressYearsId(index)) match {
      case Some(AddressYears.UnderAYear) =>
        NavigateTo.dontSave(controllers.register.establishers.individual.routes.PreviousAddressPostCodeLookupController.onPageLoad(mode, index, srn))
      case Some(AddressYears.OverAYear) =>
        exitMiniJourney(index, mode, srn, answers)
      case None =>
        NavigateTo.dontSave(controllers.routes.SessionExpiredController.onPageLoad())
    }
  }

  protected def editRoutes(from: NavigateFrom, mode: Mode, srn: Option[String]): Option[NavigateTo] = {
    from.id match {
      case EstablisherDetailsId(index) =>       exitMiniJourney(index, mode, srn, from.userAnswers)
      case EstablisherNinoId(index) =>          exitMiniJourney(index, mode, srn, from.userAnswers)
      case UniqueTaxReferenceId(index) =>       exitMiniJourney(index, mode, srn, from.userAnswers)

      case PostCodeLookupId(index) =>
        NavigateTo.dontSave(controllers.register.establishers.individual.routes.AddressListController.onPageLoad(mode, index, srn))

      case AddressListId(index) =>
        NavigateTo.dontSave(controllers.register.establishers.individual.routes.AddressController.onPageLoad(mode, index, srn))

      case AddressId(index) =>
        val isNew = from.userAnswers.get(IsEstablisherNewId(index)).contains(true)
        if(isNew || mode == CheckMode) {
          checkYourAnswers(index, journeyMode(mode), srn)
        } else {
          NavigateTo.dontSave(controllers.register.establishers.individual.routes.AddressYearsController.onPageLoad(mode, index, srn))
        }

      case AddressYearsId(index) =>
        addressYearsEditRoutes(index, mode, srn)(from.userAnswers)

      case PreviousPostCodeLookupId(index) =>
        NavigateTo.dontSave(controllers.register.establishers.individual.routes.PreviousAddressListController.onPageLoad(mode, index, srn))

      case PreviousAddressListId(index) =>
        NavigateTo.dontSave(controllers.register.establishers.individual.routes.PreviousAddressController.onPageLoad(mode, index, srn))

      case PreviousAddressId(index) =>          exitMiniJourney(index, mode, srn, from.userAnswers)
      case ContactDetailsId(index) =>           exitMiniJourney(index, mode, srn, from.userAnswers)
    }
  }
}
