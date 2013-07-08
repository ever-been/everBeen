package cz.cuni.mff.d3s.been.socketworks.twoway;

import cz.cuni.mff.d3s.been.core.utils.JSONUtils;

/**
 * Created with IntelliJ IDEA.
 * User: darklight
 * Date: 6/24/13
 * Time: 11:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class Request {
    protected String selector;
    protected String value;
    protected long timeout;

    public Request() {}

    public Request(String selector) {
        this.selector = selector;
    }

    public Request(String selector, String value) {
        this.selector = selector;
        this.value = value;
    }

    public Request(String selector, long timeout) {
        this.selector = selector;
        this.timeout = timeout;
    }

    public Request(String selector, String value, long timeout) {
        this.selector = selector;
        this.value = value;
        this.timeout = timeout;
    }

    public String toJson() {
        try {
            return JSONUtils.serialize(this);
        } catch (JSONUtils.JSONSerializerException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Request fromJson(String json) throws JSONUtils.JSONSerializerException {
        return JSONUtils.deserialize(json, Request.class);
    }

    public String getSelector() {
        return selector;
    }

    public void setSelector(String selector) {
        this.selector = selector;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        if (timeout < 0) {
            timeout = 0;
        }
        this.timeout = timeout;
    }
}
