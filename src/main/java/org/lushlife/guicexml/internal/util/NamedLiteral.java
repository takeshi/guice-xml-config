package org.lushlife.guicexml.internal.util;

import java.lang.annotation.Annotation;

import javax.inject.Named;

public class NamedLiteral implements Named {
	private String name;

	public NamedLiteral(String name) {
		this.name = name;
	}

	@Override
	public String value() {
		return name;
	}

	@Override
	public Class<? extends Annotation> annotationType() {
		return Named.class;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		NamedLiteral other = (NamedLiteral) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public static Named of(String name) {
		return new NamedLiteral(name);
	}

	@Override
	public String toString() {
		return "@Named(" + name + ")";
	}

}
