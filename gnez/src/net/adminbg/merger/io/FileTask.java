package net.adminbg.merger.io;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * 
 * Abstract file manipulation task. Extracts a map with unique keys and lookup
 * values.
 * 
 * @author kakavidon
 * 
 * @param <U> the type of the key 
 * @param <V> the type of the value 
 */

public abstract class FileTask<U, V> implements Callable<FileTask<U, V>> {

	private int percentDone;
	private final Path file;

	public FileTask(final Path file) {
		this.file = file;

	}

	public void setPercentDone(final int percentDone) {
		this.percentDone = percentDone;
	}

	public int getPercentDone() {
		return percentDone;
	}

	public Path getFile() {
		return this.file;
	}

	public abstract Map<U, V> getMap();

	public abstract int getWeight();

	public abstract FileTask<U, V> call() throws MergeException;

}
