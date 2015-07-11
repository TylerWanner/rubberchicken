package controllers

import models.dao.sapi.{AbstractModel, Survey, SurveyReactiveImpl, UserDaoReactiveImpl}
import play.api.Play
import play.api.Play.current
import play.api.libs.json._
import play.api.mvc._
import services.SurveyServices

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import models.dao.sapi.Implicits._

/**
 * Created by tashdid.khan on 7/10/2015.
 */
object ResultsAPI extends Controller {
  val serveyclient = SurveyReactiveImpl
  val userclient = UserDaoReactiveImpl

  val surveys = SurveyServices.getServey(Play.configuration)



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


  def getSurveyResults( userId: String) = Action.async {
    implicit request =>
      serveyclient.getSurveyByQueryString(Map[String,Seq[String]]("targetedUser.id" -> List(userId)) ).map {
        case survey =>
//         val result = survey.asInstanceOf[List[Survey]].map{ case s =>
//            s.lineItems.map{ case (k,v) =>
//              (k,v.map(_.score).reduce{case (x,y)=>x+y})
//            }
//          }
          Ok(Json.toJson(survey)).as("application/json")
      } recover {
        case _ => NotFound
      }

  }


  def list() = Action{
    Ok(Json.toJson(surveys)).as("application/json")
  }

  def getSurvey(id: String, userId: String) = Action.async {
    implicit request =>
      serveyclient.getSurveyById(id, userId).map {
        case Some(survey) =>
          Ok(Json.toJson(survey)).as("application/json")
        case _ => NotFound
      } recover {
        case _ => NotFound
      }

  }

  def query() = Action.async {
    implicit request =>
      serveyclient.getSurveyByQueryString(request.queryString).map {
        fileList =>
          Ok(Json.toJson(fileList)).as("application/json")
      }

  }

  def updateSurveyEntryForUser(userId: String, surveyId: String, lineItemId: String) = Action.async {
    implicit request =>
      request.contentType match {
        case Some("application/json") if request.body.asJson.isDefined =>
          val score = request.body.asJson.get.\("score").asOpt[Int].getOrElse(0)
          userclient.getUserById("",userId).flatMap {
            case Some(user) =>
              serveyclient.getSurveyById(surveyId,userId).flatMap{ case surveyOption =>
                val survey = surveyOption.getOrElse(surveys.find(_.id == surveyId).getOrElse(throw new IllegalArgumentException))
                  updateLineItemForSurvey(survey,lineItemId,score)
              serveyclient.updateSurvey(survey.copy(targetUser = user, sourceUser = user)).map{ error =>
                if(error.ok)Ok(Json.toJson(survey)).as("application/json")
                else InternalServerError(error.stringify)
              }
              }
            case _ => Future.successful(NotFound)
          } recover {
            case _ => NotFound
          }

        case _ => Future.successful(UnsupportedMediaType)
      }
  }

  def updateLineItemForSurvey(survey: Survey, lineItemId: String, score: Int): Survey ={
    val lineItemMap = survey.lineItems.find(_._2.exists(_.id == lineItemId))
    val lineItem = lineItemMap.getOrElse(throw new IllegalArgumentException)._2.find(_.id == lineItemId).getOrElse(throw new IllegalArgumentException)
    val lineItemMapList = lineItemMap.get._2.filterNot(_.id == lineItemId).::(lineItem.copy(score = score))
    survey.copy(lineItems = survey.lineItems.+(lineItemMap.get.copy(_2 = lineItemMapList)))
  }
}
