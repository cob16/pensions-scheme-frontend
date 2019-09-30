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

package views.register.establishers.individual

import controllers.register.establishers.individual.routes
import models.NormalMode
import play.api.mvc.Call
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.register.establishers.individual.whatYouWillNeedIndividualDetails

class WhatYouWillNeedIndividualDetailsViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "generic_whatYouWillNeedIndividual"
  val establisherName = "Test Name"

  lazy val href: Call = routes.EstablisherDetailsController.onPageLoad(NormalMode, 0, None)

  def createView: () => HtmlFormat.Appendable = () =>
    whatYouWillNeedIndividualDetails(frontendAppConfig, Some("testScheme"), href, None, establisherName)(fakeRequest, messages)

  "WhatYouWillNeedIndividualDetailsView" must {

    behave like normalPage(createView, messageKeyPrefix, messages(s"messages__${messageKeyPrefix}__heading", establisherName))

    "display the correct p1 and bullet points" in {
      val doc = asDocument(createView())
      assertContainsText(doc, messages("messages__whatYouWillNeedIndividual__p1", establisherName))
      assertContainsText(doc, messages("messages__whatYouWillNeedIndividual__item1"))
      assertContainsText(doc, messages("messages__whatYouWillNeedIndividual__item2"))
      assertContainsText(doc, messages("messages__whatYouWillNeedIndividual__item3"))
    }

    behave like pageWithSubmitButton(createView)

    behave like pageWithReturnLink(createView, getReturnLink)
  }
}

