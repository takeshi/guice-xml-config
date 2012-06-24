package controllers

import play.api._
import play.api.mvc._
import javax.inject.Inject
import service.FirstService
import service.FirstService
import module.ActionComposit

object Application extends Controller {

  @Inject
  var service: FirstService =_

  def index = ActionComposit {
    request =>
      service.invoke
      Ok(views.html.index("Your new application is ready."))
  }
}