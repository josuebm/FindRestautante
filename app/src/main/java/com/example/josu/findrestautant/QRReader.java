package com.example.josu.findrestautant;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;


public class QRReader extends ActionBarActivity implements QRCodeReaderView.OnQRCodeReadListener {
    private QRCodeReaderView qrCodeReaderView;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrreader);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        qrCodeReaderView = (QRCodeReaderView) findViewById(R.id.qrView);
        qrCodeReaderView.setOnQRCodeReadListener(this);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    public void tostada(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT);
    }

    @Override
    public void onQRCodeRead(String s, PointF[] pointFs) {
        Intent intent = new Intent();
        intent.putExtra("qrcode", s);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void cameraNotFound() {

    }

    @Override
    public void QRCodeNotFoundOnCamImage() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        qrCodeReaderView.getCameraManager().startPreview();
    }

    @Override
    protected void onPause() {
        super.onPause();
        qrCodeReaderView.getCameraManager().stopPreview();
    }
}