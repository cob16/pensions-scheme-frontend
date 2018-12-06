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

package views.register.trustees.company

import controllers.register.trustees.company
import forms.CompanyDetailsFormProvider
import models.{CompanyDetails, Index, NormalMode}
import play.api.data.Form
import views.behaviours.QuestionViewBehaviours
import views.html.register.trustees.company.companyDetails

class CompanyDetailsViewSpec extends QuestionViewBehaviours[CompanyDetails] {

  val messageKeyPrefix = "common__company_details"

  override val form = new CompanyDetailsFormProvider()()
  val firstIndex = Index(1)

  private def createView(isHubEnabled: Boolean = true) = () =>
    companyDetails(appConfig(isHubEnabled), form, NormalMode, firstIndex)(fakeRequest, messages)

  private def createViewUsingForm = (form: Form[_]) =>
    companyDetails(frontendAppConfig, form, NormalMode, firstIndex)(fakeRequest, messages)


  "CompanyDetails view with hub enabled" must {

    behave like normalPage(createView(), messageKeyPrefix, messages(s"messages__${messageKeyPrefix}__title"))

    behave like pageWithReturnLink(createView(), controllers.register.routes.SchemeTaskListController.onPageLoad().url)

    behave like pageWithTextFields(createViewUsingForm, messageKeyPrefix,
      company.routes.CompanyDetailsController.onSubmit(NormalMode, firstIndex).url, "companyName", "vatNumber", "payeNumber")

    "not have a back link" in {
      val doc = asDocument(createView()())
      assertNotRenderedById(doc, "back-link")
    }
  }

  "CompanyDetails view with hub disabled" must {
    behave like pageWithBackLink(createView(isHubEnabled = false))

    "not have a return link" in {
      val doc = asDocument(createView(isHubEnabled = false)())
      assertNotRenderedById(doc, "return-link")
    }
  }
}
