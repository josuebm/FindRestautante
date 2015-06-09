package com.example.josu.findrestautant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by stage on 8/06/15.
 */
public class Adaptador extends ArrayAdapter<Restaurante> {
    private Context contexto;
    private int recurso;
    private ArrayList <Restaurante> lista;
    private LayoutInflater inflador;

    public Adaptador (Context context, int resource, ArrayList<Restaurante> objects) {
        super(context, resource, objects);
        this.contexto = context;
        this.recurso = resource;
        this.lista = objects;
        inflador = (LayoutInflater)contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public class ViewHolder{
        public TextView tv;
        public int posicion;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if(convertView == null){
            convertView = inflador.inflate(recurso, null);
            vh = new ViewHolder();
            vh.tv = (TextView)convertView.findViewById(R.id.tvRestaurante);
            convertView.setTag(vh);
        }
        else
            vh = (ViewHolder)convertView.getTag();
        vh.tv.setText(lista.get(position).getNombre());
        vh.posicion = position;
        return convertView;
    }
}