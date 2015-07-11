package models.dao.sapi

import org.apache.commons.lang3.StringUtils
import play.api.libs.json._

/**
 * Created by tashdid.khan on 7/10/2015.
 */
case class Survey(id: String = null, lineItems: Map[Category, List[LineItem]] = Map[Category, List[LineItem]](),  name :String = StringUtils.EMPTY, sourceUser: User = null, targetUser: User = null) extends AbstractModel
case class LineItem (id: String = null, minRange: Int =0,maxRange: Int =0, question: String, score: Int = 0) extends AbstractModel
case class Category(categoryname: String) extends AbstractModel

case class Skill(id: String, name :String, profency: Int) extends AbstractModel
case class ExternalSource(id: String, name: String, source: String) extends AbstractModel
case class Award(id :String, ranking :Int, participantCount: String) extends AbstractModel

case class Education(id: String, schoolName :String, yearStarted: Int, yearEnded: Int, completed: Boolean, level: Int, skillslearned: List[Skill], sourcesReferenced: List[ExternalSource], awardswon: List[Award] ) extends AbstractModel
case class Experience(id: String, companyName: String, yearStarted: Int, yearEnded: Int, title: String, present: Boolean,
                       skillsUsed: List[Skill], sourcesReferenced: List[ExternalSource], awardswon: List[Award]) extends AbstractModel


object Implicits {
  implicit val implicitsurveyWrites = new Writes[Survey] {
    def writes(bar: Survey): JsValue = {
      Json.parse(bar.toJson)
    }
  }
  implicit val implicitsurveyReads = new Reads[Survey] {
    def reads(json: JsValue): JsResult[Survey] = {
      try{
        JsSuccess(AbstractModel.fromJson(Json.stringify(json),classOf[Survey]).getOrElse(throw new IllegalArgumentException("unable to read Survey")))
      }catch {
        case e :Throwable => JsError()
      }
    }
  }

  implicit val implicitExperienceWrites = new Writes[Experience] {
    def writes(bar: Experience): JsValue = {
      Json.parse(bar.toJson)
    }
  }
  implicit val implicitExperienceReads = new Reads[Experience] {
    def reads(json: JsValue): JsResult[Experience] = {
      try{
        JsSuccess(AbstractModel.fromJson(Json.stringify(json),classOf[Experience]).getOrElse(throw new IllegalArgumentException("unable to read Experience")))
      }catch {
        case e :Throwable => JsError()
      }
    }
  }

  implicit val implicitEducationWrites = new Writes[Education] {
    def writes(bar: Education): JsValue = {
      Json.parse(bar.toJson)
    }
  }
  implicit val implicitducationReads = new Reads[Education] {
    def reads(json: JsValue): JsResult[Education] = {
      try{
        JsSuccess(AbstractModel.fromJson(Json.stringify(json),classOf[Education]).getOrElse(throw new IllegalArgumentException("unable to read Education")))
      }catch {
        case e :Throwable => JsError()
      }
    }
  }
}





