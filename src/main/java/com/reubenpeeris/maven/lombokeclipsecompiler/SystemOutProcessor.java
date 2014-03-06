package com.reubenpeeris.maven.lombokeclipsecompiler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.codehaus.plexus.compiler.CompilerMessage;
import org.codehaus.plexus.util.IOUtil;

public class SystemOutProcessor implements OutputProcessor {

	@Override
	public void process(InputStream inputStream) throws IOException {
		if (inputStream == null) {
			throw new NullPointerException("inputStream");
		}
		IOUtil.copy(inputStream, System.out);
	}

	@Override
	public List<CompilerMessage> getMessages() {
		return Collections.emptyList();
	}
}
