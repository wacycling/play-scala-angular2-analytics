package controllers

import java.time.LocalDateTime
import javax.inject._

import google.{AnalyticsReportOrderBy, AnalyticsReportRequest, AnalyticsRequest}
import play.api.libs.json.{JsObject, Json}
import play.api.mvc._
import service.AnalyticsService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class AnalyticsReportsController @Inject()(service: AnalyticsService) extends InjectedController {

  private val date = LocalDateTime.now
  private val startDate = "%tF" format date.withDayOfMonth(1).minusYears(1)
  private val endDate = "%tF" format date.withDayOfMonth(1).minusDays(1)

  /**
    *
    * @param viewId
    * @return
    */
  def overview(viewId: String) = Action.async { implicit request =>
    request.session.get("token").map { token =>
      val req = AnalyticsRequest(token, viewId, startDate, endDate,
        List(
          AnalyticsReportRequest(
            List("ga:sessions", "ga:users", "ga:newUsers", "ga:pageviews", "ga:bounceRate", "ga:goalCompletionsAll"),
            List("ga:yearMonth")
          )
        )
      )
      service.reports(req).map(results => {
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
      val req = AnalyticsRequest(token, viewId, startDate, endDate,
        List(
          AnalyticsReportRequest(List("ga:sessions"), List("ga:userAgeBracket")),
          AnalyticsReportRequest(List("ga:sessions"), List("ga:userGender"))
        )
      )
      service.reports(req).map(results => {
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
      val req = AnalyticsRequest(token, viewId, startDate, endDate,
        List(
          AnalyticsReportRequest(List("ga:sessions"), List("ga:deviceCategory")),
          AnalyticsReportRequest(
            List("ga:sessions"),
            List("ga:mobileDeviceInfo"),
            List(AnalyticsReportOrderBy("ga:sessions", "DESCENDING", "VALUE")),
            10
          )
        )
      )
      service.reports(req).map(results => {
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
      val req = AnalyticsRequest(token, viewId, startDate, endDate,
        List(
          AnalyticsReportRequest(List("ga:sessions"), List("ga:channelGrouping")),
          AnalyticsReportRequest(
            List("ga:sessions"),
            List("ga:source"),
            List(AnalyticsReportOrderBy("ga:sessions", "DESCENDING", "VALUE")),
            10
          )
        )
      )
      service.reports(req).map(results => {
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
      val req = AnalyticsRequest(token, viewId, startDate, endDate,
        List(
          AnalyticsReportRequest(
            List("ga:sessions", "ga:goalCompletionsAll", "ga:goalConversionRateAll"),
            List("ga:landingPagePath"),
            List(AnalyticsReportOrderBy("ga:goalCompletionsAll", "DESCENDING", "VALUE")),
            10,
            "ga:goalCompletionsAll>0"
          )
        )
      )
      service.reports(req).map(results => {
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
      val req = AnalyticsRequest(token, viewId, startDate, endDate,
        List(
          AnalyticsReportRequest(
            List("ga:sessions", "ga:goalCompletionsAll", "ga:goalConversionRateAll", "ga:goalValueAll"),
            List("ga:yearMonth")
          ),
          AnalyticsReportRequest(
            List("ga:goalCompletionsAll", "ga:goalValueAll"),
            List("ga:goalCompletionLocation"),
            List(AnalyticsReportOrderBy("ga:goalCompletionsAll", "DESCENDING", "VALUE"))
          )
        )
      )
      service.reports(req).map(results => {
        Ok(JsObject(Seq("data" -> Json.toJson(results))))
      })
    }.getOrElse {
      Future.successful(Unauthorized("Oops, you are not token"))
    }
  }
}
