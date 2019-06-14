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

package controllers.register.establishers.partnership.partner

import controllers.ControllerSpecBase
import controllers.actions._
import forms.NinoNewFormProvider
import identifiers.register.establishers.EstablishersId
import identifiers.register.establishers.partnership.PartnershipDetailsId
import identifiers.register.establishers.partnership.partner.{PartnerDetailsId, PartnerNinoId}
import models._
import models.person.PersonDetails
import org.joda.time.LocalDate
import play.api.data.Form
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Call
import play.api.test.Helpers._
import services.FakeUserAnswersService
import utils.FakeNavigator
import viewmodels.NinoViewModel
import views.html.nino

//scalastyle:off magic.number

class PartnerNinoNewControllerSpec extends ControllerSpecBase {

  def onwardRoute: Call = controllers.routes.IndexController.onPageLoad()


  val partnershipName = "test partnership name"
  val formProvider = new NinoNewFormProvider()
  val form = formProvider("First Name Middle Name Last Name")
  val establisherIndex = Index(0)
  val partnerIndex = Index(0)
  val invalidIndex = Index(11)
  val partnerName = "First Name Middle Name Last Name"

  val validData: JsObject = Json.obj(
    EstablishersId.toString -> Json.arr(
      Json.obj(
        PartnershipDetailsId.toString -> PartnershipDetails(partnershipName),
        "partner" -> Json.arr(
          Json.obj(
            PartnerDetailsId.toString ->
              PersonDetails("First Name", Some("Middle Name"), "Last Name", LocalDate.now),
            PartnerNinoId.toString ->
              Nino.Yes("CS700100A")
          )
        )
      )
    )
  )

  val validDataNoPreviousAnswer: JsObject = Json.obj(
    EstablishersId.toString -> Json.arr(
      Json.obj(
        PartnershipDetailsId.toString -> PartnershipDetails(partnershipName),
        "partner" -> Json.arr(
          Json.obj(
            PartnerDetailsId.toString ->
              PersonDetails("First Name", Some("Middle Name"), "Last Name", LocalDate.now)
          )
        )
      )
    )
  )
  
  def viewmodel(srn:Option[String]) = NinoViewModel(
    postCall = controllers.register.establishers.partnership.partner.routes.PartnerNinoNewController.onSubmit(NormalMode, establisherIndex, partnerIndex, None),
    title = "messages__partner_yes_nino__title",
    heading ="messages__common_nino__h1",
    hint = "messages__common__nino_hint",
    personName = partnerName,
    srn = srn
  )

  def controller(dataRetrievalAction: DataRetrievalAction = getMandatoryEstablisher): PartnerNinoNewController =
    new PartnerNinoNewController(frontendAppConfig, messagesApi, FakeUserAnswersService, new FakeNavigator(desiredRoute = onwardRoute),
      FakeAuthAction, dataRetrievalAction, FakeAllowAccessProvider(), new DataRequiredActionImpl, formProvider)

  private val postCall = routes.PartnerNinoNewController.onSubmit _

  def viewAsString(form: Form[_] = form): String = nino(frontendAppConfig, form, viewmodel(None), None)(fakeRequest, messages).toString

  "PartnerNino Controller" must {

    "return OK and the correct view for a GET when establisher name is present" in {
      val getRelevantData = new FakeDataRetrievalAction(Some(validDataNoPreviousAnswer))

      val result = controller(getRelevantData).onPageLoad(NormalMode, establisherIndex, partnerIndex, None)(fakeRequest)
      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "redirect to session expired from a GET when the establisher  index is invalid" in {
      val getRelevantData = new FakeDataRetrievalAction(Some(validDataNoPreviousAnswer))
      val result = controller(getRelevantData).onPageLoad(NormalMode, invalidIndex, establisherIndex, None)(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.routes.SessionExpiredController.onPageLoad().url)
    }
    "redirect to session expired from a GET when the partner index is invalid" in {
      val getRelevantData = new FakeDataRetrievalAction(Some(validDataNoPreviousAnswer))
      val result = controller(getRelevantData).onPageLoad(NormalMode, establisherIndex, invalidIndex, None)(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.routes.SessionExpiredController.onPageLoad().url)
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val getRelevantData = new FakeDataRetrievalAction(Some(validData))
      val result = controller(getRelevantData).onPageLoad(NormalMode, establisherIndex, partnerIndex, None)(fakeRequest)
      contentAsString(result) mustBe viewAsString(form.fill("CS700100A"))
    }

    "redirect to the next page when valid data is submitted" in {

      val getRelevantData = new FakeDataRetrievalAction(Some(validDataNoPreviousAnswer))
      val postRequest = fakeRequest.withFormUrlEncodedBody(("nino", "CS700100A"))
      val result = controller(getRelevantData).onSubmit(NormalMode, establisherIndex, partnerIndex, None)(postRequest)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val getRelevantData = new FakeDataRetrievalAction(Some(validDataNoPreviousAnswer))
      val postRequest = fakeRequest.withFormUrlEncodedBody(("nino", "invalid value"))
      val boundForm = form.bind(Map("nino" -> "invalid value"))
      val result = controller(getRelevantData).onSubmit(NormalMode, establisherIndex, partnerIndex, None)(postRequest)
      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(NormalMode, establisherIndex, partnerIndex, None)(fakeRequest)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.routes.SessionExpiredController.onPageLoad().url)
    }

    "redirect to Session Expired for a GET if no existing director details data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(NormalMode, establisherIndex, partnerIndex, None)(fakeRequest)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.routes.SessionExpiredController.onPageLoad().url)
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("nino", Nino.options.head.value))
      val result = controller(dontGetAnyData).onSubmit(NormalMode, establisherIndex, partnerIndex, None)(postRequest)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.routes.SessionExpiredController.onPageLoad().url)
    }

    "redirect to Session Expired for a POST if no existing partner details data is found" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("nino", Nino.options.head.value))
      val result = controller(dontGetAnyData).onSubmit(NormalMode, establisherIndex, partnerIndex, None)(postRequest)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.routes.SessionExpiredController.onPageLoad().url)
    }
  }
}