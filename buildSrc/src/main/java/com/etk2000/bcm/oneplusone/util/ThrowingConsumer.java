package com.etk2000.bcm.oneplusone.util;

@FunctionalInterface
public interface ThrowingConsumer<T, E extends Exception> {
	void accept(T var1) throws E;
}