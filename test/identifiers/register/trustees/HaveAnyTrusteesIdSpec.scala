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

import identifiers.register.trustees.company._
import identifiers.register.trustees.individual._
import identifiers.register.trustees.partnership.PartnershipDetailsId
import models.address.Address
import models.person.PersonDetails
import models._
import models.register.trustees.TrusteeKind
import org.joda.time.LocalDate
import org.scalatest.{MustMatchers, OptionValues, WordSpec}
import play.api.libs.json.Json
import utils.{Enumerable, UserAnswers}

class HaveAnyTrusteesIdSpec extends WordSpec with MustMatchers with OptionValues with Enumerable.Implicits {

  import HaveAnyTrusteesIdSpec._

  "cleanup" when {

    "`HaveAnyTrustees` changed from Yes to No" must {
      val result = haveTrusteesYes.set(HaveAnyTrusteesId)(false).asOpt.value

      "remove all the data for `Trustees`" in {
        result.get(TrusteeKindId(0)) mustNot be(defined)
        result.get(CompanyDetailsId(0)) mustNot be(defined)
        result.get(CompanyRegistrationNumberId(0)) mustNot be(defined)
        result.get(CompanyUniqueTaxReferenceId(0)) mustNot be(defined)
        result.get(CompanyPostcodeLookupId(0)) mustNot be(defined)
        result.get(CompanyAddressId(0)) mustNot be(defined)
        result.get(CompanyAddressYearsId(0)) mustNot be(defined)
        result.get(CompanyPreviousAddressPostcodeLookupId(0)) mustNot be(defined)
        result.get(CompanyPreviousAddressId(0)) mustNot be(defined)
        result.get(CompanyContactDetailsId(0)) mustNot be(defined)
        result.get(TrusteeKindId(1)) mustNot be(defined)
        result.get(TrusteeDetailsId(1)) mustNot be(defined)
        result.get(TrusteeKindId(2)) mustNot be(defined)
        result.get(CompanyDetailsId(2)) mustNot be(defined)
        result.get(TrusteeKindId(3)) mustNot be(defined)
        result.get(PartnershipDetailsId(2)) mustNot be(defined)
        result.get(MoreThanTenTrusteesId) mustNot be(defined)
      }
    }

    "`HaveAnyTrustees` changed from Yes to Yes" must {
      val result = haveTrusteesYes.set(HaveAnyTrusteesId)(true).asOpt.value

      "not remove any data for `Trustees`" in {
        result.get(CompanyDetailsId(0)) must be(defined)
        result.get(CompanyRegistrationNumberId(0)) must be(defined)
        result.get(CompanyUniqueTaxReferenceId(0)) must be(defined)
        result.get(CompanyPostcodeLookupId(0)) must be(defined)
        result.get(CompanyAddressId(0)) must be(defined)
        result.get(CompanyAddressYearsId(0)) must be(defined)
        result.get(CompanyPreviousAddressPostcodeLookupId(0)) must be(defined)
        result.get(CompanyPreviousAddressId(0)) must be(defined)
        result.get(CompanyContactDetailsId(0)) must be(defined)
        result.get(TrusteeDetailsId(1)) must be(defined)
        result.get(CompanyDetailsId(2)) must be(defined)
        result.get(MoreThanTenTrusteesId) must be(defined)
      }
    }
  }
}

object HaveAnyTrusteesIdSpec extends OptionValues with Enumerable.Implicits {

  val haveTrusteesYes = UserAnswers(Json.obj())
    .set(HaveAnyTrusteesId)(true)
    .flatMap(_.set(TrusteeKindId(0))(TrusteeKind.Company))
    .flatMap(_.set(CompanyDetailsId(0))(CompanyDetails("first", None, None)))
    .flatMap(_.set(CompanyRegistrationNumberId(0))(CompanyRegistrationNumber.No("")))
    .flatMap(_.set(CompanyUniqueTaxReferenceId(0))(UniqueTaxReference.No("")))
    .flatMap(_.set(CompanyPostcodeLookupId(0))(Seq.empty))
    .flatMap(_.set(CompanyAddressId(0))(Address("", "", None, None, None, "")))
    .flatMap(_.set(CompanyAddressYearsId(0))(AddressYears.UnderAYear))
    .flatMap(_.set(CompanyPreviousAddressPostcodeLookupId(0))(Seq.empty))
    .flatMap(_.set(CompanyPreviousAddressId(0))(Address("", "", None, None, None, "")))
    .flatMap(_.set(CompanyContactDetailsId(0))(ContactDetails("", "")))
    .flatMap(_.set(TrusteeKindId(1))(TrusteeKind.Individual))
    .flatMap(_.set(TrusteeDetailsId(1))(PersonDetails("", None, "", LocalDate.now)))
    .flatMap(_.set(TrusteeKindId(2))(TrusteeKind.Company))
    .flatMap(_.set(CompanyDetailsId(2))(CompanyDetails("second", None, None)))
    .flatMap(_.set(TrusteeKindId(3))(TrusteeKind.Partnership))
    .flatMap(_.set(PartnershipDetailsId(3))(models.PartnershipDetails("third")))
    .flatMap(_.set(MoreThanTenTrusteesId)(true))
    .asOpt.value
}

