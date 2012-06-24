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

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import org.lushlife.guicexml.internal.el.Expressions;
import org.lushlife.guicexml.internal.util.Generics;

/**
 * @author Takeshi Kondo
 */
public class ListPropertyValue implements PropertyValue {
	private PropertyValue[] values;

	public PropertyValue[] getValues() {
		return values;
	}

	public ListPropertyValue(PropertyValue[] values) {
		this.values = values;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(values);
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
		ListPropertyValue other = (ListPropertyValue) obj;
		if (!Arrays.equals(values, other.values))
			return false;
		return true;
	}

	@Override
	public Object resolveString(Type type, Expressions expressions) {
		if (type instanceof GenericArrayType) {
			Type genericType = ((GenericArrayType) type)
					.getGenericComponentType();
			Object object = Array.newInstance(Generics.toRawType(genericType),
					values.length);
			for (int i = 0; i < values.length; i++) {
				Array.set(object, i, values[i].resolveString(genericType,
						expressions));
			}
			return object;
		}
		if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			Class<?> rawType = Generics.toRawType(type);
			if (List.class.isAssignableFrom(rawType)) {
				Type valueType = parameterizedType.getActualTypeArguments()[0];
				return createList(rawType, valueType, expressions);
			}
		}
		if (type instanceof Class<?>) {
			Class<?> clazz = (Class<?>) type;
			if (List.class.isAssignableFrom(clazz)) {
				return createList(clazz, String.class, expressions);
			}
		}
		throw new IllegalArgumentException("unsupported type " + type);
	}

	private Object createList(Class<?> rawType, Type valueType,
			Expressions expressions) {
		List<Object> list = Generics.createList(rawType);
		for (PropertyValue value : values) {
			list.add(value.resolveString(valueType, expressions));
		}
		return list;
	}

	public String toString() {
		return Arrays.toString(values);
	}
}
