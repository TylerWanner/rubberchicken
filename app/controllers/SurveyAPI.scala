package controllers

import controllers.UserAPI._
import models.dao.sapi._
import play.api.Play
import play.api.Play.current
import play.api.libs.json._
import play.api.mvc._
import services.SurveyServices

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.collection.JavaConverters._
import models.dao.sapi.Implicits._

/**
 * Created by tashdid.khan on 7/10/2015.
 */
object SurveyAPI extends Controller {
  val serveyclient = SurveyReactiveImpl
  val userclient = UserDaoReactiveImpl

  val surveys = SurveyServices.getServey(Play.configuration)

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

  def addExperience(userId: String)= Action.async {
    implicit request =>
      request.contentType match {
        case Some("application/json") if request.body.asJson.isDefined =>
          val expr = request.body.asJson.get.asOpt[Experience]
          userclient.getUserById("",userId).flatMap {
            case Some(user) =>
             val modifiedUser = user.withExperience(expr)
              userclient.updateUser(modifiedUser).map{case error =>
                if(error.ok)Ok(Json.toJson(modifiedUser)).as("application/json")
                else InternalServerError(error.stringify)
              }
          }
      }
  }

  def addEducation(userId: String)= Action.async {
    implicit request =>
      request.contentType match {
        case Some("application/json") if request.body.asJson.isDefined =>
          val expr = request.body.asJson.get.asOpt[Education]
          userclient.getUserById("",userId).flatMap {
            case Some(user) =>
              val modifiedUser = user.withEducation(expr)
              userclient.updateUser(modifiedUser).map{case error =>
                if(error.ok)Ok(Json.toJson(modifiedUser)).as("application/json")
                else InternalServerError(error.stringify)
              }
          }
      }
  }
}