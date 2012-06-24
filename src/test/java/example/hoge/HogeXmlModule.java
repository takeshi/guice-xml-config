package example.hoge;

import org.lushlife.guicexml.XmlModule;

import com.google.inject.AbstractModule;

public class HogeXmlModule extends AbstractModule {

	@Override
	protected void configure() {
		/**
		 * load xml configuration from ClassPath.
		 */
		install(new XmlModule("Hoge.xml"));
	}

}
