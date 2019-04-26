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

package utils

import identifiers.{IsAboutBankDetailsCompleteId, IsAboutBenefitsAndInsuranceCompleteId, IsAboutMembersCompleteId}
import models.register.Entity
import models.{Link, NormalMode}
import play.api.i18n.Messages
import viewmodels._

class HsTaskListHelperRegistration(answers: UserAnswers)(implicit messages: Messages) extends HsTaskListHelper(answers) {

  override protected lazy val beforeYouStartLinkText = messages("messages__schemeTaskList__before_you_start_link_text")

  override protected[utils] def aboutSection(userAnswers: UserAnswers): Seq[SchemeDetailsTaskListSection] = {
    val membersLink = userAnswers.get(IsAboutMembersCompleteId) match {
      case Some(true) => Link(aboutMembersLinkText, controllers.routes.CheckYourAnswersMembersController.onPageLoad(NormalMode, None).url)
      case _ => Link(aboutMembersLinkText, controllers.routes.WhatYouWillNeedMembersController.onPageLoad().url)
    }

    val benefitsAndInsuranceLink = userAnswers.get(IsAboutBenefitsAndInsuranceCompleteId) match {
      case Some(true) => Link(aboutBenefitsAndInsuranceLinkText,
        controllers.routes.CheckYourAnswersBenefitsAndInsuranceController.onPageLoad(NormalMode, None).url)
      case _ => Link(aboutBenefitsAndInsuranceLinkText, controllers.routes.WhatYouWillNeedBenefitsInsuranceController.onPageLoad.url)
    }

    val bankDetailsLink = userAnswers.get(IsAboutBankDetailsCompleteId) match {
      case Some(true) => Link(aboutBankDetailsLinkText, controllers.routes.CheckYourAnswersBankDetailsController.onPageLoad().url)
      case _ => Link(aboutBankDetailsLinkText, controllers.routes.WhatYouWillNeedBankDetailsController.onPageLoad().url)
    }

    Seq(SchemeDetailsTaskListSection(userAnswers.get(IsAboutMembersCompleteId), membersLink, None),
      SchemeDetailsTaskListSection(userAnswers.get(IsAboutBenefitsAndInsuranceCompleteId), benefitsAndInsuranceLink, None),
      SchemeDetailsTaskListSection(userAnswers.get(IsAboutBankDetailsCompleteId), bankDetailsLink, None))
  }

  protected[utils] def declarationSection(userAnswers: UserAnswers): Option[SchemeDetailsTaskListDeclarationSection] =
    Some(SchemeDetailsTaskListDeclarationSection(declarationLink(userAnswers)))

  protected def listOf(sections: Seq[Entity[_]], userAnswers: UserAnswers): Seq[SchemeDetailsTaskListSection] = {
    val notDeletedElements = for ((section, index) <- sections.zipWithIndex) yield {
      if (section.isDeleted) None else {
        Some(SchemeDetailsTaskListSection(
          Some(section.isCompleted),
          Link(linkText(section), linkTarget(section, index, NormalMode, None)),
          Some(section.name))
        )
      }
    }
    notDeletedElements.flatten
  }

  protected[utils] def establishers(userAnswers: UserAnswers): Seq[SchemeDetailsTaskListSection] =
    listOf(userAnswers.allEstablishers, userAnswers)

  protected[utils] def trustees(userAnswers: UserAnswers): Seq[SchemeDetailsTaskListSection] =
    listOf(userAnswers.allTrustees, userAnswers)

  def taskList: SchemeDetailsTaskList = {
    SchemeDetailsTaskList(
      beforeYouStartSection(answers, NormalMode, None),
      messages("messages__schemeTaskList__about_header"),
      aboutSection(answers),
      workingKnowledgeSection(answers),
      addEstablisherHeader(answers, NormalMode, None),
      establishers(answers),
      addTrusteeHeader(answers, NormalMode, None),
      trustees(answers),
      declarationSection(answers),
      messages("messages__schemeTaskList__heading"),
      messages("messages__schemeTaskList__before_you_start_header"),
      None,
      messages("messages__schemeTaskList__title")
    )
  }

}