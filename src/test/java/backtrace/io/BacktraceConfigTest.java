package backtrace.io;

import org.junit.Assert;
import org.junit.Test;

public class BacktraceConfigTest {
    @Test
    public void initBacktraceCredentials(){
        // GIVEN
        String token = "token";
        String url = "url";

        // WHEN
        BacktraceCredentials credentials = new BacktraceCredentials(url, token);

        // THEN
        Assert.assertEquals(token, credentials.getSubmissionToken());
        Assert.assertEquals(url, credentials.getEndpointUrl());
    }

    @Test(expected = NullPointerException.class)
    public void initBacktraceConfigWithNullToken(){
        // GIVEN
        String token = null;
        String url = "url";

        // WHEN
        new BacktraceConfig(url, token);
    }

    @Test(expected = NullPointerException.class)
    public void initBacktraceConfigWithNullUrl(){
        // GIVEN
        String token = "token";
        String url = null;

        // WHEN
        new BacktraceConfig(url, token);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setNegativeNumberOfRetriesInDatabaseConfig(){
        // GIVEN
        int retryNumber = -1;
        // WHEN
        BacktraceDatabaseConfig databaseConfig = new BacktraceDatabaseConfig();
        databaseConfig.setDatabaseRetryLimit(retryNumber);
    }

    @Test
    public void setNumberOfRetriesInBacktraceConfig() {
        // GIVEN
        int retryNumber = 100;
        // WHEN
        BacktraceConfig backtraceConfig = new BacktraceConfig("","");
        backtraceConfig.setDatabaseRetryLimit(retryNumber);
        // THEN
        Assert.assertEquals(retryNumber, backtraceConfig.getDatabaseConfig().getDatabaseRetryLimit());
    }
}
