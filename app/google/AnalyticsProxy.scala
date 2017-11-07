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

class AnalyticsProxy (config: AnalyticsConfig) {
  private val jsonFactory = GsonFactory.getDefaultInstance
  private val httpTransport = GoogleNetHttpTransport.newTrustedTransport
  private val clientSecrets = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(new FileInputStream(config.clientSecretJson)))

  def getAuthorizationUrl():String = {
    getGoogleFlow().newAuthorizationUrl().setRedirectUri(config.redirectUrl).build()
  }

  def getAccessToken(code: String):String = {
    val response = getGoogleFlow().newTokenRequest(code).setRedirectUri(config.redirectUrl).execute
    response.getAccessToken
  }

  def getProperty(token: String, accountId: String, propertyId: String): AnalyticsProperty = {
    val analytics = new Analytics.Builder(httpTransport, jsonFactory, getGoogleCredential(token))
      .setApplicationName(config.applicationName)
      .build()

    val property = analytics.management().webproperties().get(accountId, propertyId).execute()
    AnalyticsProperty.make(property)
  }

  def getAccounts(token: String): List[AnalyticsAccount] = {
    val credential = getGoogleCredential(token)

    val analytics = new Analytics.Builder(httpTransport, jsonFactory, credential)
      .setApplicationName(config.applicationName)
      .build()

    val accountSummaries: AccountSummaries = analytics.management().accountSummaries().list().execute()
    AnalyticsAccount.makeList(accountSummaries.getItems)
  }

  def getReports(request: AnalyticsRequest) = {
    // Construct the AnalyticsReporting object.
    val analytics = new AnalyticsReporting.Builder(httpTransport, jsonFactory, getGoogleCredential(request.token))
      .setApplicationName(config.applicationName)
      .build

    // Create the GetReportsRequest object.
    val requests = request.toGoogleRequest()

    // Call the batchGet method.
    val response: GetReportsResponse = analytics.reports.batchGet(requests).execute
    response.getReports.asScala.map(AnalyticsReport.make(_))
  }

  private def getGoogleFlow():GoogleAuthorizationCodeFlow = {
    // Set up authorization code flow for all authorization scopes.
    new GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory, clientSecrets, AnalyticsReportingScopes.all())
      .setAccessType("offline")
      .build()
  }

  private def getGoogleCredential(token: String): GoogleCredential = {
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