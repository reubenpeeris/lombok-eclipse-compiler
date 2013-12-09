package com.reubenpeeris.maven.lombokeclipsecompiler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.codehaus.plexus.util.StringUtils;

public final class Utils {
	private Utils() {}

	public static String getMatchingPath(String regex, List<String> paths, String description) {
		if (regex == null) {
			throw new NullPointerException("regex");
		}
		if (paths == null) {
			throw new NullPointerException("paths");
		}
		if (description == null) {
			throw new NullPointerException("description");
		}

		Pattern pattern = Pattern.compile(regex);

		List<String> results = new LinkedList<String>();
		for (String path : paths) {
			String unixPath = path.replace('\\', '/');
			if (pattern.matcher(unixPath).matches()) {
				results.add(path);
			}
		}

		if (results.size() > 1) {
			throw new IllegalStateException("Multiple " + description + " found using pattern '" + regex + "': " + results);
		}
		if (results.isEmpty()) {
			return null;
		}

		return results.get(0);
	}

	public static String toPath(List<String> list) {
		if (list == null) {
			throw  new NullPointerException("list");
		}
		return StringUtils.join(list.iterator(), System.getProperty("path.separator"));
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
		if (clazz == null) {
			throw new NullPointerException("clazz");
		}

		String slashClass = clazz.getCanonicalName().replace('.', '/') + ".class";
		if (clazz.getClassLoader() == null) {
			throw new IllegalStateException("Class loaded by boot ClassLoader");
		}
		URL url = clazz.getClassLoader().getResource(slashClass);
		if (!url.getProtocol().equals("jar")) {
			throw new IllegalStateException("Class not in a jar");
		}

		return url.getFile().replaceFirst("^file:(.*)!.*", "$1");
	}

	/**
	 * Converts a classpath or filesystem resource to a file. If present the classpath resource will be returned.
	 *
	 * @param resourcePath
	 *			classpath or filesystem path
	 * @return a file system file path
	 */
	public static File getFile(String resourcePath) {
		if (resourcePath == null) {
			throw new NullPointerException("resourcePath");
		}
		if (resourcePath.isEmpty()) {
			throw new IllegalArgumentException("resourcePath is empty");
		}

		try {
			InputStream resource = Utils.class.getResourceAsStream(resourcePath.replaceFirst("^(?!/)", "/"));
			try {
				if (resource != null) {
					File newFile = File.createTempFile("lec-", "");
					newFile.deleteOnExit();
					OutputStream output = new FileOutputStream(newFile);
					try {
						IOUtils.copy(resource, output);
					} finally {
						output.close();
					}

					return newFile;
				}
			} finally {
				if (resource != null) {
					resource.close();
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return new File(resourcePath);
	}
}
