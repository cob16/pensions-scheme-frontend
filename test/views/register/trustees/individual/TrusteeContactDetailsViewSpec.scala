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

package views.register.trustees.individual

import play.api.data.Form
import forms.ContactDetailsFormProvider
import models.{ContactDetails, Index, NormalMode}
import views.behaviours.QuestionViewBehaviours
import views.html.register.trustees.individual.trusteeContactDetails

class TrusteeContactDetailsViewSpec extends QuestionViewBehaviours[ContactDetails] {

  private val messageKeyPrefix = "trusteeContactDetails"
  private val index = Index(0)
  private val trusteeName = "Joe Bloggs"

  override val form = new ContactDetailsFormProvider()()

  private def createView =
    () => trusteeContactDetails(
      frontendAppConfig,
      form,
      NormalMode,
      index,
      trusteeName
    )(fakeRequest, messages)

  private def createViewUsingForm =
    (form: Form[_]) => trusteeContactDetails(
      frontendAppConfig,
      form,
      NormalMode,
      index,
      trusteeName
    )(fakeRequest, messages)

  "TrusteeContactDetails view" must {

    behave like normalPage(createView, messageKeyPrefix, messages(s"messages__${messageKeyPrefix}__title"))

    behave like pageWithBackLink(createView)

    behave like pageWithSecondaryHeader(createView, trusteeName)

    behave like pageWithTextFields(
      createViewUsingForm,
      messageKeyPrefix,
      controllers.register.trustees.individual.routes.TrusteeContactDetailsController.onSubmit(NormalMode, index).url,
      "emailAddress",
      "phoneNumber"
    )

    behave like pageWithSubmitButton(createView)
  }

}