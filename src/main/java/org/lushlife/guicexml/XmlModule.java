/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.lushlife.guicexml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.dom4j.Element;
import org.dom4j.Namespace;
import org.lushlife.guicexml.internal.GuiceXmlLogMessage;
import org.lushlife.guicexml.internal.property.PropertyManagement;
import org.lushlife.guicexml.internal.util.Preconditions;
import org.lushlife.guicexml.internal.xml.ComponentXmlReader;
import org.lushlife.guicexml.internal.xml.CreatorXmlReader;
import org.lushlife.guicexml.internal.xml.FactoryXmlReader;
import org.lushlife.guicexml.internal.xml.InstallReader;
import org.lushlife.guicexml.internal.xml.InterceptorMappingReader;
import org.lushlife.guicexml.internal.xml.XML;
import org.lushlife.stla.Log;
import org.lushlife.stla.Logging;

import com.google.inject.AbstractModule;

/**
 * @author Takeshi Kondo
 */
public class XmlModule extends AbstractModule {
	static private Log log = Logging.getLog(XmlModule.class);
	protected URL url;
	protected PropertyManagement propertyManagement;

	protected XmlModule(URL url, PropertyManagement propertyManagement) {
		this.url = url;
		this.propertyManagement = propertyManagement;
	}

	public String toString() {
		return "XmlModule(url='" + url + "')";
	}

	public XmlModule(URL url) {
		this(Preconditions.checkNotNull(url, "url"), new PropertyManagement());
	}

	public XmlModule(File file) throws MalformedURLException {
		this(Preconditions.checkNotNull(file, "file").toURI().toURL(),
				new PropertyManagement());
	}

	public XmlModule(String classPath) {
		this(
				Thread.currentThread()
						.getContextClassLoader()
						.getResource(
								Preconditions.checkNotNull(classPath,
										"classPath")));
	}

	@Override
	protected void configure() {
		InputStream stream = null;
		try {
			log.log(GuiceXmlLogMessage.READ_XML_FILE, url);
			if (url == null) {
				return;
			}
			stream = url.openStream();
			if (stream == null) {
				return;
			}
			Element rootElement = XML.getRootElement(stream);
			for (Object obj : rootElement.elements()) {
				Element element = (Element) obj;

				if (Boolean.valueOf(element.attributeValue("disabled"))) {
					log.log(GuiceXmlLogMessage.DISABLED, element.asXML());
					configure();
				}

				Namespace namespace = element.getNamespace();
				// no namespace
				if (namespace.getText().isEmpty()) {
					configureDefaultNameSpace(element);
					continue;
				}
				// default namespace
				if ("http://code.google.com/p/guice-xml".equals(namespace
						.getStringValue())) {
					configureDefaultNameSpace(element);
					continue;
				}
				// urn import
				if (namespace.getText().startsWith("urn:import:")) {
					String packageName = namespace.getText().substring(
							"urn:import:".length());
					String className = element.getName();
					ComponentXmlReader.create(packageName, className, element,
							propertyManagement).bind(this.binder());
					continue;
				}
				// NameSpaceBinding
				ComponentXmlReader.create(namespace, element.getName(),
						element, propertyManagement).bind(this.binder());

			}
		} catch (Exception e) {
			addError(e);
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
				}
			}
		}
	}

	private void configureDefaultNameSpace(Element element) {
		if (element.getName().equals("component")) {
			ComponentXmlReader.create(element, propertyManagement).bind(
					this.binder());
		}
		if (element.getName().equals("bean")) {
			ComponentXmlReader.create(element, propertyManagement).bind(
					this.binder());
		}
		if (element.getName().equals("factory")) {
			FactoryXmlReader.create(element, propertyManagement).bind(
					this.binder());
		}
		if (element.getName().equals("creator")) {
			CreatorXmlReader.create(element, propertyManagement).bind(
					this.binder());
		}
		if (element.getName().equals("interceptor-mapping")) {
			InterceptorMappingReader.create(element, propertyManagement).bind(
					this.binder());
		}
		if (element.getName().equals("install")) {
			InstallReader.create(element, propertyManagement).bind(
					this.binder());
		}
	}
}
