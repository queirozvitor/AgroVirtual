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

import com.example.tent1.Model.LocalEntrega;
import com.example.tent1.Model.NotaAvaliacao;
import com.example.tent1.Model.Produto;
import com.example.tent1.R;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Jamilton
 */

public class AdapterCompradorLocalEntrega extends RecyclerView.Adapter<AdapterCompradorLocalEntrega.MyViewHolder> {

    private List<LocalEntrega> locais;
    private Context context;

    public AdapterCompradorLocalEntrega(List<LocalEntrega> locais, Context context) {
        this.locais = locais;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_local_entrega, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        LocalEntrega localEntrega = locais.get(i);
        holder.diaLocalEntrega.setText(localEntrega.getDia());
        holder.ruaLocalEntrega.setText("Rua: "+localEntrega.getRua());
        holder.referenciaLocalEntrega.setText("Ponto de referência: "+localEntrega.getReferencia());
        holder.horaLocalEntrega.setText(localEntrega.getHora());

    }

    @Override
    public int getItemCount() {
        return locais.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener{

        TextView diaLocalEntrega;
        TextView ruaLocalEntrega;
        TextView referenciaLocalEntrega;
        TextView horaLocalEntrega;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnTouchListener(this);
            diaLocalEntrega = itemView.findViewById(R.id.textDiaLocalEntrega);
            ruaLocalEntrega = itemView.findViewById(R.id.textRuaLocalEntrega);
            referenciaLocalEntrega = itemView.findViewById(R.id.textReferenciaLocalEntrega);
            horaLocalEntrega = itemView.findViewById(R.id.textHoraLocalEntrega);
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
