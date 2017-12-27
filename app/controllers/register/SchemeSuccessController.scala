/*
 * Copyright 2017 HM Revenue & Customs
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

package controllers.register

import javax.inject.Inject

import play.api.i18n.{I18nSupport, MessagesApi}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import controllers.actions._
import config.FrontendAppConfig
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import play.api.mvc.{Action, AnyContent}
import views.html.register.schemeSuccess

class SchemeSuccessController @Inject()(appConfig: FrontendAppConfig,
                                         override val messagesApi: MessagesApi,
                                         authenticate: AuthAction,
                                         getData: DataRetrievalAction,
                                         requireData: DataRequiredAction) extends FrontendController with I18nSupport {

  val currentDate = LocalDate.now()

  def onPageLoad: Action[AnyContent] = (authenticate andThen getData andThen requireData) {
    implicit request =>
      val dateFormatToBeDisplayed = "d MMMM yyyy"
      val dateFormat = DateTimeFormat.forPattern(dateFormatToBeDisplayed)

      //TODO: Replace the harcoded application number to the actual application number
      Ok(schemeSuccess(
        appConfig,
        request.userAnswers.schemeDetails.map(_.schemeName),
        dateFormat.print(currentDate),
        "XX123456789132"))
  }
}
