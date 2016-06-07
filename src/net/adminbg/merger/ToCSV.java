package net.adminbg.merger;
/* ====================================================================
Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==================================================================== */

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.adminbg.merger.io.CSVImporter;
import net.adminbg.merger.io.ImportException;
import net.adminbg.merger.io.Importer;
import net.adminbg.merger.io.InvalidFileException;
import net.adminbg.merger.io.XSLXImporter;

/**
 * Demonstrates <em>one</em> way to convert an Excel spreadsheet into a CSV
 * file. This class makes the following assumptions; <list>
 * <li>1. Where the Excel workbook contains more that one worksheet, then a
 * single CSV file will contain the data from all of the worksheets.</li>
 * <li>2. The data matrix contained in the CSV file will be square. This means
 * that the number of fields in each record of the CSV file will match the
 * number of cells in the longest row found in the Excel workbook. Any short
 * records will be 'padded' with empty fields - an empty field is represented in
 * the the CSV file in this way - ,,.</li>
 * <li>3. Empty fields will represent missing cells.</li>
 * <li>4. A record consisting of empty fields will be used to represent an empty
 * row in the Excel workbook.</li> </list> Therefore, if the worksheet looked
 * like this;
 *
 * <pre>
*  ___________________________________________
*     |       |       |       |       |       |
*     |   A   |   B   |   C   |   D   |   E   |
*  ___|_______|_______|_______|_______|_______|
*     |       |       |       |       |       |
*   1 |   1   |   2   |   3   |   4   |   5   |
*  ___|_______|_______|_______|_______|_______|
*     |       |       |       |       |       |
*   2 |       |       |       |       |       |
*  ___|_______|_______|_______|_______|_______|
*     |       |       |       |       |       |
*   3 |       |   A   |       |   B   |       |
*  ___|_______|_______|_______|_______|_______|
*     |       |       |       |       |       |
*   4 |       |       |       |       |   Z   |
*  ___|_______|_______|_______|_______|_______|
*     |       |       |       |       |       |
*   5 | 1,400 |       |  250  |       |       |
*  ___|_______|_______|_______|_______|_______|
 *
 * </pre>
 *
 * Then, the resulting CSV file will contain the following lines (records);
 * 
 * <pre>
* 1,2,3,4,5
* ,,,,
* ,A,,B,
* ,,,,Z
* "1,400",,250,,
 * </pre>
 * <p>
 * Typically, the comma is used to separate each of the fields that, together,
 * constitute a single record or line within the CSV file. This is not however a
 * hard and fast rule and so this class allows the user to determine which
 * character is used as the field separator and assumes the comma if none other
 * is specified.
 * </p>
 * <p>
 * If a field contains the separator then it will be escaped. If the file should
 * obey Excel's CSV formatting rules, then the field will be surrounded with
 * speech marks whilst if it should obey UNIX conventions, each occurrence of
 * the separator will be preceded by the backslash character.
 * </p>
 * <p>
 * If a field contains an end of line (EOL) character then it too will be
 * escaped. If the file should obey Excel's CSV formatting rules then the field
 * will again be surrounded by speech marks. On the other hand, if the file
 * should follow UNIX conventions then a single backslash will precede the EOL
 * character. There is no single applicable standard for UNIX and some
 * appications replace the CR with \r and the LF with \n but this class will not
 * do so.
 * </p>
 * <p>
 * If the field contains double quotes then that character will be escaped. It
 * seems as though UNIX does not define a standard for this whilst Excel does.
 * Should the CSV file have to obey Excel's formmating rules then the speech
 * mark character will be escaped with a second set of speech marks. Finally, an
 * enclosing set of speah marks will also surround the entire field. Thus, if
 * the following line of text appeared in a cell - "Hello" he said - it would
 * look like this when converted into a field within a CSV file - """Hello"" he
 * said".
 * </p>
 * <p>
 * Finally, it is worth noting that talk of CSV 'standards' is really slightly
 * missleading as there is no such thing. It may well be that the code in this
 * class has to be modified to produce files to suit a specific application or
 * requirement.
 * </p>
 * 
 * @author Mark B
 * @version 1.00 9th April 2010 1.10 13th April 2010 - Added support for
 *          processing all Excel workbooks in a folder along with the ability to
 *          specify a field separator character. 2.00 14th April 2010 - Added
 *          support for embedded characters; the field separator, EOL and double
 *          quotes or speech marks. In addition, gave the client the ability to
 *          select how these are handled, either obeying Excel's or UNIX
 *          formatting conventions.
 */
public class ToCSV {

	public static void main(String[] args) {
		final DBManager1 instance = DBManager1.getInstance();
		instance.start();
		Importer i = new CSVImporter();
		XSLXImporter ex = new XSLXImporter();
		try {
			i.importFiles(Paths.get("C:\\Users\\Lachezar.Nedelchev\\git\\adminmerge\\store"));
			ex.setDestination(Paths.get("D:\\Dev\\result.xlsx"));
			ex.importFiles(Paths.get("C:\\Users\\Lachezar.Nedelchev\\git\\adminmerge\\shop"));
		} catch (InvalidFileException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ImportException e) {

			e.printStackTrace();
		}


	}
}