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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;

import org.lushlife.guicexml.internal.el.Expressions;
import org.lushlife.guicexml.internal.util.Generics;

/**
 * @author Takeshi Kondo
 */
public class MapPropertyValue implements PropertyValue {

	private Map<PropertyValue, PropertyValue> value;

	public MapPropertyValue(Map<PropertyValue, PropertyValue> value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MapPropertyValue other = (MapPropertyValue) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public Object resolveString(Type type, Expressions expressions) {
		if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			Class<?> rawType = Generics.toRawType(parameterizedType
					.getRawType());
			if (!Map.class.isAssignableFrom(rawType)) {
				throw new IllegalArgumentException("unsupported type " + type);
			}
			Type keyType = parameterizedType.getActualTypeArguments()[0];
			Type valueType = parameterizedType.getActualTypeArguments()[1];
			return createMap(rawType, keyType, valueType, expressions);
		}
		if (type instanceof Class<?>) {
			Class<?> clazz = (Class<?>) type;
			if (Map.class.isAssignableFrom(clazz)) {
				return createMap(clazz, String.class, String.class, expressions);
			}
		}
		throw new IllegalArgumentException("unsupported type " + type);

	}

	private Object createMap(Class<?> rawType, Type keyType, Type valueType,
			Expressions expressions) {
		Map<Object, Object> map = Generics.createMap(rawType);
		for (Entry<PropertyValue, PropertyValue> entry : value.entrySet()) {
			map.put(entry.getKey().resolveString(keyType, expressions), entry
					.getValue().resolveString(valueType, expressions));
		}
		return map;
	}

	public String toString() {
		return value.toString();
	}
}
