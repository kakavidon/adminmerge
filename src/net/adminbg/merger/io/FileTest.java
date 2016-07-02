package net.adminbg.merger.io;

import static net.adminbg.merger.ui.Configuration.EMPTY_STRING;

import static net.adminbg.merger.ui.Configuration.FILETEST_MESSAGE_CANNOT_COUNT;
import static net.adminbg.merger.ui.Configuration.FILETEST_MESSAGE_CANNOT_READ;
import static net.adminbg.merger.ui.Configuration.FILETEST_MESSAGE_CANNOT_WRITE;
import static net.adminbg.merger.ui.Configuration.FILETEST_MESSAGE_INVALID_EXTENSION;
import static net.adminbg.merger.ui.Configuration.FILETEST_MESSAGE_IS_EMPTY;
import static net.adminbg.merger.ui.Configuration.FILETEST_MESSAGE_NOT_DIR;
import static net.adminbg.merger.ui.Configuration.FILETEST_MESSAGE_NOT_EXISTS;
import static net.adminbg.merger.ui.Configuration.FILETEST_MESSAGE_NOT_REGULAR_FILE;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * 
 * I/O checking routines.
 * 
 * @author kakavidon
 * 
 */
public enum FileTest {

	EXISTS {

		@Override
		public boolean check(final Path target) {
			final boolean exists = Files.exists(target,
					LinkOption.NOFOLLOW_LINKS);
			if (!exists) {
				final String msg = FILETEST_MESSAGE_NOT_EXISTS;
				message = String.format(msg, target.toString());
			}
			return exists;
		}
	},
	READABLE {
		@Override
		public boolean check(Path target) {
			final boolean isReadable = Files.isReadable(target);
			if (!isReadable) {
				final String msg = FILETEST_MESSAGE_CANNOT_READ;
				message = String.format(msg, target.toString());
			}
			return isReadable;
		}
	},
	WRITABLE {
		@Override
		public boolean check(Path target) {
			final boolean isWritable = Files.isReadable(target);
			if (!isWritable) {
				final String msg = FILETEST_MESSAGE_CANNOT_WRITE;
				message = String.format(msg, target.toString());
			}
			return isWritable;
		}
	},
	IS_FILE {
		@Override
		public boolean check(Path target) {
			final boolean isFile = Files.isRegularFile(target,
					LinkOption.NOFOLLOW_LINKS);
			if (!isFile) {
				final String msg = FILETEST_MESSAGE_NOT_REGULAR_FILE;
				message = String.format(msg, target.toString());
			}
			return isFile;
		}
	},
	IS_DIRECTORY {
		@Override
		public boolean check(Path target) {
			final boolean isDirectory = Files.isDirectory(target,
					LinkOption.NOFOLLOW_LINKS);
			if (!isDirectory) {
				final String msg = FILETEST_MESSAGE_NOT_DIR;
				message = String.format(msg, target.toString());
			}
			return isDirectory;
		}
	},
	NON_EMPTY {
		@Override
		public boolean check(Path target) throws MergeException {
			boolean nonEmpty = false;
			try {
				nonEmpty = Files.size(target) > 0;
			} catch (IOException e) {
				throw new MergeException(e.getMessage(), e);
			}
			if (!nonEmpty) {
				final String msg = FILETEST_MESSAGE_IS_EMPTY;
				message = String.format(msg, target.toString());
			}
			return nonEmpty;
		}
	};

	private static String message;

	public abstract boolean check(final Path target) throws MergeException;

	public static void validate(Path target, FileTest... tests)
			throws MergeException {
		for (FileTest test : tests) {
			if (!test.check(target)) {
				throw new MergeException(message);
			}
		}
	}

	public static boolean isEmptyDirectory(final Path target, final String ext)
			throws MergeException {
		if (!IS_DIRECTORY.check(target)) {
			final String msg = FILETEST_MESSAGE_NOT_DIR;
			message = String.format(msg, target.toString());
			throw new MergeException(message);
		}

		if (ext == null || EMPTY_STRING.equals(ext)) {
			final String msg = FILETEST_MESSAGE_CANNOT_COUNT;
			message = String.format(msg, target.toString());
			throw new MergeException(message);
		}
		try {
			final Stream<Path> list = Files.list(target).filter(
					new Predicate<Path>() {
						@Override
						public boolean test(Path t) {
							return t.endsWith(ext);
						}

					});
			return list.iterator().hasNext();

		} catch (IOException e) {
			throw new MergeException(e);
		}

	}

	public static Set<Path> listDirectory(final Path target, final String ext)
			throws MergeException {
		Set<Path> result = new TreeSet<>();
		if (!IS_DIRECTORY.check(target)) {
			final String msg = FILETEST_MESSAGE_NOT_DIR;
			message = String.format(msg, target.toString());
			throw new MergeException(message);
		}

		if (ext == null || EMPTY_STRING.equals(ext)) {
			final String msg = FILETEST_MESSAGE_INVALID_EXTENSION;
			message = String.format(msg, target.toString());
			throw new MergeException(message);
		}

		try {
			final Stream<Path> list = Files.list(target).filter(
					new Predicate<Path>() {
						@Override
						public boolean test(Path t) {
							return t.endsWith(ext);
						}

					});
			final Iterator<Path> iterator = list.iterator();
			while (iterator.hasNext()) {
				Path path = (Path) iterator.next();
				result.add(path);
			}
			return result;

		} catch (IOException e) {
			throw new MergeException(e);
		}

	}
}
