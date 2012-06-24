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
package org.lushlife.guicexml.internal.converter;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

import org.lushlife.guicexml.spi.Converter;

/**
 * @author Takeshi Kondo
 */
public class Conversions {

	static private Map<Type, Converter<?>> converters;
	static {
		initialize();

	}

	@SuppressWarnings("unchecked")
	static public void initialize() {
		converters = new ConcurrentHashMap<Type, Converter<?>>();
		addConverter(new BooleanConverter());
		addConverter(new ByteConverter());
		addConverter(new CharConverter());
		addConverter(new DoubleConverter());
		addConverter(new FloatConverter());
		addConverter(new IntegerConverter());
		addConverter(new LongConverter());
		addConverter(new StringConverter());

		addConverter(new BigDecimalConverter());
		addConverter(new DateformatConverter());
		addConverter(new PatternConverter());
		addConverter(new ObjectNameConverter());
		addConverter(new ValueExpressionConverter());

		ServiceLoader<Converter> loader = ServiceLoader.load(Converter.class);
		for (Converter<?> converter : loader) {
			addConverter(converter);
		}
	}

	public static Map<Type, Converter<?>> getConverters() {
		return converters;
	}

	public static void setConverters(Map<Type, Converter<?>> converters) {
		Conversions.converters = converters;
	}

	static public void addConverter(Converter<?> converter) {
		for (Type type : converter.getTypes()) {
			converters.put(type, converter);
		}
	}

	static public Converter<?> getConverter(Type type) {
		if (!converters.containsKey(type)) {
			throw new IllegalStateException("converter not found " + type);
		}
		return converters.get(type);
	}

}
