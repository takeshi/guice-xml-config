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

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.regex.Pattern;

import javax.interceptor.AroundInvoke;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.lushlife.guicexml.internal.aop.AOP;
import org.lushlife.guicexml.internal.aop.Ejb3ToAopAlianceInverceptor;
import org.lushlife.guicexml.internal.el.Expressions;
import org.lushlife.guicexml.internal.property.PropertyValue;
import org.lushlife.stla.Log;
import org.lushlife.stla.Logging;

import com.google.inject.Binder;
import com.google.inject.Provider;
import com.google.inject.matcher.AbstractMatcher;

/**
 * @author Takeshi Kondo
 */
public class InterceptorMapping implements GuiceBinder {
	static Log log = Logging.getLog(InterceptorMapping.class);

	class ReferenceInterceptor implements MethodInterceptor {
		int pos;

		public ReferenceInterceptor(int pos) {
			this.pos = pos;
		}

		@Override
		public Object invoke(MethodInvocation invocation) throws Throwable {
			Object obj = interceptors[pos].resolveString(Object.class,
					expressions.get());
			if (obj instanceof MethodInterceptor) {
				return ((MethodInterceptor) obj).invoke(invocation);
			} else {
				return new Ejb3ToAopAlianceInverceptor(AOP.toInterceptor(obj))
						.invoke(invocation);
			}
		}
	}

	class ClassMatcher extends AbstractMatcher<Class<?>> {

		@Override
		public boolean matches(Class<?> clazz) {
			if (MethodInvocation.class.isAssignableFrom(clazz)) {
				return false;
			}
			for (Method method : clazz.getMethods()) {
				if (method.isAnnotationPresent(AroundInvoke.class)) {
					return false;
				}
			}
			String name = clazz.getName();
			if (targetClass != null) {
				if (targetClass.matcher(name).matches()) {
					if (excludeClass != null) {
						if (excludeClass.matcher(name).matches()) {
							return false;
						}
					}
					return true;
				} else {
					return false;
				}
			}
			if (excludeClass != null) {
				if (excludeClass.matcher(name).matches()) {
					return false;
				}
			}
			return true;
		}
	}

	class MethodMatcher extends AbstractMatcher<Method> {

		@Override
		public boolean matches(Method method) {
			if (method.isAnnotationPresent(AroundInvoke.class)) {
				return false;
			}
			String name = method.getName();
			if (targetMethod != null) {
				if (targetMethod.matcher(name).matches()) {
					if (excludeMethod != null) {
						if (excludeMethod.matcher(name).matches()) {
							return false;
						}
					}
					return true;
				} else {
					return false;
				}
			}
			if (excludeMethod != null) {
				if (excludeMethod.matcher(name).matches()) {
					return false;
				}
			}
			return true;
		}
	}

	PropertyValue[] interceptors;
	Pattern targetClass;
	Pattern excludeClass;
	Pattern targetMethod;
	Pattern excludeMethod;

	private Provider<Expressions> expressions;

	public InterceptorMapping(PropertyValue[] interceptors,
			Pattern targetClass, Pattern excludeClass, Pattern targetMethod,
			Pattern excludeMethod) {
		this.interceptors = interceptors;
		this.targetClass = targetClass;
		this.excludeClass = excludeClass;
		this.targetMethod = targetMethod;
		this.excludeMethod = excludeMethod;
	}

	public void bind(Binder binder) {
		this.expressions = binder.getProvider(Expressions.class);
		if (log.isEnableFor(GuiceXmlLogMessage.INTERCEPTOR)) {
			log.log(GuiceXmlLogMessage.INTERCEPTOR, targetClass, excludeClass,
					targetMethod, excludeMethod, Arrays.toString(interceptors));
		}
		for (int i = 0; i < interceptors.length; i++) {
			binder.bindInterceptor(new ClassMatcher(), new MethodMatcher(),
					new ReferenceInterceptor(i));
		}
	}
}
