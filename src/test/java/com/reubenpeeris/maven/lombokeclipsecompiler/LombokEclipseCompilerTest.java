package com.reubenpeeris.maven.lombokeclipsecompiler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.compiler.CompilerConfiguration;
import org.codehaus.plexus.compiler.CompilerException;
import org.codehaus.plexus.compiler.CompilerMessage;
import org.codehaus.plexus.compiler.CompilerMessage.Kind;
import org.codehaus.plexus.compiler.CompilerOutputStyle;
import org.codehaus.plexus.compiler.CompilerResult;
import org.codehaus.plexus.logging.Logger;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.reubenpeeris.maven.lombokeclipsecompiler.ListLogger.Message;
import com.reubenpeeris.maven.lombokeclipsecompiler.Program.ProgramParsingProcessor;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.CoreMatchers.theInstance;
import static org.hamcrest.collection.IsArrayWithSize.arrayWithSize;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

public class LombokEclipseCompilerTest {
	private static final int JAVA_INDEX = 0;
	private static final int COMPILER_JAR_INDEX = 1;
	private static final int MAIN_CLASS_INDEX = 2;
	private static final int _SOURCE_INDEX = 3;
	private static final int SOURCE_INDEX = 4;
	private static final int _TARGET_INDEX = 5;
	private static final int TARGET_INDEX = 6;
	private static final int _ENCODING_INDEX = 7;
	private static final int ENCODING_INDEX = 8;
	private static final int _CLASSPATH_INDEX = 9;
	private static final int CLASSPATH_INDEX = 10;
	private static final int _DESTINATION_INDEX = 11;
	private static final int DESTINATION_INDEX = 12;
	
	private static final int JVM_ARGUMENTS_STARTING_INDEX = 1;
	private static final int LOMBOK_JAR_INDEX = 2;
	private static final int CUSTOM_ARGUMENTS_STARTING_INDEX = 13;
	
	private static final String A_STRING = "a string value";
	private static final String JVM_PREFIX = "-J";
	
	
	private ListLogger logger;
	private LombokEclipseCompiler compiler;
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Before
	public void setUpCompiler() {
		logger = new ListLogger();
		compiler = new LombokEclipseCompiler();
		compiler.enableLogging(logger);
	}
	
	@Test
	public void verifyCompilerMode() {
		assertThat(compiler.getCompilerOutputStyle(), is(equalTo(CompilerOutputStyle.ONE_OUTPUT_FILE_PER_INPUT_FILE)));
	}
	
	@Test
	public void verifyInputFileEndings() throws CompilerException {
		assertThat(compiler.getInputFileEnding(null), is(equalTo(".java")));
	}
	
	@Test
	public void verifyOutputFileEndings() throws CompilerException {
		assertThat(compiler.getOutputFileEnding(null), is(equalTo(".class")));
	}
	
	@Test
	public void verifyStaticCommandLineArgumens() throws CompilerException {
		String[] commandLine = compiler.createCommandLine(new CompilerConfiguration());
		
		assertThat(commandLine[MAIN_CLASS_INDEX], is(equalTo("org.eclipse.jdt.internal.compiler.batch.Main")));
		assertThat(commandLine[_SOURCE_INDEX], is(equalTo("-source")));
		assertThat(commandLine[SOURCE_INDEX], is(nullValue()));
		assertThat(commandLine[_TARGET_INDEX], is(equalTo("-target")));
		assertThat(commandLine[TARGET_INDEX], is(nullValue()));
		assertThat(commandLine[_ENCODING_INDEX], is(equalTo("-encoding")));
		assertThat(commandLine[ENCODING_INDEX], is(nullValue()));
		assertThat(commandLine[_CLASSPATH_INDEX], is(equalTo("-cp")));
		assertThat(commandLine[CLASSPATH_INDEX], is(equalTo("")));
		assertThat(commandLine[_DESTINATION_INDEX], is(equalTo("-d")));
		assertThat(commandLine[DESTINATION_INDEX], is(nullValue()));
		
		assertThat(commandLine, arrayWithSize(DESTINATION_INDEX + 1));
	}
	
