package org.lushlife.guicexml.internal.util;

public class Preconditions {
	static public <T> T checkNotNull(T obj, String name) {
		assert obj != null;
		return obj;
	}
}
