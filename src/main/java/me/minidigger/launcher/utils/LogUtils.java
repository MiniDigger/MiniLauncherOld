package me.minidigger.launcher.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import javax.swing.*;

public class LogUtils {

    private static JTextArea textArea;
    private static final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
    private static final List<String> values = new ArrayList<>();
    private static final String LINE_SEPARATOR = System.lineSeparator();

    public static void setTextArea(final JTextArea textArea) {
        LogUtils.textArea = textArea;
    }

    public static void append(final String text) {
        System.out.print(text + " ");
        if (textArea != null) {
            textArea.append(text + " ");
        }
    }

    public static void log(final String text) {
        values.add(text);
        if (values.size() > 100) {
            values.remove(0);
        }
        System.out.println(text);
        if (textArea != null) {
            textArea.append(text + LINE_SEPARATOR);
            textArea.setCaretPosition(textArea.getDocument().getLength());
        }
    }

    public static void log(final Level level, final String text) {
        log(level == null ? "" : "[" + formatter.format(Calendar.getInstance().getTime()) + " " + level + "] " + text);
    }

    public static String getStackTrace(final Throwable throwable) {
        final StringWriter writer = new StringWriter();
        final PrintWriter printer = new PrintWriter(writer);
        throwable.printStackTrace(printer);
        printer.close();
        return writer.toString().replace(LINE_SEPARATOR + LINE_SEPARATOR, LINE_SEPARATOR);
    }

    public static class ErrorOutputStream extends OutputStream {

        @Override
        public void write(final int b) throws IOException {
            write(new byte[]{(byte) b}, 0, 1);
        }

        @Override
        public void write(final byte[] buffer, final int offset, final int length) throws IOException {
            log(Level.SEVERE, new String(buffer, offset, length));
        }

    }

}
