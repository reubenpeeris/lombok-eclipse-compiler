package com.reubenpeeris.maven.lombokeclipsecompiler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.codehaus.plexus.compiler.CompilerMessage;

public class ParserProcessor implements OutputProcessor {
    private final List<CompilerMessage> messages = new ArrayList<CompilerMessage>();

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

    private void processString(String input) {
        CompilerMessage message = Parser.parseMessage(input);
        if (message == null) {
            System.out.print(input);
        } else {
            messages.add(message);
        }
    }

    @Override
    public List<CompilerMessage> getMessages() {
        return Collections.unmodifiableList(messages);
    }
}
