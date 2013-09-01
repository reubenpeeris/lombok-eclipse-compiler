package com.reubenpeeris.maven.lombokeclipsecompiler;

import java.io.File;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class Utils {
	public static String getFromList(String regex, List<String> list,
			String description) {
		if (regex == null || list == null || description == null) {
			throw new NullPointerException();
		}
		Pattern pattern = Pattern.compile(regex);

		List<String> results = new LinkedList<String>();
		for (String entry : list) {
			if (pattern.matcher(entry).matches()) {
				results.add(entry);
			}
		}

		if (results.size() > 1) {
			throw new IllegalStateException("Multiple " + description
					+ " found using regex '" + regex + "': " + results);
		}
		if (results.isEmpty()) {
			return null;
		}

		return results.get(0);
	}

	/**
	 * Returns a path string delimited by the system path.seperator for files that exist in the list.
	 * 
	 * @param list
	 * @return the path for existing files
	 */
	public static String toPath(List<String> list) {
		if (list == null) {
			throw new NullPointerException();
		}

		String delimiter = System.getProperty("path.separator");
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String entry : list) {
			File file = new File(entry);
			if (file.exists()) {
				if (first) {
					first = false;
				} else {
					sb.append(delimiter);
				}
				sb.append(entry);
			}
		}

		return sb.toString();
	}

	public static File findJava() {
		File binDir = new File(System.getProperty("java.home"), "bin");
		File javaExe = new File(binDir, "java");
		if (!javaExe.exists()) {
			javaExe = new File(binDir, "java.exe");
		}
		if (!javaExe.exists()) {
			throw new IllegalStateException("Failed to find the java binary");
		}
		return javaExe;
	}

	public static String getJarFor(Class<?> clazz) {
		String slashClass = clazz.getCanonicalName().replace('.', '/')
				+ ".class";
		if (clazz.getClassLoader() == null) {
			throw new IllegalStateException("class loaded by boot classpath!");
		}
		URL url = clazz.getClassLoader().getResource(slashClass);
		if (!url.getProtocol().equals("jar")) {
			throw new IllegalStateException();
		}

		return url.getFile().replaceFirst("^file:(.*)!.*", "$1");
	}
}
