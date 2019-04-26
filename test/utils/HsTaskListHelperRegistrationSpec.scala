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

import identifiers._
import identifiers.register.establishers.{IsEstablisherCompleteId, IsEstablisherNewId}
import identifiers.register.establishers.company.{CompanyDetailsId => EstablisherCompanyDetailsId}
import identifiers.register.establishers.individual.EstablisherDetailsId
import identifiers.register.establishers.partnership.{PartnershipDetailsId => EstablisherPartnershipDetailsId}
import identifiers.register.trustees.{IsTrusteeCompleteId, IsTrusteeNewId, TrusteeKindId}
import identifiers.register.trustees.company.{CompanyDetailsId => TrusteeCompanyDetailsId}
import identifiers.register.trustees.individual.TrusteeDetailsId
import identifiers.register.trustees.partnership.{IsPartnershipCompleteId, PartnershipDetailsId => TrusteePartnershipDetailsId}
import models._
import models.person.PersonDetails
import models.register.trustees.TrusteeKind
import org.joda.time.LocalDate
import utils.behaviours.HsTaskListHelperBehaviour
import viewmodels.SchemeDetailsTaskListSection

class HsTaskListHelperRegistrationSpec extends HsTaskListHelperBehaviour with Enumerable.Implicits {

  override val createTaskListHelper: UserAnswers => HsTaskListHelper = ua => new HsTaskListHelperRegistration(ua)

  "h1" must {
    "display appropriate heading" in {
      val userAnswers = UserAnswers()
      val helper = new HsTaskListHelperRegistration(userAnswers)
      helper.taskList.h1 mustBe messages("messages__schemeTaskList__heading")
    }
  }

  "h2" must {
    "display appropriate text" in {
      val userAnswers = UserAnswers()
      val helper = new HsTaskListHelperRegistration(userAnswers)
      helper.taskList.h2 mustBe messages("messages__schemeTaskList__before_you_start_header")
    }
  }

  "h3" must {
    "not display \"Scheme Information\"" in {
      val userAnswers = UserAnswers()
      val helper = new HsTaskListHelperRegistration(userAnswers)
      helper.taskList.h3 mustBe None
    }
  }

  "about header" must {
    "display \"About\"" in {
      val userAnswers = UserAnswers()
      val helper = new HsTaskListHelperRegistration(userAnswers)
      helper.taskList.aboutHeader mustBe messages("messages__schemeTaskList__about_header")
    }
  }


  "page title" must {
    "display Pension scheme details" in {
      val userAnswers = UserAnswers()
      val helper = new HsTaskListHelperRegistration(userAnswers)
      helper.taskList.pageTitle mustBe messages("messages__schemeTaskList__title")
    }
  }

  "beforeYouStartSection " must {
    behave like beforeYouStartSection(
      new HsTaskListHelperRegistration(_),
      beforeYouStartLinkText,
      NormalMode,
      None
    )
  }

  "aboutSection " must {
    "return the the Seq of members, bank details and benefits section with " +
      "links of the first pages of individual sub sections when not completed " in {
      val userAnswers = UserAnswers().set(IsAboutMembersCompleteId)(false).flatMap(
        _.set(IsAboutBankDetailsCompleteId)(false).flatMap(
          _.set(IsAboutBenefitsAndInsuranceCompleteId)(false)
        )
      ).asOpt.value
      val helper = new HsTaskListHelperRegistration(userAnswers)
      helper.aboutSection(userAnswers) mustBe
        Seq(
          SchemeDetailsTaskListSection(Some(false), Link(aboutMembersLinkText,
            controllers.routes.WhatYouWillNeedMembersController.onPageLoad().url), None),
          SchemeDetailsTaskListSection(Some(false), Link(aboutBenefitsAndInsuranceLinkText,
            controllers.routes.WhatYouWillNeedBenefitsInsuranceController.onPageLoad().url), None),
          SchemeDetailsTaskListSection(Some(false), Link(aboutBankDetailsLinkText,
            controllers.routes.WhatYouWillNeedBankDetailsController.onPageLoad().url), None)
        )
    }

    "return the the Seq of members, bank details and benefits section with " +
      "links of the cya pages of individual sub sections when completed " in {
      val userAnswers = UserAnswers().set(IsAboutMembersCompleteId)(true).flatMap(
        _.set(IsAboutBankDetailsCompleteId)(true).flatMap(
          _.set(IsAboutBenefitsAndInsuranceCompleteId)(true)
        )
      ).asOpt.value
      val helper = new HsTaskListHelperRegistration(userAnswers)
      helper.aboutSection(userAnswers) mustBe
        Seq(
          SchemeDetailsTaskListSection(Some(true), Link(aboutMembersLinkText,
            controllers.routes.CheckYourAnswersMembersController.onPageLoad(NormalMode, None).url), None),
          SchemeDetailsTaskListSection(Some(true), Link(aboutBenefitsAndInsuranceLinkText,
            controllers.routes.CheckYourAnswersBenefitsAndInsuranceController.onPageLoad(NormalMode, None).url), None),
          SchemeDetailsTaskListSection(Some(true), Link(aboutBankDetailsLinkText,
            controllers.routes.CheckYourAnswersBankDetailsController.onPageLoad.url), None)
        )
    }
  }

