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

package forms.register

import javax.inject.Inject

import forms.mappings.{EmailMapping, PhoneNumberMapping}
import play.api.data.Form
import play.api.data.Forms._
import models.register.AdviserDetails

class AdviserDetailsFormProvider @Inject() extends EmailMapping with PhoneNumberMapping {

  val nameLength: Int = 107

  def apply(): Form[AdviserDetails] = Form(
    mapping(
      "adviserName" -> text("messages__adviserDetails__error__name_required")
        .verifying(
          firstError(
            maxLength(
              nameLength,
              "messages__adviserDetails__error__adviser_name_length"
            ),
            safeText("messages__adviserDetails__error__adviser_name_invalid")
          )
        ),

      "emailAddress" -> emailMapping(
        "messages__error__email",
        "messages__error__email_length",
        "messages__error__email_invalid"
      ),
      "phoneNumber" -> phoneNumberMapping(
        "messages__error__phone",
        "messages__error__phone_length",
        "messages__error__phone_invalid"
      )
    )(AdviserDetails.apply)(AdviserDetails.unapply)
  )
}
