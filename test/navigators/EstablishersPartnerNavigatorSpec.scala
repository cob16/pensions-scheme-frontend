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

import base.SpecBase
import connectors.FakeUserAnswersCacheConnector
import controllers.register.establishers.partnership.partner._
import identifiers.Identifier
import identifiers.register.establishers.EstablishersId
import identifiers.register.establishers.partnership.partner._
import identifiers.register.establishers.partnership.{AddPartnersId, PartnershipDetailsId}
import models.Mode.checkMode
import models._
import models.person.PersonDetails
import org.joda.time.LocalDate
import org.scalatest.OptionValues
import org.scalatest.prop.TableFor6
import play.api.libs.json.Json
import play.api.mvc.Call
import utils.UserAnswers

class EstablishersPartnerNavigatorSpec extends SpecBase with NavigatorBehaviour {
  //scalastyle:off line.size.limit
  //scalastyle:off magic.number
  import EstablishersPartnerNavigatorSpec._

  private val navigator = new EstablishersPartnerNavigator(FakeUserAnswersCacheConnector, frontendAppConfig)

  private def commonRoutes(mode: Mode): TableFor6[Identifier, UserAnswers, Call, Boolean, Option[Call], Boolean] = Table(
    ("Id", "User Answers", "Next Page (Normal Mode)", "Save (NM)", "Next Page (Check Mode)", "Save (CM)"),
    (AddPartnersId(0), emptyAnswers, partnerDetails(0, mode), true, Some(partnerDetails(0, checkMode(mode))), true),
    (AddPartnersId(0), addPartnersTrue, partnerDetails(1, mode), true, Some(partnerDetails(1, checkMode(mode))), true),
    (AddPartnersId(0), addPartnersFalse, partnershipReview(mode), true, Some(partnershipReview(checkMode(mode))), true),
    (AddPartnersId(0), addOnePartner, sessionExpired, false, Some(sessionExpired), false),
    (AddPartnersId(0), addPartnersMoreThan10, otherPartners(mode), true, Some(otherPartners(checkMode(mode))), true),
    (PartnerDetailsId(0, 0), emptyAnswers, partnerNino(mode), true, Some(exitJourney(mode)), true),
    (PartnerNinoId(0, 0), emptyAnswers, partnerUtr(mode), true, Some(exitJourney(mode)), true),
    (PartnerUniqueTaxReferenceId(0, 0), emptyAnswers, partnerAddressPostcode(mode), true, Some(exitJourney(mode)), true),
    (PartnerAddressPostcodeLookupId(0, 0), emptyAnswers, partnerAddressList(mode), true, Some(partnerAddressList(checkMode(mode))), true),
    (PartnerAddressListId(0, 0), emptyAnswers, partnerAddress(mode), true, Some(partnerAddress(checkMode(mode))), true),
    (PartnerAddressId(0, 0), emptyAnswers, partnerAddressYears(mode), true, Some(exitJourney(mode)), true),
    (PartnerAddressYearsId(0, 0), addressYearsUnderAYear, partnerPreviousAddPostcode(mode), true, Some(partnerPreviousAddPostcode(checkMode(mode))), true),
    (PartnerAddressYearsId(0, 0), addressYearsOverAYear, partnerContactDetails(mode), true, Some(exitJourney(mode)), true),
    (PartnerAddressYearsId(0, 0), emptyAnswers, sessionExpired, false, Some(sessionExpired), false),
    (PartnerPreviousAddressPostcodeLookupId(0, 0), emptyAnswers, partnerPreviousAddList(mode), true, Some(partnerPreviousAddList(checkMode(mode))), true),
    (PartnerPreviousAddressListId(0, 0), emptyAnswers, partnerPreviousAddress(mode), true, Some(partnerPreviousAddress(checkMode(mode))), true),
    (PartnerPreviousAddressId(0, 0), emptyAnswers, partnerContactDetails(mode), true, Some(exitJourney(mode)), true),
    (PartnerContactDetailsId(0, 0), emptyAnswers, checkYourAnswers(mode), true, Some(exitJourney(mode)), true)
  )


  private def normalRoutes(mode:Mode): TableFor6[Identifier, UserAnswers, Call, Boolean, Option[Call], Boolean] = commonRoutes(mode) ++ Table(
    ("Id", "User Answers", "Next Page (Normal Mode)", "Save (NM)", "Next Page (Check Mode)", "Save (CM)"),
    (ConfirmDeletePartnerId(0), emptyAnswers, addPartners(mode), false, None, false),
    (CheckYourAnswersId(0, 0), emptyAnswers, addPartners(mode), true, None, true)
  )

  private def editRoutes(mode:Mode): TableFor6[Identifier, UserAnswers, Call, Boolean, Option[Call], Boolean] = commonRoutes(mode) ++ Table(
    ("Id", "User Answers", "Next Page (Normal Mode)", "Save (NM)", "Next Page (Check Mode)", "Save (CM)"),
    (ConfirmDeletePartnerId(0), emptyAnswers, anyMoreChanges, false, None, false),
    (CheckYourAnswersId(0, 0), emptyAnswers, anyMoreChanges, true, None, true)
  )

