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
package org.lushlife.guicexml.internal.context;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author Takeshi Kondo
 */
public class ThreadLocalContexts {

	public static ThreadLocal<LinkedList<Map<String, Object>>> context = new ThreadLocal<LinkedList<Map<String, Object>>>() {
		@Override
		protected LinkedList<Map<String, Object>> initialValue() {
			return new LinkedList<Map<String, Object>>();
		}
	};

	static public Map<String, Object> getContextMap() {
		LinkedList<Map<String, Object>> list = context.get();
		if (list.size() == 0) {
			list.add(new HashMap<String, Object>());
		}
		return list.getLast();
	}

	public static Object get(String property) {
		LinkedList<Map<String, Object>> list = context.get();
		for (int i = list.size() - 1; i >= 0; i--) {
			Map<String, Object> map = list.get(i);
			if (map.containsKey(property)) {
				return map.get(property);
			}
		}
		return null;
	}
}
