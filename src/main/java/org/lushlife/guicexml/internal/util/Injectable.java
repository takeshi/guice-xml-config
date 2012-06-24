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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author Takeshi Kondo
 */
public interface Injectable {
	static public class FieldInject implements Injectable {
		private Field field;

		public FieldInject(Field field) {
			field.setAccessible(true);
			this.field = field;
		}

		@Override
		public Type getType() {
			return field.getGenericType();
		}

		@Override
		public void setValue(Object instance, Object value) {
			try {
				field.set(instance, value);
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}
	}

	static public class SetterInjection implements Injectable {
		private Method setter;

		public SetterInjection(Method setter) {
			this.setter = setter;
		}

		@Override
		public Type getType() {
			return setter.getGenericParameterTypes()[0];
		}

		@Override
		public void setValue(Object instance, Object value) {
			try {
				setter.invoke(instance, value);
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}
	}

	public Type getType();

	public void setValue(Object instance, Object value);

}
