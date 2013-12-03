package com.reubenpeeris.maven.lombokeclipsecompiler;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import org.codehaus.plexus.util.StringInputStream;
import org.codehaus.plexus.util.StringOutputStream;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

public class SystemOutProcessorTest {
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private final SystemOutProcessor systemOutProcessor = new SystemOutProcessor();

	@Test
	public void verifyProcessThrowsOnNullInput() throws IOException {
		thrown.expect(NullPointerException.class);
		thrown.expectMessage("inputStream");

		systemOutProcessor.process(null);
	}

	@Test
	public void verifyProcessCopiesInputToSystemOut() throws IOException {
		String inString = "some text";
		InputStream inputStream = new StringInputStream(inString);
		StringOutputStream out = new StringOutputStream();
		SystemOutSetter outSetter = new SystemOutSetter(new PrintStream(out));
		try {
			systemOutProcessor.process(inputStream);

			assertThat(systemOutProcessor.getMessages(), hasSize(0));
			assertThat(out.toString(), is(equalTo(inString)));
		} finally {
			outSetter.close();
		}
	}

	private static class SystemOutSetter implements AutoCloseable {
		private final PrintStream original;

		public SystemOutSetter(PrintStream out) {
			original = System.out;
			System.setOut(out);
		}

		@Override
		public void close() {
			System.setOut(original);
		}
	}
}
