package controllers

import javax.inject._

import play.api.mvc._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class ApplicationController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
  /**
    *
    * @param any
    * @return
    */
  def index(any: String) = Action {
    Ok(views.html.index())
  }
}
