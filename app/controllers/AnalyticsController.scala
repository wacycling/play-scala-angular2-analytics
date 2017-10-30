package controllers

import javax.inject._

import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import service.AnalyticsService

@Singleton
class AnalyticsController @Inject()(cc: ControllerComponents, service: AnalyticsService) extends AbstractController(cc) {

  /**
    *
    * @return
    */
  def index = Action {
    Redirect(service.getAuthorizationUrl)
  }

  /**
    *
    * @return
    */
  def auth = Action { implicit request =>
    request.getQueryString("code").map {code =>
      val token = service.getAccessToken(code)
      Redirect("/analytics/accounts").withSession("token" -> token)
    }.getOrElse {
      Redirect("/")
    }
  }

}
