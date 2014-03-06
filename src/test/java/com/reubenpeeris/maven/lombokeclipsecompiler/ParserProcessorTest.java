package com.reubenpeeris.maven.lombokeclipsecompiler;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.codehaus.plexus.compiler.CompilerMessage;
import org.codehaus.plexus.compiler.CompilerMessage.Kind;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.util.StringInputStream;
import org.codehaus.plexus.util.StringUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.reubenpeeris.maven.lombokeclipsecompiler.ListLogger.Message;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertThat;

public class ParserProcessorTest {
	private static final String PARSEABLE_MESSAGE_PATTERN = "%s. %s in %s (at line %s)\n"
			+ "\tpublic class TestClass implements Serializable {\n"
			+ "\t			  ^^^^^^^^^\n"
			+ "The serializable class TestClass does not declare a static final serialVersionUID field of type long\n";

	private static String DEFAULT_MESSAGE_NUMBER = "1";
	private static String DEFAULT_KIND = "ERROR";
	private static String DEFAULT_FILE = "";
	private static String DEFAULT_LINE_NUMBER = "3";

	private static final String NON_PARSEABLE_MESSAGE = "multiple Class-Path headers in manifest of jar file: "
			+ "/home/reuben/.m2/repository/com/sun/xml/ws/webservices-rt/2.0.1/webservices-rt-2.0.1.jar";
	private static final String SPACER = "----------\n";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private ListLogger logger;
	private ParserProcessor parserProcessor;

	@Before
	public void setUp() {
		logger = new ListLogger();
		parserProcessor = new ParserProcessor(logger);
	}

	@Test
	public void verifyBasicInputParsesCorrectly() throws IOException {
		verifyMessageParses(DEFAULT_MESSAGE_NUMBER, DEFAULT_KIND, DEFAULT_FILE, DEFAULT_LINE_NUMBER);
	}

	@Test
	public void verifyWindowsEolBasicInputParsesCorrectly() throws IOException {
		verifyMessageParses(PARSEABLE_MESSAGE_PATTERN.replaceAll("\n","\r\n"), DEFAULT_MESSAGE_NUMBER, DEFAULT_KIND, DEFAULT_FILE, DEFAULT_LINE_NUMBER);
	}

	@Test
	public void verifyNonNumericMessageNumberDoesNotParse() throws IOException {
		verifyMessageDoesNotParse("a", DEFAULT_KIND, DEFAULT_FILE, DEFAULT_LINE_NUMBER);
	}

	@Test
	public void verifyWarningParsesCorrectly() throws IOException {
		verifyMessageParses(DEFAULT_MESSAGE_NUMBER, "WARNING", DEFAULT_FILE, DEFAULT_LINE_NUMBER);
	}

	@Test
	public void verifyInvalidKindDoesNotParse() throws IOException {
		verifyMessageDoesNotParse(DEFAULT_MESSAGE_NUMBER, "FAKE KIND", DEFAULT_FILE, DEFAULT_LINE_NUMBER);
	}

	@Test
	public void verifyFileWithWindowsPathParses() throws IOException {
		verifyMessageParses(DEFAULT_MESSAGE_NUMBER, DEFAULT_KIND, "c:\\windows\\style\\path", DEFAULT_LINE_NUMBER);
	}

	@Test
	public void verifyFileWithSpaceParses() throws IOException {
		verifyMessageParses(DEFAULT_MESSAGE_NUMBER, DEFAULT_KIND, "file with spaces", DEFAULT_LINE_NUMBER);
	}

	@Test
	public void verifyDifferentLineNumberParses() throws IOException {
		verifyMessageParses(DEFAULT_MESSAGE_NUMBER, DEFAULT_KIND, DEFAULT_FILE, "100");
	}

	@Test
	public void verifyNonNumericLineNumberDoesNotParse() throws IOException {
		verifyMessageDoesNotParse(DEFAULT_MESSAGE_NUMBER, DEFAULT_KIND, DEFAULT_FILE, "a");
	}

	@Test
	public void verifyConstructorThrowsForNullInput() {
		thrown.expect(NullPointerException.class);
		thrown.expectMessage("logger");

		new ParserProcessor(null);
	}

