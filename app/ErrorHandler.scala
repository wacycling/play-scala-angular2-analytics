import play.api.http.HttpErrorHandler
import play.api.mvc._
import play.api.mvc.Results.{InternalServerError, _}

import scala.concurrent._
import javax.inject.Singleton

import com.google.api.client.googleapis.json.{GoogleJsonError, GoogleJsonResponseException}
import play.api.libs.json.{JsString, Json};

@Singleton
class ErrorHandler extends HttpErrorHandler {

  def onClientError(request: RequestHeader, statusCode: Int, message: String) = {
    Future.successful(
      Status(statusCode)("A client error occurred: " + message)
    )
  }

  def onServerError(request: RequestHeader, exception: Throwable) = exception match {
    case exception: GoogleJsonResponseException => {
      Future.successful(Unauthorized(
        Json.obj(
          "code" -> Option(exception.getStatusCode).map(_.toString),
          "message" -> Option(exception.getDetails).map(_.getMessage)
        )
      ))
    }
    case _ => Future.successful(InternalServerError("A server error occurred: " + exception.getMessage))
  }
}