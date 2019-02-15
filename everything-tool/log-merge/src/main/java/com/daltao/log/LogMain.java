package com.daltao.log;

import com.daltao.exception.UnexpectedException;
import com.daltao.template.KMPAutomaton;
import com.daltao.utils.StringUtils;
import com.google.common.base.Predicates;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LogMain {
    /**
     * java -jar xxx ./* --region="1,2" --trim-head --trim-tail --delimiter="-------" --output-delimiter --charset=utf8
     *
     * @param args
     */
    public static void main(String[] args) throws IOException {
        CommandLineArguments arguments = new CommandLineArguments(args);
        KMPAutomaton automaton = buildAutomaton(escape(arguments.get("--delimiter", "\n")));
        String outputDelimiter = escape(arguments.get("--output-delimiter", "\n"));
        boolean trimHead = arguments.contain("--trim-head");
        boolean trimTail = arguments.contain("--trim-tail");
        Function<String, String> function = s -> StringUtils.trim(s, trimHead, trimTail);

        Charset charset = getCharset(arguments);
        List<Iterator<String>> logReaderList = arguments.getRequired()
                .stream()
                .map(fileName -> new File(fileName))
                .map(file -> openFile(file, charset))
                .map(reader -> new LogReader(reader, new KMPAutomaton(automaton)))
                .map(logReader -> new TransformedLogReader(logReader, function))
                .map(logReader -> new FilteredLogReader(logReader, Predicates.not(String::isEmpty)))
                .collect(Collectors.toList());

        if (logReaderList.isEmpty()) {
            return;
        }

        LogMerger merger = new LogMerger(logReaderList, getComparator(arguments));

        BufferedOutputStream outputStream = new BufferedOutputStream(System.out);
        byte[] outputDelimiterBytes = outputDelimiter.getBytes(charset);
        while (merger.hasNext()) {
            outputStream.write(merger.next().getBytes(charset));
            outputStream.write(outputDelimiterBytes);
        }

        outputStream.flush();
    }

    private static Comparator<String> getComparator(CommandLineArguments arguments) {
        Comparator<String> comparator = Comparator.naturalOrder();
        if (arguments.contain("--region")) {
            final String[] region = arguments.get("--region", null).split(",");
            int from = Integer.parseInt(region[0]);
            int to = Integer.parseInt(region[1]);
            comparator = (a, b) -> compare(a, b, from, to);
        }
        return comparator;
    }

    private static String escape(String s) {
        return StringUtils.percentageEscapeDecode(s);
    }

    private static Charset getCharset(CommandLineArguments arguments) {
        Charset charset = Charset.defaultCharset();
        if (arguments.contain("--charset")) {
            charset = Charset.forName(arguments.get("--charset", null));
        }
        return charset;
    }

    private static int compare(String a, String b, int from, int to) {
        for (int i = from, until = Math.min(to, Math.min(a.length(), b.length())); i < to; i++) {
            int d = a.charAt(i) - b.charAt(i);
            if (d != 0) {
                return d;
            }
        }
        return a.length() - b.length();
    }

    private static KMPAutomaton buildAutomaton(String s) {
        KMPAutomaton automaton = new KMPAutomaton(s.length());
        for (char c : s.toCharArray()) {
            automaton.build(c);
        }
        return automaton;
    }

    private static Reader openFile(File file, Charset charset) {
        try {
            return new InputStreamReader(new BufferedInputStream(new FileInputStream(file)), charset);
        } catch (FileNotFoundException e) {
            throw new UnexpectedException(e);
        }
    }
}
