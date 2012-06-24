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

import javax.el.ValueExpression;

import org.lushlife.guicexml.internal.el.Expressions;

/**
 * @author Takeshi Kondo
 */
public class ExpressionPropertyValue implements PropertyValue {
	private ValueExpression valueExpression;

	public ExpressionPropertyValue(ValueExpression valueExpression) {
		this.valueExpression = valueExpression;
	}

	@Override
	public Object resolveString(Type type, Expressions expressions) {
		Object obj = valueExpression.getValue(expressions.createELContext());
		if (obj instanceof String) {
			return new SimplePropertyValue((String) obj).resolveString(type,
					expressions);
		}
		return obj;
	}

	public String toString() {
		return valueExpression.getExpressionString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((valueExpression == null) ? 0 : valueExpression.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExpressionPropertyValue other = (ExpressionPropertyValue) obj;
		if (valueExpression == null) {
			if (other.valueExpression != null)
				return false;
		} else if (!valueExpression.equals(other.valueExpression))
			return false;
		return true;
	}

}
