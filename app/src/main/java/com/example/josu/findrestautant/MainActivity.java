package com.example.josu.findrestautant;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static EditText etUser, etPassword;
    private static FloatLabeledEditText etFloating, etFloating2;
    private static TextView tvWelcome;
    private static Button btLogin, btAccess, btRegister;
    private static boolean logged;
    private static String user, password;
    public static String sessionToken;
    private static MenuItem miWiki, miWeb, miProfile;
    private final int QR_CODE_BROWSER = 2, QR_CODE_ACTIVITY = 3, VALORAR = 4;
    public static ArrayList<Link> links;
    private boolean alert, qrMessage;
    private static ImageView ivVisibility;
    public static DateTime date;
    private String alertMessage;
    private ProgressDialog dialog;
    private boolean qrBrowser;
    private static Context context;
    private boolean loginDialog, registerDialog;
    private AlertDialog loginD, registerD;
    private static FloatingActionsMenu fam;
    private static FloatingActionButton btScanner, btCheckIn;
    private boolean enableScanner, enableCheckIn;
    // LogCat tag
    private static final String TAG = MainActivity.class.getSimpleName();

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private Location mLastLocation;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    /*
    ************************** ON METHODS *************************
    * */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkLinks();
        context = this;
        tvWelcome = (TextView) findViewById(R.id.tvWelcome);
        btAccess = (Button) findViewById(R.id.btAccess);
        btRegister = (Button) findViewById(R.id.btRegister);
        fam = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
        fam.setVisibility(View.GONE);
        btScanner = (FloatingActionButton) findViewById(R.id.btScanner2);
        btCheckIn = (FloatingActionButton) findViewById(R.id.btCheckIn2);
        if (logged) {
            tvWelcome.setText(getResources().getString(R.string.tv_welcome) + " ");
            tvWelcome.append(setTextColour(user, getResources().getColor(R.color.blue_pfe)));
            tvWelcome.setVisibility(View.VISIBLE);
            btAccess.setText("Logout");
            btRegister.setVisibility(View.GONE);
            fam.setVisibility(View.VISIBLE);
            setLogged(true);
        }
        // First we need to check availability of play services
        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.action_profile){
            Intent intent = new Intent(this, Profile.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //To log out if app closes
        if (isFinishing())
            loginOut();
    }

    /*
    * This allows us to work with MenuItems. We access this method before showing the menu.
    * */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        miProfile = menu.findItem(R.id.action_profile);
        setMenuItemsEnabled(logged);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.getBoolean("logged"))
            logueado();
        alert = savedInstanceState.getBoolean("alert");
        alertMessage = savedInstanceState.getString("alertMessage");
        if (alert)
            alertDialogMessage(alertMessage);
        qrMessage = savedInstanceState.getBoolean("qrMessage");
        qrBrowser = savedInstanceState.getBoolean("qrBrowser");
        if (qrMessage)
            qrMessage(qrBrowser);
        loginDialog = savedInstanceState.getBoolean("loginDialog");
        registerDialog = savedInstanceState.getBoolean("registerDialog");
        if (loginDialog) {
            loginDialog();
            etUser.setText(savedInstanceState.getString("user"));
            etUser.setSelection(etUser.getText().length());
            etPassword.setText(savedInstanceState.getString("password"));
            etPassword.setSelection(etPassword.getText().length());
            if (savedInstanceState.getBoolean("passwordVisible"))
                setPasswordVisible(true);
            else
                setPasswordVisible(false);
            if (savedInstanceState.getBoolean("passwordFocus"))
                etPassword.requestFocus();
        }
        if(registerDialog){
            registerDialog();
            etUser.setText(savedInstanceState.getString("user"));
            etUser.setSelection(etUser.getText().length());
            etPassword.setText(savedInstanceState.getString("password"));
            etPassword.setSelection(etPassword.getText().length());
            if (savedInstanceState.getBoolean("passwordVisible"))
                setPasswordVisible(true);
            else
                setPasswordVisible(false);
            if (savedInstanceState.getBoolean("passwordFocus"))
                etPassword.requestFocus();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.v("LOGGG", "save " + logged + "");
        outState.putBoolean("logged", logged);
        outState.putBoolean("alert", alert);
        outState.putBoolean("qrMessage", qrMessage);
        outState.putBoolean("qrBrowser", qrBrowser);
        outState.putString("alertMessage", alertMessage);
        outState.putBoolean("loginDialog", loginDialog);
        outState.putBoolean("registerDialog", registerDialog);
        if (loginDialog) {
            outState.putString("user", etUser.getText().toString());
            outState.putString("password", etPassword.getText().toString());
            outState.putBoolean("passwordVisible", isPasswordVisible());
            outState.putBoolean("passwordFocus", etPassword.isFocused());
            loginD.dismiss();
        }
        if(registerDialog){
            outState.putString("user", etUser.getText().toString());
            outState.putString("password", etPassword.getText().toString());
            outState.putBoolean("passwordVisible", isPasswordVisible());
            outState.putBoolean("passwordFocus", etPassword.isFocused());
            registerD.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == QR_CODE_ACTIVITY) {
            Intent intent = new Intent(this, SecActivity.class);
            intent.putExtra("url", data.getExtras().getString("qrcode"));
            intent.putExtra("location", mLastLocation.getLatitude() + "," + mLastLocation.getLongitude());
            intent.putExtra("title", getResources().getString(R.string.menu_item_qr));
            startActivity(intent);
        }

    }

    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {

        // Once connected with google api, get the location
        displayLocation();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    /*
    ************************** INNER CLASSES *************************
    * */

    /*
    * A class to work with server.
    * */

    public class GetRestful extends AsyncTask<String, Void, String> {

        private boolean directWebPage;

        @Override
        protected String doInBackground(String... params) {
            try {
                String r = ClientRestFul.get(links.get(1).getUrl() + params[0]);
                directWebPage = Boolean.parseBoolean(params[1]);
                Log.v("LOGGG", "PETICION:" + links.get(1).getUrl() + params[0]);
                return r;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(MainActivity.this, "",
                    getResources().getString(R.string.progress_dialog_message), true);
            btLogin.setClickable(false);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        }

        @Override
        protected void onPostExecute(String r) {
            super.onPostExecute(r);
            Log.v("LOGGG", "RESPUESTA: " + r);
            dialog.dismiss();
            JSONTokener tokenerWebFields = new JSONTokener(r);
            WebFields webFields = null;
            try {
                JSONObject jsonObject = new JSONObject(tokenerWebFields);
                webFields = new WebFields(jsonObject);
            } catch (Exception e) {
                Log.v("ERROR", e.toString());
            }
            if (webFields.isLogged()) {
                sessionToken = webFields.getSessionToken();
                enableScanner = webFields.isEnableScanner();
                enableCheckIn = webFields.isEnableCheckIn();
                logueado();
                if (directWebPage) {
                    //Esto pasaria despues de la primera vez y una vez que han pasado los 20 min.
                    Intent intent = new Intent(MainActivity.this, WebActivity.class);
                    intent.putExtra("title", links.get(1).getTitle());
                    intent.putExtra("url", links.get(1).getUrl() + "shift/?username=" + user.toLowerCase() + "&sessionToken=" + sessionToken);
                    startActivity(intent);
                }
                DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
                date = DateTime.parse(webFields.getExpireSessionDateTime(), formatter);
                //Por ahora el logueo dura 4 horas en el servidor, asi que lo tengo puesto de forma manual pero deberia ser de forma programatica.
                date = date.minusHours(4);
            } else {
                alertMessage = getResources().getString(R.string.error_user_password);
                alertDialogMessage(alertMessage);
            }
            btLogin.setClickable(true);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
    }

    /*
    ************************** SETTERS AND GETTERS *************************
    * */

    /*
    * Setter and getter of 'logged' boolean.
    * */

    public static void setLogged(boolean logueado) {
        logged = logueado;
        if(logged)
            btRegister.setVisibility(View.GONE);
        setMenuItemsEnabled(logged);
    }

    public static boolean getLogged() {
        return logged;
    }

    /*
    * Method to enable or disable menu's options and to change icon's colours depending of the current state.
    * */
    public static void setMenuItemsEnabled(boolean enabled) {
        miProfile.setEnabled(enabled);
        if (enabled) {
            //White icons for enabled state
            miProfile.setIcon(R.drawable.ic_person_white_24dp);
        } else {
            //Grey icons for disabled state
            miProfile.setIcon(R.drawable.ic_person_grey_24dp);
        }
        //To say to menu that options have changed
        new MainActivity().supportInvalidateOptionsMenu();
    }

    /*
    * Setter and getter of control on password visibility.
    * */
    public void setPasswordVisible(boolean visible) {
        if (visible) {
            ivVisibility.setImageResource(R.drawable.ic_action_action_visibility);
            etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            etPassword.setSelection(etPassword.getText().length());
            ivVisibility.setTag("on");
        } else {
            ivVisibility.setImageResource(R.drawable.ic_action_action_visibility_off);
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            etPassword.setSelection(etPassword.getText().length());
            ivVisibility.setTag("off");
        }
    }

    public boolean isPasswordVisible() {
        if (ivVisibility.getTag().equals("on"))
            return true;
        else
            return false;
    }

    /*
    * It access to Shared Preferences. We save if we want to show the previous message to QR Code Scanner.
    * */
    public void setPreferenciasCompartidas(boolean mantenerMensaje) {
        SharedPreferences pc;
        pc = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = pc.edit();
        ed.putBoolean("qrMessage", mantenerMensaje);
        ed.commit();
    }

    public Boolean getPreferenciasCompartidas() {
        SharedPreferences pc;
        pc = getPreferences(Context.MODE_PRIVATE);
        Boolean r = pc.getBoolean("qrMessage", true);
        return r;
    }

    /*
    * It allows us to change text colour.
    * */
    public static SpannableStringBuilder setTextColour(String text, int colour) {
        SpannableStringBuilder sb = new SpannableStringBuilder(text);
        ForegroundColorSpan fcs = new ForegroundColorSpan(colour);
        sb.setSpan(fcs, 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return sb;
    }

    /*
    ************************** CHECKINGS *************************
    * */

    /*
    * It checks if we have Internet connection before connecting with server.
    * */
    private boolean checkConectivity() {
        boolean enabled = true;

        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected() || !networkInfo.isAvailable()) {
            enabled = false;
        }
        return enabled;
    }

    /*
    * It reads links from a file. These links will be used from menu's options.
    * */
    private void checkLinks() {
        links = new ArrayList();
        String[] st = getResources().getStringArray(R.array.links);
        for (int i = 0; i < 2; i++)
            switch (i) {
                case 0:
                    links.add(new Link("About", st[i]));
                    break;
                case 1:
                    links.add(new Link("Search", st[i]));
            }
    }

    /*
    ************************** MESSAGES *************************
    * */

    /*
    * It shows a message if user and/or password are invalid.
    * */
    public void alertDialogMessage(String message) {
        final String title;
        if (message.equalsIgnoreCase(getResources().getString(R.string.error_user_password)))
            title = getResources().getString(R.string.error_user_password_title);
        else
            title = getResources().getString(R.string.error_qr_title);
        alert = true;
        View view = View.inflate(this, R.layout.dialog_checkbox, null);
        TextView aviso = (TextView) view.findViewById(R.id.tvMessage);
        aviso.setText(message);
        new DialogBuilder(MainActivity.this)
                //Login
                .setTitle(title)
                .setTitleColor(getResources().getColor(R.color.blue_pfe))
                .setDividerColor(getResources().getColor(R.color.yellow_pfe))
                .setIcon(R.drawable.ic_error_red_24dp)
                .setView(view)
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alert = false;
                        if (title.equalsIgnoreCase(getResources().getString(R.string.error_user_password_title))) {
                            etUser.setText("");
                            etPassword.setText("");
                            etUser.requestFocus();
                        }
                    }
                })
                .show();
    }

    /*
    * Its shows a message to say user how QR Scanner should be used.
    * */
    public void qrMessage(final boolean browser) {
        qrMessage = true;
        Boolean mensaje = getPreferenciasCompartidas();
        View view = View.inflate(this, R.layout.dialog_checkbox, null);
        final CheckBox cb = (CheckBox) view.findViewById(R.id.cbQRMessage);
        cb.setVisibility(View.VISIBLE);
        if (mensaje) {
            TextView textView = (TextView) view.findViewById(R.id.tvMessage);
            textView.setText(getResources().getString(R.string.message_qr));
            new DialogBuilder(MainActivity.this)
                    .setTitle(getResources().getString(R.string.message_qr_title))
                    .setTitleColor(getResources().getColor(R.color.blue_pfe))
                    .setDividerColor(getResources().getColor(R.color.yellow_pfe))
                    .setIcon(R.drawable.ic_warning_amber_24dp)
                    .setView(view)
                    .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            qrMessage = false;
                            if (cb.isChecked())
                                setPreferenciasCompartidas(false);
                            Intent intent = new Intent(MainActivity.this, QRReader.class);
                            startActivityForResult(intent, QR_CODE_ACTIVITY);
                        }
                    })
                    .show();
        } else {
            qrMessage = false;
        }
    }

    /*
    * Method to show Login Dialog
    * */

    public void loginDialog() {
        View view = View.inflate(this, R.layout.dialog_login, null);
        btLogin = (Button) view.findViewById(R.id.btLogin);
        etUser = (EditText) view.findViewById(R.id.etUser);
        etPassword = (EditText) view.findViewById(R.id.etPassword);
        ivVisibility = (ImageView) view.findViewById(R.id.ivVisibility);
        etFloating = (FloatLabeledEditText) view.findViewById(R.id.etFloating);
        etFloating2 = (FloatLabeledEditText) view.findViewById(R.id.etFloating2);
        loginDialog = true;
        loginD = new DialogBuilder(MainActivity.this)
                //Login
                .setTitle("Enter your access details")
                .setTitleColor(getResources().getColor(R.color.blue_pfe))
                .setDividerColor(getResources().getColor(R.color.yellow_pfe))
                .setIcon(R.drawable.ic_action_ic_person_white_48dp)
                .setView(view)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        loginDialog = false;
                    }
                })
                .show();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            loginD.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    public void registerDialog() {
        View view = View.inflate(this, R.layout.dialog_login, null);
        btLogin = (Button) view.findViewById(R.id.btLogin);
        btLogin.setText("Registrar");
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerD.dismiss();
                registerDialog = false;
            }
        });
        etUser = (EditText) view.findViewById(R.id.etUser);
        etPassword = (EditText) view.findViewById(R.id.etPassword);
        ivVisibility = (ImageView) view.findViewById(R.id.ivVisibility);
        etFloating = (FloatLabeledEditText) view.findViewById(R.id.etFloating);
        etFloating2 = (FloatLabeledEditText) view.findViewById(R.id.etFloating2);
        registerDialog = true;
        registerD = new DialogBuilder(MainActivity.this)
                //Register
                .setTitle("Enter your access details")
                .setTitleColor(getResources().getColor(R.color.blue_pfe))
                .setDividerColor(getResources().getColor(R.color.yellow_pfe))
                .setIcon(R.drawable.ic_action_ic_person_white_48dp)
                .setView(view)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        registerDialog = false;
                    }
                })
                .show();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            registerD.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

     /*
    * Method to show messages on screen.
    * */

    public void tostada(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /*
    ************************** BUTTON METHODS *************************
    * */

    /*
    * Method called by pressing "Access to your account" button
    * */

    public void access(View v) {
        if (btAccess.getText().toString().equalsIgnoreCase("Accede a tu cuenta"))
            loginDialog();
        else
            loginOut();
    }

    public void register(View v) {
        registerDialog();
    }

    /*
    * Method called by pressing 'Login' button. It checks if we are logged or not.
    * */

    public void login(View v) {
        loginD.dismiss();
        loginDialog = false;
        if (checkConectivity()) {
            loginIn();
        } else {
            alertMessage = getResources().getString(R.string.error_conexion);
            alertDialogMessage(alertMessage);
        }
    }

    /*
    * Method called by a button to control password visibility.
    * */
    public void passwordVisibility(View v) {
        if (ivVisibility.getTag().equals("off"))
            setPasswordVisible(true);
        else
            setPasswordVisible(false);
    }

    public void qrCodeScanner(View v) {
        qrMessage(qrBrowser = true);
    }

    public void checkIn(View v) {
        displayLocation();
        qrMessage(false);
    }

    /*
    ************************** LOGIN *************************
    * */

    /*
    * It checks if 'User' and 'Password' fields are filled. If yes, it sends a login petition to server.
    * */

    public void loginIn() {
        if (!etUser.getText().toString().isEmpty() && !etPassword.getText().toString().isEmpty()) {
            user = etUser.getText().toString();
            password = etPassword.getText().toString();
            //LO COMENTAMOS PARA ADAPTARLO A LA NUEVA APP
            //new GetRestful().execute(new String[]{"webService/login?username=" + user.toLowerCase() + "&password=" + password, "false"});
            enableScanner = true;
            enableCheckIn = true;
            logueado();
        }
    }

    /*
    * It hides 'User' and 'Password' fields and shows a welcome message.
    * */

    public void logueado() {
        tvWelcome.setText(getResources().getString(R.string.tv_welcome) + " ");
        tvWelcome.append(setTextColour(user, getResources().getColor(R.color.blue_pfe)));
        tvWelcome.setVisibility(View.VISIBLE);
        if (enableScanner && enableCheckIn)
            fam.setVisibility(View.VISIBLE);
        else if (enableScanner)
            btScanner.setVisibility(View.VISIBLE);
        else if (enableCheckIn)
            btCheckIn.setVisibility(View.VISIBLE);
        btAccess.setText("Logout");
        setPasswordVisible(false);
        setLogged(true);
    }

    /*
    * It shows the required fields to log in
    * */

    public static void loginOut() {
        tvWelcome.setVisibility(View.GONE);
        btAccess.setText("Accede a tu cuenta");
        fam.setVisibility(View.GONE);
        btScanner.setVisibility(View.GONE);
        btCheckIn.setVisibility(View.GONE);
        setLogged(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    /**
     * Method to display the location on UI
     */
    private void displayLocation() {

        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();

            //tostada(latitude + ", " + longitude);

        } else {
            buildAlertMessageNoGps();
            /*lblLocation
                    .setText("(Couldn't get the location. Make sure location is enabled on the device)");*/
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();

    }

    /**
     * Creating google api client object
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Method to verify google play services on the device
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    public void encontrar(View v){
        Intent intent = new Intent(this, Encontrar.class);
        startActivity(intent);
    }
}
