package com.reubenpeeris.maven.lombokeclipsecompiler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

import static org.hamcrest.CoreMatchers.*;

public class UtilsTest {
	private static final String VALID_REGEX = ".*/library-1.jar";
	private static final String VALID_DESCRIPTION = "my-library jar";
	private static final String MATCHING_ENTRY = "/path/to/library-1.jar";
	private static final String NON_MATCHING_ENTRY = "/path/to/library-2.jar";
	private static final List<String> VALID_LIST = Arrays.asList(MATCHING_ENTRY, NON_MATCHING_ENTRY);

	@Rule
	public ExpectedException thrown= ExpectedException.none();

	@Test
	public void testGetFromListNullRegex() {
		thrown.expect(NullPointerException.class);
		thrown.expectMessage("regex");

		Utils.getMatchingPath(null, VALID_LIST, VALID_DESCRIPTION);
	}

	@Test
	public void testGetFromListNullPaths() {
		thrown.expect(NullPointerException.class);
		thrown.expectMessage("paths");

		Utils.getMatchingPath(VALID_REGEX, null, VALID_DESCRIPTION);
	}

	@Test
	public void testGetFromListNullDescription() {
		thrown.expect(NullPointerException.class);
		thrown.expectMessage("description");

		Utils.getMatchingPath(VALID_REGEX, VALID_LIST, null);
	}

	@Test
	public void testGetFromListInvalidPattern() {
		thrown.expect(PatternSyntaxException.class);

		Utils.getMatchingPath("*", VALID_LIST, VALID_DESCRIPTION);
	}

	@Test
	public void testGetFromListNoMatchingEntries() {
		String lombokJar = Utils.getMatchingPath(VALID_REGEX, Collections.<String> emptyList(), VALID_DESCRIPTION);
		assertNull(lombokJar);
	}

	@Test
	public void testGetFromListNoMultipleEntries() {
		String regex = ".*";
		try {
			Utils.getMatchingPath(regex, VALID_LIST, VALID_DESCRIPTION);
			fail();
		} catch (IllegalStateException e) {
			assertEquals("Multiple " + VALID_DESCRIPTION
					+ " found using pattern '" + regex + "': " + VALID_LIST,
					e.getMessage());
		}
	}

	@Test
	public void testGetFromListValid() {
		String resultFound = Utils.getMatchingPath(VALID_REGEX, VALID_LIST, VALID_DESCRIPTION);
		assertEquals(MATCHING_ENTRY, resultFound);
	}

	@Test
	public void testToPathNList() {
		thrown.expect(NullPointerException.class);
		thrown.expectMessage("list");

		Utils.toPath(null);
	}

	@Test
	public void testToPathEmptyList() {
		assertEquals("", Utils.toPath(Collections.<String> emptyList()));
	}

	@Test
	public void testToPathSlash() {
		verifyToPath("/");
	}

	@Test
	public void testToPathBackSlash() {
		verifyToPath("\\");
	}

	@Test
	public void testFindJavaForLinux() {
		verifyFindJava("linux", "java");
	}

	@Test
	public void testFindJavaForWindows() {
		verifyFindJava("windows", "java.exe");
	}

	@Test
	public void testFindJavaFails() {
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Failed to find the java binary");

		verifyFindJava("fake", null);
	}

	@Test
	public void testGetJarForWithNullClass() {
		thrown.expect(NullPointerException.class);
		thrown.expectMessage("clazz");

		Utils.getJarFor(null);
	}

	@Test
	public void testGetJarForBootClassLoaderClass() {
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Class loaded by boot ClassLoader");

		Utils.getJarFor(Object.class);
	}

	@Test
	public void testGetJarForNoneJarClass() {
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Class not in a jar");

		Utils.getJarFor(UtilsTest.class);
	}

	@Test
	public void testGetJarForJarClass() {
		String jar = Utils.getJarFor(Test.class);
		File file = new File(jar);

		assertThat(file.exists(), is(true));
		assertThat(file.getName(), is(equalTo("junit-4.11.jar")));
	}

	@Test
	public void getFileThrowsForNullResource() {
		thrown.expect(NullPointerException.class);
		thrown.expectMessage("resourcePath");
		Utils.getFile(null);
	}

	@Test
	public void getFileThrowsForEmptyResource() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("resourcePath is empty");
		Utils.getFile("");
	}

	@Test
	public void getFileReturnsFileForFileSystemResource() throws IOException {
		File existingFile = File.createTempFile("testFile", ".txt");
		existingFile.deleteOnExit();
		File file = Utils.getFile(existingFile.getAbsolutePath());
		assertEquals(existingFile.getAbsolutePath(), file.getAbsolutePath());
	}

	@Test
	public void getFileReturnsFileWithExpectedContentsForClasspathResource() throws IOException {
		File file = Utils.getFile("classpathResource");
		StringWriter writer = new StringWriter();
		IOUtils.copy(new FileInputStream(file), writer);
		assertEquals("this is a resource on the classpath", writer.toString());
	}

	private void verifyFindJava(String systemType, String executable) {
		File folder = new File(getClass().getResource("").getFile());
		File javaHome = new File(folder, systemType);
		PropertySetter propertySetter = new PropertySetter("java.home", javaHome.getAbsolutePath());
		try {
			File java = Utils.findJava();
			assertThat(java, is(equalTo(new File(new File(javaHome, "bin"), executable))));
		} finally {
			propertySetter.close();
		}
	}

	private void verifyToPath(String separator) {
		PropertySetter propertySetter = new PropertySetter("path.separator", separator);
		try {
			List<String> someFiles = Arrays.asList("one", "two", "three");
			String path = Utils.toPath(someFiles);

			assertThat(path, is(equalTo("one" + separator + "two" + separator + "three")));
		} finally {
			propertySetter.close();
		}
	}

	private static class PropertySetter implements AutoCloseable {
		private final String property;
		private final String initialValue;

		public PropertySetter(String property, String value) {
			this.property = property;
			this.initialValue = System.getProperty(property);
			System.setProperty(property, value);
		}

		@Override
		public void close() {
			System.setProperty(property, initialValue);
		}
	}
}
