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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Takeshi Kondo
 */
public class Generics {
	static public Class<?> toRawType(Type type) {
		if (type instanceof Class<?>) {
			return (Class<?>) type;
		}
		if (type instanceof ParameterizedType) {
			return toRawType(((ParameterizedType) type).getRawType());
		}
		throw new IllegalArgumentException("not support " + type);
	}

	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> createMap(Class<?> rawType) {
		try {
			if (Map.class.equals(rawType)) {
				return new LinkedHashMap<K, V>();
			}
			return (Map<K, V>) rawType.newInstance();
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <E> List<E> createList(Class<?> rawType) {
		try {
			if (List.class.equals(rawType)) {
				return new ArrayList<E>();
			}
			return (List<E>) rawType.newInstance();
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

}
