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

import identifiers.TypedIdentifier
import identifiers.register.establishers.{EstablishersId, IsEstablisherNewId}
import identifiers.register.establishers.company.CompanyDetailsId
import models.{Link, PartnershipDetails}
import play.api.i18n.Messages
import play.api.libs.json.JsPath
import utils.UserAnswers
import utils.checkyouranswers.{CheckYourAnswers, CompanyDetailsCYA}
import viewmodels.AnswerRow

case class PartnershipDetailsId(index: Int) extends TypedIdentifier[PartnershipDetails] {
  override def path: JsPath = EstablishersId(index).path \ PartnershipDetailsId.toString
}

object PartnershipDetailsId {
  override lazy val toString: String = "partnershipDetails"

  implicit def cya(implicit userAnswers: UserAnswers, messages: Messages): CheckYourAnswers[PartnershipDetails] = {
    new CheckYourAnswers[PartnershipDetails] {

      override def row(id: PartnershipDetails)(changeUrl: String, userAnswers: UserAnswers): Seq[AnswerRow] =
        CompanyDetailsCYA()().row(id)(changeUrl, userAnswers)

      override def updateRow(id: CompanyDetailsId)(changeUrl: String, userAnswers: UserAnswers): Seq[AnswerRow] =
        userAnswers.get(id) match {
          case Some(companyDetails) => userAnswers.get(IsEstablisherNewId(id.index)) match {
            case Some(true) => Seq(AnswerRow("messages__common__cya__name", Seq(s"${companyDetails.companyName}"), answerIsMessageKey = false,
              Some(Link("site.change", changeUrl, Some("messages__visuallyhidden__common__name")))))
            case _  => Seq(AnswerRow("messages__common__cya__name", Seq(s"${companyDetails.companyName}"), answerIsMessageKey = false, None))
          }
          case _ => Seq.empty[AnswerRow]
        }
    }
  }
}


