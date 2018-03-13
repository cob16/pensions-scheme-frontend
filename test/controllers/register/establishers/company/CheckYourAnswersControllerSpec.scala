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

package controllers.register.establishers.company

import controllers.ControllerSpecBase
import controllers.actions._
import models.Index
import models.register.CountryOptions
import play.api.test.Helpers._
import utils.{CheckYourAnswersFactory, InputOption}
import viewmodels.{AnswerRow, AnswerSection}
import views.html.check_your_answers

class CheckYourAnswersControllerSpec extends ControllerSpecBase {

  val countryOptions: CountryOptions = new CountryOptions(Seq(InputOption("GB", "United Kingdom")))
  val index = Index(0)
  val testSchemeName = "Test Scheme Name"

  val checkYourAnswersFactory = new CheckYourAnswersFactory(countryOptions)
  val postUrl = controllers.register.establishers.company.routes.CheckYourAnswersController.onSubmit(index)

  val answersCD: Seq[AnswerRow] =
      Seq(AnswerRow("messages__common__cya__name", Seq("test company name"), false,
        "/pensions-scheme/register/establishers/1/company/changeCompanyDetails"),
      AnswerRow("messages__company__cya__vat", Seq("123456789"), false,
        "/pensions-scheme/register/establishers/1/company/changeCompanyDetails"),
      AnswerRow("messages__company__cya__paye_ern", Seq("12345"), false,
        "/pensions-scheme/register/establishers/1/company/changeCompanyDetails"))



  val answers = Seq(AnswerSection(Some("messages__common__company_details__title"), answersCD),
    AnswerSection(Some("messages__common__company_contact_details__title"), Seq.empty))



  def controller(dataRetrievalAction: DataRetrievalAction = getMandatorySchemeName) =
    new CheckYourAnswersController(frontendAppConfig, messagesApi, FakeAuthAction,
      dataRetrievalAction, new DataRequiredActionImpl, checkYourAnswersFactory)

  def viewAsString() = check_your_answers(frontendAppConfig, answers, Some(testSchemeName), postUrl)(fakeRequest, messages).toString

  "CheckYourAnswers Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(index)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }
  }
}




