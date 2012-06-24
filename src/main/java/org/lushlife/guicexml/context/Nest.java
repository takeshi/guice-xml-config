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
package org.lushlife.guicexml.context;

import java.util.LinkedList;
import java.util.Map;

import org.lushlife.guicexml.internal.context.ThreadLocalContexts;

/**
 * @author Takeshi Kondo
 */
abstract public class Nest<E extends Throwable> implements Callable<E> {
	public Nest(Map<String, Object> map) throws E {
		LinkedList<Map<String, Object>> list = ThreadLocalContexts.context
				.get();
		try {
			list.add(map);
			call();
		} finally {
			list.removeLast();
		}
	}
}