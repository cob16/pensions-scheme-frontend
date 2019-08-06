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

package identifiers.register.trustees

import identifiers.TypedIdentifier
import play.api.libs.json._
import utils.UserAnswers

case class TrusteesId(index: Int) extends TypedIdentifier[Nothing] {
  override def path: JsPath = __ \ TrusteesId.toString \ index

  // TODO PODS-2940 Needs attention
  override def cleanup(value: Option[Nothing], userAnswers: UserAnswers): JsResult[UserAnswers] = {
    userAnswers.allTrustees(isHnSEnabled = false).lengthCompare(10) match {
      case x if x <= 0 => userAnswers.remove(MoreThanTenTrusteesId)
      case _ => super.cleanup(value, userAnswers)
    }
  }
}

object TrusteesId {
  override def toString: String = "trustees"
}
