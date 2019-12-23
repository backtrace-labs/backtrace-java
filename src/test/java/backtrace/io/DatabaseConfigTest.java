package backtrace.io;


import backtrace.io.data.BacktraceData;
import backtrace.io.data.BacktraceReport;
import backtrace.io.database.BacktraceDatabase;
import backtrace.io.events.OnServerResponseEvent;
import backtrace.io.events.RequestHandler;
import backtrace.io.helpers.FileHelper;
import backtrace.io.http.BacktraceResult;
import net.jodah.concurrentunit.Waiter;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DatabaseConfigTest {
    private final String databasePath = "backtrace_test/";

    /**
     * Remove database dir after each test
     */
    @Before
    @After
    public void cleanDatabaseDir() throws Exception {
        File file = new File(databasePath);
        FileHelper.deleteRecursive(file);
    }

    @Test
    public void retryLimitTest() {
        // GIVEN
        int RETRY_LIMIT = 3;
        Waiter waiter = new Waiter();

        BacktraceConfig config = new BacktraceConfig("", "");
        config.disableDatabase();

        config.setDatabaseRetryLimit(RETRY_LIMIT);

        BacktraceClient client = new BacktraceClient(config);
        BacktraceReport report = new BacktraceReport("test-message");

        // WHEN
        client.setCustomRequestHandler(new RequestHandler() {
            @Override
            public BacktraceResult onRequest(BacktraceData data) {
                return BacktraceResult.OnError(data.getReport(), new Exception());
            }
        });

        client.send(report, new OnServerResponseEvent() {
            @Override
            public void onEvent(BacktraceResult backtraceResult) {
                waiter.resume();
            }
        });

        // THEN
        try {
            waiter.await(10, TimeUnit.SECONDS, RETRY_LIMIT);
        } catch (TimeoutException e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertEquals(RETRY_LIMIT, report.getRetryCounter());
    }

    @Test
    public void limitedNumberOfRecords() {
        // GIVEN
        final int maxNumberOfRecordsInDatabase = 2;
        final Queue<BacktraceMessage> queue = new LinkedList<>();
        final BacktraceConfig config = new BacktraceConfig("", "");
        config.setDatabasePath(databasePath);
        config.setMaxRecordCount(maxNumberOfRecordsInDatabase);

        List<BacktraceData> data = new ArrayList<BacktraceData>() {{
            add(new BacktraceData(new BacktraceReport("1")));
            add(new BacktraceData(new BacktraceReport("2")));
            add(new BacktraceData(new BacktraceReport("3")));
            add(new BacktraceData(new BacktraceReport("4")));
        }};

        BacktraceDatabase database = BacktraceDatabase.init(config, queue);

        // WHEN
        for (BacktraceData backtraceData : data) {
            database.saveReport(backtraceData);
        }

        // THEN
        Assert.assertEquals(maxNumberOfRecordsInDatabase, database.getTotalNumberOfRecords());
    }

    @Test
    public void zeroDatabaseSize() {
        // GIVEN
        final long maxSize = 0;
        final BacktraceConfig config = new BacktraceConfig("", "");
        config.setDatabasePath(databasePath);

        List<BacktraceData> data = new ArrayList<BacktraceData>() {{
            add(new BacktraceData(new BacktraceReport("1")));
            add(new BacktraceData(new BacktraceReport("2")));
        }};

        BacktraceDatabase database = BacktraceDatabase.init(config, new LinkedList<>());
        config.setMaxDatabaseSize(maxSize);

        // WHEN
        for (BacktraceData backtraceData : data) {
            database.saveReport(backtraceData);
        }

        // THEN
        Assert.assertEquals(maxSize, database.getDatabaseSize());
        Assert.assertEquals(0, database.getTotalNumberOfRecords());
    }

    @Test
    public void limitedDatabaseSize() {
        // GIVEN
        final long maxSize = 100000000; // huge number enough to store 4 reports
        final BacktraceConfig config = new BacktraceConfig("", "");
        config.setDatabasePath(databasePath);
        List<BacktraceData> data = new ArrayList<BacktraceData>() {{
            add(new BacktraceData(new BacktraceReport("1")));
            add(new BacktraceData(new BacktraceReport("2")));
            add(new BacktraceData(new BacktraceReport("3")));
            add(new BacktraceData(new BacktraceReport("4")));
        }};

        BacktraceDatabase database = BacktraceDatabase.init(config, new LinkedList<>());
        config.setMaxDatabaseSize(maxSize);

        // WHEN
        for (BacktraceData backtraceData : data) {
            database.saveReport(backtraceData);
        }

        // THEN
        Assert.assertTrue(maxSize > database.getDatabaseSize());
        Assert.assertEquals(data.size(), database.getTotalNumberOfRecords());
    }

    @Test
    public void disableAndEnableDatabase() {
        // GIVEN
        final BacktraceConfig config = new BacktraceConfig("");
        config.setDatabasePath(databasePath);
        final BacktraceDatabase database = BacktraceDatabase.init(config, new LinkedList<>());
        config.disableDatabase();

        // WHEN
        database.saveReport(new BacktraceData(new BacktraceReport("Without database")));
        int databaseSizeAfterFirstSave = database.getTotalNumberOfRecords();

        config.enableDatabase();

        database.saveReport(new BacktraceData(new BacktraceReport("With database")));
        int databaseSizeAfterSecondSave = database.getTotalNumberOfRecords();

        // THEN
        Assert.assertEquals(0, databaseSizeAfterFirstSave);
        Assert.assertEquals(1, databaseSizeAfterSecondSave);
    }
}
