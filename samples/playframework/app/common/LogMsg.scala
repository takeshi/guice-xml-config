package common
import org.lushlife.stla.Info

object LogMsg extends Enumeration {
  @Info("in $0")
  val IN = Value;

  @Info("out $0")
  val OUT = Value;

}