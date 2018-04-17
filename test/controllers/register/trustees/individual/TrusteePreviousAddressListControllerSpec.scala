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

package controllers.register.trustees.individual

import base.CSRFRequest
import connectors.{DataCacheConnector, FakeDataCacheConnector}
import controllers.ControllerSpecBase
import controllers.actions._
import forms.address.AddressListFormProvider
import identifiers.register.trustees.individual.{IndividualPreviousAddressPostCodeLookupId, TrusteeDetailsId}
import models.address.Address
import models.person.PersonDetails
import models.{Index, NormalMode}
import org.joda.time.LocalDate
import play.api.inject.bind
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.UserAnswers
import viewmodels.Message
import viewmodels.address.AddressListViewModel
import views.html.address.addressList

class TrusteePreviousAddressListControllerSpec extends ControllerSpecBase with CSRFRequest {

  private val trusteeDetails = PersonDetails("Test", Some("Trustee"), "Name", LocalDate.now)

  private val addresses = Seq(
    Address(
      "Address 1 Line 1",
      "Address 1 Line 2",
      Some("Address 1 Line 3"),
      Some("Address 1 Line 4"),
      Some("A1 1PC"),
      "GB"
    ),
    Address(
      "Address 2 Line 1",
      "Address 2 Line 2",
      Some("Address 2 Line 3"),
      Some("Address 2 Line 4"),
      Some("123"),
      "FR"
    )
  )

  private val data =
    UserAnswers(Json.obj())
      .set(TrusteeDetailsId(0))(trusteeDetails)
      .flatMap(_.set(IndividualPreviousAddressPostCodeLookupId(0))(addresses))
      .asOpt.map(_.json)

  private val dataRetrievalAction = new FakeDataRetrievalAction(data)

  "Trustee Previous Address List Controller" must {

    "return Ok and the correct view on a GET request" in {

      running(_.overrides(
        bind[AuthAction].to(FakeAuthAction),
        bind[DataCacheConnector].toInstance(FakeDataCacheConnector),
        bind[DataRetrievalAction].toInstance(dataRetrievalAction)
      )) { implicit app =>
        val request = addToken(FakeRequest(routes.TrusteePreviousAddressListController.onPageLoad(NormalMode, Index(0))))
        val result = route(app, request).value

        status(result) mustBe OK

        val viewModel: AddressListViewModel = addressListViewModel(addresses)
        val form = new AddressListFormProvider()(viewModel.addresses)

        contentAsString(result) mustBe addressList(frontendAppConfig, form, viewModel)(request, messages).toString
      }

    }

    "redirect to Individual Address Post Code Lookup if no address data on a GET request" in {

      running(_.overrides(
        bind[AuthAction].to(FakeAuthAction),
        bind[DataCacheConnector].toInstance(FakeDataCacheConnector),
        bind[DataRetrievalAction].toInstance(getEmptyData)
      )) { implicit app =>
        val request = addToken(FakeRequest(routes.TrusteePreviousAddressListController.onPageLoad(NormalMode, Index(0))))
        val result = route(app, request).value

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(routes.IndividualPreviousAddressPostcodeLookupController.onPageLoad(NormalMode, Index(0)).url)
      }

    }

    "redirect to Session Expired controller when no session data exists on a GET request" in {

      running(_.overrides(
        bind[AuthAction].to(FakeAuthAction),
        bind[DataCacheConnector].toInstance(FakeDataCacheConnector),
        bind[DataRetrievalAction].toInstance(dontGetAnyData)
      )) { implicit app =>
        val request = addToken(FakeRequest(routes.TrusteePreviousAddressListController.onPageLoad(NormalMode, Index(0))))
        val result = route(app, request).value

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.routes.SessionExpiredController.onPageLoad().url)
      }

    }

    "redirect to the next page on POST of valid data" in {

      running(_.overrides(
        bind[AuthAction].to(FakeAuthAction),
        bind[DataCacheConnector].toInstance(FakeDataCacheConnector),
        bind[DataRetrievalAction].toInstance(dataRetrievalAction)
      )) { implicit app =>
        def request =
          addToken(
            FakeRequest(routes.TrusteePreviousAddressListController.onSubmit(NormalMode, Index(0)))
              .withFormUrlEncodedBody(("value", "0"))
          )

        def result = route(app, request).value

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe
          Some(controllers.routes.IndexController.onPageLoad().url)
      }

    }

    "redirect to Session Expired controller when no session data exists on a POST request" in {

      running(_.overrides(
        bind[AuthAction].to(FakeAuthAction),
        bind[DataCacheConnector].toInstance(FakeDataCacheConnector),
        bind[DataRetrievalAction].toInstance(dontGetAnyData)
      )) { implicit app =>
        val request =
          addToken(
            FakeRequest(routes.TrusteePreviousAddressListController.onSubmit(NormalMode, Index(0)))
              .withFormUrlEncodedBody(("value", "0"))
          )

        val result = route(app, request).value

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.routes.SessionExpiredController.onPageLoad().url)
      }

    }

    "redirect to Individual Address Post Code Lookup if no address data on a POST request" in {

      running(_.overrides(
        bind[AuthAction].to(FakeAuthAction),
        bind[DataCacheConnector].toInstance(FakeDataCacheConnector),
        bind[DataRetrievalAction].toInstance(getEmptyData)
      )) { implicit app =>
        val request =
          addToken(
            FakeRequest(routes.TrusteePreviousAddressListController.onSubmit(NormalMode, Index(0)))
              .withFormUrlEncodedBody(("value", "0"))
          )

        val result = route(app, request).value

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(routes.IndividualPreviousAddressPostcodeLookupController.onPageLoad(NormalMode, Index(0)).url)
      }

    }

  }

  private def addressListViewModel(addresses: Seq[Address]): AddressListViewModel = {
    AddressListViewModel(
      routes.TrusteePreviousAddressListController.onSubmit(NormalMode, Index(0)),
      routes.TrusteePreviousAddressController.onPageLoad(NormalMode, Index(0)),
      addresses,
      heading = "messages__select_the_previous_address__title",
      subHeading = Some(Message(trusteeDetails.fullName))
    )
  }
}