package com.example.tent1.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tent1.Model.Produto;
import com.example.tent1.R;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Jamilton
 */

public class AdapterProduto extends RecyclerView.Adapter<AdapterProduto.MyViewHolder> {

    private List<Produto> produtos;
    private Context context;

    public AdapterProduto(List<Produto> produtos, Context context) {
        this.produtos = produtos;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_produto, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        Produto produto = produtos.get(i);
        holder.nomeEmpresa.setText(produto.getNome());
        holder.descricao.setText(produto.getDescricao() + " - ");

        DecimalFormat df = new DecimalFormat("0.00");

        holder.preco.setText("R$ " + df.format(produto.getPreco()));

        //Carregar imagem
        String urlImagem = produto.getUrlImagem();
        Picasso.get().load( urlImagem ).into( holder.imagemEmpresa );
        if (produto.getDesabilitar()==1){
            holder.box.setChecked(true);
        }else if (produto.getDesabilitar()==0){
            holder.box.setChecked(false);
        }

    }

    @Override
    public int getItemCount() {
        return produtos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener{

        ImageView imagemEmpresa;
        TextView nomeEmpresa;
        TextView descricao;
        TextView preco;
        CheckBox box;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnTouchListener(this);
            nomeEmpresa = itemView.findViewById(R.id.textNomeEmpresa);
            descricao = itemView.findViewById(R.id.textDescricao);
            preco = itemView.findViewById(R.id.textPreco);
            imagemEmpresa = itemView.findViewById(R.id.imageEmpresa);
            box = itemView.findViewById(R.id.checkDesabilitado);
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
