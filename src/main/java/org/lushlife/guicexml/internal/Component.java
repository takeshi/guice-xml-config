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
package org.lushlife.guicexml.internal;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.lushlife.guicexml.internal.el.Expressions;
import org.lushlife.guicexml.internal.property.PropertyValue;
import org.lushlife.guicexml.internal.util.Injectable;
import org.lushlife.guicexml.internal.util.NamedLiteral;
import org.lushlife.stla.Log;
import org.lushlife.stla.Logging;

import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.binder.ScopedBindingBuilder;
import com.google.inject.spi.InjectionPoint;

/**
 * @author Takeshi Kondo
 */
public class Component<T> implements GuiceBinder {
	static private Log log = Logging.getLog(Component.class);

	static private AtomicInteger counter = new AtomicInteger();

	class ComponentInjectionListener implements Provider<T> {

		Provider<Expressions> expressions;
		Component<T> ownerComponent;
		Provider<Injector> injector;

		public ComponentInjectionListener(Provider<Expressions> expressions,
				Provider<Injector> injector) {
			this.expressions = expressions;
			this.injector = injector;
		}

		@Override
		public T get() {
			T t = injector.get().getInstance(internalKey);
			injectValues(t, expressions.get());
			postConstruct(t);
			return t;
		}

	}

	protected Type bindType;
	final protected Class<T> clazz;
	protected Annotation qualifier;
	protected Class<? extends Annotation> scopeType;
	protected boolean eagerSingleton;
	final protected Map<String, PropertyValue> attribute;
	final protected Map<String, Injectable> attribuiteInject = new HashMap<String, Injectable>();
	protected Method postConstruct;

	private Key<T> internalKey;

	public Component(Type bindTypes, Class<T> clazz, Annotation qualifier,
			Class<? extends Annotation> scopeType, boolean eagerSingleton,
			Map<String, PropertyValue> attribuite) {
		this(clazz, attribuite);
		this.bindType = bindTypes;
		this.qualifier = qualifier;
		this.scopeType = scopeType;
		this.eagerSingleton = eagerSingleton;
	}

	protected Component(Class<T> clazz, Map<String, PropertyValue> attribuite) {
		this.clazz = clazz;
		this.attribute = attribuite;
		initialize();
	}

	public String toString() {
		return "Component(qualifier=\"" + qualifier + "\" class=\"" + clazz
				+ "\" type=\"" + bindType + "\" scope=\""
				+ ((scopeType == null) ? "depend" : scopeType)
				+ "\" attribute=" + attribute + ")";
	}

	public void postConstruct(T t) {
		try {
			if (postConstruct != null) {
				int length = postConstruct.getParameterTypes().length;
				if (length == 1) {
					postConstruct.invoke(t, this);
					return;
				}
				if (length == 0) {
					postConstruct.invoke(t);
					return;
				}
				throw new IllegalStateException();
			}
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}

	}

	public void injectValues(T t, Expressions expressions) {
		for (Entry<String, PropertyValue> entry : attribute.entrySet()) {
			Injectable injectable = attribuiteInject.get(entry.getKey());
			if (injectable == null) {
				throw new IllegalArgumentException("error " + this + " key "
						+ entry.getKey());
			}
			Type type = injectable.getType();
			Object object = entry.getValue().resolveString(type, expressions);
			injectable.setValue(t, object);
		}
	}

	protected void initialize() {
		this.internalKey = Key.get(clazz,
				NamedLiteral.of("_internal_" + counter.getAndIncrement()));
		initSetterInject();
		initFieldInject();
		this.postConstruct = findPostConstructMethod();
	}

	private Method findPostConstructMethod() {
		Class<?> clazz = this.clazz;
		while (clazz != null) {
			for (Method method : clazz.getDeclaredMethods()) {
				if (method.isAnnotationPresent(PostConstruct.class)) {
					method.setAccessible(true);
					return method;
				}
			}
			clazz = clazz.getSuperclass();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public void bind(Binder binder) {
		log.log(GuiceXmlLogMessage.INSTALL, this);

		binder.bind(internalKey).toConstructor(
				(Constructor) InjectionPoint.forConstructorOf(clazz)
						.getMember());

		Provider<Expressions> expressions = binder
				.getProvider(Expressions.class);
		Provider<Injector> injector = binder.getProvider(Injector.class);
		Key<T> key = (Key<T>) ((qualifier != null) ? Key.get(bindType,
				qualifier) : Key.get(bindType));
		ScopedBindingBuilder scopedBindingBuilder;
		scopedBindingBuilder = binder.bind(key).toProvider(
				(com.google.inject.Provider<T>) new ComponentInjectionListener(
						expressions, injector));
		if (eagerSingleton) {
			scopedBindingBuilder.asEagerSingleton();
		} else if (scopeType != null) {
			scopedBindingBuilder.in(scopeType);
		}
	}

	protected void initFieldInject() {
		Class<?> clazz = this.clazz;
		while (clazz != null) {
			for (Field field : clazz.getDeclaredFields()) {
				if (!attribuiteInject.containsKey(field.getName())) {
					attribuiteInject.put(field.getName(),
							new Injectable.FieldInject(field));
				}
			}
			clazz = clazz.getSuperclass();
		}
	}

	protected void initSetterInject() {
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
			for (PropertyDescriptor desc : beanInfo.getPropertyDescriptors()) {
				Method writeMethod = desc.getWriteMethod();
				if (writeMethod != null) {
					attribuiteInject.put(desc.getName(),
							new Injectable.SetterInjection(writeMethod));
				}
			}
		} catch (IntrospectionException e) {
			throw new IllegalStateException(e);
		}
	}

}
