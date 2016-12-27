package mx.com.broadcastv.util;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import mx.com.broadcastv.R;
import mx.com.broadcastv.model.Request;

public class AsyncTaskHelper extends AsyncTask<String, Void, JsonNode> {
    public Activity activity;
    public Request inputServices;
    public _Callback callback;
    public String url;
    public static String Error = "";
    public AsyncTaskHelper( Activity _activity,Request _request,_Callback _callback,String _url ){
        super();
        this.activity      = _activity;
        this.inputServices = _request;
        this.callback      = _callback;
        this.url           = _url;

    }
    public JsonNode getServicesExchange(){
        try {
            String  host        = this.activity.getResources().getString(R.string.hostName);
            String  user        = this.activity.getResources().getString(R.string.AuthenticationUser);
            String  pass        = this.activity.getResources().getString(R.string.AuthenticationPassword);
            String  restURLFull = host+url;

            HttpAuthentication authHeader = new HttpBasicAuthentication(user,pass);
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setAuthorization(authHeader);
            HttpEntity<?> requestEntity = this.callback.setHeader(this.inputServices,requestHeaders);
            requestHeaders.setContentType(MediaType.APPLICATION_JSON);
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
//            List<ClientHttpRequestInterceptor> interceptors = new ArrayList<ClientHttpRequestInterceptor>();
//            interceptors.add(new LoggingRequestInterceptor());
//            restTemplate.setInterceptors(interceptors);

            ResponseEntity<JsonNode> response = restTemplate.exchange(restURLFull, HttpMethod.POST, requestEntity, JsonNode.class);
            JsonNode users = response.getBody();
            Log.d(String.valueOf("MainListActivity"), "respuesta:"+ users.toString());
            return users;

        } catch (Exception e) {
            System.out.println("Error: " + e);
            AsyncTaskHelper.Error = e.toString();
            return null;
        }
    }
    @Override
    protected JsonNode doInBackground(String... params) {
        JsonNode response = getServicesExchange();
        return response;
    }
    @Override
    protected void onPostExecute(JsonNode greeting) {
        if(null != greeting){
            this.callback.execute(greeting, this.activity);
        }else{
            this.callback.Failure(greeting,this.activity);
        }
    }
}

class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        traceRequest(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        traceResponse(response);
        return response;
    }

    private void traceRequest(HttpRequest request, byte[] body) throws IOException {
        Log.d("MainListActivity","===========================request begin================================================");
        Log.d("MainListActivity","URI         : {}" + request.getURI());
        Log.d("MainListActivity","Method      : {}"+ request.getMethod());
        Log.d("MainListActivity","Headers     : {}"+ request.getHeaders() );
        Log.d("MainListActivity","Request body: {}"+ new String(body, "UTF-8"));
        Log.d("MainListActivity","==========================request end================================================");
    }

    private void traceResponse(ClientHttpResponse response) throws IOException {
        StringBuilder inputStringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody(), "UTF-8"));
        String line = bufferedReader.readLine();
        while (line != null) {
            inputStringBuilder.append(line);
            inputStringBuilder.append('\n');
            line = bufferedReader.readLine();
        }
        Log.d("MainListActivity","============================response begin==========================================");
        Log.d("MainListActivity","Status code  : {}"+ response.getStatusCode());
        Log.d("MainListActivity","Status text  : {}"+ response.getStatusText());
        Log.d("MainListActivity","Headers      : {}"+ response.getHeaders());
        Log.d("MainListActivity","Response body: {}"+ inputStringBuilder.toString());
        Log.d("MainListActivity","=======================response end=================================================");
    }

}

