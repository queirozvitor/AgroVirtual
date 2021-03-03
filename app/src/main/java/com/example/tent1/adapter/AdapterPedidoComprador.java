package com.example.tent1.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tent1.Activity.CompradorPedidos;
import com.example.tent1.Model.ItemPedido;
import com.example.tent1.Model.Pedido;
import com.example.tent1.Model.Produto;
import com.example.tent1.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class AdapterPedidoComprador extends RecyclerView.Adapter<AdapterPedidoComprador.ExampleViewHolder> {
    private List<ItemPedido> mExampleList;
    private Pedido pedidoRecuperado;
    private OnItemClickListener mListener;
    private ExampleViewHolder holder;


    public interface OnItemClickListener {
        void onAddClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
    public static class ExampleViewHolder extends RecyclerView.ViewHolder {
        ImageView imagemEmpresa;
        TextView nomeEmpresa;
        TextView descricao;
        TextView preco;
        TextView quantidade;
        TextView CompradorQ;
        ImageView CarrinhoRemove;

        public ExampleViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            nomeEmpresa = itemView.findViewById(R.id.textNomeEmpresaCompradorPedido);
            descricao = itemView.findViewById(R.id.textDescricaoCompradorPedido);
            preco = itemView.findViewById(R.id.textPrecoCompradorPedido);
            imagemEmpresa = itemView.findViewById(R.id.imageEmpresaCompradorPedido);
            CarrinhoRemove = itemView.findViewById(R.id.ImageAddCarrinhoPedido);
            quantidade = itemView.findViewById(R.id.textQuantidadePedido);

            CarrinhoRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onAddClick(position);
                        }
                    }
                }
            });
        }
    }
    public AdapterPedidoComprador(List<ItemPedido> exampleList) {
        this.mExampleList = exampleList;
        //his.mExampleList = this.pedidoRecuperado.getItens();
        System.out.println(String.format("quantidade no carrinho: %s", exampleList.size() ));
    }
    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_pedido_comprador, parent, false);
        ExampleViewHolder evh = new ExampleViewHolder(v, mListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleViewHolder holder, int position) {
        this.holder = holder;
        ItemPedido itemPedido= mExampleList.get(position);
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
        return mExampleList.size();
    }

    //============================================================================================//

}