	@Test
	public void verifyJavaCommandLineArgument() throws CompilerException {
		String[] commandLine = compiler.createCommandLine(new CompilerConfiguration());
		assertThat(commandLine[JAVA_INDEX], is(equalTo(Utils.findJava().getAbsolutePath())));
	}
	
	@Test
	public void verifyComplierJarCommandLineArgument() throws CompilerException {
		String[] commandLine = compiler.createCommandLine(new CompilerConfiguration());
		assertThat(commandLine[COMPILER_JAR_INDEX], startsWith("-Xbootclasspath/a:"));
		assertThat(commandLine[COMPILER_JAR_INDEX], endsWith("repository/org/eclipse/tycho/org.eclipse.jdt.core/3.9.1.v20130905-0837/org.eclipse.jdt.core-3.9.1.v20130905-0837.jar"));
	}
	
	@Test
	public void verifySourceCommandLineArgument() throws CompilerException {
		// /usr/lib/jvm/java-7-openjdk-amd64/jre/bin/java, -Xbootclasspath/a:/home/reuben/.m2/repository/org/eclipse/tycho/org.eclipse.jdt.core/3.9.1.v20130905-0837/org.eclipse.jdt.core-3.9.1.v20130905-0837.jar, org.eclipse.jdt.internal.compiler.batch.Main, -source, 1.7, -target, 1.7, -encoding, UTF-8, -cp, /home/reuben/workspace/github/lombok-compiler/test-project/target/classes, -d, /home/reuben/workspace/github/lombok-compiler/test-project/target/classes, -properties, .settings/org.eclipse.jdt.core.prefs, /home/reuben/workspace/github/lombok-compiler/test-project/src/main/java

		CompilerConfiguration config = new CompilerConfiguration();
		config.setSourceVersion(A_STRING);
		assertCommandLineArgument(config, SOURCE_INDEX);
	}
	
	@Test
	public void verifyTargetCommandLineArgument() throws CompilerException {
		CompilerConfiguration config = new CompilerConfiguration();
		config.setTargetVersion(A_STRING);
		assertCommandLineArgument(config, TARGET_INDEX);
	}
	
	@Test
	public void verifyEncodingCommandLineArgument() throws CompilerException {
		CompilerConfiguration config = new CompilerConfiguration();
		config.setSourceEncoding(A_STRING);
		assertCommandLineArgument(config, ENCODING_INDEX);
	}
	
	@Test
	public void verifyDestinationCommandLineArgument() throws CompilerException {
		CompilerConfiguration config = new CompilerConfiguration();
		config.setOutputLocation(A_STRING);
		assertCommandLineArgument(config, DESTINATION_INDEX);
	}
	
	@Test
	public void verifyClasspathCommandLineArgumentForMultipleEntries() throws CompilerException {
		CompilerConfiguration config = new CompilerConfiguration();
		List<String> entries = Arrays.asList("one", "two", "three");
		config.setClasspathEntries(entries);
		String[] commandLine = compiler.createCommandLine(config);
		assertThat(commandLine[CLASSPATH_INDEX], is(equalTo(Utils.toPath(entries))));
	}
	
	@Test
	public void verifyJvmArgumentsWithValue() throws CompilerException {
		String argument =  "-arg";
		CompilerConfiguration config = configWithCustomArgument(JVM_PREFIX + argument, A_STRING);
		assertCustomArgumens(config, JVM_ARGUMENTS_STARTING_INDEX, argument, A_STRING);
	}
	
