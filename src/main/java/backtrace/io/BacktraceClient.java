package backtrace.io;


import backtrace.io.events.BeforeSendEvent;
import backtrace.io.events.OnServerResponseEvent;
import backtrace.io.events.RequestHandler;

import java.util.HashMap;
import java.util.Map;

public class BacktraceClient {
    private Backtrace backtrace;
    private BacktraceConfig config;
    private final Map<String, Object> customAttributes;

    public BacktraceClient(BacktraceConfig config){
        this(config, null);
    }

    public BacktraceClient(BacktraceConfig config, Map<String, Object> attributes){
        this.customAttributes = attributes != null? attributes : new HashMap<>();
        this.config = config;
        this.backtrace = new Backtrace(config);
    }


    public void enableUncaughtExceptionsHandler(){
        this.enableUncaughtExceptionsHandler(false);
    }

    public void enableUncaughtExceptionsHandler(boolean blockThread){
        BacktraceExceptionHandler.enable(this, blockThread);
    }

    public void setCustomRequestHandler(RequestHandler customRequestHandler){
        config.setRequestHandler(customRequestHandler);
    }

    public void setBeforeSendEvent(BeforeSendEvent beforeSendEvent){
        config.setBeforeSendEvent(beforeSendEvent);
    }

    public void send(BacktraceReport report){
        this.send(report, null);
    }

    public void send(BacktraceReport report, OnServerResponseEvent callback){
        this.backtrace.send(report, this.customAttributes, callback);
    }
}
