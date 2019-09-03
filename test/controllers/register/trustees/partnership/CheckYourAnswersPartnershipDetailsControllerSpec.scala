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

package controllers.register.trustees.partnership

import controllers.ControllerSpecBase
import controllers.actions.{DataRetrievalAction, FakeAuthAction, _}
import controllers.behaviours.ControllerAllowChangeBehaviour
import identifiers.register.trustees.IsTrusteeNewId
import identifiers.register.trustees.partnership._
import models.Mode.checkMode
import models.requests.DataRequest
import models.{Index, NormalMode, _}
import org.scalatest.OptionValues
import play.api.mvc.{AnyContent, Call}
import play.api.test.Helpers._
import services.FakeUserAnswersService
import utils.{CountryOptions, FakeCountryOptions, FakeNavigator, UserAnswers, _}
import viewmodels.{AnswerRow, AnswerSection}
import views.html.check_your_answers

class CheckYourAnswersPartnershipDetailsControllerSpec extends ControllerSpecBase with ControllerAllowChangeBehaviour {

  import CheckYourAnswersPartnershipDetailsControllerSpec._

  "Check Your Answers Partnership Details Controller " when {
    "when in registration journey" must {
      "return OK and the correct view with full answers when user has answered yes to all questions" in {
        val request = FakeDataRequest(fullAnswersYes())
        val result = controller(fullAnswersYes().dataRetrievalAction).onPageLoad(NormalMode, index, None)(request)

        status(result) mustBe OK
        contentAsString(result) mustBe viewAsString(partnershipDetailsAllValues(NormalMode, None)(request))
      }

      "return OK and the correct view with full answers when user has answered no to all questions" in {
        val request = FakeDataRequest(fullAnswersNo)
        val result = controller(fullAnswersNo.dataRetrievalAction).onPageLoad(NormalMode, index, None)(request)

        status(result) mustBe OK
        contentAsString(result) mustBe viewAsString(partnershipDetailsAllReasons(NormalMode, None)(request))
      }
    }

    "when in variations journey with existing establisher" must {
      "return OK and the correct view with full answers when user has answered yes to all questions" in {
        val request = FakeDataRequest(fullAnswersYes(false))
        val result = controller(fullAnswersYes(false).dataRetrievalAction).onPageLoad(UpdateMode, index, srn)(request)

        status(result) mustBe OK
        contentAsString(result) mustBe
          viewAsString(partnershipDetailsAllExistingAnswers(UpdateMode, srn)(request), UpdateMode, srn)
      }

      "return OK and the correct view with full answers when user has answered no to all questions" in {
        val request = FakeDataRequest(fullAnswersNo)
        val result = controller(fullAnswersNo.dataRetrievalAction).onPageLoad(UpdateMode, index, srn)(request)

        status(result) mustBe OK
        contentAsString(result) mustBe
          viewAsString(partnershipDetailsAddLinksValues(request), UpdateMode, srn)
      }
    }

    "when in variations journey with new establisher" must {

      "return OK and the correct view with full answers when user has answered yes to all questions" in {
        val answers = fullAnswersYes().set(IsTrusteeNewId(0))(true).asOpt.value
        val request = FakeDataRequest(answers)
        val result = controller(answers.dataRetrievalAction).onPageLoad(UpdateMode, index, srn)(request)

        status(result) mustBe OK
        contentAsString(result) mustBe viewAsString(partnershipDetailsAllValues(UpdateMode, srn)(request), mode = UpdateMode, srn = srn)
      }

      "return OK and the correct view with full answers when user has answered no to all questions" in {
        val answers = fullAnswersNo.set(IsTrusteeNewId(0))(true).asOpt.value
        val request = FakeDataRequest(answers)
        val result = controller(answers.dataRetrievalAction).onPageLoad(UpdateMode, index, srn)(request)

        status(result) mustBe OK
        contentAsString(result) mustBe viewAsString(partnershipDetailsAllReasons(UpdateMode, srn)(request), mode = UpdateMode, srn = srn)
      }
    }
  }

}

