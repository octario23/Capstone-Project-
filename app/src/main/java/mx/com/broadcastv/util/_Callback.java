package mx.com.broadcastv.util;

import android.content.Context;

import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import mx.com.broadcastv.model.Request;

public class _Callback {
    public void execute(JsonNode response, Context context) {

    }

    ;

    public void executeSoap(String response, Context context) {

    }

    ;

    public void Failure(JsonNode response, Context context) {

    }

    ;

    public HttpEntity<?> setHeader(Request request, HttpHeaders headers) {
        return null;
    }
}
