package com.reubenpeeris.maven.lombokeclipsecompiler;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.plexus.compiler.CompilerMessage;
import org.codehaus.plexus.compiler.CompilerMessage.Kind;

public class Parser {
    private static final Pattern PATTERN =
            Pattern.compile(
                    "(?:\\d+\\. )?\\[?(WARNING|ERROR)\\]?\\s*(?:in)?\\s*(.*)(?: \\(at line\\s*(\\d+)\\)|:\\[(\\d+),).*(?:\\r?\\n[^\\^]*)+(?:\\r?\\n(\\s*)([\\^]+).*)\\r?\\n(?:\\s*\\[.*\\]\\s*)?(.*)(?:\\r?\\n)");

    public static List<CompilerMessage> parse(String input) {
        Matcher matcher = PATTERN.matcher(input);
        List<CompilerMessage> messages = new ArrayList<CompilerMessage>();

        while (matcher.find()) {
            messages.add(createMessage(matcher));
        }

        return messages;
    }

    private static CompilerMessage createMessage(Matcher matcher) {
        String file = matcher.group(2);
        Kind kind = "WARNING".equals(matcher.group(1)) ? Kind.WARNING : Kind.ERROR;
        int startLine = getLine(matcher);

        String message = matcher.group(7);

        return new CompilerMessage(file, kind, startLine, 0, startLine, 0, message);
    }

    private static int getLine(Matcher matcher) {
        String possibleLine1 = matcher.group(3);
        String possibleLine2 = matcher.group(4);

        String line;
        if (possibleLine1 != null) {
            line = possibleLine1;
        } else if (possibleLine2 != null) {
            line = possibleLine2;
        } else {
            line = "-1";
        }

        return Integer.parseInt(line);
    }

    public static CompilerMessage parseMessage(String input) {
        Matcher matcher = PATTERN.matcher(input);

        if (matcher.matches()) {
            return createMessage(matcher);
        } else {
            return null;
        }
    }
}
