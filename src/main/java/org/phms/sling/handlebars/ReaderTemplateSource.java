
package org.phms.sling.handlebars;

import com.github.jknack.handlebars.io.AbstractTemplateSource;

import java.io.IOException;
import java.io.Reader;

public class ReaderTemplateSource extends AbstractTemplateSource {
    private Reader reader;
    private long lastModified;
    private String filename;
    private static final int TEMPLATE_READER_BUFFER_SIZE = 2000;

    public ReaderTemplateSource(String filename, long lastModified, Reader reader) {
        this.reader = reader;
        this.filename = filename;
        this.lastModified = lastModified;
    }

    @Override
    public String content() throws IOException {
        StringBuilder templateAsString = new StringBuilder(TEMPLATE_READER_BUFFER_SIZE);
        try {
            char[] buffer = new char[TEMPLATE_READER_BUFFER_SIZE];
            int nrOfChars;
            if (reader != null) {
                while ((nrOfChars = reader.read(buffer, 0, TEMPLATE_READER_BUFFER_SIZE)) != -1) {
                    templateAsString.append(buffer, 0, nrOfChars);
                }
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return templateAsString.toString();
    }

    @Override
    public String filename() {
        return filename;
    }

    @Override
    public long lastModified() {
        return lastModified;
    }
}
