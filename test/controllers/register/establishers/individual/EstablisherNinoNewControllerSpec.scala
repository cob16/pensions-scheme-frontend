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

package controllers.register.establishers.individual

import controllers.ControllerSpecBase
import controllers.actions._
import forms.NinoNewFormProvider
import identifiers.SchemeNameId
import identifiers.register.establishers.EstablisherNewNinoId
import identifiers.register.establishers.individual.EstablisherDetailsId
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

class EstablisherNinoNewControllerSpec extends ControllerSpecBase {

  import EstablisherNinoNewControllerSpec._

  "EstablisherNinoNew Controller" must {

    "return OK and the correct view for a GET when establisher name is present" in {
      val result = controller().onPageLoad(UpdateMode, firstIndex, srn)(fakeRequest)
      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString(form, UpdateMode, firstIndex, srn)
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val getRelevantData = new FakeDataRetrievalAction(Some(alreadySubmittedData))
      val result = controller(getRelevantData).onPageLoad(UpdateMode, firstIndex, srn)(fakeRequest)
      contentAsString(result) mustBe viewAsString(form.fill("CS700100A"), UpdateMode, firstIndex, srn)
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody("nino" -> "CS700100A")
      val result = controller().onSubmit(UpdateMode, firstIndex, srn)(postRequest)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody("nino" -> "invalid value")
      val boundForm = form.bind(Map("nino" -> "invalid value"))
      val result = controller().onSubmit(UpdateMode, firstIndex, srn)(postRequest)
      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm, UpdateMode, firstIndex, srn)
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(NormalMode, firstIndex, None)(fakeRequest)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.routes.SessionExpiredController.onPageLoad().url)
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", Nino.options.head.value))
      val result = controller(dontGetAnyData).onSubmit(NormalMode, firstIndex, None)(postRequest)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.routes.SessionExpiredController.onPageLoad().url)
    }

    "redirect to Session Expired page when the index is not valid" in {
      val getRelevantData = new FakeDataRetrievalAction(Some(alreadySubmittedData))
      val result = controller(getRelevantData).onPageLoad(NormalMode, Index(2), None)(fakeRequest)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.routes.SessionExpiredController.onPageLoad().url)
    }
  }
}

object EstablisherNinoNewControllerSpec extends ControllerSpecBase {
  private def onwardRoute: Call = controllers.routes.IndexController.onPageLoad()

  private val establisherName = "test first name test last name"

  private val srn = Some("srn")
  private val schemeName = "Test Scheme Name"
  private val formProvider = new NinoNewFormProvider()
  private val form = formProvider(establisherName)
  private val firstIndex = Index(0)
  private val establisherDetails = PersonDetails("test first name", None, "test last name", LocalDate.now, false)

  private val alreadySubmittedData: JsObject = Json.obj(
    "establishers" -> Json.arr(
      Json.obj(
        EstablisherDetailsId.toString -> establisherDetails,
        EstablisherNewNinoId.toString -> Json.obj(
          "nino" -> "CS700100A"
        )
      )
    ),
    SchemeNameId.toString -> schemeName)

  private val basicData: FakeDataRetrievalAction = new FakeDataRetrievalAction(Some(
    Json.obj(
      "establishers" -> Json.arr(
        Json.obj(
          EstablisherDetailsId.toString -> establisherDetails
        )
      ),
      SchemeNameId.toString -> schemeName
    )))

  private def controller(dataRetrievalAction: DataRetrievalAction = basicData): EstablisherNinoNewController =
    new EstablisherNinoNewController(frontendAppConfig, messagesApi, FakeUserAnswersService, new FakeNavigator(desiredRoute = onwardRoute),
      FakeAuthAction, dataRetrievalAction, FakeAllowAccessProvider(), new DataRequiredActionImpl, formProvider)

  private def vm(mode: Mode, index: Index, srn: Option[String]) = NinoViewModel(
    postCall = controllers.register.establishers.individual.routes.EstablisherNinoNewController.onSubmit(mode, index, srn),
    title = "messages__common_nino__title",
    heading = "messages__common_nino__h1",
    hint = "messages__common__nino_hint",
    personName = establisherName,
    srn = srn
  )

  private def viewAsString(form: Form[_], mode: Mode, index: Index, srn: Option[String]): String = nino(frontendAppConfig, form,
    vm(mode, index, srn), Some(schemeName))(fakeRequest, messages).toString
}