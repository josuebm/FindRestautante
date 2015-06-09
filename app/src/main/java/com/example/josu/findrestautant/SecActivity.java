package com.example.josu.findrestautant;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class SecActivity extends ActionBarActivity {

    private TextView tv;
    private Button bt;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sec);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getResources().getString(R.string.menu_item_checkin));
        tv = (TextView)findViewById(R.id.tvSec);
        bt = (Button)findViewById(R.id.btValorar);
        String code = getIntent().getExtras().getString("url");
        tv.setText(changeColour("The restaurant is at " + "\n", getResources().getColor(R.color.blue_pfe)));
        tv.append(changeColour(code, getResources().getColor(R.color.yellow_pfe)));
        tv.append("\n");
        tv.append(changeColour("and you are at", getResources().getColor(R.color.blue_pfe)));
        tv.append("\n");
        String location = getIntent().getExtras().getString("location");
        tv.append(changeColour(location, getResources().getColor(R.color.yellow_pfe)));
        double lat1, long1, lat2, long2;
        lat1 = Double.valueOf(code.substring(0, code.lastIndexOf(",")));
        long1 = Double.valueOf(code.substring(code.lastIndexOf(",")+1, location.length()));
        lat2 = Double.valueOf(location.substring(0, location.lastIndexOf(",")));
        long2 = Double.valueOf(location.substring(location.lastIndexOf(",")+1, location.length()));
        //tostada("LAT1: " + lat1 + " LONG1: " + long1 + " LAT2: " + lat2 + " LONG2: " + long2);
        double diferencia;
        if(lat1 > lat2)
            diferencia = lat1-lat2;
        else
            diferencia = lat2-lat1;
        if(long1 > long2)
            diferencia += long1-long2;
        else
            diferencia += long2-long1;
        tv.append("\n");
        if(diferencia <= 0.005){
            tv.append(changeColour("You can review ", Color.parseColor("#17770C")));
            bt.setVisibility(View.VISIBLE);
        }
        else{
            tv.append(changeColour("You cannot review ", Color.parseColor("#A11116")));

        }

    }

    public SpannableStringBuilder changeColour(String cadena, int color){
        SpannableStringBuilder sb = new SpannableStringBuilder(cadena);
        ForegroundColorSpan fcs = new ForegroundColorSpan(color);
        sb.setSpan(fcs, 0, cadena.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return sb;
    }

    public void tostada(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void valorar(View v){
        alertDialogMessage("Valora este restaurante");
    }

    public void alertDialogMessage(String title) {
        //alert = true;
        view = View.inflate(this, R.layout.detalle_main, null);
        TextView tvNombre = (TextView)view.findViewById(R.id.tvNombreRestaurante);
        tvNombre.setText("Restaurante Blabla");
        TextView tvTipo = (TextView)view.findViewById(R.id.tvTipoComida);
        tvTipo.setText("Comida típica blabla");
        TextView tvDistancia = (TextView)view.findViewById(R.id.tvDistancia);
        tvDistancia.setVisibility(View.INVISIBLE);
        new DialogBuilder(SecActivity.this)
                //Login
                .setTitle(title)
                .setTitleColor(getResources().getColor(R.color.blue_pfe))
                .setDividerColor(getResources().getColor(R.color.yellow_pfe))
                .setIcon(R.drawable.ic_warning_amber_24dp)
                .setView(view)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //alert = false;
                        bt.setText("VALORADO");
                        bt.setEnabled(false);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //alert = false;
                    }
                })
                .show();
    }

    public void estrella(View v){
        ImageView iv1 = (ImageView)view.findViewById(R.id.ivEstrella1);
        ImageView iv2 = (ImageView)view.findViewById(R.id.ivEstrella2);
        ImageView iv3 = (ImageView)view.findViewById(R.id.ivEstrella3);
        ImageView iv4 = (ImageView)view.findViewById(R.id.ivEstrella4);
        ImageView iv5 = (ImageView)view.findViewById(R.id.ivEstrella5);
        TextView tvDistancia = (TextView)view.findViewById(R.id.tvDistancia);
        tvDistancia.setVisibility(View.VISIBLE);
        switch (Integer.parseInt(v.getTag().toString())){
            case 1:{
                iv1.setImageResource(R.drawable.ic_star_yellow_24dp);
                iv2.setImageResource(R.drawable.ic_star_border_yellow_24dp);
                iv3.setImageResource(R.drawable.ic_star_border_yellow_24dp);
                iv4.setImageResource(R.drawable.ic_star_border_yellow_24dp);
                iv5.setImageResource(R.drawable.ic_star_border_yellow_24dp);
                tvDistancia.setText("1/5");
            }break;
            case 2:{
                iv1.setImageResource(R.drawable.ic_star_yellow_24dp);
                iv2.setImageResource(R.drawable.ic_star_yellow_24dp);
                iv3.setImageResource(R.drawable.ic_star_border_yellow_24dp);
                iv4.setImageResource(R.drawable.ic_star_border_yellow_24dp);
                iv5.setImageResource(R.drawable.ic_star_border_yellow_24dp);
                tvDistancia.setText("2/5");
            }break;
            case 3:{
                iv1.setImageResource(R.drawable.ic_star_yellow_24dp);
                iv2.setImageResource(R.drawable.ic_star_yellow_24dp);
                iv3.setImageResource(R.drawable.ic_star_yellow_24dp);
                iv4.setImageResource(R.drawable.ic_star_border_yellow_24dp);
                iv5.setImageResource(R.drawable.ic_star_border_yellow_24dp);
                tvDistancia.setText("3/5");
            }break;
            case 4:{
                iv1.setImageResource(R.drawable.ic_star_yellow_24dp);
                iv2.setImageResource(R.drawable.ic_star_yellow_24dp);
                iv3.setImageResource(R.drawable.ic_star_yellow_24dp);
                iv4.setImageResource(R.drawable.ic_star_yellow_24dp);
                iv5.setImageResource(R.drawable.ic_star_border_yellow_24dp);
                tvDistancia.setText("4/5");
            }break;
            case 5:{
                iv1.setImageResource(R.drawable.ic_star_yellow_24dp);
                iv2.setImageResource(R.drawable.ic_star_yellow_24dp);
                iv3.setImageResource(R.drawable.ic_star_yellow_24dp);
                iv4.setImageResource(R.drawable.ic_star_yellow_24dp);
                iv5.setImageResource(R.drawable.ic_star_yellow_24dp);
                tvDistancia.setText("5/5");
            }break;
        }
    }
}
