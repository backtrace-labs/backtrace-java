package backtrace.io;

import backtrace.io.data.BacktraceData;
import backtrace.io.data.BacktraceReport;
import backtrace.io.database.BacktraceDatabase;
import backtrace.io.helpers.FileHelper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.LinkedList;

public class BacktraceDatabaseTest {

    private final String databasePath = "backtrace_test/";
    private final String message = "message";

    private BacktraceConfig backtraceConfig;
    private LinkedList<BacktraceMessage> queue;
    private BacktraceData backtraceData;

    @Before
    public void init() {
        // GIVEN
        this.backtraceConfig = new BacktraceConfig("", "");
        this.backtraceConfig.setDatabasePath(databasePath);
        this.queue = new LinkedList<>();
        this.backtraceData = new BacktraceData(new BacktraceReport(this.message));
    }

    /**
     * Remove database dir after each test
     */
    @Before
    @After
    public void cleanDatabaseDir() throws Exception {
        File file = new File(databasePath);
        FileHelper.deleteRecursive(file);
    }

    @Test(expected = NullPointerException.class)
    public void invalidNullBacktraceConfig() {
        // GIVEN
        BacktraceConfig config = null;
        // WHEN
        BacktraceDatabase.init(config, new LinkedList<>());
    }

    @Test(expected = NullPointerException.class)
    public void invalidNullQueue() {
        // GIVEN
        BacktraceConfig config = new BacktraceConfig("", "");
        // WHEN
        BacktraceDatabase.init(config, null);
    }

    @Test
    public void loadReportsFromEmptyDatabase() {
        // GIVEN
        BacktraceConfig config = new BacktraceConfig("", "");
        config.setDatabasePath(databasePath);

        // WHEN
        BacktraceDatabase database = BacktraceDatabase.init(config, this.queue);

        // THEN
        Assert.assertEquals(0, database.getTotalNumberOfRecords());
        Assert.assertEquals(0, database.getDatabaseSize());
        Assert.assertEquals(0, queue.size());
    }

    @Test
    public void saveReportToDatabase() {
        // GIVEN
        BacktraceDatabase database = BacktraceDatabase.init(backtraceConfig, queue);

        // WHEN
        database.saveReport(backtraceData);
        // THEN
        Assert.assertEquals(1, database.getTotalNumberOfRecords());
        Assert.assertEquals(0, queue.size());
    }

    @Test
    public void saveAndLoadReport() {
        // GIVEN
        BacktraceDatabase database = BacktraceDatabase.init(this.backtraceConfig, this.queue);

        // WHEN
        database.saveReport(this.backtraceData);
        database = BacktraceDatabase.init(this.backtraceConfig, this.queue);

        // THEN
        Assert.assertEquals(1, database.getTotalNumberOfRecords());
        Assert.assertEquals(1, this.queue.size());
        Assert.assertEquals(this.message, this.queue.getFirst().getBacktraceData().getReport().getMessage());
    }

    @Test
    public void saveAndDelete() {
        // GIVEN
        BacktraceDatabase database = BacktraceDatabase.init(this.backtraceConfig, this.queue);

        // WHEN
        database.saveReport(this.backtraceData);
        database.removeReport(this.backtraceData);

        // THEN
        Assert.assertEquals(0, database.getTotalNumberOfRecords());
    }
}
