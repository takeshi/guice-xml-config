package common
import java.lang.Object

import org.aopalliance.intercept.MethodInterceptor
import org.aopalliance.intercept.MethodInvocation
import org.lushlife.stla.Logging

class SampleInterceptor extends MethodInterceptor {
  def logger = Logging.getLog(classOf[SampleInterceptor]);

  def invoke(invocation: MethodInvocation): Object = {
    logger.log(LogMessage.IN, invocation.getMethod());
    val ret = invocation.proceed();
    logger.log(LogMessage.OUT, invocation.getMethod());

    ret;
  }
}