package com.example.tent1.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tent1.Model.Pedido;
import com.example.tent1.Model.Produto;
import com.example.tent1.R;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class AdapterCompradorCarrinho  extends RecyclerView.Adapter<AdapterCompradorCarrinho.ExampleViewHolder> {
    private List<Produto> mExampleList;
    private OnItemClickListener mListener;
    private Pedido pedido;
    private ExampleViewHolder holder;

    public interface OnItemClickListener {
        void onAddClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
    public static class ExampleViewHolder extends RecyclerView.ViewHolder  implements View.OnTouchListener {
        ImageView imagemEmpresa;
        TextView nomeEmpresa;
        TextView descricao;
        TextView preco;
        TextView CompradorQ;
        ImageView Carrinhoadd;
        ImageView Avaliacoes;

        public ExampleViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            itemView.setOnTouchListener(this);
            nomeEmpresa = itemView.findViewById(R.id.textNomeEmpresaComprador);
            descricao = itemView.findViewById(R.id.textDescricaoComprador);
            preco = itemView.findViewById(R.id.textPrecoComprador);
            imagemEmpresa = itemView.findViewById(R.id.imageEmpresaComprador);
            Carrinhoadd = itemView.findViewById(R.id.ImageAddCarrinho);

            Carrinhoadd.setOnClickListener(new View.OnClickListener() {
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
    public AdapterCompradorCarrinho(List<Produto> exampleList) {
        this.mExampleList = exampleList;
    }
    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_produto_comprador, parent, false);
        ExampleViewHolder evh = new ExampleViewHolder(v, mListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleViewHolder holder, int position) {
        this.holder = holder;
        Produto produto = mExampleList.get(position);
        holder.nomeEmpresa.setText(produto.getNome());
        holder.descricao.setText(produto.getDescricao() + " - ");
        DecimalFormat df = new DecimalFormat("0.00");
        holder.preco.setText("R$ " + df.format(produto.getPreco()));
        //Dar // pra tirar
        //holder.CompradorQ.setText(produto.getQtdcarrinho()+ "");


        //Carregar imagem
        String urlImagem = produto.getUrlImagem();
        Picasso.get().load( urlImagem ).into( holder.imagemEmpresa );
    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }

    //============================================================================================//

    public void teste(){

    }

}
