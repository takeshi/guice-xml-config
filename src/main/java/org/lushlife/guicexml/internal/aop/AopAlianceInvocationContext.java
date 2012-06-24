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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.interceptor.InvocationContext;

import org.aopalliance.intercept.MethodInvocation;

/**
 * @author Takeshi Kondo
 */
public class AopAlianceInvocationContext implements InvocationContext {
	static private ThreadLocal<Map<String, Object>> contextData = new ThreadLocal<Map<String, Object>>();
	private MethodInvocation invocation;

	public AopAlianceInvocationContext(MethodInvocation invocation) {
		this.invocation = invocation;
	}

	@Override
	public Map<String, Object> getContextData() {
		return contextData.get();
	}

	@Override
	public Method getMethod() {
		return invocation.getMethod();
	}

	@Override
	public Object[] getParameters() {
		return invocation.getArguments();
	}

	@Override
	public Object getTarget() {
		return invocation.getThis();
	}

	@Override
	public Object proceed() throws Exception {
		Map<String, Object> map = contextData.get();
		boolean createMap = false;
		if (map == null) {
			createMap = true;
			contextData.set(new HashMap<String, Object>());
		}
		try {
			return invocation.proceed();
		} catch (Throwable e) {
			if (e instanceof Exception) {
				throw (Exception) e;
			}
			throw new InvocationTargetException(e);
		} finally {
			if (createMap) {
				contextData.remove();
			}
		}
	}

	@Override
	public void setParameters(Object[] parameters) {
		System.arraycopy(parameters, 0, getParameters(), 0, parameters.length);
	}

}
