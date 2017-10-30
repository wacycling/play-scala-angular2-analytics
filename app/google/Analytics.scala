package google

import play.api.libs.json.Json

case class AnalyticsProfile(id: String, name: String)
object AnalyticsProfile {
  implicit def jsonWrites = Json.writes[AnalyticsProfile]
  implicit def jsonReads = Json.reads[AnalyticsProfile]
}

case class AnalyticsWebProperty(id: String, name: String, webSiteUrl: String, profiles: List[AnalyticsProfile] = List.apply())
object AnalyticsWebProperty {
  implicit def jsonWrites = Json.writes[AnalyticsWebProperty]
  implicit def jsonReads = Json.reads[AnalyticsWebProperty]
}

case class AnalyticsAccount(id: String, name: String, properties: List[AnalyticsWebProperty])
object AnalyticsAccount {
  implicit def jsonWrites = Json.writes[AnalyticsAccount]
  implicit def jsonReads = Json.reads[AnalyticsAccount]
}

case class AnalyticsReportRow(dimensions: List[String], metrics: List[Float])
object AnalyticsReportRow {
  implicit def jsonWrites = Json.writes[AnalyticsReportRow]
  implicit def jsonReads = Json.reads[AnalyticsReportRow]
}

case class AnalyticsReport(dimensions: List[String], metrics: List[String], rowCount: Int, totals: List[Float], rows: List[AnalyticsReportRow])
object AnalyticsReport {
  implicit def jsonWrites = Json.writes[AnalyticsReport]
  implicit def jsonReads = Json.reads[AnalyticsReport]
}

//case class AnalyticsToken(accessToken: String, refreshToken: String)
case class AnalyticsReportOrderBy(field: String, sortOrder: String, orderType: String)
case class AnalyticsReportRequest(metrics: List[String], dimensions: List[String], orderBys: List[AnalyticsReportOrderBy] = List.apply(), pageSize: Int = 0, filter: String = "")
case class AnalyticsRequest(token: String, viewId: String, startDate: String, endDate: String, requests: List[AnalyticsReportRequest])
