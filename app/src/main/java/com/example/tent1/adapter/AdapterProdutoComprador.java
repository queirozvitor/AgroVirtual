package com.example.tent1.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tent1.Model.Produto;
import com.example.tent1.R;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

public class AdapterProdutoComprador extends RecyclerView.Adapter<AdapterProdutoComprador.MyViewHolder> {

    private List<Produto> produtos;
    private Context context;

    public AdapterProdutoComprador(List<Produto> produtos, Context context) {
        this.produtos = produtos;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterProdutoComprador.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_produto_comprador, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterProdutoComprador.MyViewHolder holder, int i) {
        Produto produto = produtos.get(i);
        holder.nomeEmpresa.setText(produto.getNome());
        holder.descricao.setText(produto.getDescricao() + " - ");
        DecimalFormat df = new DecimalFormat("0.00");
        holder.preco.setText("R$ " + df.format(produto.getPreco()));

        //Carregar imagem
        String urlImagem = produto.getUrlImagem();
        Picasso.get().load( urlImagem ).into( holder.imagemEmpresa );

    }

    @Override
    public int getItemCount() {
        return produtos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imagemEmpresa;
        TextView nomeEmpresa;
        TextView descricao;
        TextView preco;

        public MyViewHolder(View itemView) {
            super(itemView);

            nomeEmpresa = itemView.findViewById(R.id.textNomeEmpresaComprador);
            descricao = itemView.findViewById(R.id.textDescricaoComprador);
            preco = itemView.findViewById(R.id.textPrecoComprador);
            imagemEmpresa = itemView.findViewById(R.id.imageEmpresaComprador);
        }
    }
}
