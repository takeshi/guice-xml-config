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
package org.lushlife.guicexml.internal.converter;

import java.lang.reflect.Type;

import org.lushlife.guicexml.spi.Converter;

/**
 * @author Takeshi Kondo
 */
class LongConverter implements Converter<Long> {

	@Override
	public Long convert(String str) {
		if (str == null) {
			return null;
		}
		return Long.valueOf(str);
	}

	@Override
	public Type[] getTypes() {
		return new Type[] { long.class, Long.class };
	}
}
