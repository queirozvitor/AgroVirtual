package com.example.tent1.adapter;



import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tent1.Model.ItemPedido;
import com.example.tent1.Model.Itens;
import com.example.tent1.Model.Pedido;
import com.example.tent1.R;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by jamiltondamasceno
 */

public class AdapterCompradorPedidosEspecifico extends RecyclerView.Adapter<AdapterCompradorPedidosEspecifico.MyViewHolder> {

    private List<ItemPedido> pedidos;

    private String idVendedor;

    public AdapterCompradorPedidosEspecifico(List<ItemPedido> pedidos, String idVendedor) {

        this.pedidos = pedidos;
        this.idVendedor = idVendedor;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_comprador_pedidos_especifico, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {

        ItemPedido itemPedido= pedidos.get(i);
        holder.nomeEmpresa.setText(itemPedido.getNomeProduto());
        holder.descricao.setText(itemPedido.getDescricao() + " - ");
        DecimalFormat df = new DecimalFormat("0.00");
        holder.preco.setText("R$ " + df.format(itemPedido.getPreco()));
        holder.quantidade.setText(String.format("Q: %d",itemPedido.getQuantidade()));


        //Carregar imagem
        String urlImagem = itemPedido.getURLImage();
        Picasso.get().load( urlImagem ).into( holder.imagemEmpresa );
    }

    @Override
    public int getItemCount() {
        return pedidos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener {

        ImageView imagemEmpresa;
        TextView nomeEmpresa;
        TextView descricao;
        TextView preco;
        TextView quantidade;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnTouchListener(this);
            nomeEmpresa = itemView.findViewById(R.id.textNomeEmpresaCompradorPedidoEspecifico);
            descricao = itemView.findViewById(R.id.textDescricaoCompradorPedidoEspecifico);
            preco = itemView.findViewById(R.id.textPrecoCompradorPedidoEspecifico);
            imagemEmpresa = itemView.findViewById(R.id.imageEmpresaCompradorPedidoEspecifico);
            quantidade = itemView.findViewById(R.id.textQuantidadePedidoEspecifico);
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
