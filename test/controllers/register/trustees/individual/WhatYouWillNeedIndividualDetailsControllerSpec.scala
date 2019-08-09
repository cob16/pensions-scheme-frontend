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

package controllers.register.trustees.individual

import controllers.ControllerSpecBase
import controllers.actions._
import identifiers.register.trustees.individual.TrusteeNameId
import models.person.PersonName
import models.{Index, NormalMode}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mockito.MockitoSugar
import play.api.test.Helpers._
import utils.UserAnswers
import views.html.register.trustees.individual.whatYouWillNeedIndividualDetails

class WhatYouWillNeedIndividualDetailsControllerSpec extends ControllerSpecBase with MockitoSugar with BeforeAndAfterEach {

  private val personName = "Test Name"
  private val mandatoryTrustee = UserAnswers().set(TrusteeNameId(0))(PersonName("Test", "Name")).asOpt.value.dataRetrievalAction

  def controller(dataRetrievalAction: DataRetrievalAction = mandatoryTrustee): WhatYouWillNeedIndividualDetailsController =
    new WhatYouWillNeedIndividualDetailsController(frontendAppConfig,
      messagesApi,
      FakeAuthAction,
      dataRetrievalAction,
      FakeAllowAccessProvider(),
      new DataRequiredActionImpl
    )

  lazy val href = controllers.register.trustees.individual.routes.TrusteeDOBController.onPageLoad(NormalMode, index=Index(0), None)

  def viewAsString(): String = whatYouWillNeedIndividualDetails(frontendAppConfig, None, href, None, personName)(fakeRequest, messages).toString

  "WhatYouWillNeedIndividualDetailsControllerSpec" when {

    "on a GET" must {
      "return OK and the correct view" in {
        val result = controller().onPageLoad(NormalMode, Index(0), None)(fakeRequest)

        status(result) mustBe OK
        contentAsString(result) mustBe viewAsString()
      }
    }
  }
}

