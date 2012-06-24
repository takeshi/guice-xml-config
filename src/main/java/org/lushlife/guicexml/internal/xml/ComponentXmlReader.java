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
import java.util.HashMap;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.lushlife.guicexml.internal.Component;
import org.lushlife.guicexml.internal.GuiceBinder;
import org.lushlife.guicexml.internal.property.PropertyManagement;
import org.lushlife.guicexml.internal.property.PropertyValue;
import org.lushlife.guicexml.internal.util.NamedLiteral;

/**
 * @author Takeshi Kondo
 */
@SuppressWarnings("unchecked")
public class ComponentXmlReader {

	public static GuiceBinder create(Element element,
			PropertyManagement xmlManagement) {
		assert element.getName().equals("component");
		Class<?> clazz = xmlManagement.toClass(element.attributeValue("class"));
		return create(element, xmlManagement, clazz);
	}

	private static Component<?> create(Element element,
			PropertyManagement xmlManagement, Class<?> clazz) {
		Annotation qualifier = xmlManagement.toQualifier(element);
		boolean startup = Boolean.valueOf(element.attributeValue("startup"));

		Class<? extends Annotation> scope = xmlManagement.getScope(element
				.attributeValue("scope"));

		Type types = xmlManagement.toType(element.attributeValue("type"));
		Map<String, PropertyValue> attribute = createAttributeMap(element,
				xmlManagement);
		if (types == null) {
			types = clazz;
		}
		return new Component(types, clazz, qualifier, scope, startup, attribute);
	}

	public static Map<String, PropertyValue> createAttributeMap(
			Element element, PropertyManagement xmlManagement) {
		Map<String, PropertyValue> attribute = new HashMap<String, PropertyValue>();

		for (Object obj : element.attributes()) {
			Attribute attr = (Attribute) obj;
			if (attr.getName().equals("name")) {
				continue;
			}
			if (attr.getName().equals("id")) {
				continue;
			}
			if (attr.getName().equals("qualifier")) {
				continue;
			}
			if (attr.getName().equals("class")) {
				continue;
			}
			if (attr.getName().equals("types")) {
				continue;
			}
			if (attr.getName().equals("scope")) {
				continue;
			}
			if (attr.getName().equals("startup")) {
				continue;
			}
			if (attr.getName().equals("constructor-parameter-types")) {
				continue;
			}
			if (attr.getName().equals("factory-method")) {
				continue;
			}
			if (attr.getName().equals("args")) {
				continue;
			}
			attribute.put(attr.getName(), xmlManagement.toPropertyValue(attr
					.getValue()));
		}
		for (Object obj : element.elements()) {
			Element ele = (Element) obj;
			if (ele.getName().equals("property")) {
				String stringValue = ele.attributeValue("value");
				String k = ele.attributeValue("name");
				PropertyValue v = (stringValue != null) ? xmlManagement
						.toPropertyValue(stringValue) : xmlManagement
						.toPropertyValue(ele);
				attribute.put(k, v);
			} else {
				String k = ele.getName();
				PropertyValue v = xmlManagement.toPropertyValue(ele);
				attribute.put(k, v);
			}
		}
		return attribute;
	}

	public static GuiceBinder create(String packageName, String className,
			Element element, PropertyManagement xmlManagement) {
		Class<?> clazz = xmlManagement.toClass(packageName, className);
		return create(element, xmlManagement, clazz);
	}

	public static GuiceBinder create(Namespace namespace, String className,
			Element element, PropertyManagement xmlManagement) {
		Class<?> clazz = xmlManagement.toClass(namespace, className);
		return create(element, xmlManagement, clazz);
	}

}
