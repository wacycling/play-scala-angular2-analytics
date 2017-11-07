package service

import java.time.LocalDateTime
import javax.inject._

import play.api._
import google._

import scala.concurrent.Future


@Singleton
class AnalyticsService @Inject() (configuration: Configuration) {
  private val proxy = new AnalyticsProxy(configuration.get[AnalyticsConfig]("analytics.config"))
  private val date = LocalDateTime.now
  private val dateRange = AnalyticsDateRange(
    "%tF" format date.withDayOfMonth(1).minusYears(1),
    "%tF" format date.withDayOfMonth(1).minusDays(1)
  )

  def getAuthorizationUrl:String = {
    proxy.getAuthorizationUrl()
  }

  def getAccessToken(code: String): String = {
    proxy.getAccessToken(code)
  }

  def property(token: String, accountId:String, profileId: String): Future[AnalyticsProperty] = {
    Future.successful(proxy.getProperty(token, accountId, profileId))
  }

  def accounts(token: String): Future[List[AnalyticsAccount]] = {
    Future.successful(proxy.getAccounts(token))
  }

  def overviews(token: String, viewId: String) = {
    val results = proxy.getReports(
      AnalyticsRequest(token, viewId,
        List(
          AnalyticsReportRequest(
            List(dateRange),
            List("ga:sessions", "ga:users", "ga:newUsers", "ga:pageviews", "ga:bounceRate", "ga:goalCompletionsAll"),
            List("ga:yearMonth")
          )
        )
      )
    )
    Future.successful(results)
  }

  def audience(token: String, viewId: String) = {
    val results = proxy.getReports(
      AnalyticsRequest(token, viewId,
        List(
          AnalyticsReportRequest(
            List(dateRange),
            List("ga:sessions"),
            List("ga:userAgeBracket")
          ),
          AnalyticsReportRequest(
            List(dateRange),
            List("ga:sessions"),
            List("ga:userGender")
          )
        )
      )
    )
    Future.successful(results)
  }

  def mobile(token: String, viewId: String) = {
    val results = proxy.getReports(
      AnalyticsRequest(token, viewId,
        List(
          AnalyticsReportRequest(
            List(dateRange),
            List("ga:sessions"),
            List("ga:deviceCategory")
          ),
          AnalyticsReportRequest(
            List(dateRange),
            List("ga:sessions"),
            List("ga:mobileDeviceInfo"),
            List(AnalyticsOrderBy("ga:sessions", "DESCENDING", "VALUE")),
            10
          )
        )
      )
    )
    Future.successful(results)
  }

  def traffic(token: String, viewId: String) = {
    val results = proxy.getReports(
      AnalyticsRequest(token, viewId,
        List(
          AnalyticsReportRequest(
            List(dateRange),
            List("ga:sessions"),
            List("ga:channelGrouping")
          ),
          AnalyticsReportRequest(
            List(dateRange),
            List("ga:sessions"),
            List("ga:source"),
            List(AnalyticsOrderBy("ga:sessions", "DESCENDING", "VALUE")),
            10
          )
        )
      )
    )
    Future.successful(results)
  }

  def content(token: String, viewId: String) = {
    val results = proxy.getReports(
      AnalyticsRequest(token, viewId,
        List(
          AnalyticsReportRequest(
            List(dateRange),
            List("ga:sessions", "ga:goalCompletionsAll", "ga:goalConversionRateAll"),
            List("ga:landingPagePath"),
            List(AnalyticsOrderBy("ga:goalCompletionsAll", "DESCENDING", "VALUE")),
            10,
            "ga:goalCompletionsAll>0"
          )
        )
      )
    )
    Future.successful(results)
  }

  def conversions(token: String, viewId: String) = {
    val results = proxy.getReports(
      AnalyticsRequest(token, viewId,
        List(
          AnalyticsReportRequest(
            List(dateRange),
            List("ga:sessions", "ga:goalCompletionsAll", "ga:goalConversionRateAll", "ga:goalValueAll"),
            List("ga:yearMonth")
          ),
          AnalyticsReportRequest(
            List(dateRange),
            List("ga:goalCompletionsAll", "ga:goalValueAll"),
            List("ga:goalCompletionLocation"),
            List(AnalyticsOrderBy("ga:goalCompletionsAll", "DESCENDING", "VALUE"))
          )
        )
      )
    )
    Future.successful(results)
  }
}
