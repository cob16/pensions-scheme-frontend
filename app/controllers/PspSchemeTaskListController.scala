/*
 * Copyright 2020 HM Revenue & Customs
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

import controllers.actions._
import javax.inject.Inject
import models.AuthEntity.PSP
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.hstasklisthelper.HsTaskListHelperPsp
import views.html.pspTaskList

import scala.concurrent.ExecutionContext

class PspSchemeTaskListController @Inject()(
                                             override val messagesApi: MessagesApi,
                                             authenticate: AuthAction,
                                             getData: PspDataRetrievalAction,
                                             val controllerComponents: MessagesControllerComponents,
                                             val view: pspTaskList,
                                             hsTaskListHelperPsp: HsTaskListHelperPsp
                                        )(implicit val executionContext: ExecutionContext) extends
  FrontendBaseController with I18nSupport with Retrievals {

  def onPageLoad(srn: String): Action[AnyContent] = (authenticate(PSP) andThen getData(srn)) {
    implicit request =>
      request.userAnswers match {
        case Some(ua) => Ok(view(hsTaskListHelperPsp.taskList(ua, srn)))
        case _ => Redirect(controllers.routes.SessionExpiredController.onPageLoad())
      }
  }
}