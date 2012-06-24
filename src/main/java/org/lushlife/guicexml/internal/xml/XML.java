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
package org.lushlife.guicexml.internal.xml;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.UnknownHostException;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * @author Takeshi Kondo
 */
public class XML {
	public static Element getRootElement(InputStream stream)
			throws DocumentException {
		try {
			SAXReader saxReader = new SAXReader();
			saxReader.setMergeAdjacentText(true);
			return saxReader.read(stream).getRootElement();
		} catch (DocumentException e) {
			Throwable nested = e.getNestedException();
			if (nested != null) {
				if (nested instanceof FileNotFoundException) {
					throw new RuntimeException(
							"Can't find schema/DTD reference: "
									+ nested.getMessage(), e);
				} else if (nested instanceof UnknownHostException) {
					throw new RuntimeException(
							"Cannot connect to host from schema/DTD reference: "
									+ nested.getMessage()
									+ " - check that your schema/DTD reference is current",
							e);
				}
			}
			throw e;
		}
	}

}
