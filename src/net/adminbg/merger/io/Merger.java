package net.adminbg.merger.io;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFRow;

/**
 * 
 * Abstract class that defines the way how the source data should be combined.
 * The descendants of this class will determine the algorithm how to match rows
 * from the one source to the other.
 * 
 * @author kakavidon
 * 
 */
public abstract class Merger {

	private final Path targetFile;
	private final Map<?, ?> secondDirRows;
	private final Map<?, ?> firstDirRows;
	private final List<XSSFRow> headerRows;

	public Merger(Path targetFile, Map<?, ?> firstDirRows,
			Map<?, ?> secondDirRows, final List<XSSFRow> headerRow) {
		this.targetFile = targetFile;
		this.firstDirRows = firstDirRows;
		this.secondDirRows = secondDirRows;
		this.headerRows = headerRow;
	}

	/**
	 * 
	 * Merges the rows collected from the source directories into the resulting
	 * file.
	 * 
	 * @throws MergeException
	 *             if something went wrong
	 */
	abstract public void merge() throws MergeException;

	/**
	 * 
	 * Returns the target file.
	 * 
	 * @return the targetFile
	 */
	public Path getTargetFile() {
		return targetFile;
	}

	/**
	 * Returns the rows collected from the first directory.
	 * 
	 * @return the rows
	 */
	public Map<?, ?> getFirstDirRows() {
		return firstDirRows;
	}

	/**
	 * 
	 * Returns the rows collected from the second directory.
	 * 
	 * @return the rows
	 */
	public Map<?, ?> getSecondDirRows() {
		return secondDirRows;
	}

	/**
	 * Returns the header rows collected .
	 * 
	 * @return the headerRow
	 */
	public List<XSSFRow> getHeaderRows() {
		return headerRows;
	}

}
