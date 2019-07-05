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

package views

import forms.{ReasonFormProvider, VatVariationsFormProvider}
import play.api.data.Form
import play.api.mvc.Call
import play.twirl.api.HtmlFormat
import viewmodels.{Message, ReasonViewModel, VatViewModel}
import views.behaviours.QuestionViewBehaviours
import views.html.{reason, vatVariations}

class ReasonViewSpec extends QuestionViewBehaviours[String] {

  val messageKeyPrefix = "noCompanyUtr"

  val form = new ReasonFormProvider()("companyName")
  val postCall = Call("GET", "/")

  def viewmodel(srn:Option[String]): ReasonViewModel = ReasonViewModel(
    postCall = postCall,
    title = Message("messages__noCompanyUtr__title"),
    heading = Message("messages__noCompanyUtr__heading"),
    srn = srn
  )

  def createView(): () => HtmlFormat.Appendable = () =>
    reason(frontendAppConfig, form, viewmodel(Some("srn")), None)(fakeRequest, messages)

  def createViewUsingForm: Form[_] => HtmlFormat.Appendable = (form: Form[_]) =>
    reason(frontendAppConfig, form, viewmodel(None), None)(fakeRequest, messages)

  "Reason view" when {
    "rendered" must {
      behave like normalPageWithoutBrowserTitle(createView(), messageKeyPrefix, pageHeader = messages(s"messages__${messageKeyPrefix}__heading"))

      behave like pageWithReturnLinkAndSrn(createView(), getReturnLinkWithSrn)

      behave like pageWithTextFields(createViewUsingForm, messageKeyPrefix, postCall.url,
        "reason")

    }
  }
}