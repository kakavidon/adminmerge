package net.adminbg.merger.io;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

public enum FileTest {
	FILE_EXISTS {
		@Override
		public boolean check(final Path target) {
			final boolean exists = Files.exists(target, LinkOption.NOFOLLOW_LINKS);
			if (!exists) {
				final String msg = "\"%s\" does not exists";
				message = String.format(msg, target.toString());
			}
			return exists;
		}
	},
	FILE_READABLE {
		@Override
		public boolean check(Path target) {
			final boolean isReadable = Files.isReadable(target);
			if (!isReadable) {
				final String msg = "The cannot read \"%s\"";
				message = String.format(msg, target.toString());
			}
			return isReadable;
		}
	},
	FILE_WRITABLE {
		@Override
		public boolean check(Path target) {
			final boolean isWritable = Files.isReadable(target);
			if (!isWritable) {
				final String msg = "The cannot write \"%s\"";
				message = String.format(msg, target.toString());
			}
			return isWritable;
		}
	},
	IS_FILE {
		@Override
		public boolean check(Path target) {
			final boolean isFile = Files.isRegularFile(target, LinkOption.NOFOLLOW_LINKS);
			if (!isFile) {
				final String msg = "\"%s\" is not a file";
				message = String.format(msg, target.toString());
			}
			return isFile;
		}
	},
	IS_DIRECTORY {
		@Override
		public boolean check(Path target) {
			final boolean isDirectory = Files.isDirectory(target, LinkOption.NOFOLLOW_LINKS);
			if (!isDirectory) {
				final String msg = "\"%s\"  is not a directory";
				message = String.format(msg, target.toString());
			}
			return isDirectory;
		}
	};

	private static String message;

	public abstract boolean check(final Path target);

	public static void validate(Path target, FileTest... tests) throws InvalidFileException {
		for (FileTest test : tests) {
			if (!test.check(target)) {
				throw new InvalidFileException(message);
			}
		}
	}
}
