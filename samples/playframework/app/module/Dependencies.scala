package module
import org.lushlife.guicexml.XmlModule
import com.google.inject.AbstractModule
import common.TraceInterceptor

class Dependencies extends AbstractModule {

  def configure() = {
    install(new XmlModule("module/dependency.xml"));
  }

}