  "workingKnowledgeSection " must {
    "not display when do you have working knowledge is true " in {
      val userAnswers = UserAnswers().set(DeclarationDutiesId)(true).asOpt.value
      val helper = new HsTaskListHelperRegistration(userAnswers)
      helper.workingKnowledgeSection(userAnswers) mustBe None
    }

    "display and link should go to what you will need page when do you have working knowledge is false and section not completed " in {
      val userAnswers = UserAnswers().set(DeclarationDutiesId)(false).flatMap(
        _.set(IsWorkingKnowledgeCompleteId)(false)
      ).asOpt.value
      val helper = new HsTaskListHelperRegistration(userAnswers)
      helper.workingKnowledgeSection(userAnswers).value mustBe
        SchemeDetailsTaskListSection(Some(false), Link(workingKnowledgeLinkText,
          controllers.routes.WhatYouWillNeedWorkingKnowledgeController.onPageLoad.url), None)
    }

    "display and link should go to cya page when do you have working knowledge is false and section is completed " in {
      val userAnswers = UserAnswers().set(DeclarationDutiesId)(false).flatMap(
        _.set(IsWorkingKnowledgeCompleteId)(true)
      ).asOpt.value
      val helper = new HsTaskListHelperRegistration(userAnswers)
      helper.workingKnowledgeSection(userAnswers).value mustBe
        SchemeDetailsTaskListSection(Some(true), Link(workingKnowledgeLinkText,
          controllers.routes.AdviserCheckYourAnswersController.onPageLoad().url), None)
    }
  }

  "addEstablisherHeader " must {

    behave like addEstablisherHeader(NormalMode, None)
  }

  "addTrusteeHeader " must {

    behave like addTrusteeHeader(NormalMode, None)
  }

  "establishers" must {

    behave like establishersSection(NormalMode, None)
  }

  "trustees" must {

    behave like trusteesSection(NormalMode, None)
  }

  "declaration" must {
    "have a declaration section" in {
      val userAnswers = answersData().asOpt.value
      val helper = createTaskListHelper(userAnswers)
      helper.declarationSection(userAnswers).isDefined mustBe true
    }

    behave like declarationSection()

    "not have link when about bank details section not completed" in {
      val userAnswers = answersData(isCompleteAboutBank = false).asOpt.value
      mustHaveNoLink(createTaskListHelper(userAnswers), userAnswers)
    }

    "not have link when working knowledge section not completed" in {
      val userAnswers = answersData(isCompleteWk = false).asOpt.value
      mustHaveNoLink(createTaskListHelper(userAnswers), userAnswers)
    }
  }

  def establishersSection(mode: Mode, srn: Option[String]): Unit = {

    "return the seq of establishers sub sections for non deleted establishers which are all completed" in {
      val userAnswers = allEstablishers()
      val helper = new HsTaskListHelperRegistration(userAnswers)
      helper.establishers(userAnswers) mustBe
        Seq(SchemeDetailsTaskListSection(Some(true), Link(individualLinkText,
          controllers.register.establishers.individual.routes.CheckYourAnswersController.onPageLoad(mode, 0, srn).url), Some("firstName lastName")),
          SchemeDetailsTaskListSection(Some(true), Link(companyLinkText,
            controllers.register.establishers.company.routes.CompanyReviewController.onPageLoad(mode, srn, 1).url), Some("test company")),
          SchemeDetailsTaskListSection(Some(true), Link(partnershipLinkText,
            controllers.register.establishers.partnership.routes.PartnershipReviewController.onPageLoad(mode, 2, srn).url), Some("test partnership"))
        )
    }

    "return the seq of establishers sub sections for non deleted establishers which are not completed" in {
      val userAnswers = allEstablishers(isCompleteEstablisher = false)
      val helper = new HsTaskListHelperRegistration(userAnswers)
      helper.establishers(userAnswers) mustBe
        Seq(SchemeDetailsTaskListSection(Some(false), Link(individualLinkText,
          controllers.register.establishers.individual.routes.EstablisherDetailsController.onPageLoad(mode, 0, srn).url), Some("firstName lastName")),
          SchemeDetailsTaskListSection(Some(false), Link(companyLinkText,
            controllers.register.establishers.company.routes.CompanyDetailsController.onPageLoad(mode, srn, 1).url), Some("test company")),
          SchemeDetailsTaskListSection(Some(false), Link(partnershipLinkText,
            controllers.register.establishers.partnership.routes.PartnershipDetailsController.onPageLoad(mode, 2, srn).url), Some("test partnership"))
        )
    }

    "return the seq of establishers sub sections after filtering out deleted establishers" in {
      val userAnswers = UserAnswers().set(EstablisherDetailsId(0))(PersonDetails("firstName", None, "lastName", LocalDate.now())).flatMap(
        _.set(IsEstablisherCompleteId(0))(false).flatMap(
          _.set(IsEstablisherNewId(0))(true).flatMap(
          _.set(EstablisherCompanyDetailsId(1))(CompanyDetails("test company", true)).flatMap(
            _.set(IsEstablisherCompleteId(1))(true).flatMap(
              _.set(IsEstablisherNewId(1))(true).flatMap(
              _.set(EstablisherPartnershipDetailsId(2))(PartnershipDetails("test partnership", false)).flatMap(
                _.set(IsEstablisherNewId(2))(true).flatMap(
                _.set(IsEstablisherCompleteId(2))(false)
              )))))))).asOpt.value
      val helper = new HsTaskListHelperRegistration(userAnswers)
      helper.establishers(userAnswers) mustBe
        Seq(SchemeDetailsTaskListSection(Some(false), Link(individualLinkText,
          controllers.register.establishers.individual.routes.EstablisherDetailsController.onPageLoad(NormalMode, 0, None).url), Some("firstName lastName")),
          SchemeDetailsTaskListSection(Some(false), Link(partnershipLinkText,
            controllers.register.establishers.partnership.routes.PartnershipDetailsController.onPageLoad(NormalMode, 2, None).url), Some("test partnership"))
        )
    }
  }

