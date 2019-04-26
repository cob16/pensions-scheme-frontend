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

package navigators

import com.google.inject.Inject
import config.FrontendAppConfig
import connectors.UserAnswersCacheConnector
import identifiers.AnyMoreChangesId
import utils.{Enumerable, Navigator}

class VariationsNavigator @Inject()(val dataCacheConnector: UserAnswersCacheConnector,
                                    config: FrontendAppConfig)extends Navigator with Enumerable.Implicits {

  override protected def routeMap(from: NavigateFrom): Option[NavigateTo] = None

  override protected def editRouteMap(from: NavigateFrom): Option[NavigateTo] = None

  protected def updateRouteMap(from: NavigateFrom, srn: Option[String]): Option[NavigateTo] =
    from.id match {
      case AnyMoreChangesId => (from.userAnswers.get(AnyMoreChangesId), srn) match {
        case (Some(true), Some(srn)) => NavigateTo.dontSave(controllers.routes.PSASchemeDetailsController.onPageLoad(srn))
        case (Some(false), Some(srn)) => //todo - change to incomplete logic controller after PODS-2437 is done
         NavigateTo.dontSave(controllers.routes.PSASchemeDetailsController.onPageLoad(srn))
        case _ => NavigateTo.dontSave(controllers.routes.SessionExpiredController.onPageLoad)
      }
      case _ => None
    }

  protected def checkUpdateRouteMap(from: NavigateFrom, srn: Option[String]): Option[NavigateTo] = None

}