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

package controllers.register.establishers.partnership

import config.FeatureSwitchManagementService
import controllers.ControllerSpecBase
import controllers.actions.{DataRequiredActionImpl, DataRetrievalAction, FakeAllowAccessProvider, FakeAuthAction}
import controllers.behaviours.ControllerAllowChangeBehaviour
import identifiers.register.establishers.partnership._
import models.AddressYears.UnderAYear
import models._
import models.address.Address
import models.register.DeclarationDormant
import models.requests.DataRequest
import play.api.mvc.{AnyContent, Call}
import play.api.test.Helpers._
import services.FakeUserAnswersService
import utils._
import utils.checkyouranswers.Ops._
import viewmodels.{AnswerRow, AnswerSection}
import views.html.check_your_answers

class CheckYourAnswersControllerSpec extends ControllerSpecBase with ControllerAllowChangeBehaviour {

 import CheckYourAnswersControllerSpec._

  "CheckYourAnswersController" must {

    "display answers" in {


      val result = controller(partnershipAnswers.dataRetrievalAction).onPageLoad(NormalMode, firstIndex, None)(request)


      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString(Seq(partnershipDetails, partnershipContactDetails))
    }

    behave like changeableController(
      controller(partnershipAnswers.dataRetrievalAction, _:AllowChangeHelper)
        .onPageLoad(NormalMode, firstIndex, None)(request)
    )

    "return OK and the correct view for a GET with all the answers when separate ref collection toggle is on" in {
      val request = FakeDataRequest(UserAnswers())
      val result = controller(UserAnswers().dataRetrievalAction,
        fs = new FakeFeatureSwitchManagementService(true)).onPageLoad(UpdateMode, firstIndex, srn)(request)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString(payeSection(request), postUrlUpdateMode, srn)
    }

    "redirect to Add Partners page on submit" which {
      "marks partnership as complete on submit" in {

        val result = controller().onSubmit(NormalMode, firstIndex, None)(request)

        status(result) mustBe 303
        redirectLocation(result) mustBe Some(onwardRoute.url)

        FakeUserAnswersService.verify(IsPartnershipCompleteId(firstIndex), true)
      }
    }
  }

}

object CheckYourAnswersControllerSpec extends ControllerSpecBase with ControllerAllowChangeBehaviour {

  val firstIndex = Index(0)
  val partnershipName = "PartnershipName"
  val schemeName = "testScheme"
  val srn = Some("S123")
  val partnershipAnswers = UserAnswers()
    .set(PartnershipDetailsId(firstIndex))(PartnershipDetails(partnershipName))
    .flatMap(_.set(PartnershipVatId(firstIndex))(Vat.No))
    .flatMap(_.set(PartnershipUniqueTaxReferenceID(firstIndex))(UniqueTaxReference.Yes("0987654321")))
    .flatMap(_.set(PartnershipAddressId(firstIndex))(Address("Address 1", "Address 2", None, None, None, "GB")))
    .flatMap(_.set(PartnershipAddressYearsId(firstIndex))(UnderAYear))
    .flatMap(_.set(PartnershipPreviousAddressId(firstIndex))(Address("Previous Address 1", "Previous Address 2", None, None, None, "US")))
    .flatMap(_.set(PartnershipContactDetailsId(firstIndex))(ContactDetails("e@mail.co", "98765")))
    .flatMap(_.set(IsPartnershipDormantId(firstIndex))(DeclarationDormant.Yes))
    .asOpt.value

  private def payeSection(implicit request: DataRequest[AnyContent]) = Seq(AnswerSection(
    Some("messages__partnership__checkYourAnswers__partnership_details"),
    Seq(AnswerRow("messages__common__cya__paye", Seq("site.not_entered"), answerIsMessageKey = true,
      Some(Link("site.add", partnershipPayeVariationsRoute, Some("messages__visuallyhidden__establisher__paye_number_add")))))),
    AnswerSection(
      Some("messages__partnership__checkYourAnswers__partnership_contact_details"),
      Seq.empty))

  implicit val request = FakeDataRequest(partnershipAnswers)
  implicit val userAnswers = request.userAnswers
  implicit val countryOptions = new FakeCountryOptions()
  private val onwardRoute = routes.AddPartnersController.onPageLoad(NormalMode, firstIndex, None)
  private val partnershipPayeVariationsRoute = routes.PartnershipPayeVariationsController.onPageLoad(CheckUpdateMode, firstIndex, srn).url

  private def controller(dataRetrievalAction: DataRetrievalAction = getEmptyData,
                         allowChangeHelper:AllowChangeHelper = ach,
                         fs:FeatureSwitchManagementService = new FakeFeatureSwitchManagementService(false)): CheckYourAnswersController =
    new CheckYourAnswersController(
      frontendAppConfig,
      messagesApi,
      FakeAuthAction,
      dataRetrievalAction,
      FakeAllowAccessProvider(),
      new DataRequiredActionImpl,
      FakeUserAnswersService,
      new FakeNavigator(onwardRoute),
      countryOptions,
      allowChangeHelper,
      fs
    )

  val postUrl = routes.CheckYourAnswersController.onSubmit(NormalMode, firstIndex, None)
  val postUrlUpdateMode = routes.CheckYourAnswersController.onSubmit(UpdateMode, firstIndex, srn)


  val partnershipDetails = AnswerSection(
    Some("messages__partnership__checkYourAnswers__partnership_details"),
    Seq(
      PartnershipDetailsId(firstIndex).row(routes.PartnershipDetailsController.onPageLoad(CheckMode, firstIndex, None).url),
      PartnershipVatId(firstIndex).row(routes.PartnershipVatController.onPageLoad(CheckMode, firstIndex, None).url),
      PartnershipPayeId(firstIndex).row(routes.PartnershipPayeController.onPageLoad(CheckMode, firstIndex, None).url),
      PartnershipUniqueTaxReferenceID(firstIndex).row(routes.PartnershipUniqueTaxReferenceController.onPageLoad(CheckMode, firstIndex, None).url),
      IsPartnershipDormantId(firstIndex).row(routes.IsPartnershipDormantController.onPageLoad(CheckMode, firstIndex, None).url)
    ).flatten
  )

  val partnershipContactDetails = AnswerSection(
    Some("messages__partnership__checkYourAnswers__partnership_contact_details"),
    Seq(
      PartnershipAddressId(firstIndex).row(routes.PartnershipAddressController.onPageLoad(CheckMode, firstIndex, None).url),
      PartnershipAddressYearsId(firstIndex).row(routes.PartnershipAddressYearsController.onPageLoad(CheckMode, firstIndex, None).url),
      PartnershipPreviousAddressId(firstIndex).row(routes.PartnershipPreviousAddressController.onPageLoad(CheckMode, firstIndex, None).url),
      PartnershipContactDetailsId(firstIndex).row(routes.PartnershipContactDetailsController.onPageLoad(CheckMode, firstIndex, None).url)
    ).flatten
  )

  def viewAsString(answerSections: Seq[AnswerSection], postUrl: Call = postUrl, srn: Option[String] = None): String = check_your_answers(
    frontendAppConfig,
    answerSections,
    postUrl,
    None,
    hideEditLinks = false,
    srn = srn,
    hideSaveAndContinueButton = false
  )(fakeRequest, messages).toString
}