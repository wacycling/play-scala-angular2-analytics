package google

import com.google.api.services.analytics.model._
import com.google.api.services.analyticsreporting.v4.model._
import play.api.libs.json.{JsBoolean, JsString, Json, Writes}
import java.util

import scala.collection.JavaConverters._

trait AnalyticsFactory {
  type T
  type R
  def make(t: T): R
  def makeList(t: util.List[T]): List[R] = Option(t) match {
    case Some(list) => list.asScala.map(make(_)).toList
    case None => List()
  }
}

case class AnalyticsProfile(id: String, name: String)
object AnalyticsProfile extends AnalyticsFactory {
  implicit def jsonWrites = Json.writes[AnalyticsProfile]
  implicit def jsonReads = Json.reads[AnalyticsProfile]

  override type T = ProfileSummary
  override type R = AnalyticsProfile
  override def make(profile: ProfileSummary): AnalyticsProfile = {
    AnalyticsProfile(
      profile.getId,
      profile.getName
    )
  }
}

case class AnalyticsProperty(id: String, name: String, webSiteUrl: String)
object AnalyticsProperty extends AnalyticsFactory {
  implicit def jsonWrites = Json.writes[AnalyticsProperty]
  implicit def jsonReads = Json.reads[AnalyticsProperty]

  override type T = Webproperty
  override type R = AnalyticsProperty
  override def make(property: Webproperty) = {
    AnalyticsProperty(
      property.getId,
      property.getName,
      property.getWebsiteUrl
    )
  }
}

case class AnalyticsWebProperty(id: String, name: String, webSiteUrl: String, profiles: List[AnalyticsProfile])
object AnalyticsWebProperty extends AnalyticsFactory {
  implicit def jsonWrites = Json.writes[AnalyticsWebProperty]
  implicit def jsonReads = Json.reads[AnalyticsWebProperty]

  override type T = WebPropertySummary
  override type R = AnalyticsWebProperty
  override def make(property: WebPropertySummary): AnalyticsWebProperty = {
    AnalyticsWebProperty(
      property.getId,
      property.getName,
      property.getWebsiteUrl,
      AnalyticsProfile.makeList(property.getProfiles)
    )
  }
}

case class AnalyticsAccount(id: String, name: String, properties: List[AnalyticsWebProperty])
object AnalyticsAccount extends AnalyticsFactory {
  implicit def jsonWrites = Json.writes[AnalyticsAccount]
  implicit def jsonReads = Json.reads[AnalyticsAccount]

  override type T = AccountSummary
  override type R = AnalyticsAccount
  override def make(account: AccountSummary) = {
    AnalyticsAccount(
      account.getId,
      account.getName,
      AnalyticsWebProperty.makeList(account.getWebProperties)
    )
  }
}

case class AnalyticsDateRange(startDate: String, endDate: String) {
  val deteRange: DateRange = new DateRange().setStartDate(startDate).setEndDate(endDate)
}

case class AnalyticsOrderBy(field: String, sortOrder: String, orderType: String) {
  val orderBy: OrderBy = new OrderBy().setFieldName(field).setSortOrder(sortOrder).setOrderType(orderType)
}

case class AnalyticsReportRequest(dateRanges: List[AnalyticsDateRange], metrics: List[String], dimensions: List[String], orderBys: List[AnalyticsOrderBy] = List(), pageSize: Int = 0, filter: String = "") {
  def toGoogleRequest(viewId: String) = {
    val request = new ReportRequest()
      .setViewId(viewId)
      .setDateRanges(dateRanges.map(_.deteRange).asJava)
      .setMetrics(metrics.map(new Metric().setExpression(_)).asJava)
      .setDimensions(dimensions.map(new Dimension().setName(_)).asJava)
      .setOrderBys(orderBys.map(_.orderBy).asJava)

    if (pageSize > 0) request.setPageSize(pageSize)
    if (!filter.isEmpty) request.setFiltersExpression(filter)
    request
  }
}
case class AnalyticsRequest(token: String, viewId: String, requests: List[AnalyticsReportRequest]) {
  def toGoogleRequest() = {
    new GetReportsRequest()
      .setReportRequests(requests.map(request => request.toGoogleRequest(viewId)).asJava)
  }
}

case class AnalyticsReportRow(dimensions: List[String], metrics: List[Float])
object AnalyticsReportRow extends AnalyticsFactory {
  implicit def jsonWrites = Json.writes[AnalyticsReportRow]
  implicit def jsonReads = Json.reads[AnalyticsReportRow]

  override type T = ReportRow
  override type R = AnalyticsReportRow
  override def make(row: ReportRow) = {
    AnalyticsReportRow(
      row.getDimensions.asScala.map(_.toString).toList,
      row.getMetrics.asScala.flatMap(_.getValues.asScala.map(_.toFloat)).toList
    )
  }
}

case class AnalyticsReport(dimensions: List[String], metrics: List[String], rowCount: Int, totals: List[Float], rows: List[AnalyticsReportRow])
object AnalyticsReport extends AnalyticsFactory {
  implicit def jsonWrites = Json.writes[AnalyticsReport]
  implicit def jsonReads = Json.reads[AnalyticsReport]

  override type T = Report
  override type R = AnalyticsReport
  override def make(report: Report) = {
    AnalyticsReport(
      report.getColumnHeader.getDimensions.asScala.map(_.toString).toList,
      report.getColumnHeader.getMetricHeader.getMetricHeaderEntries.asScala.map(_.getName).toList,
      report.getData.getRowCount,
      report.getData.getTotals.asScala.flatMap(_.getValues.asScala.map(_.toFloat)).toList,
      AnalyticsReportRow.makeList(report.getData().getRows)
    )
  }
}