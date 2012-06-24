package example.hoge;

import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class HogeTest {

	@Test
	public void hogeTest() {
		Injector injector = Guice.createInjector(new HogeXmlModule());
		Hoge hoge = injector.getInstance(Hoge.class);
		hoge.printMessage();
	}

}
