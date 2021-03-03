package com.example.tent1.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tent1.Model.NotaAvaliacao;
import com.example.tent1.Model.Produto;
import com.example.tent1.R;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Jamilton
 */

public class AdapterCompradorAvaliacoes extends RecyclerView.Adapter<AdapterCompradorAvaliacoes.MyViewHolder> {

    private List<NotaAvaliacao> produtos;
    private Context context;

    public AdapterCompradorAvaliacoes(List<NotaAvaliacao> produtos, Context context) {
        this.produtos = produtos;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_comprador_avaliacoes, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        NotaAvaliacao notaAvaliacao = produtos.get(i);
        holder.nomeAvaliador.setText(notaAvaliacao.getNomeUsuario());
        holder.comentariodoAavaliador.setText(notaAvaliacao.getComentario());
        holder.notadoAvaliador.setText(" " + notaAvaliacao.getNota());

    }

    @Override
    public int getItemCount() {
        return produtos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nomeAvaliador;
        TextView comentariodoAavaliador;
        TextView notadoAvaliador;

        public MyViewHolder(View itemView) {
            super(itemView);

            nomeAvaliador = itemView.findViewById(R.id.textNomeCompradorAvaliacao);
            comentariodoAavaliador = itemView.findViewById(R.id.textComentarioAvaliacao);
            notadoAvaliador = itemView.findViewById(R.id.textNotaAvaliacao);
        }
    }
}
