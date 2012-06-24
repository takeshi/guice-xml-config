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
package org.lushlife.guicexml.spi;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * @author Takeshi Kondo
 */
public abstract class ScopeBinding {
	static public interface ToName {
		void toName(String name);
	}

	private Map<String, Class<? extends Annotation>> mapping;

	protected Map<String, Class<? extends Annotation>> mapping() {
		return mapping;
	}

	public void configure(Map<String, Class<? extends Annotation>> mapping) {
		this.mapping = mapping;
		configuire();
	}

	protected ToName bindScope(final Class<? extends Annotation> scopeAnnotation) {
		return new ToName() {
			@Override
			public void toName(String name) {
				mapping.put(name, scopeAnnotation);
			}
		};
	}

	protected abstract void configuire();
}
