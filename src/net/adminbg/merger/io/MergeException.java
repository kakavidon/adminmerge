package net.adminbg.merger.io;

/**
 *
 * @author lnedelc
 */
public class MergeException extends Exception {

static final long serialVersionUID = -865444010690018873L;

	public MergeException(java.lang.Throwable thrown) {
        super(thrown);
    }

    public MergeException(final String message) {
        super(message);
    }

    MergeException(String message, Throwable e) {
        super(message, e);
    }
}
