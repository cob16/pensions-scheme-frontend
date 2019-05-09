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
import identifiers.register.establishers.{EstablishersId, IsEstablisherNewId}
import models.CompanyRegistrationNumber
import play.api.i18n.Messages
import play.api.libs.json.JsPath
import utils.UserAnswers
import utils.checkyouranswers.{CheckYourAnswers, CompanyRegistrationNumberCYA}
import viewmodels.AnswerRow

case class CompanyRegistrationNumberId(index: Int) extends TypedIdentifier[CompanyRegistrationNumber] {
  override def path: JsPath = EstablishersId(index).path \ CompanyRegistrationNumberId.toString
}

object CompanyRegistrationNumberId {

  override def toString: String = "companyRegistrationNumber"

  val label: String = "messages__company__cya__crn_yes_no"
  val reasonLabel: String = "messages__company__cya__crn_no_reason"
  val changeHasCrn: String = "messages__visuallyhidden__establisher__crn_yes_no"
  val changeCrn: String = "messages__visuallyhidden__establisher__crn"
  val changeNoCrn: String = "messages__visuallyhidden__establisher__crn_no"


  implicit def cya(implicit messages: Messages): CheckYourAnswers[CompanyRegistrationNumberId] = {

    new CheckYourAnswers[CompanyRegistrationNumberId] {
      override def row(id: CompanyRegistrationNumberId)(changeUrl: String, userAnswers: UserAnswers): Seq[AnswerRow] =
        CompanyRegistrationNumberCYA(label, reasonLabel, changeHasCrn, changeCrn, changeNoCrn)().row(id)(changeUrl, userAnswers)

      override def updateRow(id: CompanyRegistrationNumberId)(changeUrl: String, userAnswers: UserAnswers): Seq[AnswerRow] =
        userAnswers.get(IsEstablisherNewId(id.index)) match {
          case Some(true) => CompanyRegistrationNumberCYA(label, reasonLabel, changeHasCrn, changeCrn, changeNoCrn)().row(id)(changeUrl, userAnswers)
          case _ => CompanyRegistrationNumberCYA(label, reasonLabel, changeHasCrn, changeCrn, changeNoCrn)().updateRow(id)(changeUrl, userAnswers)
        }
    }
  }
}
