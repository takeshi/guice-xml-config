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

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import org.dom4j.Element;
import org.lushlife.guicexml.internal.Factory;
import org.lushlife.guicexml.internal.GuiceBinder;
import org.lushlife.guicexml.internal.property.PropertyManagement;
import org.lushlife.guicexml.internal.property.PropertyValue;

/**
 * @author Takeshi Kondo
 */
public class FactoryXmlReader {

	@SuppressWarnings("unchecked")
	public static GuiceBinder create(Element element,
			PropertyManagement xmlManagement) {
		assert element.getName().equals("factory");

		Type type = xmlManagement.toType(element.attributeValue("type"));
		Annotation qualifier = xmlManagement.toQualifier(element);
		Class<? extends Annotation> scope = xmlManagement.getScope(element
				.attributeValue("scope"));
		boolean startup = Boolean.valueOf(element.attributeValue("startup"));
		PropertyValue value = xmlManagement.toPropertyValue(element, "value");
		if (value != null) {
			return new Factory(type, qualifier, value, scope, startup);
		}
		throw new IllegalArgumentException("illegal argumetns "
				+ element.asXML());
	}

}
