package net.adminbg.merger.ui;

import java.nio.file.Path;
import java.util.Objects;

/**
 *
 * @author lnedelc
 */
public class SelectionInfo implements Comparable<SelectionInfo> {

    private final Path path;
    private final String extension;
    private final String className;

    public SelectionInfo(final Path path, final String extension, final String className) {
        this.path = path;
        this.extension = extension;
        this.className = className;

    }

    /**
     * @return the path
     */
    public Path getPath() {
        return path;
    }

    /**
     * @return the extension
     */
    public String getExtension() {
        return extension;
    }

    /**
     * @return the className
     */
    public String getClassName() {
        return className;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + Objects.hashCode(this.path);
        hash = 53 * hash + Objects.hashCode(this.extension);
        hash = 53 * hash + Objects.hashCode(this.className);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SelectionInfo other = (SelectionInfo) obj;
        if (!Objects.equals(this.extension, other.extension)) {
            return false;
        }
        if (!Objects.equals(this.className, other.className)) {
            return false;
        }
        return Objects.equals(this.path, other.path);
    }

    @Override
    public int compareTo(final SelectionInfo selection) {
       
        final int classResult = className.compareTo(selection.className);
        final int extResult = extension.compareTo(selection.extension);
        final int pathResult = path.compareTo(selection.path);
        if (classResult != 0) {
            return classResult;
        }
        if (extResult != 0) {
            return extResult;
        }
        return pathResult;
    }

    @Override
    public String toString() {
        return "SelectionInfo{" + "path=" + path + ", extension=" + extension + ", className=" + className + '}';
    }

}
