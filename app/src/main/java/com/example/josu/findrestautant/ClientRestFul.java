package com.example.josu.findrestautant;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by stage on 9/04/15.
 */
public class ClientRestFul {

    public static String get(String url) throws IOException {
        HttpClient clienteHttp = new DefaultHttpClient();
        HttpGet get = new HttpGet(url);
        get.setHeader("content-type", "application/json");
        HttpResponse respuestaHttp = clienteHttp.execute(get);
        String respuesta = EntityUtils.toString(respuestaHttp.getEntity());
        return respuesta;
    }
}
