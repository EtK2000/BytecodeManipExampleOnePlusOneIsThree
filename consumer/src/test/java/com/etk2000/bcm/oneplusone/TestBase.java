package com.etk2000.bcm.oneplusone;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

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
	}

	@Test
	void testMainWithPostCompilePatch() {
		Base.main();
		assertEquals("1 + 1 = 3\n", capturedOut.toString());
	}
}