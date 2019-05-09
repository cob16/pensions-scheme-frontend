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

package identifiers.register.establishers.individual

import identifiers._
import identifiers.register.establishers.{EstablishersId, IsEstablisherNewId}
import models.Nino
import play.api.i18n.Messages
import play.api.libs.json.JsPath
import utils.UserAnswers
import utils.checkyouranswers.{CheckYourAnswers, NinoCYA}
import viewmodels.AnswerRow


case class EstablisherNinoId(index: Int) extends TypedIdentifier[Nino] {
  override def path: JsPath = EstablishersId(index).path \ EstablisherNinoId.toString
}

object EstablisherNinoId {

  override lazy val toString: String = "establisherNino"

  val label = "messages__establisher_individual_nino_question_cya_label"
  val reasonLabel = "messages__establisher_individual_nino_reason_cya_label"
  val changeHasNino = "messages__visuallyhidden__establisher__nino_yes_no"
  val changeNino = "messages__visuallyhidden__establisher__nino"
  val changeNoNino = "messages__visuallyhidden__establisher__nino_no"

  implicit val cya: CheckYourAnswers[EstablisherNinoId] = {
    new CheckYourAnswers[EstablisherNinoId] {
      override def row(id: EstablisherNinoId)(changeUrl: String, userAnswers: UserAnswers): Seq[AnswerRow] =
        NinoCYA(label, reasonLabel, changeHasNino, changeNino, changeNoNino)().row(id)(changeUrl, userAnswers)

      override def updateRow(id: EstablisherNinoId)(changeUrl: String, userAnswers: UserAnswers): Seq[AnswerRow] =
        userAnswers.get(IsEstablisherNewId(id.index)) match {
          case Some(true) => NinoCYA(label, reasonLabel, changeHasNino, changeNino, changeNoNino)().row(id)(changeUrl, userAnswers)
          case _ => NinoCYA(label, reasonLabel, changeHasNino, changeNino, changeNoNino)().updateRow(id)(changeUrl, userAnswers)
        }
    }
  }
}
