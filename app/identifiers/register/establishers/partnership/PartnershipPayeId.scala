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

package identifiers.register.establishers.partnership

import identifiers._
import identifiers.register.establishers.{EstablishersId, IsEstablisherNewId}
import models.{Link, Paye}
import play.api.i18n.Messages
import play.api.libs.json.JsPath
import utils.UserAnswers
import utils.checkyouranswers.CheckYourAnswers
import utils.checkyouranswers.CheckYourAnswers.PayeCYA
import viewmodels.AnswerRow

case class PartnershipPayeId(index: Int) extends TypedIdentifier[Paye] {
  override def path: JsPath = EstablishersId(index).path \ PartnershipPayeId.toString
}

object PartnershipPayeId {
  override def toString: String = "partnershipPaye"

  val labelYesNo = "messages__partnership__checkYourAnswers__paye"
  val hiddenLabelYesNo = "messages__visuallyhidden__partnership__paye_yes_no"
  val hiddenLabelVat = "messages__visuallyhidden__partnership__paye_number"

  implicit def cya(implicit userAnswers: UserAnswers, messages: Messages): CheckYourAnswers[PartnershipPayeId] = {
    new CheckYourAnswers[PartnershipPayeId] {

      override def row(id: PartnershipPayeId)(changeUrl: String, userAnswers: UserAnswers): Seq[AnswerRow] =
        PayeCYA(Some(labelYesNo), hiddenLabelYesNo, hiddenLabelVat)().row(id)(changeUrl, userAnswers)

      override def updateRow(id: PartnershipPayeId)(changeUrl: String, userAnswers: UserAnswers): Seq[AnswerRow] =
        userAnswers.get(id) match {
          case Some(Paye.Yes(paye)) => println("\n\n\n\n useranswers : "+userAnswers)
            userAnswers.get(IsEstablisherNewId(id.index)) match {
            case Some(true) => PayeCYA(Some(labelYesNo), hiddenLabelYesNo, hiddenLabelVat)().row(id)(changeUrl, userAnswers)
            case _  => Seq(AnswerRow(labelYesNo, Seq(paye), answerIsMessageKey = false, None))
          }
          case Some(Paye.No) => userAnswers.get(IsEstablisherNewId(id.index)) match {
            case Some(true) => PayeCYA(Some(labelYesNo), hiddenLabelYesNo, hiddenLabelVat)().row(id)(changeUrl, userAnswers)
            case _  => Seq(AnswerRow(labelYesNo, Seq("site.not_entered"), answerIsMessageKey = true,
              Some(Link("site.add", changeUrl, Some(s"${hiddenLabelVat}_add")))))
          }
          case _ => Seq.empty[AnswerRow]
        }
    }
  }
}
