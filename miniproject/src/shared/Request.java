package shared;

import java.io.Serializable;

/**
 * Generic request wrapper
 */
public class Request implements Serializable {
    private static final long serialVersionUID = 1L;
    private Action action;
    private Object payload;

    public Request() {}

    public Request(Action action, Object payload) {
        this.action = action;
        this.payload = payload;
    }

    public Action getAction() { return action; }
    public void setAction(Action action) { this.action = action; }

    public Object getPayload() { return payload; }
    public void setPayload(Object payload) { this.payload = payload; }
}
