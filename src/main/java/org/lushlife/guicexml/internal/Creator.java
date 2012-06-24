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

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;

import org.lushlife.guicexml.internal.el.Expressions;
import org.lushlife.guicexml.internal.property.PropertyValue;
import org.lushlife.stla.Log;
import org.lushlife.stla.Logging;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.binder.ScopedBindingBuilder;

/**
 * @author Takeshi Kondo
 */
public class Creator<T> extends Component<T> {
	static private Log log = Logging.getLog(Creator.class);

	class FactoryProvider implements Provider<T> {
		Provider<Expressions> expressions;

		public FactoryProvider(Provider<Expressions> expressions) {
			this.expressions = expressions;
		}

		@SuppressWarnings("unchecked")
		@Override
		public T get() {
			Expressions ex = expressions.get();
			Object[] values = new Object[args.length];
			for (int i = 0; i < values.length; i++) {
				values[i] = args[i].resolveString(
						producer.getGenericParameterTypes()[i], ex);
			}
			return (T) producer.newInstance(values);

		}
	}

	private Type type;
	private Annotation qualifier;
	Class<? extends Annotation> scope;
	private boolean startup;
	private org.lushlife.guicexml.internal.util.Producer<?> producer;
	private PropertyValue[] args;

	public Creator(Type type, Annotation qualifier,
			org.lushlife.guicexml.internal.util.Producer<T> creator,
			PropertyValue[] args, Class<? extends Annotation> scope,
			boolean startup, Map<String, PropertyValue> attribuite) {
		super(creator.getComponentType(), attribuite);
		this.type = (type != null) ? type : Object.class;
		this.producer = creator;
		this.args = args;
		this.qualifier = qualifier;
		this.scope = scope;
		this.startup = startup;
	}

	@Override
	public String toString() {
		return "Creator("
				+ ((qualifier != null) ? "qualifier=\"" + qualifier + "\" "
						: "")
				+ ((type != Object.class) ? "type=\"" + type + "\"" : "")
				+ " producer=\"" + producer + "\")";
	}

	@SuppressWarnings("unchecked")
	public void bind(Binder binder) {
		log.log(GuiceXmlLogMessage.INSTALL, this);
		Provider<Expressions> expression = binder
				.getProvider(Expressions.class);
		Key key = (qualifier != null) ? Key.get(type, qualifier) : Key
				.get(type);
		ScopedBindingBuilder scopedBindingBuilder = binder.bind(key)
				.toProvider(
						(com.google.inject.Provider<T>) new FactoryProvider(
								expression));
		if (startup) {
			scopedBindingBuilder.asEagerSingleton();
		} else if (scope != null) {
			scopedBindingBuilder.in(scope);
		}
	}
}
