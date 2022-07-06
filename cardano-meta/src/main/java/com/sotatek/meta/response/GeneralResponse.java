package com.sotatek.meta.response;

import com.fasterxml.jackson.annotation.JsonProperty;


import java.io.Serializable;


public class GeneralResponse<T> implements Serializable {


    @JsonProperty("status")
    private ResponseStatus status;

    @JsonProperty("data")
    private T data;

    public GeneralResponse() {
    }

    public GeneralResponse(ResponseStatus status, T data) {
        this.status = status;
        this.data = data;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
