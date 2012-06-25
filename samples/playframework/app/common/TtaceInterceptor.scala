package common
import java.lang.Object

import org.aopalliance.intercept.MethodInterceptor
import org.aopalliance.intercept.MethodInvocation

class TraceInterceptor extends MethodInterceptor {
  def logger = Logger[TraceInterceptor];

  def invoke(invocation: MethodInvocation): Object = {
    logger.log(LogMsg.IN, invocation.getMethod());
    val ret = invocation.proceed();
    logger.log(LogMsg.OUT, invocation.getMethod());

    ret;
  }
}