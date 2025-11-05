package shared;

import java.io.Serializable;

/**
 * Generic response wrapper
 */
public class Response implements Serializable {
    private static final long serialVersionUID = 1L;
    private Action status;
    private Object payload;

    public Response() {}

    public Response(Action status, Object payload) {
        this.status = status;
        this.payload = payload;
    }

    public Action getStatus() { return status; }
    public void setStatus(Action status) { this.status = status; }

    public Object getPayload() { return payload; }
    public void setPayload(Object payload) { this.payload = payload; }
}
