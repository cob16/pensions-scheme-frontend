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

package models.register

import identifiers.register.establishers.EstablisherKindId
import identifiers.register.establishers.company.director.DirectorDetailsId
import identifiers.register.establishers.company.{CompanyDetailsId => EstablisherCompanyDetailsId}
import identifiers.register.establishers.individual.EstablisherDetailsId
import identifiers.register.establishers.partnership.PartnershipDetailsId
import identifiers.register.establishers.partnership.partner.PartnerDetailsId
import identifiers.register.trustees.TrusteeKindId
import identifiers.register.trustees.company.{CompanyDetailsId => TrusteeCompanyDetailsId}
import identifiers.register.trustees.individual.TrusteeDetailsId
import identifiers.register.trustees.partnership.{PartnershipDetailsId => TrusteePartnershipDetailsId}
import models._
import models.register.establishers.EstablisherKind
import models.register.trustees.TrusteeKind

sealed trait Entity[ID] {
  def id: ID

  def name: String

  def isDeleted: Boolean

  def isCompleted: Boolean

  def editLink: Option[String]

  def deleteLink: Option[String]

  def index: Int
}

case class DirectorEntity(id: DirectorDetailsId, name: String, isDeleted: Boolean,
                          isCompleted: Boolean, isNewEntity: Boolean, noOfRecords : Int) extends Entity[DirectorDetailsId] {
  override def editLink: Option[String] = (isNewEntity, isCompleted) match {
    case (false, _) => None
    case (_, true) => Some(controllers.register.establishers.company.director.routes.CheckYourAnswersController.onPageLoad(
      id.establisherIndex, id.directorIndex, NormalMode, None).url)
    case (_, false) => Some(controllers.register.establishers.company.director.routes.DirectorDetailsController.onPageLoad(
      NormalMode, id.establisherIndex, id.directorIndex, None).url)
  }

  override def deleteLink: Option[String] =
    if(noOfRecords>1)
      Some(controllers.register.establishers.company.director.routes.ConfirmDeleteDirectorController.onPageLoad(
        id.establisherIndex, id.directorIndex, NormalMode, None).url)
    else None

  override def index: Int = id.directorIndex
}

case class PartnerEntity(id: PartnerDetailsId, name: String, isDeleted: Boolean,
                         isCompleted: Boolean, isNewEntity: Boolean, noOfRecords : Int) extends Entity[PartnerDetailsId] {
  override def editLink: Option[String] = (isNewEntity, isCompleted) match {
    case (false, _) => None
    case (_, true) => Some(controllers.register.establishers.partnership.partner.routes.CheckYourAnswersController.onPageLoad(
      NormalMode, id.establisherIndex, id.partnerIndex, None).url)
    case (_, false) => Some(controllers.register.establishers.partnership.partner.routes.PartnerDetailsController.onPageLoad(
      NormalMode, id.establisherIndex, id.partnerIndex, None).url)
  }

  override def deleteLink: Option[String] =
    if(noOfRecords>1)
      Some(controllers.register.establishers.partnership.partner.routes.ConfirmDeletePartnerController.onPageLoad(
        NormalMode, id.establisherIndex, id.partnerIndex, None).url)
    else None

  override def index: Int = id.partnerIndex
}

sealed trait Establisher[T] extends Entity[T]

case class EstablisherCompanyEntity(id: EstablisherCompanyDetailsId, name: String, isDeleted: Boolean,
                                    isCompleted: Boolean, isNewEntity: Boolean, noOfRecords : Int) extends Establisher[EstablisherCompanyDetailsId] {
  override def editLink: Option[String] = (isNewEntity, isCompleted) match {
    case (false, _) => None
    case (_, true) => Some(controllers.register.establishers.company.routes.CheckYourAnswersController.onPageLoad(NormalMode, None, id.index).url)
    case (_, false) => Some(controllers.register.establishers.company.routes.CompanyDetailsController.onPageLoad(NormalMode, None, id.index).url)
  }

  override def deleteLink: Option[String] =
    if(noOfRecords>1)
      Some(controllers.register.establishers.routes.ConfirmDeleteEstablisherController.onPageLoad(NormalMode, id.index, EstablisherKind.Company, None).url)
    else None

  override def index: Int = id.index
}

case class EstablisherIndividualEntity(id: EstablisherDetailsId, name: String, isDeleted: Boolean,
                                       isCompleted: Boolean, isNewEntity: Boolean, noOfRecords : Int) extends Establisher[EstablisherDetailsId] {
  override def editLink: Option[String] = (isNewEntity, isCompleted) match {
    case (false, _) => None
    case (_, true) => Some(controllers.register.establishers.individual.routes.CheckYourAnswersController.onPageLoad(NormalMode, id.index, None).url)
    case (_, false) => Some(controllers.register.establishers.individual.routes.EstablisherDetailsController.onPageLoad(NormalMode, id.index, None).url)
  }

  override def deleteLink: Option[String] =
    if(noOfRecords>1)
      Some(controllers.register.establishers.routes.ConfirmDeleteEstablisherController.onPageLoad(NormalMode, id.index, EstablisherKind.Indivdual, None).url)
    else None

  override def index: Int = id.index
}

