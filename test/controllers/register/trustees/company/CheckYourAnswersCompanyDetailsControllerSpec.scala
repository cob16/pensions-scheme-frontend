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

import controllers.ControllerSpecBase
import controllers.actions.{DataRetrievalAction, FakeAuthAction, _}
import controllers.behaviours.ControllerAllowChangeBehaviour
import identifiers.register.trustees.company._
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

class CheckYourAnswersCompanyDetailsControllerSpec extends ControllerSpecBase with ControllerAllowChangeBehaviour {

  import CheckYourAnswersCompanyDetailsControllerSpec._

  "Check Your Answers Company Details Controller " when {
    "when in registration journey" must {
      "return OK and the correct view with full answers when user has answered yes to all questions" in {
        val request = FakeDataRequest(fullAnswers)
        val result = controller(fullAnswers.dataRetrievalAction).onPageLoad(NormalMode, index, None)(request)

        status(result) mustBe OK
        contentAsString(result) mustBe viewAsString(companyDetailsAllValues(NormalMode, None)(request))
      }

      "return OK and the correct view with full answers when user has answered no to all questions" in {
        val request = FakeDataRequest(fullAnswersNo)
        val result = controller(fullAnswersNo.dataRetrievalAction).onPageLoad(NormalMode, index, None)(request)

        status(result) mustBe OK
        contentAsString(result) mustBe viewAsString(companyDetailsAllReasons(NormalMode, None)(request))
      }
    }

    "when in variations journey with existing establisher" must {
      "return OK and the correct view with full answers when user has answered yes to all questions" in {
        val request = FakeDataRequest(fullAnswers)
        val result = controller(fullAnswers.dataRetrievalAction).onPageLoad(UpdateMode, index, srn)(request)

        status(result) mustBe OK
        contentAsString(result) mustBe
          viewAsString(companyDetailsAllValues(UpdateMode, srn)(request), UpdateMode, srn, postUrlUpdateMode)
      }

      "return OK and the correct view with full answers when user has answered no to all questions" in {
        val request = FakeDataRequest(fullAnswersNo)
        val result = controller(fullAnswersNo.dataRetrievalAction).onPageLoad(UpdateMode, index, srn)(request)

        status(result) mustBe OK
        contentAsString(result) mustBe
          viewAsString(companyDetailsAddLinksValues(request), UpdateMode, srn, postUrlUpdateMode)
      }

      "return OK and the correct view with add links for values" in {
        val request = FakeDataRequest(fullAnswersNo)
        val result = controller(emptyAnswers.dataRetrievalAction).onPageLoad(UpdateMode, index, srn)(request)

        status(result) mustBe OK
        contentAsString(result) mustBe
          viewAsString(companyDetailsAddLinksValues(request), UpdateMode, srn, postUrlUpdateMode)
      }
     }

    "on Submit" must {
      "redirect to next page " in {
        val result = controller().onSubmit(NormalMode, index, None)(fakeRequest)
        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(onwardRoute.url)
      }

      "mark establisher company details as complete" in {
        val result = controller().onSubmit(NormalMode, index, None)(fakeRequest)
        status(result) mustBe SEE_OTHER
        FakeUserAnswersService.verify(IsDetailsCompleteId(index), true)
      }

      behave like changeableController(
        controller(fullAnswers.dataRetrievalAction, _: AllowChangeHelper)
          .onPageLoad(NormalMode, index, None)(FakeDataRequest(fullAnswers))
      )
    }
  }

}

object CheckYourAnswersCompanyDetailsControllerSpec extends ControllerSpecBase with Enumerable.Implicits with ControllerAllowChangeBehaviour with OptionValues {

  def onwardRoute: Call = controllers.routes.SchemeTaskListController.onPageLoad(NormalMode, None)

  private implicit val fakeCountryOptions: CountryOptions = new FakeCountryOptions
  val index = Index(0)
  val testSchemeName = "Test Scheme Name"
  val srn = Some("S123")
  val companyName = "test company name"

  private val crn = "crn"
  private val utr = "utr"
  private val vat = "vat"
  private val paye = "paye"
  private val reason = "reason"

