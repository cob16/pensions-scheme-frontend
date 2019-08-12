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

package identifiers.register.trustees.company

import base.SpecBase
import identifiers.register.trustees.{IsTrusteeCompleteId, IsTrusteeNewId}
import models.AddressYears.UnderAYear
import models._
import models.address.{Address, TolerantAddress}
import models.requests.DataRequest
import play.api.libs.json.Json
import play.api.mvc.AnyContent
import play.api.test.FakeRequest
import uk.gov.hmrc.domain.PsaId
import utils.checkyouranswers.Ops._
import utils.{CountryOptions, InputOption, UserAnswers}
import viewmodels.AnswerRow

class CompanyAddressYearsIdSpec extends SpecBase {

  "Cleanup" must {

    val answers = UserAnswers(Json.obj())
      .set(CompanyAddressYearsId(0))(AddressYears.UnderAYear)
      .flatMap(_.set(CompanyPreviousAddressPostcodeLookupId(0))(Seq.empty))
      .flatMap(_.set(CompanyPreviousAddressId(0))(Address("foo", "bar", None, None, None, "GB")))
      .flatMap(_.set(CompanyPreviousAddressListId(0))(TolerantAddress(Some("foo"), Some("bar"), None, None, None, Some("GB"))))
      .flatMap(_.set(HasBeenTradingCompanyId(0))(true))
      .flatMap(_.set(IsTrusteeCompleteId(0))(true))
      .asOpt.value

    "`AddressYears` is set to `OverAYear`" when {

      val result: UserAnswers = answers.set(CompanyAddressYearsId(0))(AddressYears.OverAYear).asOpt.value

      "remove the data for `PreviousPostCodeLookup`" in {
        result.get(CompanyPreviousAddressPostcodeLookupId(0)) mustNot be(defined)
      }

      "remove the data for `PreviousAddress`" in {
        result.get(CompanyPreviousAddressId(0)) mustNot be(defined)
      }

      "remove the data for `PreviousAddressList`" in {
        result.get(CompanyPreviousAddressListId(0)) mustNot be(defined)
      }

      "remove the data for `HasBeenTrading`" in {
        result.get(HasBeenTradingCompanyId(0)) mustNot be(defined)
      }

      "do not change the value of IsTrusteeCompleteId" in {
        result.get(IsTrusteeCompleteId(0)).value mustBe true
      }
    }

    "`AddressYears` is set to `UnderAYear`" when {

      val result: UserAnswers = UserAnswers(Json.obj())
        .set(CompanyAddressYearsId(0))(AddressYears.OverAYear)
        .flatMap(_.set(CompanyPreviousAddressPostcodeLookupId(0))(Seq.empty))
        .flatMap(_.set(CompanyPreviousAddressId(0))(Address("foo", "bar", None, None, None, "GB")))
        .flatMap(_.set(CompanyPreviousAddressListId(0))(TolerantAddress(Some("foo"), Some("bar"), None, None, None, Some("GB"))))
        .flatMap(_.set(HasBeenTradingCompanyId(0))(true))
        .flatMap(_.set(IsTrusteeCompleteId(0))(true))
        .asOpt.value.set(CompanyAddressYearsId(0))(AddressYears.UnderAYear).asOpt.value

      "set the value of IsTrusteeCompleteId to false" in {
        result.get(IsTrusteeCompleteId(0)).value mustBe false
      }

      "not remove the data for `PreviousPostCodeLookup`" in {
        result.get(CompanyPreviousAddressPostcodeLookupId(0)) mustBe defined
      }

      "not remove the data for `PreviousAddress`" in {
        result.get(CompanyPreviousAddressId(0)) mustBe defined
      }

      "not remove the data for `PreviousAddressList`" in {
        result.get(CompanyPreviousAddressListId(0)) mustBe defined
      }

      "not remove the data for `HasBeenTrading`" in {
        result.get(HasBeenTradingCompanyId(0)) mustBe defined
      }
    }

    "`AddressYears` is removed" when {

      val result: UserAnswers = answers.remove(CompanyAddressYearsId(0)).asOpt.value

      "not remove the data for `PreviousPostCodeLookup`" in {
        result.get(CompanyPreviousAddressPostcodeLookupId(0)) mustBe defined
      }

      "not remove the data for `PreviousAddress`" in {
        result.get(CompanyPreviousAddressId(0)) mustBe defined
      }

      "not remove the data for `PreviousAddressList`" in {
        result.get(CompanyPreviousAddressListId(0)) mustBe defined
      }

    }
  }

  "cya" when {
    val onwardUrl = "onwardUrl"
    val companyName = "test company name"

    def answers: UserAnswers = UserAnswers()
      .set(CompanyAddressYearsId(0))(UnderAYear).asOpt.get
      .set(CompanyDetailsId(0))(CompanyDetails(companyName)).asOpt.get

    implicit val countryOptions: CountryOptions = new CountryOptions(Seq.empty[InputOption])

    "in normal mode" must {
      "return answers rows with change links" in {
        implicit val request: DataRequest[AnyContent] = DataRequest(FakeRequest(), "id", answers, PsaId("A0000000"))
        implicit val userAnswers: UserAnswers = request.userAnswers
        CompanyAddressYearsId(0).row(onwardUrl, NormalMode) must equal(Seq(
          AnswerRow(
            messages("messages__hasBeen1Year", companyName),
            Seq(s"messages__common__under_a_year"),
            answerIsMessageKey = true,
            Some(Link("site.change", onwardUrl,
              Some(messages("messages__changeHasBeen1Year", companyName))))
          )))
      }
    }

    "in update mode for new trustee" must {

      val companyName = "test company name"

      def answersNew: UserAnswers = answers.set(IsTrusteeNewId(0))(true).asOpt.value
        .set(CompanyDetailsId(0))(CompanyDetails(companyName)).asOpt.get


      "return answers rows with change links" in {
        implicit val request: DataRequest[AnyContent] = DataRequest(FakeRequest(), "id", answersNew, PsaId("A0000000"))
        implicit val userAnswers: UserAnswers = request.userAnswers
        CompanyAddressYearsId(0).row(onwardUrl, UpdateMode) must equal(Seq(
          AnswerRow(
            messages("messages__hasBeen1Year", companyName),
            Seq(s"messages__common__under_a_year"),
            answerIsMessageKey = true,
            Some(Link("site.change", onwardUrl,
              Some(messages("messages__changeHasBeen1Year", companyName))))
          )))
      }
    }

    "in update mode for existing trustee" must {

      "return no rows" in {
        implicit val request: DataRequest[AnyContent] = DataRequest(FakeRequest(), "id", answers, PsaId("A0000000"))
        implicit val userAnswers: UserAnswers = request.userAnswers

        CompanyAddressYearsId(0).row(onwardUrl, UpdateMode) must equal(Nil)
      }
    }
  }
}
