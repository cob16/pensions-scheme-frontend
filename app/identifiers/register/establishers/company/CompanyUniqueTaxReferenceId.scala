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

package identifiers.register.establishers.company

import identifiers.TypedIdentifier
import identifiers.register.establishers.EstablishersId
import models.register.establishers.individual.UniqueTaxReference
import play.api.libs.json.JsPath

case class CompanyUniqueTaxReferenceId(index: Int) extends TypedIdentifier[UniqueTaxReference] {
  override def path: JsPath = EstablishersId.path \ index \ CompanyUniqueTaxReferenceId.toString
}

object CompanyUniqueTaxReferenceId {
  override def toString: String = "companyUniqueTaxReference"
}