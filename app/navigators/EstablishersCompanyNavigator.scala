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

package navigators

import identifiers.Identifier
import models.{AddressYears, CheckMode, NormalMode}
import play.api.mvc.Call
import utils.{Navigator, UserAnswers}
import com.google.inject.{Inject, Singleton}
import identifiers.register.establishers.company._

@Singleton
class EstablishersCompanyNavigator @Inject() extends Navigator {

  private def checkYourAnswers(index: Int)(answers: UserAnswers): Call =
    controllers.register.establishers.company.routes.CheckYourAnswersController.onPageLoad(index)

  override protected val routeMap: PartialFunction[Identifier, UserAnswers => Call] = {
    case CompanyDetailsId(index) =>
      _ => controllers.register.establishers.company.routes.CompanyRegistrationNumberController.onPageLoad(NormalMode, index)
    case CompanyRegistrationNumberId(index) =>
      _ => controllers.register.establishers.company.routes.CompanyUniqueTaxReferenceController.onPageLoad(NormalMode, index)
    case CompanyUniqueTaxReferenceId(index) =>
      _ => controllers.register.establishers.company.routes.CompanyPostCodeLookupController.onPageLoad(NormalMode, index)
    case CompanyPostCodeLookupId(index) =>
      _ => controllers.register.establishers.company.routes.CompanyAddressListController.onPageLoad(NormalMode, index)
    case CompanyAddressListId(index) =>
      _ => controllers.register.establishers.company.routes.CompanyAddressController.onPageLoad(NormalMode, index)
    case CompanyAddressId(index) =>
      _ => controllers.register.establishers.company.routes.CompanyAddressYearsController.onPageLoad(NormalMode, index)
    case CompanyAddressYearsId(index) =>
      addressYearsRoutes(index)
    case CompanyPreviousAddressPostcodeLookupId(index) =>
      _ => controllers.register.establishers.company.routes.CompanyPreviousAddressListController.onPageLoad(NormalMode, index)
    case CompanyPreviousAddressListId(index) =>
      _ => controllers.register.establishers.company.routes.CompanyPreviousAddressController.onPageLoad(NormalMode, index)
    case CompanyPreviousAddressId(index) =>
      _ => controllers.register.establishers.company.routes.CompanyContactDetailsController.onPageLoad(NormalMode, index)
    case CompanyContactDetailsId(index) =>
      _ => controllers.register.establishers.company.routes.CheckYourAnswersController.onPageLoad(index)
  }

  override protected val editRouteMap: PartialFunction[Identifier, UserAnswers => Call] = {
    case CompanyDetailsId(index) => checkYourAnswers(index)
    case CompanyRegistrationNumberId(index) => checkYourAnswers(index)
    case CompanyUniqueTaxReferenceId(index) => checkYourAnswers(index)
    case CompanyPostCodeLookupId(index) =>
      _ => controllers.register.establishers.company.routes.CompanyAddressListController.onPageLoad(CheckMode, index)
    case CompanyAddressListId(index) =>
      _ => controllers.register.establishers.company.routes.CompanyAddressController.onPageLoad(CheckMode, index)
    case CompanyAddressId(index) => checkYourAnswers(index)
    case CompanyAddressYearsId(index) => editAddressYearsRoutes(index)
    case CompanyPreviousAddressPostcodeLookupId(index) =>
      _ => controllers.register.establishers.company.routes.CompanyPreviousAddressListController.onPageLoad(CheckMode, index)
    case CompanyPreviousAddressListId(index) =>
      _ => controllers.register.establishers.company.routes.CompanyPreviousAddressController.onPageLoad(CheckMode, index)
    case CompanyPreviousAddressId(index) => checkYourAnswers(index)
    case CompanyContactDetailsId(index) => checkYourAnswers(index)
  }

  private def addressYearsRoutes(index: Int)(answers: UserAnswers): Call = {
    answers.get(CompanyAddressYearsId(index)) match {
      case Some(AddressYears.UnderAYear) =>
        controllers.register.establishers.company.routes.CompanyPreviousAddressPostcodeLookupController.onPageLoad(NormalMode, index)
      case Some(AddressYears.OverAYear) =>
        controllers.register.establishers.company.routes.CompanyContactDetailsController.onPageLoad(NormalMode, index)
      case None =>
        controllers.routes.SessionExpiredController.onPageLoad()
    }
  }

  private def editAddressYearsRoutes(index: Int)(answers: UserAnswers): Call = {
    answers.get(CompanyAddressYearsId(index)) match {
      case Some(AddressYears.UnderAYear) =>
        controllers.register.establishers.company.routes.CompanyPreviousAddressPostcodeLookupController.onPageLoad(CheckMode, index)
      case Some(AddressYears.OverAYear) =>
        controllers.register.establishers.company.routes.CheckYourAnswersController.onPageLoad(index)
      case None =>
        controllers.routes.SessionExpiredController.onPageLoad()
    }
  }
}