  private val emptyAnswers = UserAnswers().set(CompanyDetailsId(0))(CompanyDetails(companyName)).asOpt.value
  private def hasCompanyNumberRoute(mode: Mode, srn: Option[String]) =
    routes.HasCompanyNumberController.onPageLoad(checkMode(mode), 0, srn).url
  private def companyRegistrationNumberVariationsRoute(mode: Mode, srn: Option[String]) =
    routes.CompanyRegistrationNumberVariationsController.onPageLoad(checkMode(mode), srn, index).url
  private def noCompanyNumberReasonRoute(mode: Mode, srn: Option[String]) =
    routes.NoCompanyNumberController.onPageLoad(checkMode(mode), index, srn).url
  private def hasCompanyUTRRoute(mode: Mode, srn: Option[String]) =
    routes.HasCompanyUTRController.onPageLoad(checkMode(mode), index, srn).url
  private def companyUTRRoute(mode: Mode, srn: Option[String]) =
    routes.CompanyUTRController.onPageLoad(checkMode(mode), srn, index).url
  private def noCompanyUTRRoute(mode: Mode, srn: Option[String]) =
    routes.CompanyNoUTRReasonController.onPageLoad(checkMode(mode), 0, srn).url
  private def hasCompanyVatRoute(mode: Mode, srn: Option[String]) =
    routes.HasCompanyVATController.onPageLoad(checkMode(mode), 0, srn).url
  private def companyVatVariationsRoute(mode: Mode, srn: Option[String]) =
    routes.CompanyVatVariationsController.onPageLoad(checkMode(mode), 0, srn).url
  private def hasCompanyPayeRoute(mode: Mode, srn: Option[String]) =
    routes.HasCompanyPAYEController.onPageLoad(checkMode(mode), 0, srn).url
  private def companyPayeVariationsRoute(mode: Mode, srn: Option[String]) =
    routes.CompanyPayeVariationsController.onPageLoad(checkMode(mode), 0, srn).url

  private val fullAnswers = emptyAnswers
    .set(HasCompanyNumberId(0))(true).flatMap(
    _.set(CompanyRegistrationNumberVariationsId(0))(ReferenceValue(crn, isEditable = true)).flatMap(
      _.set(HasCompanyUTRId(0))(true).flatMap(
        _.set(CompanyUTRId(0))(utr).flatMap(
          _.set(HasCompanyVATId(0))(true).flatMap(
            _.set(CompanyVatVariationsId(0))(ReferenceValue(vat, isEditable = true)).flatMap(
              _.set(HasCompanyPAYEId(0))(true).flatMap(
                _.set(CompanyPayeVariationsId(0))(ReferenceValue(paye, isEditable = true))
              ))))))).asOpt.value

  private val fullAnswersNo = emptyAnswers
    .set(HasCompanyNumberId(0))(false).flatMap(
    _.set(NoCompanyNumberId(0))(reason).flatMap(
      _.set(HasCompanyUTRId(0))(false).flatMap(
        _.set(CompanyNoUTRReasonId(0))(reason).flatMap(
          _.set(HasCompanyVATId(0))(false).flatMap(
            _.set(HasCompanyPAYEId(0))(false)
          ))))).asOpt.value

  def postUrl: Call = routes.CheckYourAnswersCompanyDetailsController.onSubmit(NormalMode, index, None)

  def postUrlUpdateMode: Call = routes.CheckYourAnswersCompanyDetailsController.onSubmit(UpdateMode, index, srn)


  private def companyDetailsAddLinksValues(implicit request: DataRequest[AnyContent]): Seq[AnswerSection] =
    Seq(AnswerSection(
      None,
      Seq(
        addLink(messages("messages__checkYourAnswers__establishers__company__number"), companyRegistrationNumberVariationsRoute(UpdateMode, srn),
          messages("messages__visuallyhidden__companyNumber_add")),
        addLink(messages("messages__common__cya__vat"), companyVatVariationsRoute(UpdateMode, srn),
          messages("messages__visuallyhidden__companyVat_add")),
        addLink(messages("messages__common__cya__paye"), companyPayeVariationsRoute(UpdateMode, srn),
          messages("messages__visuallyhidden__companyPaye_add"))
      )
    ))

  private def companyDetailsAllValues(mode: Mode, srn: Option[String]
                                     )(implicit request: DataRequest[AnyContent]): Seq[AnswerSection] =
    Seq(AnswerSection(
      None,
      hasCompanyNumberYesRow(mode, srn) ++
        companyNumberRow(mode, srn) ++
        hasCompanyUTRYesRow(mode, srn) ++
        utrRow(mode, srn) ++
        hasCompanyVatYesRow(mode, srn) ++
        vatRow(mode, srn) ++
        hasCompanyPayeYesRow(mode, srn) ++
        payeRow(mode, srn)
    ))

  private def hasCompanyNumberYesRow(mode: Mode, srn: Option[String]): Seq[AnswerRow] =
    if(mode == NormalMode)
      Seq(booleanChangeLink(messages("messages__hasCompanyNumber__h1", companyName), hasCompanyNumberRoute(mode, srn), value = true,
        messages("messages__visuallyhidden__hasCompanyNumber"))) else Nil

  private def companyNumberRow(mode: Mode, srn: Option[String]): Seq[AnswerRow] =
    Seq(stringChangeLink(messages("messages__checkYourAnswers__establishers__company__number"), companyRegistrationNumberVariationsRoute(mode, srn), crn,
      messages("messages__visuallyhidden__companyNumber")))

