package backtrace.io;

import backtrace.io.data.BacktraceData;
import backtrace.io.data.BacktraceReport;
import backtrace.io.events.BeforeSendEvent;
import backtrace.io.events.OnServerResponseEvent;
import backtrace.io.events.RequestHandler;
import backtrace.io.helpers.CountLatch;
import backtrace.io.helpers.FileHelper;
import backtrace.io.http.BacktraceResult;
import net.jodah.concurrentunit.Waiter;
import org.junit.*;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


public class CustomEventsTest {
    private final String databasePath = "backtrace_test/";
    private final String message = "message";
    private BacktraceConfig config;

    @Before
    public void init() throws Exception{
        this.cleanDatabaseDir();
        config = new BacktraceConfig("url", "token");
        config.setDatabasePath(this.databasePath);
    }

    @After
    public void cleanDatabaseDir() throws Exception {
        FileHelper.deleteRecursive(new File(databasePath));
    }

    @Test
    public void useRequestHandler() throws InterruptedException {
        // GIVEN
        final BacktraceClient backtraceClient = new BacktraceClient(config);

        final LinkedList<Integer> result = new LinkedList<>();
        final RequestHandler customRequestHandler = new RequestHandler() {
            @Override
            public BacktraceResult onRequest(BacktraceData data) {
                result.add(1);
                return BacktraceResult.onSuccess(data.getReport(), "");
            }
        };
        final BacktraceReport report = new BacktraceReport(message);

        // WHEN
        backtraceClient.setCustomRequestHandler(customRequestHandler);
        backtraceClient.send(message);
        backtraceClient.send(report);

        // THEN
        try {
            backtraceClient.await(1, TimeUnit.SECONDS);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        } finally {
            backtraceClient.close();
        }
        Assert.assertEquals(2, result.size());
        Assert.assertEquals(1, result.getFirst().intValue());
    }

    @Test
    public void useCallback() throws InterruptedException {
        // GIVEN
        final BacktraceClient backtraceClient = new BacktraceClient(config);
        final Waiter waiter = new Waiter();
        final RequestHandler customRequestHandler = new RequestHandler() {
            @Override
            public BacktraceResult onRequest(BacktraceData data) {
                return BacktraceResult.onSuccess(data.getReport(), message);
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
        } finally {
            backtraceClient.close();
        }
    }

    @Test
    public void useBeforeSendEvent() throws InterruptedException {
        // GIVEN
        final BacktraceClient backtraceClient = new BacktraceClient(config);
        final BacktraceReport report = new BacktraceReport(message);
        final String newMessage = "new message";
        final BacktraceData newData = new BacktraceData(new BacktraceReport(newMessage));
        final List<BacktraceResult> result = new LinkedList<>();
        final RequestHandler customRequestHandler = new RequestHandler() {
            @Override
            public BacktraceResult onRequest(BacktraceData data) {
                BacktraceResult backtraceResult = BacktraceResult.onSuccess(data.getReport(), "");
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
        } finally {
            backtraceClient.close();
        }
        Assert.assertEquals(2, result.size());
        Assert.assertEquals(newMessage, result.get(0).getBacktraceReport().getMessage());
    }



    @Test
    public void multipleAwait() throws InterruptedException {
        // GIVEN
        final BacktraceClient backtraceClient = new BacktraceClient(config);
        final ArrayList<Integer> result = new ArrayList<>();
        backtraceClient.setCustomRequestHandler(new RequestHandler() {
            @Override
            public BacktraceResult onRequest(BacktraceData data) {
                result.add(1);
                return BacktraceResult.onSuccess(data.getReport(), "Success");
            }
        });

        // WHEN
        backtraceClient.send("");
        backtraceClient.send("");
        backtraceClient.await(5, TimeUnit.SECONDS);
        final int counterAfterFirstAwait =  result.size();

        backtraceClient.send("");
        backtraceClient.send("");
        backtraceClient.await(5, TimeUnit.SECONDS);
        backtraceClient.close();
        final int counterAfterSecondAwait = result.size();

        // THEN
        Assert.assertEquals(2, counterAfterFirstAwait);
        Assert.assertEquals(4, counterAfterSecondAwait);
    }

    @Test
    public void awaitingTime() throws InterruptedException {
        // GIVEN
        final BacktraceConfig config = new BacktraceConfig("url", "token");
        final BacktraceClient backtraceClient = new BacktraceClient(config);
        backtraceClient.setCustomRequestHandler(new RequestHandler() {
            @Override
            public BacktraceResult onRequest(BacktraceData data) {
                try {
                    Thread.sleep(10000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return BacktraceResult.onSuccess(data.getReport(), "");
            }
        });

        // WHEN
        backtraceClient.send("");

        // THEN
        try{
            boolean result = backtraceClient.await(2, TimeUnit.SECONDS);
            Assert.assertFalse(result);
        }
        catch (Exception e){
            Assert.fail(e.toString());
        } finally {
            backtraceClient.close();
        }
    }

    @Test
    public void send50Requests() throws InterruptedException {
        // GIVEN
        final BacktraceClient backtraceClient = new BacktraceClient(config);
        final int iterations = 50;
        final ArrayList<Integer> result = new ArrayList<>();
        backtraceClient.setCustomRequestHandler(new RequestHandler() {
            @Override
            public BacktraceResult onRequest(BacktraceData data) {
                result.add(Integer.parseInt(data.getReport().getMessage()));
                return BacktraceResult.onSuccess(data.getReport(), "Success");
            }
        });

        // WHEN
        for (int i = 1; i <= iterations; i++) {
            backtraceClient.send(Integer.toString(i));
        }

        // THEN
        try {
            backtraceClient.await();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        } finally {
            backtraceClient.close();
        }

        Assert.assertEquals(iterations, result.size());
        Assert.assertEquals(new Integer(1) , result.get(0));
        Assert.assertEquals(new Integer(iterations), result.get(iterations - 1));
    }

    @Test
    public void sendRequestWithCustomAttributes() throws InterruptedException {
        // GIVEN
        final BacktraceReport report = new BacktraceReport(message);
        final String attributeKey = "custom-attribute";
        final String attributeValue = "custom-value";

        final Map<String, Object> attributes = new HashMap<>();
        attributes.put(attributeKey, attributeValue);

        // WHEN
        final BacktraceClient backtraceClient = new BacktraceClient(config, attributes);
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
        } finally {
            backtraceClient.close();
        }
        Assert.assertNotEquals(0, outputAttributes.size());
        Assert.assertTrue(outputAttributes.containsKey(attributeKey));
        Assert.assertEquals(attributeValue, outputAttributes.get(attributeKey));
    }

    @Test
    public void sendRequestWithAppVersionAndName() throws InterruptedException {
        // GIVEN
        final BacktraceClient backtraceClient = new BacktraceClient(config);
        final BacktraceReport report = new BacktraceReport(message);
        final String appVersion = "release-1.0";
        final String appName = "Java-test";
        final Waiter waiter = new Waiter();

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
            waiter.await(50, TimeUnit.SECONDS);
        } catch (Exception exception) {
            waiter.fail(exception);
        } finally {
            backtraceClient.close();
        }
    }
}