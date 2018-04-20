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

package controllers.register

import javax.inject.Inject

import config.FrontendAppConfig
import connectors.DataCacheConnector
import controllers.actions._
import controllers.{Journey, JourneyType, Retrievals}
import forms.register.DeclarationFormProvider
import identifiers.register.{DeclarationDormantId, DeclarationId, SchemeDetailsId}
import models.NormalMode
import models.register.DeclarationDormant.{No, Yes}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import utils.annotations.Register
import utils.{Enumerable, Navigator, UserAnswers}
import views.html.register.declaration

import scala.concurrent.Future

class DeclarationController @Inject()(
                                       appConfig: FrontendAppConfig,
                                       override val messagesApi: MessagesApi,
                                       dataCacheConnector: DataCacheConnector,
                                       @Register navigator: Navigator,
                                       authenticate: AuthAction,
                                       getData: DataRetrievalAction,
                                       requireData: DataRequiredAction,
                                       formProvider: DeclarationFormProvider,
                                       journey: Journey
                                     ) extends FrontendController with Retrievals with I18nSupport with Enumerable.Implicits {

  private val form = formProvider()

  def onPageLoad: Action[AnyContent] = (authenticate andThen getData andThen requireData).async {
    implicit request =>
      SchemeDetailsId.retrieve.right.map { details =>
        journey.withJourneyType {
          case JourneyType.Company =>
            request.userAnswers.get(DeclarationDormantId) match {
              case Some(Yes) => Future.successful(Ok(declaration(appConfig, form, details.schemeName, isCompany = true, isDormant = true)))
              case Some(No) => Future.successful(Ok(declaration(appConfig, form, details.schemeName, isCompany = true, isDormant = false)))
              case None => Future.successful(Redirect(controllers.routes.SessionExpiredController.onPageLoad()))
            }
          case JourneyType.Individual => Future.successful(Ok(declaration(appConfig, form, details.schemeName, isCompany = false, isDormant = false)))
        }
      }
  }

  def onSubmit: Action[AnyContent] = (authenticate andThen getData andThen requireData).async {
    implicit request =>
      SchemeDetailsId.retrieve.right.map { details =>
        form.bindFromRequest().fold(
          (formWithErrors: Form[_]) =>
              journey.withJourneyType {
                case JourneyType.Company =>
                  request.userAnswers.get(DeclarationDormantId) match {
                    case Some(Yes) => Future.successful(BadRequest(declaration(appConfig, formWithErrors, details.schemeName, isCompany = true, isDormant = true)))
                    case Some(No) => Future.successful(BadRequest(declaration(appConfig, formWithErrors, details.schemeName, isCompany = true, isDormant = false)))
                    case None => Future.successful(Redirect(controllers.routes.SessionExpiredController.onPageLoad()))
                  }
                case JourneyType.Individual => Future.successful(BadRequest(declaration(appConfig, formWithErrors, details.schemeName, isCompany = false, isDormant = false)))
            },
          (value) =>
            dataCacheConnector.save(request.externalId, DeclarationId, value).map(cacheMap =>
              Redirect(navigator.nextPage(DeclarationId, NormalMode)(new UserAnswers(cacheMap))))
        )
      }
  }
}
