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

package viewmodels

import models.Link
import play.api.libs.json.{Json, OFormat}

case class SchemeDetailsTaskList(beforeYouStart: SchemeDetailsTaskListSection,
                                 aboutHeader:String,
                                 about: Seq[SchemeDetailsTaskListSection],
                                 workingKnowledge: Option[SchemeDetailsTaskListSection],
                                 addEstablisherHeader : SchemeDetailsTaskListSection,
                                 establishers: Seq[SchemeDetailsTaskListSection],
                                 addTrusteeHeader : Option[SchemeDetailsTaskListSection],
                                 trustees: Seq[SchemeDetailsTaskListSection],
                                 declaration: Option[SchemeDetailsTaskListDeclarationSection] = None,
                                 h1: String,
                                 h2: String,
                                 h3: Option[String],
                                 pageTitle: String
                                ) {
}

case class SchemeDetailsTaskListDeclarationSection(declarationLink: Option[Link])

object SchemeDetailsTaskListDeclarationSection {
  implicit val formats: OFormat[SchemeDetailsTaskListDeclarationSection] = Json.format[SchemeDetailsTaskListDeclarationSection]
}

object SchemeDetailsTaskList {
  implicit val formats: OFormat[SchemeDetailsTaskList] = Json.format[SchemeDetailsTaskList]
}

case class SchemeDetailsTaskListSection(isCompleted: Option[Boolean] = None, link: Link, header: Option[String] = None)

object SchemeDetailsTaskListSection {
  implicit val formats: OFormat[SchemeDetailsTaskListSection] = Json.format[SchemeDetailsTaskListSection]
}
