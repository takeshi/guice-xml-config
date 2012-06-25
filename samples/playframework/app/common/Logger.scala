package common
import org.lushlife.stla.Log
import org.lushlife.stla.Logging

object Logger {

  def apply[Y <: AnyRef](implicit m: Manifest[Y]): Log = Logging.getLog(m.erasure.getName)

}