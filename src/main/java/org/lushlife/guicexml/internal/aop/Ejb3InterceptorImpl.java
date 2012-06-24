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

import javax.interceptor.InvocationContext;

/**
 * @author Takeshi Kondo
 */
public class Ejb3InterceptorImpl implements Ejb3Interceptor {
	private Object owner;
	private Method method;

	public Ejb3InterceptorImpl(Object owner, Method method) {
		this.owner = owner;
		this.method = method;
	}

	@Override
	public Object aroundInvoke(InvocationContext invocationContext)
			throws Exception {
		return method.invoke(owner, invocationContext);
	}

}
