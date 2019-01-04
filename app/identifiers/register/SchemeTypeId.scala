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

package identifiers.register

import identifiers.TypedIdentifier
import identifiers.register.trustees.HaveAnyTrusteesId
import models.register.SchemeType
import models.register.SchemeType.{MasterTrust, SingleTrust}
import play.api.libs.json.JsResult
import utils.UserAnswers

case object SchemeTypeId extends TypedIdentifier[SchemeType] {
  override def toString: String = "schemeType"

  private val singleOrMasterTrustTypes = Seq(SingleTrust, MasterTrust)

  override def cleanup(value: Option[SchemeType], userAnswers: UserAnswers): JsResult[UserAnswers] = {
    value match {
      case Some(st) if singleOrMasterTrustTypes.contains(st) =>
        userAnswers.remove(HaveAnyTrusteesId)
      case _ =>
        super.cleanup(value, userAnswers)
    }
  }
}