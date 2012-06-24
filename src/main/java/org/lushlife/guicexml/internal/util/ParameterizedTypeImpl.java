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
package org.lushlife.guicexml.internal.util;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author Takeshi Kondo
 */
public class ParameterizedTypeImpl implements ParameterizedType {
	private Class<?> rawType;
	private Type[] actualType;

	public ParameterizedTypeImpl(Class<?> rawType, Type[] types) {
		this.rawType = rawType;
		this.actualType = types;
	}

	@Override
	public Type[] getActualTypeArguments() {
		return this.actualType;
	}

	@Override
	public Type getOwnerType() {
		return null;
	}

	@Override
	public Type getRawType() {
		return rawType;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(toString(rawType));
		sb.append("<");
		for (Type type : actualType) {
			sb.append(toString(type));
			sb.append(",");
		}
		sb.delete(sb.length() - 1, sb.length());
		sb.append(">");
		return sb.toString();
	}

	public String toString(Type type) {
		if (type instanceof Class<?>) {
			Class<?> clazz = (Class<?>) type;
			if (clazz.isArray()) {
				return toString(clazz.getComponentType()) + "[]";
			}
			return ((Class<?>) type).getName();
		}
		if (type instanceof GenericArrayType) {
			return toString(((GenericArrayType) type).getGenericComponentType())
					+ "[]";
		}
		return type.toString();
	}

}
