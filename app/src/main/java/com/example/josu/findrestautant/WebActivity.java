package com.example.josu.findrestautant;

import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;


public class WebActivity extends ActionBarActivity {

    private WebView wvWiki;
    private ProgressBar progressBar;
    private WebViewClient webViewClient;
    private String user, password;

    /*
    ************************** ON METHODS *************************
    * */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getIntent().getExtras().getString("title"));
        setContentView(R.layout.activity_web);
        user = getIntent().getExtras().getString("user");
        password = getIntent().getExtras().getString("password");
        wvWiki = (WebView) findViewById(R.id.wbWiki);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        webViewClient = new WebViewClient() {

            /*
            * This allows us to re-login and load the new page.
            * */
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                wvWiki.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }

            /*
            * This controls if we re-logged. In that case it cleans the history so we can't access login page by using back button and
            * by pressing it, we will return to the main activity.
            * */
            @Override
            public void onPageFinished(WebView view, String url) {
                if(url.contains("about:blank")){
                    Log.v("LOGGG", "entra en pagina en blanco");
                    wvWiki.clearCache(false);
                    wvWiki.clearHistory();
                }
                super.onPageFinished(view, url);
            }
        };
        wvWiki.setWebViewClient(webViewClient);
        wvWiki.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setProgress(0);
                progressBar.setVisibility(View.VISIBLE);
                WebActivity.this.setProgress(newProgress * 1000);
                progressBar.incrementProgressBy(newProgress);
                if (newProgress == 100)
                    progressBar.setVisibility(View.GONE);
            }

            /*
            * This allows us to update the title according to the web name.
            * */
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                //We don't want to show that we are logging in, so we don't update the title in this case.
                if(!wvWiki.getUrl().contains("about:blank"))
                    WebActivity.this.setTitle(WebActivity.this.wvWiki.getTitle());
            }

            /*
            * This allows us to control when we have been logged out from server to send a new petition of login.
            * */
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.v("LOGGG", "CONSOLE: " + consoleMessage.message());
                if (consoleMessage.message().contains("logged out")) {
                    //We skip the charge of login page in screen.
                    wvWiki.loadUrl("about:blank");
                    new GetRestful().execute("webService/login?username=" + user.toLowerCase() + "&password=" + password, "true");
                }
                return super.onConsoleMessage(consoleMessage);
            }
        });
        wvWiki.getSettings().setJavaScriptEnabled(true);
        wvWiki.getSettings().setBuiltInZoomControls(true);
        wvWiki.getSettings().setLoadWithOverviewMode(true);
        wvWiki.getSettings().setUseWideViewPort(true);
        String url = (String) getIntent().getExtras().get("url");
        Log.v("LOGGG", "PAGE: " + url);
        wvWiki.loadUrl(url);
    }

    /*
    * This allows us to navigate throw history by using back button and return to parent activity when it necessary.
    * */
    @Override
    public void onBackPressed() {
        if (wvWiki.canGoBack())
            wvWiki.goBack();
        else
            super.onBackPressed();
    }

    /*
    * If we want to delete the cache before closing the activity, we have to uncomment that line.
    * */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //wvWiki.clearCache(true);
    }

    /*
    ************************** INNER CLASSES *************************
    * */
    //Deberia crear una clase aparte para no tener este codigo con una parte que se repite.
    public class GetRestful extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                String r = ClientRestFul.get(MainActivity.links.get(1).getUrl() + params[0]);
                Log.v("LOGGG", "PETICION WEB: " + MainActivity.links.get(1).getUrl() + params[0]);
                return r;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        }

        @Override
        protected void onPostExecute(String r) {
            super.onPostExecute(r);
            Log.v("LOGGG", "RESPUESTA WEB: " + r);
            JSONTokener tokenerWebFields = new JSONTokener(r);
            WebFields webFields = null;
            try {
                JSONObject jsonObject = new JSONObject(tokenerWebFields);
                webFields = new WebFields(jsonObject);
            } catch (Exception e) {
                Log.v("ERROR", e.toString());
            }
            if (webFields.isLogged()) {
                MainActivity.sessionToken = webFields.getSessionToken();
                wvWiki.setWebViewClient(webViewClient);
                wvWiki.loadUrl(MainActivity.links.get(1).getUrl() + "shift/?username=" + user.toLowerCase() + "&sessionToken=" + MainActivity.sessionToken);
                DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
                MainActivity.date = DateTime.parse(webFields.getExpireSessionDateTime(), formatter);
                //Por ahora el logueo dura 4 horas en el servidor, asi que lo tengo puesto de forma manual pero deberia ser de forma programatica.
                MainActivity.date = MainActivity.date.minusHours(4);
            }
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
    }

    /*
    ************************** MESSAGES *************************
    * */

    public void tostada(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
