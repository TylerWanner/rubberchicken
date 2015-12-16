package controllers

import models.dao.sapi.DumbUser
import org.apache.commons.lang3.StringUtils
import play.api.libs.ws.WS
import play.api.mvc._

object Application extends Controller {

  def index = Action {
    Ok(views.html.index())
  }

  def compare = Action {
    Ok(views.html.compare())
  }

  def review360 = Action {
    Ok(views.html.review360())
  }

  def evaluation = Action {
    Ok(views.html.evaluation())
  }

  def dashboard = Action.async {
    val access_token = request2session.get("access_token").getOrElse("")
    val userOption = WS.url(s"ENDPOINT/userprofile?access_token=${access_token}").get.map { response =>
      response.json.asOpt[DumbUser]
    }
    userOption.map{
      case Some(user) if(StringUtils.isNotBlank(user.username) ) =>
        Ok(views.html.dashboard(user.favorite))
      case _ =>
        BadRequest(views.html.login())
    }
  }
  def loginFromCAS = Action{
    Ok(services.CAS.authenticate);
  }

  trait authService {
    def authenticate(username:String, password:String)
  }

/**  def doLogin = Action { implicit request =>
      loginForm.fold(
        badForm =>{
          BadRequest(views.html.login())
        },
        credentials => {
          authService.authenticate(credentials.username, credentials.password).map {
              _ match{
                  case  Some(user) => {
                    Ok(controllers.Application.dashboard)
                  }
                  case None => {
                    BadRequest(views.html.login())
              }
            }
          }
        }
      )
      Ok
    }**/
    def renderLogin = Action {
      Ok(views.html.login())
    }

}