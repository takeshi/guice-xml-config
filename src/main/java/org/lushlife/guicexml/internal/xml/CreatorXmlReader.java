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
import java.util.Map;

import org.dom4j.Element;
import org.lushlife.guicexml.internal.GuiceBinder;
import org.lushlife.guicexml.internal.property.PropertyManagement;
import org.lushlife.guicexml.internal.property.PropertyValue;
import org.lushlife.guicexml.internal.util.Producer;

/**
 * @author Takeshi Kondo
 */
public class CreatorXmlReader {

	@SuppressWarnings("unchecked")
	public static GuiceBinder create(Element element,
			PropertyManagement xmlManagement) {

		Type type = xmlManagement.toType(element.attributeValue("type"));
		Annotation qualifier = xmlManagement.toQualifier(element);
		Class<? extends Annotation> scope = xmlManagement.getScope(element
				.attributeValue("scope"));
		boolean startup = Boolean.valueOf(element.attributeValue("startup"));

		Producer<?> creator = null;
		String clazz = element.attributeValue("class");
		String factoryMethod = element.attributeValue("factory-method");
		if (factoryMethod != null) {
			creator = xmlManagement.toMethod(clazz, factoryMethod);
		}
		String constructor = element.attributeValue("constructor-parameter-types");
		if (constructor != null) {
			creator = xmlManagement.toConstructor(clazz, constructor);
		}

		if (creator == null) {
			throw new IllegalArgumentException("illegal argumetns "
					+ element.asXML());
		}
		if(type == null){
			type = creator.getComponentType();
		}

		PropertyValue[] args = xmlManagement.toListPropertyValue(
				element.attributeValue("args")).getValues();

		Map<String, PropertyValue> attribute = ComponentXmlReader
				.createAttributeMap(element, xmlManagement);

		return new org.lushlife.guicexml.internal.Creator(type, qualifier, creator,
				args, scope, startup, attribute);
	}
}
