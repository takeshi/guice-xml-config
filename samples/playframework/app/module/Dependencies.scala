package module
import com.google.inject.AbstractModule
import common.Service
import com.google.inject.matcher.Matchers
import common.SampleInterceptor
import java.lang.annotation.Annotation
import org.lushlife.guicexml.XmlModule
import com.mongodb.casbah.MongoCollection
import datastore.MongoConnectionProvider

class Dependencies extends AbstractModule {

  def configure() = {
    install(new XmlModule("module/dependency.xml"));
  }

}