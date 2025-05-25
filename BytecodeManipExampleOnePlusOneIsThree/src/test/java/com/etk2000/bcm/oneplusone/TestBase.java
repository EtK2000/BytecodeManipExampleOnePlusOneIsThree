package com.etk2000.bcm.oneplusone;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestBase {
	private final ByteArrayOutputStream capturedOut = new ByteArrayOutputStream();
	private final PrintStream stdout = System.out;

	@BeforeEach
	void setup() {
		System.setOut(new PrintStream(capturedOut));
	}

	@AfterEach
	void teardown() {
		System.setOut(stdout);
		capturedOut.reset();
	}

	@Test
	void testMain() {
		Base.main();
		assertEquals("1 + 1 = 2\n", capturedOut.toString());
	}

	/**
	 * Just to show that this example is technically possible without bytecode manipulation
	 */
	@Test
	void testMainWithReflection() throws ReflectiveOperationException {
		final Field integerValue = Integer.class.getDeclaredField("value");
		integerValue.setAccessible(true);

		try {
			integerValue.set(2, 3);

			Base.main();
			assertEquals("1 + 1 = 3\n", capturedOut.toString());
		}
		finally {
			integerValue.set(2, (byte) 2);// we cast to byte in order to prevent auto-boxing back to 3

			// validate cleanup
			capturedOut.reset();
			Base.main();
			assertEquals("1 + 1 = 2\n", capturedOut.toString());
		}
	}
}