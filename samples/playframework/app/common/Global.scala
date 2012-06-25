package common;
import play.api.mvc.Results.InternalServerError
import play.api.mvc.Handler
import play.api.mvc.RequestHeader
import play.api.Application
import play.api.GlobalSettings

object Global extends GlobalSettings {
  override def onStart(app: Application) {
    play.Logger.info("Application has started")
  }

  override def onStop(app: Application) {
    play.Logger.info("Application shutdown...")
  }

  override def onError(request: RequestHeader, ex: Throwable) = {
    InternalServerError(
      "error")
  }

  override def onRouteRequest(request: RequestHeader): Option[Handler] = {
    play.Logger.info("executed before every request:" + request.toString)
    super.onRouteRequest(request)
  }

}

