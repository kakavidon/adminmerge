/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.adminbg.merger.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author kakavidon
 */
public class ReadCsvFileTask extends FileTask {

	private Map<String, String> map = new TreeMap<>();
	private final int START_FROM = 2;

	public ReadCsvFileTask(Path file) {
		super(file);
	}

	@Override
	public FileTask call() throws Exception {
		final Charset forName = Charset.forName("Windows-1251");
		final Path file = getFile();
		final BufferedReader newBufferedReader = Files.newBufferedReader(file, forName);

		try (final BufferedReader br = newBufferedReader;) {
			StringBuilder s = new StringBuilder();
			String line = null;
			int i = 0;
			while ((line = br.readLine()) != null) {
				if (i >= START_FROM) {
					final String[] columns = line.split(";");
					if (columns == null || columns.length < 8) {
						final String msg = "File \"%s\" has invalid format.";
						throw new IOException(String.format(msg, file));
					} else {

						s.append(line).append("\n");
						map.put(columns[4], columns[7]);
					}

				}
				i++;
			}

		} catch (IOException e) {

			e.printStackTrace();
		}

		return this;
	}

	public Map<String, String> getMap() {
		return this.map;
	}

	@Override
	public int getWeight() {
		return 1;
	}

}
