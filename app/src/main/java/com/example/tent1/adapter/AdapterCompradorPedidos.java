package com.example.tent1.adapter;


import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tent1.Model.ItemPedido;
import com.example.tent1.Model.Itens;
import com.example.tent1.Model.Pedido;
import com.example.tent1.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by jamiltondamasceno
 */

public class AdapterCompradorPedidos extends RecyclerView.Adapter<AdapterCompradorPedidos.MyViewHolder> {

    private List<Pedido> pedidos;

    private String idVendedor;

    public AdapterCompradorPedidos(List<Pedido> pedidos, String idVendedor) {

        this.pedidos = pedidos;
        this.idVendedor = idVendedor;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_comprador_pedidos, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {

        Pedido pedido = pedidos.get(i);
        holder.localEntrega.setText("Local de entrega: "+ pedido.getLocalEntrega());
        holder.data.setText(" "+pedido.getData());

    }

    @Override
    public int getItemCount() {
        return pedidos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener {

        TextView localEntrega;
        TextView data;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnTouchListener(this);
            localEntrega = itemView.findViewById(R.id.textCompradorPedidosLocalEntrega);
            data = itemView.findViewById(R.id.textCompradorPedidosData);
        }
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                view.setBackgroundColor(Color.parseColor("#E6E6E6")); // No lugar da string, adicione a cor desejada.
                return true;
            }if (event.getAction() == MotionEvent.ACTION_MOVE) {
                view.setBackgroundColor(Color.parseColor("#E6E6E6")); // No lugar da string, adicione a cor desejada.
                return true;
            }

            view.setBackgroundColor(Color.parseColor("#FFFFFF")); // No lugar da string, adicione a cor desejada.
            return false;
        }

    }



}
