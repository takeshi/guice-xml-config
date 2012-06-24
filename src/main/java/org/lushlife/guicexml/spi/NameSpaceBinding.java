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

import java.util.Map;

/**
 * @author Takeshi Kondo
 */
public abstract class NameSpaceBinding {
	static public interface ToPackage {
		void toPackage(String name);

		void toPackage(Package pack);
	}

	private Map<String, String> mapping;

	protected Map<String, String> mapping() {
		return mapping;
	}

	public void configure(Map<String, String> mapping) {
		this.mapping = mapping;
		configure();
	}

	protected ToPackage namespace(final String namespace) {
		return new ToPackage() {

			@Override
			public void toPackage(String name) {
				mapping.put(namespace, name);
			}

			@Override
			public void toPackage(Package pack) {
				toPackage(pack.getName());
			}
		};
	}

	protected abstract void configure();

}
