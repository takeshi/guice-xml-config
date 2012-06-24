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
package org.lushlife.guicexml.internal.aop;

import java.lang.reflect.Method;

import javax.interceptor.AroundInvoke;

/**
 * @author Takeshi Kondo
 */
public class AOP {

	static public Ejb3Interceptor toInterceptor(Object obj) {
		if (obj instanceof Ejb3Interceptor) {
			return (Ejb3Interceptor) obj;
		}
		for (Method method : obj.getClass().getMethods()) {
			if (method.isAnnotationPresent(AroundInvoke.class)) {
				return new Ejb3InterceptorImpl(obj, method);
			}
		}

		throw new IllegalArgumentException("isn't ejb3 interceptor "
				+ obj.getClass());
	}
}
