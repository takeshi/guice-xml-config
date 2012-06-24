package org.lushlife.guicexml.internal;

import org.lushlife.stla.Log;
import org.lushlife.stla.Logging;

import com.google.inject.Binder;
import com.google.inject.Module;

public class Install implements GuiceBinder {
	static Log log = Logging.getLog(Install.class);
	private Module module;

	public Install(Module module) {
		this.module = module;
	}

	@Override
	public void bind(Binder binder) {
		log.log(GuiceXmlLogMessage.INSTALL, module);
		binder.install(module);
	}

}