	@Test
	public void verifyJvmArgumentsWithoutValue() throws CompilerException {
		String argument =  "-arg";
		CompilerConfiguration config = configWithCustomArgument(JVM_PREFIX + argument, null);
		String[] commandLine = assertCustomArgumens(config, JVM_ARGUMENTS_STARTING_INDEX, argument);
		assertThat(commandLine[JVM_ARGUMENTS_STARTING_INDEX + 1], startsWith("-Xbootclasspath/a:"));
	}
	
	@Test
	public void verifyCompilerPropertiesArgumentWithFile() throws CompilerException {
		String argument =  "-properties";
		CompilerConfiguration config = configWithCustomArgument(argument, A_STRING);
		assertCustomArgumens(config, CUSTOM_ARGUMENTS_STARTING_INDEX, argument, new File(A_STRING).getAbsolutePath());
	}
	
	@Test
	public void verifyCompilerPropertiesArgumentWithClasspathResourceCreatesFile() throws CompilerException {
		String argument =  "-properties";
		CompilerConfiguration config = configWithCustomArgument(argument, "classpathResource");
		String[] commandLine = compiler.createCommandLine(config);
		String propertiesArgument = commandLine[CUSTOM_ARGUMENTS_STARTING_INDEX];
		assertThat(propertiesArgument, is(equalTo(argument)));
		String fileArgument = commandLine[CUSTOM_ARGUMENTS_STARTING_INDEX + 1];
		assertTrue(new File(fileArgument).exists());
	}
	
	@Test
	public void verifyCompilerArgumentsWithValue() throws CompilerException {
		String argument =  "-arg";
		CompilerConfiguration config = configWithCustomArgument(argument, A_STRING);
		assertCustomArgumens(config, CUSTOM_ARGUMENTS_STARTING_INDEX, argument, A_STRING);
	}
	
	@Test
	public void verifyCompilerArgumentsWithoutValue() throws CompilerException {
		String argument =  "-arg";
		CompilerConfiguration config = configWithCustomArgument(argument, null);
		String[] commandLine = assertCustomArgumens(config, CUSTOM_ARGUMENTS_STARTING_INDEX, argument);
		assertThat(commandLine, arrayWithSize(CUSTOM_ARGUMENTS_STARTING_INDEX + 1));
	}
	
	@Test
	public void verifyInitialMemoryArguments() throws CompilerException {
		CompilerConfiguration config = new CompilerConfiguration();
		config.setMeminitial(A_STRING);
		assertCustomArgumens(config, JVM_ARGUMENTS_STARTING_INDEX, "-Xms" + A_STRING);
	}
	
	@Test
	public void verifyMaxMemoryArguments() throws CompilerException {
		CompilerConfiguration config = new CompilerConfiguration();
		config.setMaxmem(A_STRING);
		assertCustomArgumens(config, JVM_ARGUMENTS_STARTING_INDEX, "-Xmx" + A_STRING);
	}
	
	@Test
	public void verifyUnixLombokJarAddedToArguments() throws CompilerException {
		assertLombokJar("/usr/share/lombok/lombok-0.12.jar", null);
	}
	
	@Test
	public void verifyWindowsLombokJarAddedToArguments() throws CompilerException {
		assertLombokJar("\\\\usr\\\\share\\\\lombok\\\\lombok-0.12.jar", null);
	}
	
	@Test
	public void verifyUnixLombokJarAddedToArgumentsWithCustomPatterm() throws CompilerException {
		assertLombokJar("/usr/share/lombok/nubok.jar", ".*/nubok\\.jar");
	}
	
	@Test
	public void verifyWindowsLombokJarAddedToArgumentsWithCustomPatterm() throws CompilerException {
		assertLombokJar("\\\\usr\\\\share\\\\lombok\\\\nubok.jar", ".*/nubok\\.jar");
	}
	
	@Test
	public void verifyLombokJarInRootFolderAddedToArguments() throws CompilerException {
		assertLombokJar("lombok-0.12.jar", null);
	}
	
