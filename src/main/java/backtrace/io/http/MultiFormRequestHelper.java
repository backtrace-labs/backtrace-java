package backtrace.io.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;


/***
 * Helper class for building multipart/form-data request
 */
class MultiFormRequestHelper {
    private static final transient Logger LOGGER = LoggerFactory.getLogger(backtrace.io.http.MultiFormRequestHelper.class);
    private static final String BOUNDARY = "*****";
    private static final String CRLF = "\r\n";
    private static final String TWO_HYPHENS = "--";

    /**
     * Get Content-Type of request
     *
     * @return string with content type and information about boundary
     */
    static String getContentType() {
        return "multipart/form-data;boundary=" + BOUNDARY;
    }

    /**
     * Write to output data stream string which ending the request
     *
     * @param outputStream output data stream
     * @throws IOException
     */
    static void addEndOfRequest(OutputStream outputStream) throws IOException {
        if (outputStream == null) {
            LOGGER.warn("Output stream is null");
            return;
        }

        outputStream.write((TWO_HYPHENS + BOUNDARY +
                TWO_HYPHENS + CRLF).getBytes());
    }

    /**
     * Write JSON string to output data steam
     *
     * @param outputStream output data stream
     * @param json         JSON string with BacktraceData object
     * @throws IOException
     */
    static void addJson(OutputStream outputStream, String json) throws IOException {
        if (json == null || json.isEmpty() || outputStream == null) {
            LOGGER.warn("JSON is null/empty or output stream is null");
            return;
        }
        outputStream.write((TWO_HYPHENS + BOUNDARY +
                CRLF).getBytes());
        outputStream.write((getFileInfo("upload_file")).getBytes());
        outputStream.write((CRLF).getBytes());

        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        outputStream.write(bytes);
        outputStream.write((CRLF).getBytes());
    }

    /***
     * Write files data to outputStream
     * @param outputStream output data stream
     * @param attachments list of paths to files
     * @throws IOException
     */
    static void addFiles(OutputStream outputStream, List<String> attachments) throws
            IOException {
        if (attachments == null || outputStream == null) {
            LOGGER.warn("Attachments or output stream is null");
            return;
        }

        for (String fileAbsolutePath : attachments) {
            addFile(outputStream, fileAbsolutePath);
        }
    }

    /***
     * Write single file in multiform data format to outputStream
     * @param outputStream output data stream
     * @param absolutePath file absolute path
     * @throws IOException
     */
    private static void addFile(OutputStream outputStream, String absolutePath) throws IOException {
        if (absolutePath == null || outputStream == null) {
            LOGGER.warn("Absolute path or output stream is null");
            return;
        }
        String fileName = Paths.get(absolutePath).getFileName().toString();
        String fileContentType = URLConnection.guessContentTypeFromName(fileName);

        outputStream.write((TWO_HYPHENS + BOUNDARY +
                CRLF).getBytes());
        outputStream.write((getFileInfo("attachment_" + fileName)).getBytes());
        outputStream.write(("Content-Type: " + fileContentType + CRLF)
                .getBytes
                        ());
        outputStream.write((CRLF).getBytes());
        streamFile(outputStream, absolutePath);
        outputStream.write((CRLF).getBytes());

    }

    /***
     * Write file content to output data stream
     * @param outputStream output data stream
     * @param absolutePath absolute path to file
     * @throws IOException
     */
    private static void streamFile(OutputStream outputStream, String absolutePath) throws
            IOException {
        if (outputStream == null || absolutePath == null) {
            LOGGER.warn("Absolute path or output stream is null");
            return;
        }
        FileInputStream fis = new FileInputStream(absolutePath);
        byte[] b = new byte[4096];
        int c;
        while ((c = fis.read(b)) != -1) {
            outputStream.write(b, 0, c);
        }
    }


    /***
     * Get string with information about file like content-disposition, name and filename
     * @param fileName filename with extension
     * @return string with file information for multiform data
     */
    private static String getFileInfo(String fileName) {
        return "Content-Disposition: form-data; name=\"" +
                fileName + "\";filename=\"" +
                fileName + "\"" + CRLF;
    }
}
