package com.example.tent1.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.example.tent1.Model.ItemPedido;
import com.example.tent1.Model.Pedido;
import com.example.tent1.Model.Produto;
import com.example.tent1.R;
import com.example.tent1.adapter.AdapterCompradorPedidosEspecifico;
import com.example.tent1.adapter.AdapterPedidoComprador;
import com.example.tent1.config.ConfiguracaoFirebase;
import com.example.tent1.helper.UsuarioFirebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CompradorTodosPedidosEspecifico extends AppCompatActivity {

    private List<ItemPedido> itensCarrinho2 = new ArrayList<>();
    private String idUsuarioLogado;
    private DatabaseReference firebaseRef;
    private Toolbar toolbar;
    private AdapterCompradorPedidosEspecifico adapterPedidoComprador;
    private RecyclerView listaTodosPedidosEspecifico;
    private List<Produto> produtos = new ArrayList<>();
    private List<ItemPedido> itensCarrinho = new ArrayList<>();
    private List<Pedido> pedidos = new ArrayList<>();
    private Pedido pedidoRecuperado;
    private String key;

    //Configarões barra com as informações do pedido
    private int qtdItensCarrinho;
    private Double totalCarrinho;
    private TextView textCompradorPedidosQtd, textCompradorPedidosRS, textAbrirLocalEntrega;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comprador_todos_pedidos_especifico);

        listaTodosPedidosEspecifico = findViewById(R.id.listaCompradorTodosPedidosEspecifico);
        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();
        textCompradorPedidosQtd = findViewById(R.id.TextQtdPedidosVendedor);
        textCompradorPedidosRS = findViewById(R.id.TextRSPedidosVendedor);

        toolbar = findViewById(R.id.toolbarteste);
        //toolbar.setTitle("Página Inicial");
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Itens");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().getSerializableExtra("StringKey")!=null){
            key = (String) getIntent().getSerializableExtra("StringKey");
        }
        System.out.println(key);

        listaTodosPedidosEspecifico.setLayoutManager(new LinearLayoutManager(this));
        listaTodosPedidosEspecifico.setHasFixedSize(true);
        adapterPedidoComprador = new AdapterCompradorPedidosEspecifico(itensCarrinho, idUsuarioLogado);
        listaTodosPedidosEspecifico.setAdapter(adapterPedidoComprador);



        recuperarItemPedidoJamilton();

    }

    private void recuperarItemPedidoJamilton() {
        DatabaseReference produtoref = firebaseRef
                .child("pedidos")
                .child(idUsuarioLogado)
                .child(key);

        produtoref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                qtdItensCarrinho = 0;
                totalCarrinho = 0.0;

                if (dataSnapshot.getValue() != null) {
                    itensCarrinho.clear();
                    //itensCarrinho2.add(pedidoRecuperado.getItens());
                    pedidoRecuperado = dataSnapshot.getValue(Pedido.class);
                    itensCarrinho.addAll(pedidoRecuperado.getItens());
                    //pedidoRecuperado = dataSnapshot.getValue(Pedido.class);
                    //itensCarrinho2 = pedidoRecuperado.getItens();

                    //Configurações para a barra com as informações do pedido
                    for (ItemPedido itemPedido: itensCarrinho){
                        int qtde = itemPedido.getQuantidade();
                        Double preco = itemPedido.getPreco();

                        totalCarrinho += (qtde * preco);
                        qtdItensCarrinho += qtde;
                    }
                    adapterPedidoComprador.notifyDataSetChanged();
                } else {
                    //Toast.makeText(CompradorPedidos.this, "Seu carrinho está vazio", Toast.LENGTH_SHORT).show();
                }
                DecimalFormat df = new DecimalFormat("0.00");
                System.out.println(qtdItensCarrinho);
                textCompradorPedidosQtd.setText("qtd: " + String.valueOf(qtdItensCarrinho));
                textCompradorPedidosRS.setText("R$ " + df.format(totalCarrinho));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Error
            }

        });
    }
}