  private def utrRow(mode: Mode, srn: Option[String]): Seq[AnswerRow] =
    if(mode == NormalMode)
      Seq(stringChangeLink(messages("messages__companyUtr__checkyouranswerslabel"), companyUTRRoute(mode, srn), utr,
        messages("messages__visuallyhidden__companyUTR"))) else
      Seq(stringLink(messages("messages__companyUtr__checkyouranswerslabel"), companyUTRRoute(mode, srn), utr,
        messages("messages__visuallyhidden__companyUTR")))

  private def vatRow(mode: Mode, srn: Option[String]): Seq[AnswerRow] =
    Seq(stringChangeLink(messages("messages__common__cya__vat"), companyVatVariationsRoute(mode, srn), vat,
      messages("messages__visuallyhidden__companyVat")))

  private def payeRow(mode: Mode, srn: Option[String]): Seq[AnswerRow] =
    Seq(stringChangeLink(messages("messages__common__cya__paye"), companyPayeVariationsRoute(mode, srn), paye,
      messages("messages__visuallyhidden__companyPaye")))

  private def hasCompanyUTRYesRow(mode: Mode, srn: Option[String]): Seq[AnswerRow] =
    if(mode == NormalMode)
      Seq(booleanChangeLink(messages("messages__hasCompanyUtr__h1", companyName), hasCompanyUTRRoute(mode, srn), value = true,
        messages("messages__visuallyhidden__hasCompanyUtr"))) else Nil

  private def hasCompanyVatYesRow(mode: Mode, srn: Option[String]): Seq[AnswerRow] =
    if(mode == NormalMode)
      Seq(booleanChangeLink(messages("messages__hasCompanyVat__h1", companyName), hasCompanyVatRoute(mode, srn), value = true,
        messages("messages__visuallyhidden__hasCompanyVat"))) else Nil

  private def hasCompanyPayeYesRow(mode: Mode, srn: Option[String]): Seq[AnswerRow] =
    if(mode == NormalMode)
      Seq(booleanChangeLink(messages("messages__companyPayeRef__h1", companyName), hasCompanyPayeRoute(mode, srn), value = true,
        messages("messages__visuallyhidden__companyPayeRef"))) else Nil

 private def companyDetailsAllReasons(mode: Mode, srn: Option[String]
                                      )(implicit request: DataRequest[AnyContent]): Seq[AnswerSection] =
    Seq(AnswerSection(
      None,
      Seq(
        booleanChangeLink(messages("messages__hasCompanyNumber__h1", companyName), hasCompanyNumberRoute(mode, srn), value = false,
          messages("messages__visuallyhidden__hasCompanyNumber")),
        stringChangeLink(messages("messages__noCompanyNumber__establisher__heading", companyName), noCompanyNumberReasonRoute(mode, srn), reason,
          messages("messages__visuallyhidden__noCompanyNumberReason")),
        booleanChangeLink(messages("messages__hasCompanyUtr__h1", companyName), hasCompanyUTRRoute(mode, srn), value = false,
          messages("messages__visuallyhidden__hasCompanyUtr")),
        stringChangeLink(messages("messages__noCompanyUtr__heading", companyName), noCompanyUTRRoute(mode, srn), reason,
          messages("messages__visuallyhidden__noCompanyUTRReason")),
        booleanChangeLink(messages("messages__hasCompanyVat__h1", companyName), hasCompanyVatRoute(mode, srn), value = false,
          messages("messages__visuallyhidden__hasCompanyVat")),
        booleanChangeLink(messages("messages__companyPayeRef__h1", companyName), hasCompanyPayeRoute(mode, srn), value = false,
          messages("messages__visuallyhidden__companyPayeRef"))
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

  private def stringLink(label: String, changeUrl: String, ansOrReason: String, hiddenLabel: String) =
    AnswerRow(
      label,
      Seq(ansOrReason),
      answerIsMessageKey = false,
      None)


  private def addLink(label: String, changeUrl: String, hiddenLabel: String) =
    AnswerRow(label, Seq("site.not_entered"), answerIsMessageKey = true, Some(Link("site.add", changeUrl, Some(hiddenLabel))))


  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyData,
                 allowChangeHelper: AllowChangeHelper = ach,
                 isToggleOn: Boolean = false): CheckYourAnswersCompanyDetailsController =
    new CheckYourAnswersCompanyDetailsController(
      frontendAppConfig,
      messagesApi,
      FakeAuthAction,
      dataRetrievalAction,
      FakeAllowAccessProvider(),
      new DataRequiredActionImpl,
      fakeCountryOptions,
      new FakeNavigator(onwardRoute),
      FakeUserAnswersService,
      allowChangeHelper,
      new FakeFeatureSwitchManagementService(isToggleOn)
    )

  def viewAsString(answerSections: Seq[AnswerSection], mode: Mode = NormalMode,
                   srn: Option[String] = None, postUrl: Call = postUrl): String =
    check_your_answers(
      frontendAppConfig,
      answerSections,
      postUrl,
      None,
      mode = mode,
      hideEditLinks = false,
      srn = srn,
      hideSaveAndContinueButton = false
    )(fakeRequest, messages).toString

}


