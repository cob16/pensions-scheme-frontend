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

import controllers.routes
import forms.register.DeclarationFormProvider
import org.jsoup.Jsoup
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.variationDeclaration

class VariationDeclarationViewSpec extends QuestionViewBehaviours[Boolean] {
  private val messageKeyPrefix = "variationDeclaration"

  val schemeName = "Test Scheme Name"
  val form: Form[Boolean] = new DeclarationFormProvider()()

  val postCall = routes.VariationDeclarationController.onSubmit _

  def createView: () => HtmlFormat.Appendable = () => variationDeclaration(frontendAppConfig,
    form, Some(schemeName), postCall(Some("123")))(fakeRequest, messages)

  "Declaration view where no working knowledge" must {

    behave like normalPage(
      createView,
      messageKeyPrefix,
      messages(s"messages__${messageKeyPrefix}__title"),
      "_declare",
      "_statement1",
      "_statement2",
      "_statement3")

    "have a return link" in {
      Jsoup.parse(createView().toString).select("a[id=return-link]") must
        haveLink(getReturnLink)
    }
  }
}