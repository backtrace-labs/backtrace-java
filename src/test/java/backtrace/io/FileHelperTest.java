package backtrace.io;

import backtrace.io.helpers.FileHelper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class FileHelperTest {
    private static final String fileName = "test.txt";
    private static final String fileContent = "test message";
    private String absolutePath = new File(fileName).getAbsolutePath();

    @Before
    public void setUp() throws IOException {
        deleteTestFile();
        createTestFile();
    }

    @After
    public void tearDown() {
        deleteTestFile();
    }

    private void createTestFile() throws IOException {
        Files.write(Paths.get(this.absolutePath), fileContent.getBytes());
    }

    private void deleteTestFile() {
        File file = new File(absolutePath);
        file.delete();
    }

    @Test
    public void removeIncorrectPathsFromNull() {
        // GIVEN
        List<String> attachments = null;

        // WHEN
        attachments = FileHelper.filterOutFiles(attachments);

        // THEN
        Assert.assertEquals(0, attachments.size());
    }

    @Test
    public void removeIncorrectPaths() {
        // GIVEN
        List<String> attachments = Arrays.asList("", null, "random-string", "C:\\test.txt",
                new File("").getAbsolutePath(), this.absolutePath);

        // WHEN
        attachments = FileHelper.filterOutFiles(attachments);

        // THEN
        Assert.assertEquals(1, attachments.size());
        Assert.assertEquals(this.absolutePath, attachments.get(0));
    }

    @Test
    public void verifyCorrectPathExtension() {
        // GIVEN
        File sampleFile = new File("./test-dir/file.txt");

        // WHEN
        String result = FileHelper.getFileExtension(sampleFile);

        // THEN
        Assert.assertEquals("txt", result);
    }

    @Test
    public void verifyCorrectPathMultipleDotsExtension() {
        // GIVEN
        File sampleFile = new File("./test-dir/file.test.txt.pdf");

        // WHEN
        String result = FileHelper.getFileExtension(sampleFile);

        // THEN
        Assert.assertEquals("pdf", result);
    }

    @Test
    public void verifyEmptyExtension() {
        // GIVEN
        File sampleFile = new File("./test-dir/file");

        // WHEN
        String result = FileHelper.getFileExtension(sampleFile);

        // THEN
        Assert.assertEquals("", result);
    }

    @Test
    public void verifyDirPath() {
        // GIVEN
        File sampleFile = new File("./test-dir");

        // WHEN
        String result = FileHelper.getFileExtension(sampleFile);

        // THEN
        Assert.assertEquals("", result);
    }

    @Test
    public void verifyEmptyString() {
        // GIVEN
        File sampleFile = new File("");

        // WHEN
        String result = FileHelper.getFileExtension(sampleFile);

        // THEN
        Assert.assertEquals("", result);
    }
}
