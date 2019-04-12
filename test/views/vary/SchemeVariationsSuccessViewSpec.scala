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

package views.vary

import org.jsoup.Jsoup
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.vary.schemeVariationsSuccess

class SchemeVariationsSuccessViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "variations_complete"

  val schemeName = Some("a scheme")
  val srn = "srn"

  def createView: () => HtmlFormat.Appendable = () =>
    schemeVariationsSuccess(
      frontendAppConfig,
      schemeName,
      Some(srn)
    )(fakeRequest, messages)

  "SchemeVariationsSuccess view" must {

    behave like normalPage(createView, messageKeyPrefix, messages(s"messages__${messageKeyPrefix}__heading"))

    "have a link to 'print this screen'" in {
      Jsoup.parse(createView().toString()) must haveLinkOnClick("window.print();return false;", "print-this-page-link")
    }

    behave like pageWithReturnLink(createView, controllers.routes.PSASchemeDetailsController.onPageLoad(srn).url)
  }

}
