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
package org.lushlife.guicexml.internal.property;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ServiceLoader;

import javax.el.ValueExpression;

import org.dom4j.Element;
import org.dom4j.Namespace;
import org.lushlife.guicexml.internal.el.Expressions;
import org.lushlife.guicexml.internal.util.NamedLiteral;
import org.lushlife.guicexml.internal.util.ParameterizedTypeImpl;
import org.lushlife.guicexml.internal.util.Producer;
import org.lushlife.guicexml.spi.NameSpaceBinding;
import org.lushlife.guicexml.spi.ScopeBinding;

/**
 * @author Takeshi Kondo
 */
public class PropertyManagement {

	private Map<String, Class<? extends Annotation>> scopes = new HashMap<String, Class<? extends Annotation>>();
	private Map<String, String> namespace = new HashMap<String, String>();

	public PropertyManagement() {
		initialize();
	}

	protected void initialize() {
		initializeScope();
		initializeNamepase();

	}

	private void initializeNamepase() {
		// load from property file.
		bindNamespace(new NameSpaceBinding() {

			@Override
			protected void configure() {
				Properties properties = loadProperties("META-INF/namespace.properties");
				for (Object key : properties.keySet()) {
					namespace((String) key).toPackage(
							(String) properties.getProperty((String) key));
				}
			}
		});
		// load from extensions.
		ServiceLoader<NameSpaceBinding> ns = ServiceLoader
				.load(NameSpaceBinding.class);
		for (NameSpaceBinding binding : ns) {
			bindNamespace(binding);
		}
	}

	private void initializeScope() {
		// load from property file.
		bindScope(new ScopeBinding() {
			@Override
			protected void configuire() {
				Properties properties = loadProperties("META-INF/scopes.properties");
				for (Object key : properties.keySet()) {
					bindScope((Class) toClass((String) properties.get(key)))
							.toName((String) key);
				}
			}

		});
		// load services loader.
		ServiceLoader<ScopeBinding> loadScopes = ServiceLoader
				.load(ScopeBinding.class);
		for (ScopeBinding binding : loadScopes) {
			bindScope(binding);
		}
	}

