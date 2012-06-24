package org.lushlife.guicexml.internal.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public interface Producer<T> {

	Class<T> getComponentType();

	Class<?>[] getParamterTypes();

	T newInstance(Object... args);

	Type[] getGenericParameterTypes();

	static public class FactoryMethodProducer<T> implements Producer<T> {
		private Method method;

		public FactoryMethodProducer(Method method) {
			this.method = method;
		}

		@Override
		public Class<?>[] getParamterTypes() {
			return method.getParameterTypes();
		}

		@Override
		public T newInstance(Object... args) {
			try {
				return (T) method.invoke(null, args);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public Type[] getGenericParameterTypes() {
			return method.getGenericParameterTypes();
		}

		@Override
		public Class<T> getComponentType() {
			return (Class<T>) method.getReturnType();
		}

		public String toString() {
			return method.toGenericString();
		}
	}

	static public class ConstructorProducer<T> implements Producer<T> {
		Constructor<T> constructor;

		public ConstructorProducer(Constructor<T> constructor) {
			this.constructor = constructor;
		}

		@Override
		public Class<?>[] getParamterTypes() {
			return constructor.getParameterTypes();
		}

		@Override
		public T newInstance(Object... args) {
			try {
				return constructor.newInstance(args);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public Type[] getGenericParameterTypes() {
			return constructor.getParameterTypes();
		}

		@Override
		public Class<T> getComponentType() {
			return constructor.getDeclaringClass();
		}

		public String toString() {
			return constructor.toGenericString();
		}

	}
}
