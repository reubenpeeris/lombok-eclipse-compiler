package com.reubenpeeris.maven.lombokeclipsecompiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.codehaus.plexus.compiler.CompilerMessage;
import org.codehaus.plexus.util.StringUtils;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class Program {
	private static final String WORKING_DIRECTORY_PREFIX = "WorkingDirectory:";
	private static final String ARGUMENTS_PREFIX = "Arguments:";
	private static final String SYSTEM_OUT_TEXT = "System.out";
	
	public static void main(String[] args) {
		System.out.println(WORKING_DIRECTORY_PREFIX + new File("").getAbsolutePath());
		
		boolean fail = args.length > 0 && "FAIL".equals(args[0]);
		
		String arguments = StringUtils.join(args, ",");
		if (fail) {
			arguments = arguments.replaceFirst("[^,]*", "");
		}
		System.out.println(ARGUMENTS_PREFIX + arguments);
		System.err.println(SYSTEM_OUT_TEXT);
		
		if (fail) {
			System.exit(1);
			System.out.println("fail");
		}
	}
	
	public static class ProgramParsingProcessor implements OutputProcessor {
		private File workingDirectory;
		private String[] arguments;
		List<CompilerMessage> messages;
		
		@Override
		public void process(InputStream inputStream) throws IOException {
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String line = reader.readLine();
			assertThat(line, startsWith(WORKING_DIRECTORY_PREFIX));
			workingDirectory = new File(line.substring(WORKING_DIRECTORY_PREFIX.length()));
			
			line = reader.readLine();
			assertThat(line, startsWith(ARGUMENTS_PREFIX));
			String argumentsString = line.substring(ARGUMENTS_PREFIX.length());
			if (argumentsString.isEmpty()) {
				arguments = new String[0];
			} else {
				arguments = argumentsString.split(",");
			}
			
			line = reader.readLine();
			assertThat(line, is(equalTo(SYSTEM_OUT_TEXT)));
			
			assertThat(reader.readLine(), nullValue());
		}

		@Override
		public List<CompilerMessage> getMessages() {
			return messages;
		}
		
		public void setMessages(List<CompilerMessage> messages) {
			this.messages = messages;
		}

		public File getWorkingDirectory() {
			return workingDirectory;
		}

		public String[] getArguments() {
			return arguments;
		}
	}
}