	@Test
	public void verifyZeroInputGivesNoOutput() throws IOException {
		assertProcessorOutput("", Collections.<CompilerMessage>emptyList(), Collections.<Message>emptyList());
	}

	@Test
	public void verifyLoneMessageComesOut() throws IOException {
		assertProcessorOutput(NON_PARSEABLE_MESSAGE, Collections.<CompilerMessage>emptyList(), Arrays.asList(new Message(Logger.LEVEL_INFO, NON_PARSEABLE_MESSAGE)));
	}

	@Test
	public void verifyMultipleUnixMessageComeOutAsMultipleMessages() throws IOException {
		verifyMultipleMessageComeOutAsMultipleMessages("\n");
	}

	@Test
	public void verifyMultipleWindowsMessageComeOutAsMultipleMessages() throws IOException {
		verifyMultipleMessageComeOutAsMultipleMessages("\r\n");
	}

	@Test
	public void verifyMessageBeforeSpacerComesOut() throws IOException {
		assertProcessorOutput(NON_PARSEABLE_MESSAGE + "\n" + SPACER, Collections.<CompilerMessage> emptyList(),
				Arrays.asList(new Message(Logger.LEVEL_INFO, NON_PARSEABLE_MESSAGE)));
	}

	@Test
	public void verifyMessageAfterSpacerComesOut() throws IOException {
		assertProcessorOutput(SPACER + NON_PARSEABLE_MESSAGE, Collections.<CompilerMessage>emptyList(), Arrays.asList(new Message(Logger.LEVEL_INFO, NON_PARSEABLE_MESSAGE)));
	}

	private void verifyMessageDoesNotParse(String messageNumber, String kind, String file, String lineNumber) throws IOException {
		String input = String.format(PARSEABLE_MESSAGE_PATTERN,  messageNumber, kind, file, lineNumber);
		parserProcessor.process(new StringInputStream(input));
		assertThat(parserProcessor.getMessages(), hasSize(0));
	}

	public void verifyMultipleMessageComeOutAsMultipleMessages(String eol) throws IOException {
		Message message = new Message(Logger.LEVEL_INFO, NON_PARSEABLE_MESSAGE);
		assertProcessorOutput(NON_PARSEABLE_MESSAGE + eol + NON_PARSEABLE_MESSAGE, Collections.<CompilerMessage> emptyList(),
				Arrays.asList(message, message));
	}

	private void verifyMessageParses(String pattern, String messageNumber, String kind, String file, String lineNumber) throws IOException {
		String input = String.format(pattern,  messageNumber, kind, file, lineNumber);
		parserProcessor.process(new StringInputStream(input));
		List<CompilerMessage> actual = parserProcessor.getMessages();
		CompilerMessage expected = new CompilerMessage(file,
				Kind.valueOf(kind),
				Integer.parseInt(lineNumber),
				0,
				Integer.parseInt(lineNumber),
				0,
				"The serializable class TestClass does not declare a static final serialVersionUID field of type long");
		assertEqual(expected, actual);
	}

	private void verifyMessageParses(String messageNumber, String kind, String file, String lineNumber) throws IOException {
		verifyMessageParses(PARSEABLE_MESSAGE_PATTERN, messageNumber, kind, file, lineNumber);
	}

	private void assertEqual(CompilerMessage expected, List<CompilerMessage> actual) {
		assertThat(Lists.transform(actual, messageToString), contains(messageToString.apply(expected)));
	}

	public static Function<CompilerMessage, String> messageToString = new Function<CompilerMessage, String>() {
		@Override
		public String apply(CompilerMessage message) {
			return StringUtils.join(new Object[]{message.getFile(),
					message.getKind(),
					message.getStartLine(),
					message.getStartColumn(),
					message.getEndLine(),
					message.getEndColumn(),
					message.getMessage()}, ",");
		}
	};

	private void assertProcessorOutput(String input, List<CompilerMessage> compilerMessages, List<Message> loggerMessages) throws IOException {
		parserProcessor.process(new StringInputStream(input));
		assertThat(Lists.transform(parserProcessor.getMessages(), messageToString), is(equalTo(Lists.transform(compilerMessages, messageToString))));

		assertThat(logger.getMessages(), is(equalTo(loggerMessages)));
	}
}