  navigator.getClass.getSimpleName must {
    appRunning()
    behave like navigatorWithRoutes(navigator, FakeUserAnswersCacheConnector, normalRoutes(NormalMode), dataDescriber)
    behave like navigatorWithRoutes(navigator, FakeUserAnswersCacheConnector, editRoutes(UpdateMode), dataDescriber, UpdateMode)
    behave like nonMatchingNavigator(navigator)
  }

}

object EstablishersPartnerNavigatorSpec extends OptionValues {
  private def dataDescriber(answers: UserAnswers): String = answers.toString

  private val emptyAnswers = UserAnswers(Json.obj())
  val establisherIndex = Index(0)
  val partnerIndex = Index(0)
  private val johnDoe = PersonDetails("John", None, "Doe", LocalDate.now())

  val addressYearsOverAYear = UserAnswers(Json.obj())
    .set(PartnerAddressYearsId(establisherIndex, partnerIndex))(AddressYears.OverAYear).asOpt.value
  val addressYearsUnderAYear = UserAnswers(Json.obj())
    .set(PartnerAddressYearsId(establisherIndex, partnerIndex))(AddressYears.UnderAYear).asOpt.value

  private def validData(partners: PersonDetails*) = {
    Json.obj(
      EstablishersId.toString -> Json.arr(
        Json.obj(
          PartnershipDetailsId.toString -> PartnershipDetails("test partnership name", false),
          "partner" -> partners.map(d => Json.obj(PartnerDetailsId.toString -> Json.toJson(d)))
        )
      )
    )
  }

  private val addPartnersTrue = UserAnswers(validData(johnDoe)).set(AddPartnersId(0))(true).asOpt.value
  private val addPartnersFalse = UserAnswers(validData(johnDoe)).set(AddPartnersId(0))(false).asOpt.value
  private val addPartnersMoreThan10 = UserAnswers(validData(Seq.fill(10)(johnDoe): _*))
  private val addOnePartner = UserAnswers(validData(johnDoe))

  private def anyMoreChanges = controllers.routes.AnyMoreChangesController.onPageLoad(None)

  private def exitJourney(mode: Mode) = if (mode == NormalMode) checkYourAnswers(mode) else anyMoreChanges

  private def partnerNino(mode: Mode) = routes.PartnerNinoController.onPageLoad(mode, establisherIndex, partnerIndex, None)

  private def partnerDetails(partnerIndex: Index = Index(0), mode: Mode) = routes.PartnerDetailsController.onPageLoad(mode, 0, partnerIndex, None)

  private def partnerUtr(mode: Mode) = routes.PartnerUniqueTaxReferenceController.onPageLoad(mode, partnerIndex, establisherIndex, None)

  private def partnerContactDetails(mode: Mode) = routes.PartnerContactDetailsController.onPageLoad(mode, partnerIndex, establisherIndex, None)

  private def partnerAddressPostcode(mode: Mode) = routes.PartnerAddressPostcodeLookupController.onPageLoad(mode, partnerIndex, establisherIndex, None)

  private def partnerAddressList(mode: Mode) = routes.PartnerAddressListController.onPageLoad(mode, partnerIndex, establisherIndex, None)

  private def partnerAddress(mode: Mode) = routes.PartnerAddressController.onPageLoad(mode, partnerIndex, establisherIndex, None)

  private def partnerAddressYears(mode: Mode) = routes.PartnerAddressYearsController.onPageLoad(mode, partnerIndex, establisherIndex, None)

  private def partnerPreviousAddPostcode(mode: Mode) = routes.PartnerPreviousAddressPostcodeLookupController.onPageLoad(mode, partnerIndex, establisherIndex, None)

  private def partnerPreviousAddList(mode: Mode) = routes.PartnerPreviousAddressListController.onPageLoad(mode, partnerIndex, establisherIndex, None)

  private def partnerPreviousAddress(mode: Mode) = routes.PartnerPreviousAddressController.onPageLoad(mode, partnerIndex, establisherIndex, None)

  private def checkYourAnswers(mode: Mode) = routes.CheckYourAnswersController.onPageLoad(mode, establisherIndex, partnerIndex, None)

  private def sessionExpired = controllers.routes.SessionExpiredController.onPageLoad()

  private def addPartners(mode: Mode) = controllers.register.establishers.partnership.routes.AddPartnersController.onPageLoad(mode, establisherIndex, None)

  private def partnershipReview(mode: Mode) = controllers.register.establishers.partnership.routes.PartnershipReviewController.onPageLoad(mode, establisherIndex, None)

  private def otherPartners(mode: Mode) = controllers.register.establishers.partnership.routes.OtherPartnersController.onPageLoad(mode, 0, None)
}
