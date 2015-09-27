package org.phms.sling.handlebars.helper.include;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class StringResponseWrapper extends HttpServletResponseWrapper implements AutoCloseable {
    private BufferedServletOutputStream bufferedOut = new BufferedServletOutputStream();
    private PrintWriter writer;

    StringResponseWrapper(final HttpServletResponse response) {
        super(response);
    }

    public String getStringOutput() throws IOException {
        flushBuffer();
        return bufferedOut.toString(getCharacterEncoding());
    }

    @Override
    public void flushBuffer() throws IOException {
        if (writer != null) {
            writer.flush();
        } else {
            super.flushBuffer();
        }
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (writer == null) {
            writer = new PrintWriter(new OutputStreamWriter(bufferedOut, super.getCharacterEncoding()));
        }
        return writer;
    }

    @Override
    public void close() throws IOException {
        bufferedOut.close();
    }

    private static class BufferedServletOutputStream extends ServletOutputStream {

        private final ByteArrayOutputStream baops = new ByteArrayOutputStream();

        public String toString(String charset) throws IOException {
            return baops.toString(charset);
        }

        @Override
        public void close() throws IOException {
            baops.reset();
            super.close();
        }

        @Override
        public void write(final int b) throws IOException {
            baops.write(b);
        }

    }

}
