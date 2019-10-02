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

package controllers.register.establishers.partnership.partner

import config.FrontendAppConfig
import controllers.Retrievals
import controllers.actions._
import identifiers.register.establishers.partnership.PartnershipDetailsId
import javax.inject.Inject
import models.{Index, Mode}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import views.html.register.establishers.partnership.partner.whatYouWillNeed

import scala.concurrent.Future

class WhatYouWillNeedPartnerController @Inject()(appConfig: FrontendAppConfig,
                                                 override val messagesApi: MessagesApi,
                                                 authenticate: AuthAction,
                                                 getData: DataRetrievalAction,
                                                 allowAccess: AllowAccessActionProvider,
                                                 requireData: DataRequiredAction
                                                ) extends FrontendController with I18nSupport with Retrievals {

  def onPageLoad(mode: Mode, index: Index, srn: Option[String] = None): Action[AnyContent] = (authenticate andThen
    getData(mode, srn) andThen allowAccess(srn) andThen requireData).async {
    implicit request =>
      val partnerIndex = request.userAnswers.allPartners(index, isHnSEnabled = true).size
      PartnershipDetailsId(index).retrieve.right.map { partnershipDetails =>
        Future.successful(Ok(whatYouWillNeed(
          appConfig,
          existingSchemeName,
          srn,
          partnershipDetails.name,
          routes.PartnerNameController.onPageLoad(mode, index, partnerIndex, srn)
        )))
      }
  }
}
