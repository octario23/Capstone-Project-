package mx.com.broadcastv.model;

import java.io.Serializable;

public class Request<T> implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    T rqt;

    /**
     *
     */
    public Request() {

    }

    public T getRqt() {
        return rqt;
    }

    public void setRqt(T rqt) {
        this.rqt= rqt;
    }

}

