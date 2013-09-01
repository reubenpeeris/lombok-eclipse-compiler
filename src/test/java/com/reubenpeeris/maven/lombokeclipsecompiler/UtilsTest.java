package com.reubenpeeris.maven.lombokeclipsecompiler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import org.junit.Test;

import com.reubenpeeris.maven.lombokeclipsecompiler.Utils;

import static org.junit.Assert.*;

public class UtilsTest {
	private static final String VALID_REGEX = ".*/library-1.jar";
	private static final String VALID_DESCRIPTION = "my-library jar";
	private static final String MATCHING_ENTRY = "/path/to/library-1.jar";
	private static final String NON_MATCHING_ENTRY = "/path/to/library-2.jar";
	private static final List<String> VALID_LIST = Arrays.asList(
			MATCHING_ENTRY, NON_MATCHING_ENTRY);

	@Test(expected = NullPointerException.class)
	public void testGetFromListNullRegex() {
		Utils.getFromList(null, VALID_LIST, VALID_DESCRIPTION);
	}

	@Test(expected = NullPointerException.class)
	public void testGetFromListNullList() {
		Utils.getFromList(VALID_REGEX, null, VALID_DESCRIPTION);
	}

	@Test(expected = NullPointerException.class)
	public void testGetFromListNullDescription() {
		Utils.getFromList(VALID_REGEX, VALID_LIST, null);
	}

	@Test(expected = PatternSyntaxException.class)
	public void testGetFromListInvalidPattern() {
		Utils.getFromList("*", VALID_LIST, VALID_DESCRIPTION);
	}

	@Test
	public void testGetFromListNoMatchingEntries() {
		String lombokJar = Utils.getFromList(VALID_REGEX,
				Collections.<String> emptyList(), VALID_DESCRIPTION);
		assertNull(lombokJar);
	}

	@Test
	public void testGetFromListNoMultipleEntries() {
		String regex = ".*";
		try {
			Utils.getFromList(regex, VALID_LIST, VALID_DESCRIPTION);
			fail();
		} catch (IllegalStateException e) {
			assertEquals("Multiple " + VALID_DESCRIPTION
					+ " found using regex '" + regex + "': " + VALID_LIST,
					e.getMessage());
		}
	}

	@Test
	public void testGetFromListValid() {
		String resultFound = Utils.getFromList(VALID_REGEX, VALID_LIST,
				VALID_DESCRIPTION);
		assertEquals(MATCHING_ENTRY, resultFound);
	}

	@Test(expected = NullPointerException.class)
	public void testToPathNull() {
		Utils.toPath(null);
	}

	@Test
	public void testToPathEmptyList() {
		assertEquals("", Utils.toPath(Collections.<String> emptyList()));
	}

	private List<String> someFiles() throws IOException {
		int size = 3;
		List<String> list = new ArrayList<String>(size);
		for (int i = 0; i < size; i++) {
			File file = File.createTempFile("toPathTest", null);
			list.add(file.getAbsolutePath());
		}

		return list;
	}

	@Test
	public void testToPathSimple() throws IOException {
		String separator = "-";
		System.setProperty("path.separator", separator);
		List<String> someFiles = someFiles();
		String path = Utils.toPath(someFiles);

		String[] split = path.split(separator);
		assertEquals(someFiles.size(), split.length);
		for (int i = 0; i < split.length; i++) {
			assertEquals(someFiles.get(i), split[i]);
		}
	}

	@Test
	public void testToPathMissingFile() throws IOException {
		List<String> actualFiles = someFiles();
		List<String> fullList = new ArrayList<String>(actualFiles);
		fullList.add("this is not a file path");
		String path = Utils.toPath(fullList);

		String[] split = path.split(System.getProperty("path.separator"));
		assertEquals(actualFiles.size(), split.length);
		for (int i = 0; i < split.length; i++) {
			assertEquals(actualFiles.get(i), split[i]);
		}
	}
}
