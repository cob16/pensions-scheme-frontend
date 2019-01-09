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
import forms.WorkingKnowledgeFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.workingKnowledge

class WorkingKnowledgeViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "workingKnowledge"

  val workingKnowledgeOptions = Seq("true", "false")

  val form = new WorkingKnowledgeFormProvider()()

  def createView: () => HtmlFormat.Appendable = () =>
    workingKnowledge(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  def createViewUsingForm: Form[_] => HtmlFormat.Appendable = (form: Form[_]) =>
    workingKnowledge(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  "Working Knowledge view" must {

    behave like normalPage(createView, messageKeyPrefix, pageHeader = messages(s"messages__${messageKeyPrefix}__heading"),
      expectedGuidanceKeys = "_p1", "_p2", "_p3")

    behave like yesNoPage(
      createView = createViewUsingForm,
      messageKeyPrefix = messageKeyPrefix,
      expectedFormAction = routes.WorkingKnowledgeController.onSubmit(NormalMode).url
    )

    behave like pageWithSubmitButton(createView)

    behave like pageWithReturnLink(createView, frontendAppConfig.managePensionsSchemeOverviewUrl.url)

  }
}
