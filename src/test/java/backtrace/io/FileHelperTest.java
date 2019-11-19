package backtrace.io;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class FileHelperTest {
    private static final String fileName = "test.txt";
    private static final String fileContent = "test message";
    private String absolutePath = new File(fileName).getAbsolutePath();

    @Before
    public void setUp() throws IOException{
        deleteTestFile();
        createTestFile();
    }

    @After
    public void tearDown() {
        deleteTestFile();
    }

    private void createTestFile() throws IOException{
        Files.write(Paths.get(this.absolutePath), fileContent.getBytes());
    }

    private void deleteTestFile() {
        File file = new File(absolutePath);
        file.delete();
    }

    @Test public void removeIncorrectPathsFromNull () {
        // GIVEN
        List<String> attachments = null;

        // WHEN
        attachments = FileHelper.filterOutFiles(attachments);

        // THEN
        Assert.assertEquals(0, attachments.size());
    }

    @Test public void removeIncorrectPaths() {
        // GIVEN
        List<String> attachments = Arrays.asList("", null, "random-string", "C:\\test.txt",
                                                new File("").getAbsolutePath(), this.absolutePath);

        // WHEN
        attachments = FileHelper.filterOutFiles(attachments);

        // THEN
        Assert.assertEquals(1, attachments.size());
        Assert.assertEquals(this.absolutePath, attachments.get(0));
    }
}
