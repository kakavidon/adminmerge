package net.adminbg.merger;

import java.util.function.Supplier;

public class EmptySupplier<T> implements Supplier<String> {

	@Override
	public String get() {
		return "";
	}

}