	@Test
	public void multipleLombokJarsThrows() throws CompilerException {
		CompilerConfiguration config = new CompilerConfiguration();
		config.addClasspathEntry("lombok-0.1.jar");
		config.addClasspathEntry("lombok-0.2.jar");
		
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Multiple lombok jars found using pattern '(?:.*/)?lombok-[^/]*\\.jar': [lombok-0.1.jar, lombok-0.2.jar]");
		compiler.createCommandLine(config);
	}
	
	@Test
	public void verifyLombokNotFoundMessage() throws CompilerException {
		CompilerConfiguration config = new CompilerConfiguration();
		compiler.createCommandLine(config);
		
		assertThat(logger.getMessages(), contains(new Message(Logger.LEVEL_INFO, "Lombok not found using pattern '(?:.*/)?lombok-[^/]*\\.jar'")));
	}
	
	@Test
	public void verifyLombokFoundMessage() throws CompilerException {
		CompilerConfiguration config = new CompilerConfiguration();
		config.addClasspathEntry("lombok-0.1.jar");
		compiler.createCommandLine(config);
		
		assertThat(logger.getMessages(), contains(new Message(Logger.LEVEL_INFO, "Using Lombok from 'lombok-0.1.jar'")));
	}
	
	@Test
	public void verifyDefaultOutputIsParser() throws CompilerException {
		LombokEclipseCompilerAsserter compiler = assertingCompiler();
		compiler.outputProcessor(ParserProcessor.class);
		CompilerConfiguration config = new CompilerConfiguration();
		compiler.performCompile(config);
	}

	@Test
	public void verifySystemOutProcessor() throws CompilerException {
		LombokEclipseCompilerAsserter compiler = assertingCompiler();
		compiler.outputProcessor(SystemOutProcessor.class);
		CompilerConfiguration config = configWithCustomArgument("-directoutput", null);
		compiler.performCompile(config);
	}
	
	@Test
	public void verifyBasicRunCommand() {
		runCommand(rootFolder(), true, true, false, Collections.<CompilerMessage>emptyList());
	}
	
	@Test
	public void verifyRunCommandWorkingDirectory() throws IOException {
		File file = File.createTempFile("prefix", "suffix");
		file.deleteOnExit();
		runCommand(file.getParentFile(), true, true, false, Collections.<CompilerMessage>emptyList());
	}
	
	@Test
	public void verifyRunCommandExitFailure() {
		runCommand(rootFolder(), false, false, false, Collections.<CompilerMessage>emptyList());
	}
	
	@Test
	public void verifyRunCommandMessages() {
		runCommand(rootFolder(), true, true, false, Arrays.asList(new CompilerMessage("A message", Kind.ERROR)));
	}
	
	@Test
	public void verifyRunCommandWithWarning() {
		runCommand(rootFolder(), true, true, false, Arrays.asList(new CompilerMessage("A message", Kind.WARNING)));
	}
	
	@Test
	public void verifyRunCommandWithWarningAndFailOnWarning() {
		runCommand(rootFolder(), true, false, true, Arrays.asList(new CompilerMessage("A message", Kind.WARNING)), LombokEclipseCompiler.W_ERROR_MESSAGE, "-M-failOnWarning");
	}

	@Test
	public void verifyRunCommandArguments() {
		runCommand(rootFolder(), true, true, false, Collections.<CompilerMessage>emptyList(), "arg-one", "arg-two");
	}
	
	@Test
	public void verifyPerformCompileWorkingDirectory() throws CompilerException {
		File workingDirectory = rootFolder();
		CompilerConfiguration config = new CompilerConfiguration();
		config.setWorkingDirectory(workingDirectory);
		
		assertingCompiler().workingDirectory(workingDirectory).performCompile(config);
	}
	
	@Test
	public void verifyPerformCompileArgumnets() throws CompilerException {
		CompilerConfiguration config = new CompilerConfiguration();
		String[] expectedCommandLine = compiler.createCommandLine(config);
		
		assertingCompiler().commandLine(expectedCommandLine).performCompile(config);
	}
	
