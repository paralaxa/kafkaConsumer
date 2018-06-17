package sk.stopangin;

import java.io.Serializable;

public class Warpper<T> {
    private T payload;
    private boolean error;

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }
}
