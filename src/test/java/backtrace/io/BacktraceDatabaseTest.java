package backtrace.io;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.LinkedList;

public class BacktraceDatabaseTest {

    private final String databasePath = "./backtrace_test/";

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
        BacktraceConfig config = new BacktraceConfig("","");
        // WHEN
        BacktraceDatabase.init(config, null);
    }

    @Test
    public void loadReportsFromEmptyDatabase(){
        // GIVEN
        BacktraceConfig config = new BacktraceConfig("", "");
        config.setDatabasePath(databasePath);
        LinkedList<BacktraceMessage> queue = new LinkedList<>();

        // WHEN
        BacktraceDatabase database = BacktraceDatabase.init(config, queue);

        // THEN
        Assert.assertEquals(0, database.size());
        Assert.assertEquals(0, queue.size());
    }

    /**
     * Remove database dir after each test
     */
    @After
    public void cleanDatabaseDir(){
        File file = new File(databasePath);
        if(file.exists()){
            file.delete();
        }
    }
}
