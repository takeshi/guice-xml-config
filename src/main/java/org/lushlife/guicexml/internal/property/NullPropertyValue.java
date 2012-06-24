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

import java.lang.reflect.Type;

import org.lushlife.guicexml.internal.el.Expressions;

/**
 * @author Takeshi Kondo
 */
public class NullPropertyValue implements PropertyValue {
	// for trace
	transient private Object createor;

	public NullPropertyValue(Object createor) {
		this.createor = createor;
	}

	@Override
	public Object resolveString(Type type, Expressions expressions) {
		return null;
	}

	public String toString() {
		return "NullPropertyValue( created ty " + createor.getClass() + ")";
	}
}
