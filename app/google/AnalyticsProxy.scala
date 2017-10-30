package google

import java.io.{FileInputStream, InputStreamReader}

import scala.collection.JavaConverters._
import com.google.api.client.googleapis.auth.oauth2.{GoogleAuthorizationCodeFlow, GoogleClientSecrets, GoogleCredential}
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.analytics.Analytics
import com.google.api.services.analytics.model.AccountSummaries
import com.google.api.services.analyticsreporting.v4.{AnalyticsReporting, AnalyticsReportingScopes}
import com.google.api.services.analyticsreporting.v4.model._

class AnalyticsProxy(config: AnalyticsConfig) {

  private val jsonFactory = GsonFactory.getDefaultInstance
  private val httpTransport = GoogleNetHttpTransport.newTrustedTransport

  def getAuthorizationUrl():String = {
    getGoogleFlow().newAuthorizationUrl().setRedirectUri(config.redirectUrl).build()
  }

  def getAccessToken(code: String):String = {
    val response = getGoogleFlow().newTokenRequest(code).setRedirectUri(config.redirectUrl).execute
    response.getAccessToken
  }

  def property(token: String, accountId: String, propertyId: String) = {
    val credential = getGoogleCredential(token)

    // Construct the Analytics service object.
    val analytics = new Analytics.Builder(httpTransport, jsonFactory, credential)
      .setApplicationName(config.applicationName)
      .build()

    val property = analytics.management().webproperties().get(accountId, propertyId).execute()
    AnalyticsWebProperty(
      property.getId,
      property.getName,
      property.getWebsiteUrl
    )
  }

  def accounts(token: String) = {
    val credential = getGoogleCredential(token)

    // Construct the Analytics service object.
    val analytics = new Analytics.Builder(httpTransport, jsonFactory, credential)
      .setApplicationName(config.applicationName)
      .build()

    val accountSummaries: AccountSummaries = analytics.management().accountSummaries().list().execute()
    accountSummaries.getItems().asScala.map(item => {
      AnalyticsAccount(
        item.getId,
        item.getName,
        item.getWebProperties.asScala.map(property => {
          AnalyticsWebProperty(
            property.getId,
            property.getName,
            property.getWebsiteUrl,
            property.getProfiles.asScala.map(profile => {
              AnalyticsProfile(
                profile.getId,
                profile.getName
              )
            }).toList
          )
        }).toList
      )
    })
  }

  def reports(request: AnalyticsRequest) = {
    val credential = getGoogleCredential(request.token)

    // Construct the AnalyticsReporting object.
    val analytics = new AnalyticsReporting.Builder(httpTransport, jsonFactory, credential)
      .setApplicationName(config.applicationName)
      .build

    val dateRange = new DateRange().setStartDate(request.startDate).setEndDate(request.endDate)

    // Create the GetReportsRequest object.
    val getReports = new GetReportsRequest().setReportRequests(
      request.requests.map(req => {
        val metrics = req.metrics.map(v => new Metric().setExpression(v)).asJava
        val dimensions = req.dimensions.map(v => new Dimension().setName(v)).asJava
        val orderBys = req.orderBys.map(v => {
          new OrderBy().setFieldName(v.field).setSortOrder(v.sortOrder).setOrderType(v.orderType)
        }).asJava
        val reportRequest = new ReportRequest()
          .setViewId(request.viewId)
          .setDateRanges(java.util.Arrays.asList(dateRange))
          .setMetrics(metrics)
          .setDimensions(dimensions)
          .setOrderBys(orderBys)

        if (req.pageSize > 0) reportRequest.setPageSize(req.pageSize)
        if (!req.filter.isEmpty) reportRequest.setFiltersExpression(req.filter)

        reportRequest
      }).asJava
    )

    // Call the batchGet method.
    val response: GetReportsResponse = analytics.reports.batchGet(getReports).execute
    response.getReports.asScala.map(report => {
      AnalyticsReport(
        report.getColumnHeader.getDimensions.asScala.map(_.toString).toList,
        report.getColumnHeader.getMetricHeader.getMetricHeaderEntries.asScala.map(e => e.getName).toList,
        report.getData.getRowCount,
        report.getData.getTotals.asScala.flatten(t => t.getValues.asScala.map(_.toFloat)).toList,
        Option(report.getData.getRows) match {
          case Some(rows) => rows.asScala.map(row => {
            AnalyticsReportRow(
              row.getDimensions.asScala.map(_.toString).toList,
              row.getMetrics.asScala.flatten(m => m.getValues.asScala.map(_.toFloat)).toList
            )
          }).toList
          case _ => List.apply()
        }
      )
    })
  }

  private def getGoogleFlow():GoogleAuthorizationCodeFlow = {
    // Load client secrets.
    val clientSecrets = getGoogleClientSecrets()

    // Set up authorization code flow for all authorization scopes.
    new GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory, clientSecrets, AnalyticsReportingScopes.all())
      .setAccessType("offline")
      .build()
  }

  private def getGoogleClientSecrets():GoogleClientSecrets = {
    GoogleClientSecrets.load(jsonFactory, new InputStreamReader(new FileInputStream(config.clientSecretJson)))
  }

  private def getGoogleCredential(token: String): GoogleCredential = {
    val clientSecrets = getGoogleClientSecrets()
    val credential = new GoogleCredential.Builder()
      .setClientSecrets(clientSecrets.getDetails.getClientId, clientSecrets.getDetails.getClientSecret)
      .setJsonFactory(jsonFactory)
      .setTransport(httpTransport)
      .build

    credential.setAccessToken(token)
//    credential.setRefreshToken(token)
//    credential.refreshToken
    credential
  }

}