object CheckYourAnswersPartnershipDetailsControllerSpec extends ControllerSpecBase with Enumerable.Implicits
  with ControllerAllowChangeBehaviour with OptionValues {

  def onwardRoute(mode: Mode = NormalMode, srn: Option[String] = None): Call =
    controllers.routes.SchemeTaskListController.onPageLoad(mode, srn)

  private implicit val fakeCountryOptions: CountryOptions = new FakeCountryOptions
  val index = Index(0)
  val testSchemeName = "Test Scheme Name"
  val srn = Some("S123")
  val partnershipName = "test partnership name"

  private val utr = "utr"
  private val vat = "vat"
  private val paye = "paye"
  private val reason = "reason"

  private val emptyAnswers = UserAnswers().set(PartnershipDetailsId(0))(PartnershipDetails(partnershipName)).asOpt.value

  private def hasPartnershipUTRRoute(mode: Mode, srn: Option[String]) =
    routes.PartnershipHasUTRController.onPageLoad(checkMode(mode), index, srn).url

  private def partnershipUTRRoute(mode: Mode, srn: Option[String]) =
    routes.PartnershipUTRController.onPageLoad(checkMode(mode), index, srn).url

  private def noPartnershipUTRRoute(mode: Mode, srn: Option[String]) =
    routes.PartnershipNoUTRReasonController.onPageLoad(checkMode(mode), 0, srn).url

  private def hasPartnershipVatRoute(mode: Mode, srn: Option[String]) =
    routes.PartnershipHasVATController.onPageLoad(checkMode(mode), 0, srn).url

  private def partnershipEnterVATRoute(mode: Mode, srn: Option[String]) =
    routes.PartnershipEnterVATController.onPageLoad(checkMode(mode), 0, srn).url

  private def hasPartnershipPayeRoute(mode: Mode, srn: Option[String]) =
    routes.PartnershipHasPAYEController.onPageLoad(checkMode(mode), 0, srn).url

  private def partnershipPayeVariationsRoute(mode: Mode, srn: Option[String]) =
    routes.PartnershipPayeVariationsController.onPageLoad(checkMode(mode), 0, srn).url

  private def fullAnswersYes(isEditable: Boolean = true) = emptyAnswers
    .set(PartnershipHasUTRId(0))(true).flatMap(
        _.set(PartnershipUTRId(0))(ReferenceValue(utr, isEditable)).flatMap(
          _.set(PartnershipHasVATId(0))(true).flatMap(
            _.set(PartnershipEnterVATId(0))(ReferenceValue(vat, isEditable)).flatMap(
              _.set(PartnershipHasPAYEId(0))(true).flatMap(
                _.set(PartnershipPayeVariationsId(0))(ReferenceValue(paye, isEditable))
              ))))).asOpt.value

  private val fullAnswersNo = emptyAnswers
    .set(PartnershipHasUTRId(0))(false).flatMap(
    _.set(PartnershipNoUTRReasonId(0))(reason).flatMap(
      _.set(PartnershipHasVATId(0))(false).flatMap(
            _.set(PartnershipHasPAYEId(0))(false)
          ))).asOpt.value


  private def partnershipDetailsAddLinksValues(implicit request: DataRequest[AnyContent]): Seq[AnswerSection] =
    Seq(AnswerSection(
      None,
      Seq(
        addLink(messages("messages__cya__utr"), partnershipUTRRoute(UpdateMode, srn),
          messages("messages__visuallyhidden__partnership__utr_add")),
        addLink(messages("messages__common__cya__vat"), partnershipEnterVATRoute(UpdateMode, srn),
          messages("messages__visuallyhidden__partnership__vat_number_add")),
        addLink(messages("messages__common__cya__paye"), partnershipPayeVariationsRoute(UpdateMode, srn),
          messages("messages__visuallyhidden__trustee__paye_number_add"))
      )
    ))

  private def partnershipDetailsAllValues(mode: Mode, srn: Option[String]
                                     )(implicit request: DataRequest[AnyContent]): Seq[AnswerSection] =
    Seq(AnswerSection(
      None,
      Seq(
        booleanChangeLink(messages("messages__partnershipHasUtr__heading", partnershipName), hasPartnershipUTRRoute(mode, srn), value = true,
          messages("messages__visuallyhidden__partnership__utr_yes_no")),
        stringChangeLink(messages("messages__cya__utr"), partnershipUTRRoute(mode, srn), utr,
          messages("messages__visuallyhidden__partnership__utr")),
        booleanChangeLink(messages("messages__vat__heading", partnershipName), hasPartnershipVatRoute(mode, srn), value = true,
          messages("messages__visuallyhidden__partnership__vat_yes_no")),
        stringChangeLink(messages("messages__common__cya__vat"), partnershipEnterVATRoute(mode, srn), vat,
          messages("messages__visuallyhidden__partnership__vat_number")),
        booleanChangeLink(messages("messages__hasPaye__h1", partnershipName), hasPartnershipPayeRoute(mode, srn), value = true,
          messages("messages__visuallyhidden__partnership__paye_yes_no")),
        stringChangeLink(messages("messages__common__cya__paye"), partnershipPayeVariationsRoute(mode, srn), paye,
          messages("messages__visuallyhidden__trustee__paye_number"))
      )
    ))

  private def partnershipDetailsAllExistingAnswers(mode: Mode, srn: Option[String]
                                         )(implicit request: DataRequest[AnyContent]): Seq[AnswerSection] =
    Seq(AnswerSection(
      None,
      Seq(
        stringNoLink(messages("messages__cya__utr"), utr),
        stringNoLink(messages("messages__common__cya__vat"), vat),
        stringNoLink(messages("messages__common__cya__paye"), paye)
      )
    ))


  private def partnershipDetailsAllReasons(mode: Mode, srn: Option[String]
                                      )(implicit request: DataRequest[AnyContent]): Seq[AnswerSection] =
    Seq(AnswerSection(
      None,
      Seq(
        booleanChangeLink(messages("messages__partnershipHasUtr__heading", partnershipName), hasPartnershipUTRRoute(mode, srn), value = false,
          messages("messages__visuallyhidden__partnership__utr_yes_no")),
        stringChangeLink(messages("messages__noGenericUtr__heading", partnershipName), noPartnershipUTRRoute(mode, srn), reason,
          messages("messages__visuallyhidden__partnership__utr_no")),
        booleanChangeLink(messages("messages__vat__heading", partnershipName), hasPartnershipVatRoute(mode, srn), value = false,
          messages("messages__visuallyhidden__partnership__vat_yes_no")),
        booleanChangeLink(messages("messages__hasPaye__h1", partnershipName), hasPartnershipPayeRoute(mode, srn), value = false,
          messages("messages__visuallyhidden__partnership__paye_yes_no"))
      )
    ))

  private def booleanChangeLink(label: String, changeUrl: String, value: Boolean, hiddenLabel: String) =
    AnswerRow(label, Seq(if (value) "site.yes" else "site.no"),
      answerIsMessageKey = true,
      Some(Link("site.change", changeUrl, Some(hiddenLabel))))

  private def stringChangeLink(label: String, changeUrl: String, ansOrReason: String, hiddenLabel: String) =
    AnswerRow(
      label,
      Seq(ansOrReason),
      answerIsMessageKey = false,
      Some(Link("site.change", changeUrl,
        Some(hiddenLabel)
      )))

  private def stringNoLink(label: String, ansOrReason: String) =
    AnswerRow(
      label,
      Seq(ansOrReason),
      answerIsMessageKey = false,
      None)


  private def addLink(label: String, changeUrl: String, hiddenLabel: String) =
    AnswerRow(label, Seq("site.not_entered"), answerIsMessageKey = true, Some(Link("site.add", changeUrl, Some(hiddenLabel))))


  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyData,
                 allowChangeHelper: AllowChangeHelper = ach,
                 isToggleOn: Boolean = false): CheckYourAnswersPartnershipDetailsController =
    new CheckYourAnswersPartnershipDetailsController(
      frontendAppConfig,
      messagesApi,
      FakeAuthAction,
      dataRetrievalAction,
      FakeAllowAccessProvider(),
      new DataRequiredActionImpl,
      fakeCountryOptions,
      new FakeNavigator(onwardRoute()),
      FakeUserAnswersService,
      allowChangeHelper,
      new FakeFeatureSwitchManagementService(isToggleOn)
    )

  def viewAsString(answerSections: Seq[AnswerSection], mode: Mode = NormalMode,
                   srn: Option[String] = None): String =
    check_your_answers(
      frontendAppConfig,
      answerSections,
      onwardRoute(mode, srn),
      None,
      mode = mode,
      hideEditLinks = false,
      srn = srn,
      hideSaveAndContinueButton = false
    )(fakeRequest, messages).toString

}