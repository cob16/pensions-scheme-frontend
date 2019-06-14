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

package controllers

import config.FrontendAppConfig
import controllers.actions.DataRetrievalAction
import forms.NinoNewFormProvider
import identifiers.TypedIdentifier
import javax.inject.Inject
import models.requests.DataRequest
import models.{Mode, NormalMode}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{AnyContent, Call, Request, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{FakeUserAnswersService, UserAnswersService}
import uk.gov.hmrc.domain.PsaId
import utils.{FakeNavigator, Navigator, UserAnswers}
import viewmodels.{Message, NinoViewModel}
import views.html.nino

import scala.concurrent.Future


class NinoControllerSpec extends ControllerSpecBase {

  val viewmodel = NinoViewModel(
    postCall = Call("POST", "/"),
    title = Message("messages__common_nino__title"),
    heading = Message("messages__common_nino__h1"),
    hint = Message("messages__common__nino_hint"),
    personName = "Mark",
    srn = None
  )

  object FakeIdentifier extends TypedIdentifier[String]

  private def onwardRoute = controllers.routes.IndexController.onPageLoad()

  val formProvider = new NinoNewFormProvider()
  val form = formProvider("Mark")

  class TestNinoController @Inject()(
                                  override val appConfig: FrontendAppConfig,
                                  override val messagesApi: MessagesApi,
                                  override val userAnswersService: UserAnswersService,
                                  override val navigator: Navigator
                                ) extends NinoController {

    def onPageLoad(answers: UserAnswers): Future[Result] = {
      get(FakeIdentifier, form, viewmodel)(DataRequest(FakeRequest(), "cacheId", answers, PsaId("A0000000")))
    }

    def onSubmit(mode: Mode, answers: UserAnswers, fakeRequest: Request[AnyContent]): Future[Result] = {
      post(FakeIdentifier, NormalMode, form, viewmodel)(DataRequest(fakeRequest, "cacheId", answers, PsaId("A0000000")))
    }
  }

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyData): TestNinoController =
    new TestNinoController(
      frontendAppConfig,
      messagesApi,
      FakeUserAnswersService,
      new FakeNavigator(desiredRoute = onwardRoute)
    )

  private def viewAsString(form: Form[_] = form) = nino(frontendAppConfig, form, viewmodel, None)(fakeRequest, messages).toString


  "NinoController" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(UserAnswers())

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val result = controller().onPageLoad(UserAnswers().set(FakeIdentifier)("nino").asOpt.get)

      contentAsString(result) mustBe viewAsString(form.fill("nino"))
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("nino", "CS700100A"))

      val result = controller().onSubmit(NormalMode, UserAnswers(), postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors invalid data is submitted" in {
        val postRequest = fakeRequest.withFormUrlEncodedBody(("nino", "invalid value"))
        val boundForm = form.bind(Map("nino" -> "invalid value"))

        val result = controller().onSubmit(NormalMode, UserAnswers(), postRequest)

        status(result) mustBe BAD_REQUEST
        contentAsString(result) mustBe viewAsString(boundForm)
    }

  }
}