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

package utils

import identifiers.TypedIdentifier
import identifiers.register.establishers.company.{CompanyDetailsId => EstablisherCompanyDetailsId}
import identifiers.register.establishers.individual.EstablisherDetailsId
import identifiers.register.establishers.partnership.{PartnershipDetailsId => EstablisherPartnershipDetailsId}
import identifiers.register.trustees.HaveAnyTrusteesId
import identifiers.register.trustees.company.{CompanyDetailsId => TrusteeCompanyDetailsId}
import identifiers.register.trustees.individual.TrusteeDetailsId
import identifiers.register.trustees.partnership.{PartnershipDetailsId => TrusteePartnershipDetailsId}
import identifiers.register.{IsAboutSchemeCompleteId, IsWorkingKnowledgeCompleteId, SchemeDetailsId}
import models.NormalMode
import models.register.{Entity, SchemeType}
import play.api.i18n.Messages
import play.api.libs.json.Reads
import viewmodels.{JourneyTaskList, JourneyTaskListSection, Link}

class TaskListHelper(journey: Option[UserAnswers])(implicit messages: Messages) {

  def taskList: JourneyTaskList = {
    journey.fold(
      blankJourneyTaskList
    )(implicit userAnswers =>
      JourneyTaskList(
        aboutSection,
        listOf(userAnswers.allEstablishersAfterDelete),
        listOf(userAnswers.allTrusteesAfterDelete),
        workingKnowledgeSection,
        declarationLink)
    )
  }

  private def blankJourneyTaskList: JourneyTaskList = {
    JourneyTaskList(
      JourneyTaskListSection(None, aboutSectionDefaultLink, None),
      Seq.empty,
      Seq.empty,
      JourneyTaskListSection(None, workingKnowledgeDefaultLink, None),
      None)
  }

  private val aboutSectionDefaultLink: Link = {
    Link(messages("messages__schemeTaskList__about_link_text"),
      controllers.register.routes.SchemeDetailsController.onPageLoad(NormalMode).url)
  }

  private val workingKnowledgeDefaultLink: Link = {
    Link(messages("messages__schemeTaskList__working_knowledge_add_link"),
      controllers.routes.WorkingKnowledgeController.onPageLoad().url)
  }

  private def aboutSection(implicit userAnswers: UserAnswers): JourneyTaskListSection = {
    val link = userAnswers.get(IsAboutSchemeCompleteId) match {
      case Some(true) => Link(messages("messages__schemeTaskList__about_link_text"),
        controllers.register.routes.CheckYourAnswersController.onPageLoad.url)
      case _ => aboutSectionDefaultLink
    }
    JourneyTaskListSection(userAnswers.get(IsAboutSchemeCompleteId), link, None)
  }

  private def workingKnowledgeSection = JourneyTaskListSection(
    flagValue(IsWorkingKnowledgeCompleteId),
    Link(messages("messages__schemeTaskList__working_knowledge_add_link"),
      controllers.routes.WorkingKnowledgeController.onPageLoad().url),
    None)

  private def declarationLink(implicit userAnswers: UserAnswers): Option[Link] =
    if (declarationEnabled)
      Some(Link(messages("messages__schemeTaskList__declaration_link"),
        controllers.register.routes.DeclarationController.onPageLoad().url))
    else None

  private def listOf(sections: Seq[Entity[_]]): Seq[JourneyTaskListSection] =
    for ((section, index) <- sections.zipWithIndex) yield
      JourneyTaskListSection(
        Some(section.isCompleted),
        Link(linkText(section),  linkTarget(section, index)),
        Some(section.name))

  private def linkText(item: Entity[_]): String =
    item.id match {
      case EstablisherCompanyDetailsId(_) | TrusteeCompanyDetailsId(_) => messages("messages__schemeTaskList__company_link")
      case EstablisherDetailsId(_) | TrusteeDetailsId(_) => messages("messages__schemeTaskList__individual_link")
      case EstablisherPartnershipDetailsId(_) | TrusteePartnershipDetailsId(_) => messages("messages__schemeTaskList__partnership_link")
    }

  private def flagValue[A](flag : TypedIdentifier[A])(implicit rds: Reads[A]): Option[A] ={
    journey.flatMap(_.get[A](flag))
  }

  private def isAllEstablishersCompleted(userAnswers: UserAnswers) : Boolean = {
    userAnswers.allEstablishersAfterDelete.nonEmpty && userAnswers.allEstablishersAfterDelete.forall(_.isCompleted)
  }

  private def isAllTrusteesCompleted(userAnswers: UserAnswers) : Boolean = {

    val listOfSchemeTypeTrusts: Seq[SchemeType] = Seq(SchemeType.SingleTrust, SchemeType.MasterTrust)
    val isTrusteesOptional = flagValue(SchemeDetailsId).forall(scheme => !listOfSchemeTypeTrusts.contains(scheme.schemeType))

    val isOptionalTrusteesJourney = flagValue(HaveAnyTrusteesId).forall(_==false) && isTrusteesOptional
    val isMandatoryTrusteesJourney = userAnswers.allTrusteesAfterDelete.nonEmpty && userAnswers.allTrusteesAfterDelete.forall(_.isCompleted)

    isOptionalTrusteesJourney || isMandatoryTrusteesJourney
  }

  private[utils] def declarationEnabled(implicit userAnswers: UserAnswers): Boolean =
    Seq(
      flagValue(IsAboutSchemeCompleteId),
      flagValue(IsWorkingKnowledgeCompleteId),
      Some(isAllEstablishersCompleted(userAnswers)),
      Some(isAllTrusteesCompleted(userAnswers))
    ).forall(_.contains(true))

  private[utils] def linkTarget(item: Entity[_], index : Int): String =
    item.id match {
      case EstablisherCompanyDetailsId(_) if item.isCompleted =>
        controllers.register.establishers.company.routes.CompanyReviewController.onPageLoad(index).url
      case EstablisherPartnershipDetailsId(_) if item.isCompleted =>
        controllers.register.establishers.partnership.routes.PartnershipReviewController.onPageLoad(index).url
      case _ => item.editLink
    }
}
