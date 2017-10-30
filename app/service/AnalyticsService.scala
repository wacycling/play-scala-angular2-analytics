package service

import javax.inject._

import play.api._
import google._

import scala.concurrent.Future

@Singleton
class AnalyticsService @Inject() (configuration: Configuration) {

  private val analytics = new AnalyticsProxy(configuration.get[AnalyticsConfig]("analytics.config"))

  def getAuthorizationUrl:String = {
    analytics.getAuthorizationUrl()
  }

  def getAccessToken(code: String): String = {
    analytics.getAccessToken(code)
  }

  def property(token: String, accountId:String, profileId: String): Future[AnalyticsWebProperty] = {
    Future.successful(analytics.property(token, accountId, profileId))
  }

  def accounts(token: String): Future[List[AnalyticsAccount]] = {
    Future.successful(analytics.accounts(token).toList)
  }

  def reports(request: AnalyticsRequest): Future[List[AnalyticsReport]] = {
    Future.successful(analytics.reports(request).toList)
  }

}
