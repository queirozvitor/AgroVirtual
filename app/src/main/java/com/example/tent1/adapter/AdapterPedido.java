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

public class AdapterPedido extends RecyclerView.Adapter<AdapterPedido.MyViewHolder> {

    private List<Pedido> pedidos;

    private String idVendedor;

    public AdapterPedido(List<Pedido> pedidos, String idVendedor) {

        this.pedidos = pedidos;
        this.idVendedor = idVendedor;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_pedidos, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {

        Pedido pedido = pedidos.get(i);
        holder.nomeComprador.setText( pedido.getNome() );
        holder.cidade.setText( "Cidade: "+pedido.getCidade() );
        holder.telefone.setText( "Telefone: "+ pedido.getTelefoneComprador() );
        holder.localEntrega.setText("Local de entrega: "+ pedido.getLocalEntrega());
        holder.data.setText("Data do pedido: "+pedido.getData());
        if (pedido.getStatus().equals("pendente")){
            holder.status.setTextColor(Color.parseColor("#B40404"));
            holder.status.setText("Pendente");
        }else if (pedido.getStatus().equals("finalizado")){
            holder.status.setTextColor(Color.parseColor("#0B610B"));
            holder.status.setText("Finalizado");
        }
        DecimalFormat df = new DecimalFormat("0.00");

        List<ItemPedido> itens = new ArrayList<>();
        itens = pedido.getItens();
        String descricaoItens = "";

        int numeroItem = 1;
        Double total = 0.0;
        for( ItemPedido itemPedido : itens ){
            if (itemPedido.getIdVendedor().equals(idVendedor)){
                int qtde = itemPedido.getQuantidade();
                Double preco = itemPedido.getPreco();
                total += (qtde * preco);

                String nome = itemPedido.getNomeProduto();
                descricaoItens +=""+ numeroItem + " - " + nome + " / (" + qtde + " x R$ " + df.format(preco) + ") \n";
                numeroItem++;
            }
        }
        descricaoItens += "\nTotal: R$ " + df.format(total);
        holder.total.setText(descricaoItens);

        int metodoPagamento = pedido.getMetodoPagamento();
        String pagamento = metodoPagamento == 0 ? "Dinheiro" : "Máquina cartão" ;
        holder.infoItem.setText( "Forma de pagamento: " + pagamento );

    }

    @Override
    public int getItemCount() {
        return pedidos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener {

        TextView nomeComprador;
        TextView cidade;
        TextView telefone;
        TextView total;
        TextView infoItem;
        TextView localEntrega;
        TextView status;
        TextView data;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnTouchListener(this);
            nomeComprador = itemView.findViewById(R.id.textPedidoNomeComprador);
            cidade = itemView.findViewById(R.id.textPedidoCidade);
            telefone = itemView.findViewById(R.id.textPedidoTelefone);
            infoItem = itemView.findViewById(R.id.textPedidoTotal);
            total = itemView.findViewById(R.id.textPedidoItens);
            localEntrega = itemView.findViewById(R.id.textLocalEntrega);
            status = itemView.findViewById(R.id.textPedidoStatus);
            data = itemView.findViewById(R.id.textPedidoData);
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
