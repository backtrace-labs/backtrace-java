package backtrace.io;

import backtrace.io.database.BacktraceDatabaseConfig;
import org.junit.Assert;
import org.junit.Test;

import java.net.URI;

public class BacktraceConfigTest {
    @Test
    public void initBacktraceCredentialsWithUrlAndToken(){
        // GIVEN
        String token = "token";
        String url = "url";

        // WHEN
        BacktraceCredentials credentials = new BacktraceCredentials(url, token);

        // THEN
        Assert.assertNotNull(token, credentials.getSubmissionUrl());
    }

    @Test
    public void initBacktraceCredentialsWithUriString(){
        // GIVEN
        String uriString = "https://submit.backtrace.io/test_universe/test_token/json";

        // WHEN
        BacktraceCredentials credentials = new BacktraceCredentials(uriString);

        // THEN
        Assert.assertEquals(uriString, credentials.getSubmissionUrl().toString());
        Assert.assertNotNull(credentials.getSubmissionUrl());
    }

    @Test
    public void initBacktraceCredentialsWithUri(){
        // GIVEN
        URI uriString = URI.create("https://submit.backtrace.io/test_universe/test_token/json");

        // WHEN
        BacktraceCredentials credentials = new BacktraceCredentials(uriString);

        // THEN
        Assert.assertEquals(uriString, credentials.getSubmissionUrl());
        Assert.assertNotNull(credentials.getSubmissionUrl());
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
