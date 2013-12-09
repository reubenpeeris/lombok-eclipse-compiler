package com.reubenpeeris.maven.lombokeclipsecompiler;

import java.io.File;

import org.codehaus.plexus.compiler.CompilerResult;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsArrayContainingInOrder.arrayContaining;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class LombokEclipseCompilerAsserter extends LombokEclipseCompiler {
    private Class<? extends OutputProcessor> expectedOutputProcessor;
    private File expectedWorkingDirectory;
    private String[] expectedCommandLine;
    private CompilerResult expectedCompilerResult;

    public LombokEclipseCompilerAsserter outputProcessor(Class<? extends OutputProcessor> expectedOutputProcessor) {
        this.expectedOutputProcessor = expectedOutputProcessor;
        return this;
    }

    public LombokEclipseCompilerAsserter workingDirectory(File expectedWorkingDirectory) {
        this.expectedWorkingDirectory = expectedWorkingDirectory;
        return this;
    }

    public LombokEclipseCompilerAsserter commandLine(String[] expectedCommandLine) {
        this.expectedCommandLine = expectedCommandLine;
        return this;
    }

    public LombokEclipseCompilerAsserter compilerResult(CompilerResult expectedCompilerResult) {
        this.expectedCompilerResult = expectedCompilerResult;
        return this;
    }

    @Override
    public CompilerResult runCommand(File workingDirctory, String[] commandLine, OutputProcessor outputProcessor) {
        if (expectedOutputProcessor != null) {
            assertEquals(expectedOutputProcessor, outputProcessor.getClass());
        }

        if (expectedWorkingDirectory != null) {
            assertThat(workingDirctory, is(equalTo(expectedWorkingDirectory)));
        }

        if (expectedCommandLine != null) {
            assertThat(commandLine, is(arrayContaining(expectedCommandLine)));
        }

        return expectedCompilerResult;
    }
}
