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

package controllers

import config.FrontendAppConfig
import controllers.actions._
import identifiers._
import javax.inject.Inject
import models.Mode
import models.Mode._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import utils.checkyouranswers.Ops._
import utils.{CountryOptions, Enumerable, SectionComplete}
import viewmodels.AnswerSection
import views.html.check_your_answers
import models.Mode._
import services.UserAnswersService

import scala.concurrent.ExecutionContext

class CheckYourAnswersBenefitsAndInsuranceController @Inject()(appConfig: FrontendAppConfig,
                                                               override val messagesApi: MessagesApi,
                                                               authenticate: AuthAction,
                                                               getData: DataRetrievalAction,
                                                               requireData: DataRequiredAction,
                                                               userAnswersService: UserAnswersService,
                                                               implicit val countryOptions: CountryOptions
                                                              )(implicit val ec: ExecutionContext) extends FrontendController
  with Enumerable.Implicits with I18nSupport with Retrievals {

  def onPageLoad(mode: Mode, srn: Option[String]): Action[AnyContent] = (authenticate andThen getData(mode, srn) andThen requireData) {
    implicit request =>
      implicit val userAnswers = request.userAnswers
      val benefitsAndInsuranceSection = AnswerSection(
        None,
        InvestmentRegulatedSchemeId.row(routes.InvestmentRegulatedSchemeController.onPageLoad(checkMode(mode)).url, mode) ++
          OccupationalPensionSchemeId.row(routes.OccupationalPensionSchemeController.onPageLoad(checkMode(mode)).url, mode) ++
          TypeOfBenefitsId.row(routes.TypeOfBenefitsController.onPageLoad(checkMode(mode)).url, mode) ++
          BenefitsSecuredByInsuranceId.row(routes.BenefitsSecuredByInsuranceController.onPageLoad(checkMode(mode), srn).url, mode) ++
          InsuranceCompanyNameId.row(routes.InsuranceCompanyNameController.onPageLoad(checkMode(mode), srn).url, mode) ++
          InsurancePolicyNumberId.row(routes.InsurancePolicyNumberController.onPageLoad(checkMode(mode), srn).url, mode) ++
          InsurerConfirmAddressId.row(routes.InsurerConfirmAddressController.onPageLoad(checkMode(mode), srn).url, mode)
      )

      Ok(check_your_answers(
        appConfig,
        Seq(benefitsAndInsuranceSection),
        routes.CheckYourAnswersBenefitsAndInsuranceController.onSubmit(mode, srn),
        existingSchemeName,
        mode = mode,
        viewOnly = request.viewOnly
      ))
  }

  def onSubmit(mode: Mode, srn: Option[String]): Action[AnyContent] = (authenticate andThen getData(mode, srn) andThen requireData).async {
    implicit request =>
      userAnswersService.setCompleteFlag(mode, srn, IsAboutBenefitsAndInsuranceCompleteId, request.userAnswers, value = true) map { _ =>
        Redirect(controllers.routes.SchemeTaskListController.onPageLoad())
      }
  }

}