	private Properties loadProperties(String classPath) {
		Properties properties = new Properties();
		try {
			Enumeration<URL> resources = Thread.currentThread()
					.getContextClassLoader().getResources(classPath);
			while (resources.hasMoreElements()) {
				URL url = resources.nextElement();
				properties.load(url.openStream());
			}
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		return properties;
	}

	protected void bindNamespace(NameSpaceBinding binding) {
		binding.configure(namespace);
	}

	protected void bindScope(ScopeBinding scopeBinding) {
		scopeBinding.configure(scopes);
	}

	@SuppressWarnings("unchecked")
	public Class<? extends Annotation> getScope(String name) {
		if (name == null) {
			return null;
		}
		if (name.startsWith("@")) {
			return (Class) toClass(name.substring(1));
		}
		return scopes.get(name);
	}

	public Type[] toTypes(String type) {
		if (type == null) {
			return null;
		}
		String[] values = type.split(",");
		Type[] types = new Type[values.length];
		for (int i = 0; i < types.length; i++) {
			types[i] = toType(values[i]);
		}
		return types;
	}

	public Type toType(String type) {
		if (type == null) {
			return null;
		}
		if (type.contains("<")) {
			return parseGenericType(type);
		}
		if (type.contains("[")) {
			return parseArrayType(type);
		}
		return toClass(type);

	}

	private Type parseArrayType(String type) {
		char[] value = type.toCharArray();
		final StringBuilder sb = new StringBuilder();
		int i = 0;
		int demension = 0;
		while (i < value.length) {
			if (value[i] == '[') {
				demension++;
				i += 2;
				continue;
			}
			sb.append(value[i]);
			i++;
		}
		return toClass(sb.toString(), demension);
	}

	public Type parseGenericType(final String type) {
		char[] value = type.toCharArray();
		final StringBuilder sb = new StringBuilder();
		int i = 0;
		while (i < value.length) {
			if (value[i] == '<') {
				final Type[] types = toTypes(new String(value, i + 1,
						value.length - 2 - i));
				return new ParameterizedTypeImpl(toClass(sb.toString()), types);
			}
			sb.append(value[i]);
			i++;
		}
		throw new IllegalArgumentException();
	}

	private Class<?> toClass(String clazzName, int arraydemention) {
		Class<?> clazz = toClass(clazzName);
		while (arraydemention != 0) {
			clazz = Array.newInstance(clazz, 0).getClass();
			arraydemention--;
		}
		return clazz;
	}

	public <T> Class<T> toClass(String clazzName) {
		try {
			return (Class<T>) Class.forName(clazzName);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(e);
		}

	}

	public PropertyValue toPropertyValue(String value) {
		if (value == null) {
			return new NullPropertyValue(this);
		}
		if (value.startsWith("#{")) {
			ValueExpression valueExpression = Expressions
					.createValueExpression(value);
			return new ExpressionPropertyValue(valueExpression);
		} else if (value.contains(",")) {
			String[] values = value.split(",");
			PropertyValue[] propertyValues = new PropertyValue[values.length];
			for (int i = 0; i < propertyValues.length; i++) {
				propertyValues[i] = toPropertyValue(values[i]);
			}
			return new ListPropertyValue(propertyValues);
		} else {
			return new SimplePropertyValue(value);
		}
	}

	@SuppressWarnings("unchecked")
	public PropertyValue toPropertyValue(Element value) {
		List list = value.elements();
		if (list.size() == 0) {
			return toPropertyValue(value.getTextTrim());
		}
		Element element = (Element) list.get(0);
		if (element.getName().equals("value")) {
			return toListPropertyValue(value);
		}
		if (element.getName().equals("key")) {
			return toMapPropertyValue(value);
		}
		// TODO message
		throw new IllegalArgumentException(""
				+ value.asXML().replace("\r", " "));
	}

	@SuppressWarnings("unchecked")
	public MapPropertyValue toMapPropertyValue(Element value) {
		List list = value.elements();
		Map<PropertyValue, PropertyValue> map = new LinkedHashMap<PropertyValue, PropertyValue>();
		for (int i = 0; i < list.size(); i += 2) {
			Element k = (Element) list.get(i);
			Element v = (Element) list.get(i + 1);
			if (!k.getName().equals("key")) {
				// TODO message
				throw new IllegalArgumentException(""
						+ value.asXML().replace("\n", " "));
			}
			if (!v.getName().equals("value")) {
				// TODO message
				throw new IllegalArgumentException(""
						+ value.asXML().replace("\n", " "));
			}
			map.put(toPropertyValue(k), toPropertyValue(v));
		}
		return new MapPropertyValue(map);
	}

	public ListPropertyValue toListPropertyValue(String value) {
		if (value == null) {
			return new NullListPropertyValue(this);
		}
		String[] values = value.split(",");
		PropertyValue[] propertyValues = new PropertyValue[values.length];
		for (int i = 0; i < values.length; i++) {
			propertyValues[i] = toPropertyValue(values[i]);
		}
		return new ListPropertyValue(propertyValues);
	}

	public ListPropertyValue toListPropertyValue(Element value) {
		List<PropertyValue> list = new ArrayList<PropertyValue>();
		for (Object element : value.elements()) {
			Element el = (Element) element;
			if (!el.getName().equals("value")) {
				// TODO message
				throw new IllegalArgumentException("" + value.asXML());
			}
			list.add(toPropertyValue(el));
		}
		return new ListPropertyValue(list.toArray(new PropertyValue[0]));
	}

	public Class<?> toClass(String packageName, String className) {
		return toClass(packageName + "." + toChamelCase(className));
	}

	public Class<?> toClass(Namespace namespace, String className) {
		String packageName = this.namespace.get(namespace.getText());
		if (packageName == null) {
			throw new IllegalArgumentException("don't bind namespace "
					+ namespace);
		}
		return toClass(packageName, className);
	}

	public String toChamelCase(String clazzName) {
		char[] value = clazzName.toCharArray();
		StringBuilder sb = new StringBuilder();
		sb.append(Character.toUpperCase(value[0]));
		for (int i = 1; i < value.length; i++) {
			if (value[i] == '-') {
				if (i + 1 >= value.length) {
					throw new IllegalArgumentException("illegal name "
							+ clazzName);
				}
				sb.append(Character.toUpperCase(value[i + 1]));
				i++;
				continue;
			}
			sb.append(value[i]);
		}
		return sb.toString();
	}

	public <T> Producer<T> toMethod(String clazzName, String factoryMethod) {
		Class<T> clazz = toClass(clazzName);
		String[] spt = factoryMethod.split("\\(");
		String methodName = spt[0];
		Type[] parameterTypes = toTypes(spt[1]
				.substring(0, spt[1].length() - 1));
		Class<?>[] parameters = new Class<?>[parameterTypes.length];
		for (int i = 0; i < parameters.length; i++) {
			parameters[i] = toRawType(parameterTypes[i]);
		}
		try {
			return new Producer.FactoryMethodProducer<T>(clazz.getMethod(
					methodName, parameters));
		} catch (Exception e) {
			// TODO error handling
			throw new RuntimeException(e);
		}
	}

	public <T> Producer<T> toConstructor(String clazzName, String constructor) {
		Class<T> clazz = toClass(clazzName);
		Type[] parameterTypes = toTypes(constructor);
		Class<?>[] parameters = new Class<?>[parameterTypes.length];
		for (int i = 0; i < parameters.length; i++) {
			parameters[i] = toRawType(parameterTypes[i]);
		}
		try {
			return new Producer.ConstructorProducer<T>(clazz
					.getConstructor(parameters));
		} catch (Exception e) {
			// TODO error handling
			throw new RuntimeException(e);
		}
	}

	public Class<?> toRawType(Type type) {
		if (type instanceof Class<?>) {
			return (Class<?>) type;
		}
		if (type instanceof ParameterizedType) {
			return toRawType(((ParameterizedType) type).getRawType());
		}
		if (type instanceof GenericArrayType) {
			Class<?> rawType = toRawType(((GenericArrayType) type)
					.getGenericComponentType());
			return Array.newInstance(rawType, 0).getClass();
		}
		throw new IllegalArgumentException("illegal " + type);
	}

	@SuppressWarnings("unchecked")
	public PropertyValue toPropertyValue(Element element, String attribute) {
		PropertyValue value;
		List list = element.elements();
		if (list.size() == 0) {
			String attr = element.attributeValue(attribute);
			if (attr != null) {
				value = toPropertyValue(attr);
			} else {
				value = toPropertyValue(element.getTextTrim());
			}
		} else {
			value = toPropertyValue(element);
		}
		return value;
	}

	public Annotation toQualifier(Element element) {
		String name = element.attributeValue("name");
		if (name == null) {
			name = element.attributeValue("id");
		}
		if (name != null) {
			return new NamedLiteral(name);
		}
		String qualifier = element.attributeValue("qualifier");
		if (qualifier != null) {
			if (qualifier.startsWith("@")) {
				return toMarkerAnnotation(qualifier);
			}
		}
		return null;
	}

	public Annotation toMarkerAnnotation(String qualifier) {
		final Class<? extends Annotation> qType = toClass(qualifier
				.substring(1));
		return (Annotation) Proxy.newProxyInstance(Thread.currentThread()
				.getContextClassLoader(), new Class[] { qType },
				new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method,
							Object[] args) throws Throwable {
						if ("toString".equals(method.getName())) {
							return "@" + qType.getName();
						}
						if ("equals".equals(method.getName())) {
							if (args[0] instanceof Annotation) {
								return qType.equals(((Annotation) args[0])
										.annotationType());
							} else {
								return false;
							}
						}
						if ("hashCode".equals(method.getName())) {
							return qType.hashCode();
						}
						if ("annotationType".equals(method.getName())) {
							return qType;
						}
						return null;
					}
				});
	}
}
