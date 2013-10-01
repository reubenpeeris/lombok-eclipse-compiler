package com.reubenpeeris.maven.lombokeclipsecompiler;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.codehaus.plexus.compiler.CompilerMessage;

public interface OutputProcessor {
    void process(InputStream inputStream) throws IOException;
    List<CompilerMessage> getMessages();
}
