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

package identifiers.register.establishers.company

import identifiers.TypedIdentifier
import identifiers.register.establishers.EstablishersId
import models.UniqueTaxReference
import play.api.libs.json.JsPath
import utils.checkyouranswers.{CheckYourAnswers, UniqueTaxReferenceCYA}

case class CompanyUniqueTaxReferenceId(index: Int) extends TypedIdentifier[UniqueTaxReference] {
  override def path: JsPath = EstablishersId(index).path \ CompanyUniqueTaxReferenceId.toString
}

object CompanyUniqueTaxReferenceId {
  override def toString: String = "companyUniqueTaxReference"

  implicit val cya: CheckYourAnswers[CompanyUniqueTaxReferenceId] =
    UniqueTaxReferenceCYA(
      label = "messages__company__cya__utr_yes_no",
      utrLabel = "messages__company__cya__utr",
      reasonLabel = "messages__company__cya__utr_no_reason",
      changeHasUtr = "messages__visuallyhidden__establisher__utr_yes_no",
      changeUtr = "messages__visuallyhidden__establisher__utr",
      changeNoUtr = "messages__visuallyhidden__establisher__utr_no"
    )()
}
