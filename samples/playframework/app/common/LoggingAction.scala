package common
import play.api.mvc.Request
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.Result

object LoggingAction {
  def apply(f: Request[AnyContent] => Result): Action[AnyContent] = {
    Action { request =>
      play.Logger.info("accept request " + request);
      val response = f(request)
      play.Logger.info("end request " + response);
      response;
    }
  }

}