	@Test
	public void verifyPerformCompileCompilerResult() throws CompilerException {
		CompilerConfiguration config = new CompilerConfiguration();
		CompilerResult expectedCompilerResult = new CompilerResult();
		
		CompilerResult result = assertingCompiler().compilerResult(expectedCompilerResult).performCompile(config);
		assertThat(result, theInstance(expectedCompilerResult));
	}
	
	private File rootFolder() {
		return File.listRoots()[0];
	}
	

	private void runCommand(File workingDirectory, boolean success, boolean expectedSuccess, boolean failOnWarning, List<CompilerMessage> messages, String... arguments) {
		runCommand(workingDirectory, success, expectedSuccess, failOnWarning, messages, null, arguments);
	}
	
	private void runCommand(File workingDirectory, boolean success, boolean expectedSuccess, boolean failOnWarning, List<CompilerMessage> messages, CompilerMessage additionalMessage, String... arguments) {
		List<String> commandLine = new ArrayList<String>();
		commandLine.add(Utils.findJava().getAbsolutePath());
		commandLine.add("-cp");
		commandLine.add(System.getProperty("java.class.path"));
		commandLine.add(Program.class.getCanonicalName());
		
		if (!success) {
			commandLine.add("FAIL");
		}
		commandLine.addAll(Arrays.asList(arguments));
		ProgramParsingProcessor processor = new Program.ProgramParsingProcessor();
		processor.setMessages(new ArrayList<CompilerMessage>(messages));

		CompilerResult result = compiler.runCommand(workingDirectory, commandLine.toArray(new String[commandLine.size()]), failOnWarning, processor);
		
		assertThat(processor.getArguments(), is(equalTo(arguments)));
		assertThat(processor.getWorkingDirectory(), is(equalTo(workingDirectory)));
		
		assertThat(result.isSuccess(), is(equalTo(expectedSuccess)));
		List<CompilerMessage> expectedMessages = new ArrayList<CompilerMessage>(messages);
		if (additionalMessage != null) {
			expectedMessages.add(additionalMessage);
		}
		assertThat(result.getCompilerMessages(), is(equalTo(expectedMessages)));
	}
	
	private LombokEclipseCompilerAsserter assertingCompiler() {
		LombokEclipseCompilerAsserter compiler = new LombokEclipseCompilerAsserter();
		compiler.enableLogging(logger);
		
		return compiler;
	}
	
	private void assertLombokJar(String jar, String pattern) throws CompilerException {
		CompilerConfiguration config = new CompilerConfiguration();
		config.addClasspathEntry(jar);
		if (pattern != null) {
			Map<String, String> customCompilerArguments = new HashMap<String, String>();
			customCompilerArguments.put("-lombokjar", pattern);
			config.setCustomCompilerArgumentsAsMap(customCompilerArguments);
		}
		
		assertCustomArgumens(config, LOMBOK_JAR_INDEX, "-Xbootclasspath/a:" + jar, "-javaagent:" + jar);
	}
	
	private CompilerConfiguration configWithCustomArgument(String property, String value) {
		CompilerConfiguration config = new CompilerConfiguration();
		Map<String, String> customCompilerArguments = new HashMap<String, String>();
		customCompilerArguments.put(property, value);
		config.setCustomCompilerArgumentsAsMap(customCompilerArguments);
		
		return config;
	}
	
	private String[] assertCustomArgumens(CompilerConfiguration config, int index, String... expectedArguments) throws CompilerException {
		String[] commandLine = compiler.createCommandLine(config);
		for (String argument: expectedArguments) {
			assertThat(commandLine[index], is(equalTo(argument)));
			index++;
		}
		
		return commandLine;
	}
	
	private void assertCommandLineArgument(CompilerConfiguration config, int index) throws CompilerException {
		String[] commandLine = compiler.createCommandLine(config);
		assertThat(commandLine[index], is(equalTo(A_STRING)));
	}
}
