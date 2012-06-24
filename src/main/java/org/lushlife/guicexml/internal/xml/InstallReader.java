package org.lushlife.guicexml.internal.xml;

import java.io.File;
import java.net.URL;

import org.dom4j.Element;
import org.lushlife.guicexml.XmlModule;
import org.lushlife.guicexml.internal.GuiceBinder;
import org.lushlife.guicexml.internal.Install;
import org.lushlife.guicexml.internal.property.PropertyManagement;

import com.google.inject.Module;

public class InstallReader {

	public static GuiceBinder create(Element element,
			PropertyManagement propertyManagement) {
		try {
			String file = element.attributeValue("file");
			if (file != null) {
				return new Install(new XmlModule(new File(file)));
			}

			String url = element.attributeValue("url");
			if (url != null) {
				return new Install(new XmlModule(new URL(url)));
			}

			String classPath = element.attributeValue("class-path");
			if (classPath != null) {
				return new Install(new XmlModule(classPath));
			}
			String clazz = element.attributeValue("module-class");
			if (clazz != null) {
				return new Install((Module) propertyManagement.toClass(clazz)
						.newInstance());
			}
			throw new IllegalArgumentException("illegal xml " + element.asXML());
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}

	}

}
