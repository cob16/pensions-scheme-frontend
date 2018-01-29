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

package forms.register.establishers.individual

import javax.inject.Inject

import forms.mappings.Mappings
import models.addresslookup.Address
import play.api.data.Forms.{mapping, optional, tuple}
import play.api.data.{Form, Forms}
import uk.gov.voa.play.form.ConditionalMappings.mandatoryIfEqual

class ManualAddressFormProvider @Inject() extends Mappings {

  val addressLineMaxLength = 35
  val postCodeRegex = "^(?i)[A-Z]{1,2}[0-9][0-9A-Z]?[ ]?[0-9][A-Z]{2}"

  def apply(): Form[Address] = Form(
    mapping(
      "addressLine1" -> text("messages__error__addr1").verifying(maxLength(addressLineMaxLength, "messages__error__addr1_length")),
      "addressLine2" -> text("messages__error__addr2").verifying(maxLength(addressLineMaxLength, "messages__error__addr2_length")),
      "addressLine3" -> optional(Forms.text.verifying(maxLength(addressLineMaxLength, "messages__error__addr3_length"))),
      "addressLine4" -> optional(Forms.text.verifying(maxLength(addressLineMaxLength, "messages__error__addr4_length"))),
      "postCode" -> mandatoryIfEqual[String]("country", "GB", text("messages__error__postcode").verifying(
        regexp(postCodeRegex, "messages__error__postcode_invalid"))),
      "country" -> text("messages__error__scheme_country")
    )(Address.apply)(Address.unapply)
  )
 }
