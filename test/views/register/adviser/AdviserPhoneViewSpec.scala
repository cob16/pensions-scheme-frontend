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

package views.register.adviser

import forms.register.adviser.AdviserEmailFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.register.adviser.adviserPhone

class AdviserPhoneViewSpec extends QuestionViewBehaviours[String] {

  val messageKeyPrefix = "adviser__phone"
  val form = new AdviserEmailFormProvider().apply()
  val adviserName = "test adviser"

  private val createView: () => HtmlFormat.Appendable = () => adviserPhone(frontendAppConfig, form, NormalMode, adviserName)(fakeRequest, messages)

  private val createViewWithForm: Form[String] => HtmlFormat.Appendable =
    (form: Form[String]) => adviserPhone(frontendAppConfig, form, NormalMode, adviserName)(fakeRequest, messages)

  behave like normalPage(createView, messageKeyPrefix,
    messages("messages__adviser__phone__heading", adviserName))

  behave like pageWithTextFields(
    createViewWithForm,
    messageKeyPrefix,
    controllers.register.adviser.routes.AdviserPhoneController.onSubmit(NormalMode).url,
    "phone"
  )

  behave like pageWithSubmitButton(createView)

  behave like pageWithReturnLink(createView, controllers.register.routes.SchemeTaskListController.onPageLoad().url)
}