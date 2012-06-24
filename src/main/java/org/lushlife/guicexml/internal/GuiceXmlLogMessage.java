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
package org.lushlife.guicexml.internal;

import org.lushlife.stla.Info;

/**
 * @author Takeshi Kondo
 */
public enum GuiceXmlLogMessage {
	@Info("read xml file {0}")
	READ_XML_FILE,

	@Info("install {0}")
	INSTALL,

	@Info("disabled {0}")
	DISABLED,

	@Info("interceptor-mapping  target-class=''{0}'' exclude-class=''{1}'' target-method=''{2}'' exclude-method=''{3}'' interceptors=''{4}''")
	INTERCEPTOR

}
