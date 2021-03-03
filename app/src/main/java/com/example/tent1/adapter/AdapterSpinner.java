package com.example.tent1.adapter;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.tent1.Model.Categoria;
import com.example.tent1.R;

import java.util.List;

public class AdapterSpinner extends BaseAdapter {

    private Context context;
    private List<Categoria> lista;

    public AdapterSpinner (Context context, List<Categoria> lista){
        this.context = context;
        this.lista = lista;
    }

    @Override
    public int getCount() {
        return lista.size();
    }

    @Override
    public Object getItem(int position) {
        return lista.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vista = convertView;
        LayoutInflater inflater = LayoutInflater.from(context);
        vista = inflater.inflate(R.layout.item_spinner, null);

        TextView categoria = (TextView) vista.findViewById(R.id.TextSpinner);
        categoria.setText(lista.get(position).getNome());
        return vista;
    }
}
