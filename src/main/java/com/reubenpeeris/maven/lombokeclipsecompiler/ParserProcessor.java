package com.reubenpeeris.maven.lombokeclipsecompiler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.plexus.compiler.CompilerMessage;
import org.codehaus.plexus.compiler.CompilerMessage.Kind;
import org.codehaus.plexus.logging.Logger;

public class ParserProcessor implements OutputProcessor {
	private static final Pattern AT_LINE_PATTERN = Pattern.compile(
			"\\d+\\. (WARNING|ERROR)\\s*in\\s*(.*) \\(at line (\\d+)\\).*\\n"
					+ "[^\\^]*\\n"
					+ "\\s*[\\^]+.*\\n"
					+ "(.*)\\n");
	private static final Pattern JVM_OPTION_PATTERN = Pattern.compile("Unrecognized option: ([^\n]*).*", Pattern.DOTALL);
	private static final Pattern COMPILER_OPTION_PATTERN = Pattern.compile("Unrecognized option : ([^\n]*).*", Pattern.DOTALL);

	private final List<CompilerMessage> messages = new ArrayList<CompilerMessage>();
	private final Logger logger;

	public ParserProcessor(Logger logger) {
		if (logger == null) {
			throw new NullPointerException("logger");
		}
		this.logger = logger;
	}

	@Override
	public void process(InputStream inputStream) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			if ("----------".equals(line)) {
				processString(sb.toString());
				sb = new StringBuilder();
			} else {
				sb.append(line).append('\n');
			}
		}

		processString(sb.toString());
	}

	private CompilerMessage parseMessage(String input) {
		Matcher atLineMatcher = AT_LINE_PATTERN.matcher(input);
		if (atLineMatcher.matches()) {
			String file = atLineMatcher.group(2);
			Kind kind = "WARNING".equals(atLineMatcher.group(1)) ? Kind.WARNING : Kind.ERROR;
			int startLine = Integer.parseInt(atLineMatcher.group(3));

			String message = atLineMatcher.group(4);

			return new CompilerMessage(file, kind, startLine, 0, startLine, 0, message);
		} else {
			Matcher jvmOptionMatcher = JVM_OPTION_PATTERN.matcher(input);
			if (jvmOptionMatcher.matches()) {
				return new CompilerMessage("Unrecognized option: -J" + jvmOptionMatcher.group(1), Kind.ERROR);
			} else {
				Matcher compilerOptionMatcher = COMPILER_OPTION_PATTERN.matcher(input);
				if (compilerOptionMatcher.matches()) {
					return new CompilerMessage("Unrecognized option: " + compilerOptionMatcher.group(1), Kind.ERROR);
				}
			}
			return null;
		}
	}

	private void processString(String input) {
		if (!input.isEmpty()) {
			CompilerMessage message = parseMessage(input);
			if (message == null) {
				for (String line : input.split("\r?\n")) {
					logger.info(line);
				}
			} else {
				messages.add(message);
			}
		}
	}

	@Override
	public List<CompilerMessage> getMessages() {
		return messages;
	}
}
