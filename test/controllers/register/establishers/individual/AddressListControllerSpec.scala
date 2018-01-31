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

package controllers.register.establishers.individual

import play.api.data.Form
import play.api.libs.json.Json
import utils.{Enumerable, FakeNavigator, MapFormats}
import connectors.FakeDataCacheConnector
import controllers.actions._
import play.api.test.Helpers._
import forms.register.establishers.individual.AddressListFormProvider
import identifiers.register.establishers.individual.{PostCodeLookupId, EstablisherDetailsId, UniqueTaxReferenceId}
import models.{Index, NormalMode}
import models.register.establishers.individual.{EstablisherDetails, UniqueTaxReference}
import views.html.register.establishers.individual.addressList
import controllers.ControllerSpecBase
import identifiers.register.SchemeDetailsId
import models.addresslookup.Address
import models.register.{SchemeDetails, SchemeType}
import org.joda.time.LocalDate
import play.api.mvc.Call

class AddressListControllerSpec extends ControllerSpecBase with Enumerable.Implicits with MapFormats {

  def onwardRoute: Call = controllers.routes.IndexController.onPageLoad()

  val formProvider = new AddressListFormProvider()
  val form = formProvider(Seq(0))
  val firstIndex = Index(0)
  val establisherName: String = "test first name test last name"

  val addresses = Seq(
    address("test post code 1"),
    address("test post code 2")
  )

  def controller(dataRetrievalAction: DataRetrievalAction = getMandatoryEstablisher): AddressListController =
    new AddressListController(frontendAppConfig, messagesApi, FakeDataCacheConnector, new FakeNavigator(desiredRoute = onwardRoute), FakeAuthAction,
      dataRetrievalAction, new DataRequiredActionImpl, formProvider)

  def viewAsString(form: Form[_] = form, address: Seq[Address] = addresses): String =
    addressList(frontendAppConfig, form, NormalMode, firstIndex, address, establisherName)(fakeRequest, messages).toString

  def address(postCode: String): Address = Address("address line 1", "address line 2", Some("test town"),
    Some("test county"), postcode = Some(postCode), country = "GB")

  val validData = Json.obj(SchemeDetailsId.toString -> Json.toJson(
    SchemeDetails("value 1", SchemeType.SingleTrust)),
    "establishers" -> Json.arr(
      Json.obj(
        EstablisherDetailsId.toString ->
          EstablisherDetails("test first name", "test last name", LocalDate.now),
        UniqueTaxReferenceId.toString ->
          UniqueTaxReference.Yes("1234567891")
      )),
    PostCodeLookupId.toString -> addresses)

  "AddressResults Controller" must {

    "return OK and the correct view for a GET when establisher name is present" in {
      val result = controller(new FakeDataRetrievalAction(Some(validData))).onPageLoad(NormalMode, firstIndex)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString(address = Seq(address("test post code 1"), address("test post code 2")))
    }

    "redirect to Session Expired page when establisher name is not present" in {
      val result = controller(getEmptyData).onPageLoad(NormalMode, firstIndex)(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.routes.SessionExpiredController.onPageLoad().url)
    }

    "redirect to Address look up page when no addresses are present after lookup" in {
      val result = controller().onPageLoad(NormalMode, firstIndex)(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(
        controllers.register.establishers.individual.routes.PostCodeLookupController.onPageLoad(NormalMode, firstIndex).url)
    }

    "redirect to Address look up page when no addresses are present after lookup (post)" in {
      val result = controller().onSubmit(NormalMode, firstIndex)(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(
        controllers.register.establishers.individual.routes.PostCodeLookupController.onPageLoad(NormalMode, firstIndex).url)
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "0"))

      val result = controller(new FakeDataRetrievalAction(Some(validData))).onSubmit(NormalMode, firstIndex)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when no data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", ""))
      val boundForm = form.bind(Map("value" -> ""))

      val result = controller(new FakeDataRetrievalAction(Some(validData))).onSubmit(NormalMode, firstIndex)(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(NormalMode, firstIndex)(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.routes.SessionExpiredController.onPageLoad().url)
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", ""))
      val result = controller(dontGetAnyData).onSubmit(NormalMode, firstIndex)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.routes.SessionExpiredController.onPageLoad().url)
    }
  }
}
