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
package org.lushlife.guicexml.internal.xml;

import java.util.regex.Pattern;

import org.dom4j.Element;
import org.lushlife.guicexml.internal.GuiceBinder;
import org.lushlife.guicexml.internal.InterceptorMapping;
import org.lushlife.guicexml.internal.property.PropertyManagement;
import org.lushlife.guicexml.internal.property.PropertyValue;

/**
 * @author Takeshi Kondo
 */
public class InterceptorMappingReader {
	public static GuiceBinder create(Element element,
			PropertyManagement xmlManagement) {
		assert element.getName().equals("interceptor-mapping");

		PropertyValue[] interceptors = xmlManagement.toListPropertyValue(
				element.attributeValue("interceptors")).getValues();
		Pattern targetClass = (Pattern) xmlManagement.toPropertyValue(
				element.attributeValue("target-class")).resolveString(
				Pattern.class, null);
		Pattern excludeClass = (Pattern) xmlManagement.toPropertyValue(
				element.attributeValue("exclude-class")).resolveString(
				Pattern.class, null);
		Pattern targetMethod = (Pattern) xmlManagement.toPropertyValue(
				element.attributeValue("target-method")).resolveString(
				Pattern.class, null);
		Pattern excludeMethod = (Pattern) xmlManagement.toPropertyValue(
				element.attributeValue("exclude-method")).resolveString(
				Pattern.class, null);

		Element interceptorsElement = element.element("interceptors");

		if (interceptorsElement != null) {
			interceptors = xmlManagement.toListPropertyValue(
					interceptorsElement).getValues();
		}

		return new InterceptorMapping(interceptors, targetClass, excludeClass,
				targetMethod, excludeMethod);

	}

}
