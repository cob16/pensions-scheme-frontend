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

package views.register.establishers.company

import play.api.data.Form
import controllers.register.establishers.company.routes
import forms.register.establishers.company.CompanyContactDetailsFormProvider
import models.{CompanyContactDetails, Index, NormalMode}
import views.behaviours.QuestionViewBehaviours
import views.html.register.establishers.company.companyContactDetails

class CompanyContactDetailsViewSpec extends QuestionViewBehaviours[CompanyContactDetails] {

  val messageKeyPrefix = "establisher_company_contact_details"
  val index = Index(1)
  val companyName = "test company name"
  override val form = new CompanyContactDetailsFormProvider()()

  def createView = () => companyContactDetails(frontendAppConfig, form, NormalMode, index, companyName)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[_]) => companyContactDetails(frontendAppConfig, form, NormalMode, index, companyName)(fakeRequest, messages)


  "CompanyContactDetails view" must {

    behave like normalPage(createView, messageKeyPrefix, messages("messages__establisher_company_contact_details__title"))

    behave like pageWithTextFields(createViewUsingForm, messageKeyPrefix,
      routes.CompanyContactDetailsController.onSubmit(NormalMode, index).url, "emailAddress", "phoneNumber")
  }
}