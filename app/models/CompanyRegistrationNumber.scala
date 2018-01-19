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

package models

import utils.{Enumerable, InputOption, WithName}

sealed trait CompanyRegistrationNumber

object CompanyRegistrationNumber {

  case object Yes extends WithName("yes") with CompanyRegistrationNumber
  case object No extends WithName("no") with CompanyRegistrationNumber

  val values: Seq[CompanyRegistrationNumber] = Seq(
    Yes, No
  )

  val options: Seq[InputOption] = values.map {
    value =>
      InputOption(value.toString, s"site.${value.toString}")
  }

  implicit val enumerable: Enumerable[CompanyRegistrationNumber] =
    Enumerable(values.map(v => v.toString -> v): _*)
}