case class EstablisherPartnershipEntity(id: PartnershipDetailsId, name: String, isDeleted: Boolean,
                                        isCompleted: Boolean, isNewEntity: Boolean, noOfRecords : Int) extends Establisher[PartnershipDetailsId] {
  override def editLink: Option[String] = (isNewEntity, isCompleted) match {
    case (false, _) => None
    case (_, true) => Some(controllers.register.establishers.partnership.routes.CheckYourAnswersController.onPageLoad(NormalMode, id.index, None).url)
    case (_, false) => Some(controllers.register.establishers.partnership.routes.PartnershipDetailsController.onPageLoad(NormalMode, id.index, None).url)
  }

  override def deleteLink: Option[String] =
    if(noOfRecords>1)
      Some(controllers.register.establishers.routes.ConfirmDeleteEstablisherController.onPageLoad(NormalMode, id.index, EstablisherKind.Partnership, None).url)
    else None

  override def index: Int = id.index
}

case class EstablisherSkeletonEntity(id: EstablisherKindId) extends Establisher[EstablisherKindId] {

  override def index: Int = id.index

  override def name: String = ""
  
  override def isDeleted: Boolean = true

  override def isCompleted: Boolean = false

  override def editLink: Option[String] = None

  override def deleteLink: Option[String] = None
}

sealed trait Trustee[T] extends Entity[T]

case class TrusteeCompanyEntity(id: TrusteeCompanyDetailsId, name: String, isDeleted: Boolean,
                                isCompleted: Boolean, isNewEntity: Boolean, noOfRecords : Int, schemeType: Option[String]) extends Trustee[TrusteeCompanyDetailsId] {
  override def editLink: Option[String] = (isNewEntity, isCompleted) match {
    case (false, _) => None
    case (_, true) => Some(controllers.register.trustees.company.routes.CheckYourAnswersController.onPageLoad(NormalMode, id.index, None).url)
    case (_, false) => Some(controllers.register.trustees.company.routes.CompanyDetailsController.onPageLoad(NormalMode, id.index, None).url)
  }

  override def deleteLink: Option[String] = {
    val mode : Mode = NormalMode
    mode match {
      case NormalMode | CheckMode =>
        Some(controllers.register.trustees.routes.ConfirmDeleteTrusteeController.onPageLoad(mode, id.index, TrusteeKind.Company, None).url)
      case UpdateMode | CheckUpdateMode if (noOfRecords > 1 && schemeType.fold(false)(scheme => Seq("single", "master").exists(_.equals(scheme)))) =>
        Some(controllers.register.trustees.routes.ConfirmDeleteTrusteeController.onPageLoad(mode, id.index, TrusteeKind.Company, None).url)
      case _ => None
    }
  }
  override def index: Int = id.index
}

case class TrusteeIndividualEntity(id: TrusteeDetailsId, name: String, isDeleted: Boolean,
                                   isCompleted: Boolean, isNewEntity: Boolean, noOfRecords : Int, schemeType: Option[String]) extends Trustee[TrusteeDetailsId] {
  override def editLink: Option[String] = (isNewEntity, isCompleted) match {
    case (false, _) => None
    case (_, true) => Some(controllers.register.trustees.individual.routes.CheckYourAnswersController.onPageLoad(NormalMode, id.index, None).url)
    case (_, false) => Some(controllers.register.trustees.individual.routes.TrusteeDetailsController.onPageLoad(NormalMode, id.index, None).url)
  }

  override def deleteLink: Option[String] = {
    val mode : Mode = NormalMode
    mode match {
      case NormalMode | CheckMode =>
        Some(controllers.register.trustees.routes.ConfirmDeleteTrusteeController.onPageLoad(mode, id.index, TrusteeKind.Individual, None).url)
      case UpdateMode | CheckUpdateMode if (noOfRecords > 1 && schemeType.fold(false)(scheme => Seq("single", "master").exists(_.equals(scheme)))) =>
        Some(controllers.register.trustees.routes.ConfirmDeleteTrusteeController.onPageLoad(mode, id.index, TrusteeKind.Individual, None).url)
      case _ => None
    }
  }
  override def index: Int = id.index
}

case class TrusteePartnershipEntity(id: TrusteePartnershipDetailsId, name: String, isDeleted: Boolean,
                                    isCompleted: Boolean, isNewEntity: Boolean, noOfRecords : Int, schemeType: Option[String]) extends Trustee[TrusteePartnershipDetailsId] {
  override def editLink: Option[String] = (isNewEntity, isCompleted) match {
    case (false, _) => None
    case (_, true) => Some(controllers.register.trustees.partnership.routes.CheckYourAnswersController.onPageLoad(NormalMode, id.index, None).url)
    case (_, false) => Some(controllers.register.trustees.partnership.routes.TrusteeDetailsController.onPageLoad(NormalMode, id.index, None).url)
  }

  override def deleteLink: Option[String] = {
    val mode : Mode = NormalMode
    mode match {
      case NormalMode | CheckMode =>
        Some(controllers.register.trustees.routes.ConfirmDeleteTrusteeController.onPageLoad(mode, id.index, TrusteeKind.Partnership, None).url)
      case UpdateMode | CheckUpdateMode if (noOfRecords > 1 && schemeType.fold(false)(scheme => Seq("single", "master").exists(_.equals(scheme)))) =>
        Some(controllers.register.trustees.routes.ConfirmDeleteTrusteeController.onPageLoad(mode, id.index, TrusteeKind.Partnership, None).url)
      case _ => None
    }
  }
  override def index: Int = id.index
}

case class TrusteeSkeletonEntity(id: TrusteeKindId) extends Trustee[TrusteeKindId] {
  override def name: String = ""
  
  override def isDeleted: Boolean = true

  override def isCompleted: Boolean = false

  override def editLink: Option[String] = None

  override def deleteLink: Option[String] = None

  override def index: Int = id.index
}
