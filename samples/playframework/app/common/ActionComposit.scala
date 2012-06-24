package module
import play.api.mvc.Request
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.Result
import play.Logger

object ActionComposit {
  def apply(f: Request[AnyContent] => Result): Action[AnyContent] = {
    Action { request =>
      Logger.info("accept request " + request);
      val response = f(request)
      Logger.info("end request " + response);
      response;
    }
  }

}