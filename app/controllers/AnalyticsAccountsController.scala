package controllers

import javax.inject._

import play.api.libs.json._
import play.api.mvc._
import service.AnalyticsService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class AnalyticsAccountsController @Inject()(service: AnalyticsService) extends InjectedController {

  /**
    *
    * @return
    */
  def index = Action.async { implicit request =>
    request.session.get("token").map { token =>
      service.accounts(token).map(results => Ok(Json.toJson(results)))
    }.getOrElse {
      Future.successful(Unauthorized("Oops, you are not token"))
    }
  }

  def property(accountId:String, profileId: String) = Action.async { implicit request =>
    request.session.get("token").map { token =>
      service.property(token, accountId, profileId).map(results => Ok(Json.toJson(results)))
    }.getOrElse {
      Future.successful(Unauthorized("Oops, you are not token"))
    }
  }

}
