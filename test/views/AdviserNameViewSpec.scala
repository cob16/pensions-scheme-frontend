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

import forms.register.AdviserNameFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.register.adviser.adviserName

class AdviserNameViewSpec extends QuestionViewBehaviours[String] {

  val messageKeyPrefix = "adviserName"

  override val form = new AdviserNameFormProvider()()

  def createView: () => HtmlFormat.Appendable = () => adviserName(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  def createViewUsingForm: Form[_] => HtmlFormat.Appendable = (form: Form[_]) => adviserName(frontendAppConfig, form, NormalMode)(fakeRequest, messages)


  "AdviserName view" must {

    behave like normalPage(createView, messageKeyPrefix, messages(s"messages__${messageKeyPrefix}__title"))

    behave like pageWithTextFields(createViewUsingForm, messageKeyPrefix, controllers.register.adviser.routes.AdviserNameController.onSubmit(NormalMode).url,
      "adviserName")
  }
}
