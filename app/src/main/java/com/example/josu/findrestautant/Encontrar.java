package com.example.josu.findrestautant;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;


public class Encontrar extends ActionBarActivity {

    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encontrar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        lv = (ListView)findViewById(R.id.lvEncontrar);
        ArrayList<Restaurante> restaurantes = new ArrayList<>();
        restaurantes.add(new Restaurante("Pizeria Blabla", "", "", "", "", ""));
        restaurantes.add(new Restaurante("Arroceria Blabla", "", "", "", "", ""));
        restaurantes.add(new Restaurante("Pub Blabla", "", "", "", "", ""));
        restaurantes.add(new Restaurante("Restaurante Blabla", "", "", "", "", ""));
        Adaptador adaptador = new Adaptador(this, R.layout.detalle_valoracion, restaurantes);
        lv.setAdapter(adaptador);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_encontrar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
