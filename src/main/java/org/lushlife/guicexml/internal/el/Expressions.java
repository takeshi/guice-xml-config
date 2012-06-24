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
package org.lushlife.guicexml.internal.el;

import java.beans.FeatureDescriptor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.el.ArrayELResolver;
import javax.el.BeanELResolver;
import javax.el.CompositeELResolver;
import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.FunctionMapper;
import javax.el.ListELResolver;
import javax.el.MapELResolver;
import javax.el.ValueExpression;
import javax.el.VariableMapper;

import org.lushlife.guicexml.internal.context.ThreadLocalContexts;

import com.google.inject.Binding;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.sun.el.lang.FunctionMapperImpl;
import com.sun.el.lang.VariableMapperImpl;

/**
 * @author Takeshi Kondo
 */
@Singleton
public class Expressions {

	protected class InternalContextELResolver extends ELResolver {

		@Override
		public Class<?> getCommonPropertyType(ELContext context, Object base) {
			return null;
		}

		@Override
		public Iterator<FeatureDescriptor> getFeatureDescriptors(
				ELContext context, Object base) {
			return null;
		}

		@Override
		public Class<?> getType(ELContext context, Object base, Object property) {
			return null;
		}

		@Override
		public Object getValue(ELContext context, Object base, Object property) {
			if (base == null) {
				if (property instanceof String) {
					Object object = ThreadLocalContexts.get((String) property);
					if (object != null) {
						context.setPropertyResolved(true);
						return object;
					}
					Key<?> key = nameKeyMap.get(property);
					if (key != null) {
						context.setPropertyResolved(true);
						return injector.getInstance(key);
					}
				}
			}
			return null;
		}

		@Override
		public boolean isReadOnly(ELContext context, Object base,
				Object property) {
			return true;
		}

		@Override
		public void setValue(ELContext context, Object base, Object property,
				Object value) {
		}

	}

	private static ExpressionFactory expressionFactory = ExpressionFactory
			.newInstance();

	protected CompositeELResolver elResolver;
	protected Injector injector;
	protected Map<String, Key<?>> nameKeyMap;

	@Inject
	public void initialization(Injector injector) {
		elResolver = new CompositeELResolver();
		elResolver.add(new InternalContextELResolver());
		elResolver.add(new MapELResolver());
		elResolver.add(new ArrayELResolver());
		elResolver.add(new BeanELResolver());
		elResolver.add(new ListELResolver());
		this.injector = injector;
		this.nameKeyMap = createNameMap(injector);
	}

	public Map<String, Key<?>> createNameMap(Injector injector) {
		Map<Key<?>, Binding<?>> bindings = injector.getBindings();
		Map<String, Key<?>> map = new HashMap<String, Key<?>>();
		for (Key<?> key : bindings.keySet()) {
			if (key.getAnnotation() instanceof Named) {
				map.put(((Named) key.getAnnotation()).value(), key);
			}
		}
		return map;
	}

	public ELContext createELContext() {
		return new ELContext() {

			@Override
			public VariableMapper getVariableMapper() {
				return new VariableMapperImpl();
			}

			@Override
			public FunctionMapper getFunctionMapper() {
				return new FunctionMapperImpl();
			}

			@Override
			public ELResolver getELResolver() {
				return elResolver;
			}
		};
	}

	private static ELContext _elCotnext() {
		return new ELContext() {

			@Override
			public VariableMapper getVariableMapper() {
				return new VariableMapperImpl();
			}

			@Override
			public FunctionMapper getFunctionMapper() {
				return new FunctionMapperImpl();
			}

			@Override
			public ELResolver getELResolver() {
				return new CompositeELResolver();
			}
		};
	}

	public static ValueExpression createValueExpression(String expression) {
		return expressionFactory.createValueExpression(_elCotnext(),
				expression, Object.class);
	}

}
