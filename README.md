# Backtrace Java support

[Backtrace](http://backtrace.io/)'s integration with Java applications which allows customers to capture and report handled and unhandled java exceptions to their Backtrace instance, instantly offering the ability to prioritize and debug software errors.

[![Build Status](https://travis-ci.org/backtrace-labs/backtrace-java.png?branch=master)](https://travis-ci.org/backtrace-labs/backtrace-java)

# Features Summary <a name="features-summary"></a>
* Light-weight Java client library that quickly submits exceptions and crashes to your Backtrace dashboard. Can include callstack, system metadata, custom metadata and file attachments if needed.
* Supports offline database for error report storage and re-submission in case of network outage.
* Fully customizable and extendable event handlers and base classes for custom implementations.

# Installation via Gradle or Maven<a name="installation"></a>

* Gradle
```
dependencies {
    implementation 'com.github.backtrace-labs.backtrace-java:backtrace-java:1.0.0-rc.1'
}
```

* Maven
```
<dependency>
  <groupId>com.github.backtrace-labs.backtrace-java</groupId>
  <artifactId>backtrace-java</artifactId>
  <version>1.0.0-rc.1</version>
</dependency>
```

# Using Backtrace library  <a name="using-backtrace"></a>
## Initialize a new BacktraceClient <a name="using-backtrace-initialization"></a>

First create a `BacktraceConfig` instance with your `Backtrace endpoint URL`. This endpoint URL will either be through a central https://submit.backtrace.io server on standard port 80/443, or directly to your Backtrace instance through a listener on port 6097/6098 (e.g. https://xxx.sp.backtrace.io:6098). Either integration will require a submission token to be included in the endpoint

Option 1 - https://submit.backtrace.io URL format option for creating a BacktraceConfig object. Use the URL to which the report is to be sent, pass URL string as parameter to BacktraceConfig constructor:

```java
BacktraceConfig config = new BacktraceConfig("https://submit.backtrace.io/{yourInstance}/{token}/json");
BacktraceClient backtraceClient = new BacktraceClient(config);
```

Option 2 - https://<yourInstance>.sp,backtrace.io:6098 format for creating BacktraceCredentials object. Pass in both your instance submission URL and token as parameters of the BacktraceConfig constructor:

Java
```java
BacktraceConfig config = new BacktraceConfig("https://<yourInstance>.sp.backtrace.io:6098/", "<submissionToken>");
BacktraceClient backtraceClient = new BacktraceClient(config);
```

## Setting application name and version

In order to easily distinguish which errors belong to which application it is recommended to set the application version and its name. This data will be sent as attributes to the Backtrace console.

```java
backtraceClient.setApplicationName("Backtrace Demo");
backtraceClient.setApplicationVersion("1.0.0");
```

## Database <a name=""></a>

### 
By default, BacktraceClient stores error reports to the local disk using BacktraceDatabase. Each report is serialized and stored in separate file in database directory.

### Disabling database
If you don't want to use local file database, you can disable that.
```java
backtraceConfig.disableDatabase();
```

If the database is disabled, you can enable it.

```java
backtraceConfig.enableDatabase();
```

### Max database size
You can set a maximum database size in bytes, by default size is unlimited. If the database size reaches the limit, the oldest error reports will be deleted.

```java
backtraceConfig.setMaxDatabaseSize(size);
```

### Max record count

If a limit is set, the oldest error reports will be deleted if there will be try to exceed the limit.
```java
backtraceConfig.setMaxRecordCount(numberOfRecords);
```

### Max retry limit

The retry limit specifies the number of times `BacktraceClient` will try to send the error report again if sending will finished with fail.

```java
backtraceConfig.setDatabaseRetryLimit(retryLimit);
```

## Sending an error report <a name="using-backtrace-sending-report"></a>
Method `BacktraceClient.send` will send an error report to the Backtrace endpoint specified. There `send` method is overloaded, see examples below:

### Using `BacktraceReport`
The `BacktraceReport` class represents a single error report. (Optional) You can also submit custom attributes using the `attributes` parameter. You can also pass list of file paths to files which will be send to API in `attachmentPaths` parameter.

```java
try {
    // throw exception here
} catch (Exception e) {
    BacktraceReport report = new BacktraceReport(e, 
    new HashMap<String, Object>() {{
        put("key", "value");
    }}, new ArrayList<String>() {{
        add("absoulte_file_path_1");
        add("absoulte_file_path_2");
    }});
    backtraceClient.send(report);
}
```

### Asynchronous `send` support

Method `send` behind the mask use dedicated thread which sending report to server. You can specify the method that should be performed after completion.

```java
client.send(report, new OnServerResponseEvent() {
    @Override
    public void onEvent(BacktraceResult backtraceResult) {
        // process result here
    }
});
```

Method `await` of BacktraceReport allows to block current thread until report will be sent, as a parameter you can set set the maximum time you want to wait for an answer.

```java
report.await(5, TimeUnit.SECONDS);
```

### Other `BacktraceReport` overloads

`BacktraceClient` can also automatically create `BacktraceReport` given an exception or a custom message using the following overloads of the `BacktraceClient.send` method:

Java
```java
try {
  // throw exception here
} catch (Exception exception) {

  backtraceClient.send(new BacktraceReport(exception));
  
  // pass exception to send method
  backtraceClient.send(exception);
  
  // pass your custom message to send method
  backtraceClient.send("message");
}
```


## Attaching custom event handlers <a name="documentation-events"></a>

All events are written in *listener* pattern. `BacktraceClient` allows you to attach your custom event handlers. For example, you can trigger actions before the `send` method:
 
```java
backtraceClient.setBeforeSendEvent(new BeforeSendEvent() {
        @Override
        public BacktraceData onEvent(BacktraceData data) {
            // another code
            return data;
        }
    });
```

`BacktraceClient` currently supports the following events:
- `BeforeSend`
- `RequestHandler`

## Reporting unhandled application exceptions
`BacktraceClient` supports reporting of unhandled application exceptions not captured by your try-catch blocks. To enable reporting of unhandled exceptions run the code below.
```java
backtraceClient.enableUncaughtExceptionsHandler();
```

# Documentation  <a name="documentation"></a>

## BacktraceReport  <a name="documentation-BacktraceReport"></a>
**`BacktraceReport`** is a class that describe a single error report. Contains attributes, message, exception stack and paths to attachments.

## BacktraceClient  <a name="documentation-BacktraceClient"></a>
`BacktraceClient` is a class that allows you to instantiate a client instance that interacts with Backtrace. This class sets up connection to the Backtrace endpoint and manages error reporting behavior. It also prepares error report, gather device attributes and add to queue from which special thread gets report and sends to Backtrace. This class also allows to enable `UncaughtExceptionsHandler` or set custom events.
## BacktraceData  <a name="documentation-BacktraceData"></a>
**`BacktraceData`** is a serializable class that holds the data to create a diagnostic JSON to be sent to the Backtrace endpoint . You can add additional pre-processors for `BacktraceData` by attaching an event handler to the `BacktraceClient.setBeforeSendEvent(event)` event. `BacktraceData` require `BacktraceReport` and `BacktraceClient` client attributes.

## BacktraceResult  <a name="documentation-BacktraceResult"></a>
**`BacktraceResult`** is a class that holds response and result from a `send` method call. The class contains a `status` property that indicates whether the call was completed (`OK`), the call returned with an error (`ServerError`). Additionally, the class has a `message` property that contains details about the status.
