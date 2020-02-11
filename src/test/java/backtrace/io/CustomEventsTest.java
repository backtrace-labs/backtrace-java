package backtrace.io;

import backtrace.io.data.BacktraceData;
import backtrace.io.data.BacktraceReport;
import backtrace.io.events.BeforeSendEvent;
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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class CustomEventsTest {
    private final String databasePath = "backtrace_test/";
    private final String message = "message";
    private BacktraceClient backtraceClient;
    private BacktraceConfig config;

    @Before
    public void init() {
        config = new BacktraceConfig("url", "token");
        config.setDatabasePath(this.databasePath);
        this.backtraceClient = new BacktraceClient(config);
    }

    @Before
    @After
    public void cleanDatabaseDir() throws Exception {
        File file = new File(databasePath);
        file.mkdir();
        FileHelper.deleteRecursive(file);
        file.mkdir();
    }


    @Test
    public void useRequestHandler() {
        // GIVEN
        final LinkedList<Integer> result = new LinkedList<>();
        RequestHandler customRequestHandler = new RequestHandler() {
            @Override
            public BacktraceResult onRequest(BacktraceData data) {
                result.add(1);
                return BacktraceResult.OnSuccess(data.getReport(), "");
            }
        };
        BacktraceReport report = new BacktraceReport(message);

        // WHEN
        backtraceClient.setCustomRequestHandler(customRequestHandler);
        backtraceClient.send(message);
        backtraceClient.send(report);

        // THEN
        try {
            backtraceClient.await(1, TimeUnit.SECONDS);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertEquals(2, result.size());
        Assert.assertEquals(1, result.getFirst().intValue());
    }

    @Test
    public void useCallback() {
        // GIVEN
        final Waiter waiter = new Waiter();
        RequestHandler customRequestHandler = new RequestHandler() {
            @Override
            public BacktraceResult onRequest(BacktraceData data) {
                return BacktraceResult.OnSuccess(data.getReport(), message);
            }
        };
        backtraceClient.setCustomRequestHandler(customRequestHandler);

        // WHEN

        OnServerResponseEvent callback = new OnServerResponseEvent() {
            @Override
            public void onEvent(BacktraceResult backtraceResult) {
                Assert.assertEquals(message, backtraceResult.getMessage());
                waiter.resume();
            }
        };

        backtraceClient.send(this.message, callback);


        // THEN
        try {
            waiter.await(1, TimeUnit.SECONDS);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void useBeforeSendEvent() {
        // GIVEN
        BacktraceReport report = new BacktraceReport(message);
        String newMessage = "new message";
        final BacktraceData newData = new BacktraceData(new BacktraceReport(newMessage));
        List<BacktraceResult> result = new LinkedList<>();
        RequestHandler customRequestHandler = new RequestHandler() {
            @Override
            public BacktraceResult onRequest(BacktraceData data) {
                BacktraceResult backtraceResult = BacktraceResult.OnSuccess(data.getReport(), "");
                result.add(backtraceResult);
                return backtraceResult;
            }
        };
        backtraceClient.setCustomRequestHandler(customRequestHandler);

        // WHEN
        backtraceClient.setBeforeSendEvent(new BeforeSendEvent() {
            @Override
            public BacktraceData onEvent(BacktraceData data) {
                return newData;
            }
        });
        backtraceClient.send(new Exception(message));
        backtraceClient.send(report);

        // THEN
        try {
            backtraceClient.await();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertEquals(2, result.size());
        Assert.assertEquals(newMessage, result.get(0).getBacktraceReport().getMessage());
    }

    @Test
    public void sendRequestWithCustomAttributes() {
        // GIVEN
        BacktraceReport report = new BacktraceReport(message);
        String attributeKey = "custom-attribute";
        String attributeValue = "custom-value";

        Map<String, Object> attributes = new HashMap<>();
        attributes.put(attributeKey, attributeValue);

        // WHEN
        backtraceClient = new BacktraceClient(config, attributes);
        final Map<String, Object> outputAttributes = new HashMap<>();
        backtraceClient.setCustomRequestHandler(new RequestHandler() {
            @Override
            public BacktraceResult onRequest(BacktraceData data) {
                outputAttributes.putAll(data.getAttributes());
                return null;
            }
        });
        backtraceClient.send(report);

        // THEN
        try {
            backtraceClient.await();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        System.out.println(outputAttributes.size());
        Assert.assertNotEquals(0, outputAttributes.size());
        Assert.assertTrue(outputAttributes.containsKey(attributeKey));
        Assert.assertEquals(attributeValue, outputAttributes.get(attributeKey));
    }

    @Test
    public void sendRequestWithAppVersionAndName() {
        // GIVEN
        BacktraceReport report = new BacktraceReport(message);
        String appVersion = "release-1.0";
        String appName = "Java-test";
        backtraceClient = new BacktraceClient(config);
        Waiter waiter = new Waiter();

        // WHEN
        backtraceClient.setApplicationName(appName);
        backtraceClient.setApplicationVersion(appVersion);

        backtraceClient.setCustomRequestHandler(new RequestHandler() {
            @Override
            public BacktraceResult onRequest(BacktraceData data) {
                // THEN
                waiter.assertEquals(appName, data.getAttributes().get("application"));
                waiter.assertEquals(appVersion, data.getAttributes().get("version"));
                waiter.resume();
                return null;
            }
        });

        backtraceClient.send(report);

        try {
            waiter.await(5, TimeUnit.SECONDS);
        } catch (Exception exception) {
            waiter.fail(exception);
        }
    }
}