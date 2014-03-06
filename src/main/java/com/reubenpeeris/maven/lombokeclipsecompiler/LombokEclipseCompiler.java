package com.reubenpeeris.maven.lombokeclipsecompiler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.plexus.compiler.AbstractCompiler;
import org.codehaus.plexus.compiler.CompilerConfiguration;
import org.codehaus.plexus.compiler.CompilerException;
import org.codehaus.plexus.compiler.CompilerOutputStyle;
import org.codehaus.plexus.compiler.CompilerResult;
import org.eclipse.jdt.internal.compiler.batch.Main;

import static com.reubenpeeris.maven.lombokeclipsecompiler.Utils.*;

public class LombokEclipseCompiler extends AbstractCompiler {
	private static final String JVM_PROPERTY_PREFIX = "-J";
	private static final String LOMBOK_JAR_PROPERTY = "-lombokjar";
	private static final String DIRECT_OUTPUT_PROPERTY = "-directoutput";
	private static final String FAIL_ON_WARNING_PROPERTY = "-failOnWarning";
	private static final Set<String> CUSTOM_ARGUMENTS = Collections.unmodifiableSet(
			new HashSet<String>(Arrays.asList(
			LOMBOK_JAR_PROPERTY,
			DIRECT_OUTPUT_PROPERTY,
			FAIL_ON_WARNING_PROPERTY)));
	
	public LombokEclipseCompiler() {
		super(CompilerOutputStyle.ONE_OUTPUT_FILE_PER_INPUT_FILE, ".java", ".class", null);
	}

	@Override
	public CompilerResult performCompile(CompilerConfiguration config) throws CompilerException {
		OutputProcessor outputProcessor;
		if (config.getCustomCompilerArgumentsAsMap().containsKey(DIRECT_OUTPUT_PROPERTY)) {
			outputProcessor = new SystemOutProcessor();
		} else {
			outputProcessor = new ParserProcessor(getLogger());
		}
		
		return runCommand(config.getWorkingDirectory(), createCommandLine(config), failOnWarning(config), outputProcessor);
	}

	private boolean failOnWarning(CompilerConfiguration config) {
		return config.getCustomCompilerArgumentsAsMap().containsKey(FAIL_ON_WARNING_PROPERTY);
	}

	public CompilerResult runCommand(File workingDirectory, String[] commandLine, boolean failOnWarning, OutputProcessor outputProcessor) {
		ProcessBuilder pb = new ProcessBuilder(commandLine);
		pb.redirectErrorStream(true);
		pb.directory(workingDirectory);

		try {
			Process process = pb.start();

			outputProcessor.process(process.getInputStream());
			int result = process.waitFor();

			boolean success = result == 0
					&& (!failOnWarning || outputProcessor.getMessages().isEmpty());
			
			return new CompilerResult(success, outputProcessor.getMessages());
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String[] createCommandLine(CompilerConfiguration config) throws CompilerException {
		File javaExe = findJava();
		String lombokJarRegex = config.getCustomCompilerArgumentsAsMap().get(LOMBOK_JAR_PROPERTY);
		if (lombokJarRegex == null) {
			lombokJarRegex = "(?:.*/)?lombok-[^/]*\\.jar";
		}
		String lombokJar = getMatchingPath(lombokJarRegex, config.getClasspathEntries(), "lombok jars");

		List<String> commandLine = new ArrayList<String>();
		commandLine.add(javaExe.getAbsolutePath());

		String jdtJar = getJarFor(org.eclipse.jdt.internal.compiler.Compiler.class);

		for (Map.Entry<String, String> entry : config.getCustomCompilerArgumentsAsMap().entrySet()) {
			if (entry.getKey().startsWith(JVM_PROPERTY_PREFIX)) {
				commandLine.add(entry.getKey().substring(JVM_PROPERTY_PREFIX.length()));
				if (entry.getValue() != null) {
					commandLine.add(entry.getValue());
				}
			}
		}

		if (config.getMeminitial() != null) {
			commandLine.add("-Xms" + config.getMeminitial());
		}
		if (config.getMaxmem() != null) {
			commandLine.add("-Xmx" + config.getMaxmem());
		}

		commandLine.add("-Xbootclasspath/a:" + jdtJar);

		if (lombokJar != null) {
			getLogger().info("Using Lombok from '" + lombokJar + "'");
			commandLine.add("-Xbootclasspath/a:" + lombokJar);
			commandLine.add("-javaagent:" + lombokJar);
		} else {
			getLogger().info("Lombok not found using pattern '" + lombokJarRegex +"'");
		}
		commandLine.add(Main.class.getCanonicalName());
		commandLine.add("-source");
		commandLine.add(config.getSourceVersion());
		commandLine.add("-target");
		commandLine.add(config.getTargetVersion());
		commandLine.add("-encoding");
		commandLine.add(config.getSourceEncoding());
		commandLine.add("-cp");
		commandLine.add(toPath(config.getClasspathEntries()));
		commandLine.add("-d");
		commandLine.add(config.getOutputLocation());

		for (Map.Entry<String, String> entry : config.getCustomCompilerArgumentsAsMap().entrySet()) {
			if (!entry.getKey().startsWith(JVM_PROPERTY_PREFIX)
					&& !CUSTOM_ARGUMENTS.contains(entry.getKey())) {
				commandLine.add(entry.getKey());
				if (entry.getKey().equals("-properties")) {
					commandLine.add(Utils.getFile(entry.getValue()).getAbsolutePath());
				} else if (entry.getValue() != null) {
					commandLine.add(entry.getValue());
				}
			}
		}

		commandLine.addAll(config.getSourceLocations());
		return commandLine.toArray(new String[commandLine.size()]);
	}
}
