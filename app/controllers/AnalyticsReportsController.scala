package controllers

import javax.inject._

import play.api.libs.json.{JsObject, Json}
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import service.AnalyticsService

@Singleton
class AnalyticsReportsController @Inject()(service: AnalyticsService) extends InjectedController {

  /**
    *
    * @param viewId
    * @return
    */
  def overview(viewId: String) = Action.async { implicit request =>
    request.session.get("token").map { token =>
      service.overviews(token, viewId).map(results => {
        Ok(JsObject(Seq("data" -> Json.toJson(results))))
      })
    }.getOrElse {
      Future.successful(Unauthorized("Oops, you are not token"))
    }
  }

  /**
    *
    * @param viewId
    * @return
    */
  def audience(viewId: String) = Action.async { implicit request =>
    request.session.get("token").map { token =>
      service.audience(token, viewId).map(results => {
        Ok(JsObject(Seq("data" -> Json.toJson(results))))
      })
    }.getOrElse {
      Future.successful(Unauthorized("Oops, you are not token"))
    }
  }

  /**
    *
    * @param viewId
    * @return
    */
  def mobile(viewId: String) = Action.async { implicit request =>
    request.session.get("token").map { token =>
      service.mobile(token, viewId).map(results => {
        Ok(JsObject(Seq("data" -> Json.toJson(results))))
      })
    }.getOrElse {
      Future.successful(Unauthorized("Oops, you are not token"))
    }
  }

  /**
    *
    * @param viewId
    * @return
    */
  def traffic(viewId: String) = Action.async { implicit request =>
    request.session.get("token").map { token =>
      service.traffic(token, viewId).map(results => {
        Ok(JsObject(Seq("data" -> Json.toJson(results))))
      })
    }.getOrElse {
      Future.successful(Unauthorized("Oops, you are not token"))
    }
  }

  /**
    *
    * @param viewId
    * @return
    */
  def content(viewId: String) = Action.async { implicit request =>
    request.session.get("token").map { token =>
      service.content(token, viewId).map(results => {
        Ok(JsObject(Seq("data" -> Json.toJson(results))))
      })
    }.getOrElse {
      Future.successful(Unauthorized("Oops, you are not token"))
    }
  }

  /**
    *
    * @param viewId
    * @return
    */
  def conversions(viewId: String) = Action.async { implicit request =>
    request.session.get("token").map { token =>
      service.conversions(token, viewId).map(results => {
        Ok(JsObject(Seq("data" -> Json.toJson(results))))
      })
    }.getOrElse {
      Future.successful(Unauthorized("Oops, you are not token"))
    }
  }
}