  def trusteesSection(mode: Mode, srn: Option[String]): Unit = {

    "return the seq of trustees sub sections for non deleted trustees which are all completed" in {
      val userAnswers = allTrustees()
      val helper = new HsTaskListHelperRegistration(userAnswers)
      helper.trustees(userAnswers) mustBe
        Seq(SchemeDetailsTaskListSection(Some(true), Link(individualLinkText,
          controllers.register.trustees.individual.routes.CheckYourAnswersController.onPageLoad(mode, 0, srn).url), Some("firstName lastName")),
          SchemeDetailsTaskListSection(Some(true), Link(companyLinkText,
            controllers.register.trustees.company.routes.CheckYourAnswersController.onPageLoad(mode, 1, srn).url), Some("test company")),
          SchemeDetailsTaskListSection(Some(true), Link(partnershipLinkText,
            controllers.register.trustees.partnership.routes.CheckYourAnswersController.onPageLoad(mode, 2, srn).url), Some("test partnership"))
        )
    }

    "return the seq of trustees sub sections for non deleted trustees which are not completed" in {
      val userAnswers = allTrustees(isCompleteTrustees = false)
      val helper = new HsTaskListHelperRegistration(userAnswers)
      helper.trustees(userAnswers) mustBe
        Seq(SchemeDetailsTaskListSection(Some(false), Link(individualLinkText,
          controllers.register.trustees.individual.routes.TrusteeDetailsController.onPageLoad(mode, 0, srn).url), Some("firstName lastName")),
          SchemeDetailsTaskListSection(Some(false), Link(companyLinkText,
            controllers.register.trustees.company.routes.CompanyDetailsController.onPageLoad(mode, 1, srn).url), Some("test company")),
          SchemeDetailsTaskListSection(Some(false), Link(partnershipLinkText,
            controllers.register.trustees.partnership.routes.TrusteeDetailsController.onPageLoad(mode, 2, srn).url), Some("test partnership"))
        )
    }

    "return the seq of trustees sub sections after filtering out deleted trustees" in {
      val userAnswers = UserAnswers().set(TrusteeDetailsId(0))(PersonDetails("firstName", None, "lastName", LocalDate.now())).flatMap(
        _.set(IsTrusteeCompleteId(0))(false).flatMap(
          _.set(IsTrusteeNewId(0))(true).flatMap(
        _.set(TrusteeKindId(0))(TrusteeKind.Individual).flatMap(
          _.set(TrusteeCompanyDetailsId(1))(CompanyDetails("test company", true)).flatMap(
            _.set(IsTrusteeCompleteId(1))(false).flatMap(
              _.set(IsTrusteeNewId(1))(true).flatMap(
              _.set(TrusteeKindId(1))(TrusteeKind.Company).flatMap(
              _.set(TrusteePartnershipDetailsId(2))(PartnershipDetails("test partnership", false)).flatMap(
                _.set(TrusteeKindId(2))(TrusteeKind.Partnership).flatMap(
                  _.set(IsTrusteeNewId(2))(true).flatMap(
                _.set(IsPartnershipCompleteId(2))(false)
              ))))))))))).asOpt.value
      val helper = new HsTaskListHelperRegistration(userAnswers)
      helper.trustees(userAnswers) mustBe
        Seq(SchemeDetailsTaskListSection(Some(false), Link(individualLinkText,
          controllers.register.trustees.individual.routes.TrusteeDetailsController.onPageLoad(mode, 0, srn).url), Some("firstName lastName")),
          SchemeDetailsTaskListSection(Some(false), Link(partnershipLinkText,
            controllers.register.trustees.partnership.routes.TrusteeDetailsController.onPageLoad(mode, 2, srn).url), Some("test partnership"))
        )
    }
  }
}
