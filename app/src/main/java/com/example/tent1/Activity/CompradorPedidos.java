package com.example.tent1.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tent1.Model.Categoria;
import com.example.tent1.Model.ItemPedido;
import com.example.tent1.Model.Itens;
import com.example.tent1.Model.Notificacao;
import com.example.tent1.Model.NotificacaoDados;
import com.example.tent1.Model.Pedido;
import com.example.tent1.Model.Produto;
import com.example.tent1.Model.Usuario;
import com.example.tent1.R;
import com.example.tent1.adapter.AdapterCompradorCarrinho;
import com.example.tent1.adapter.AdapterPedidoComprador;
import com.example.tent1.adapter.AdapterProduto;
import com.example.tent1.adapter.AdapterProdutoComprador;
import com.example.tent1.adapter.AdapterSpinner;
import com.example.tent1.api.NotificacaoService;
import com.example.tent1.config.ConfiguracaoFirebase;
import com.example.tent1.helper.UsuarioFirebase;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CompradorPedidos extends AppCompatActivity {

    //====================
    private List<ItemPedido> itensCarrinho2 = new ArrayList<>();
    private String idUsuarioLogado;
    private DatabaseReference firebaseRef;
    private Toolbar toolbar;
    private AdapterPedidoComprador adapterPedidoComprador;
    private RecyclerView listaPedidoComprador;
    private List<Produto> produtos = new ArrayList<>();
    private List<ItemPedido> itensCarrinho = new ArrayList<>();
    private List<Pedido> pedidos = new ArrayList<>();
    private Pedido pedidoRecuperado;

    //Configarões barra com as informações do pedido
    private int qtdItensCarrinho;
    private Double totalCarrinho;
    private TextView textCompradorPedidosQtd, textCompradorPedidosRS, textAbrirLocalEntrega;
    //=========================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comprador_pedidos);

        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        toolbar = findViewById(R.id.toolbarteste);
        //toolbar.setTitle("Página Inicial");
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Carrinho");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listaPedidoComprador = findViewById(R.id.listaPedidoComprador);
        textCompradorPedidosQtd = findViewById(R.id.TextQtdPedidos);
        textCompradorPedidosRS = findViewById(R.id.TextRSPedidos);

        //Configurando RecyclerView
        listaPedidoComprador.setLayoutManager(new LinearLayoutManager(this));
        listaPedidoComprador.setHasFixedSize(true);
        adapterPedidoComprador = new AdapterPedidoComprador(itensCarrinho);
        listaPedidoComprador.setAdapter(adapterPedidoComprador);
        adapterPedidoComprador.setOnItemClickListener(new AdapterPedidoComprador.OnItemClickListener() {
            @Override
            public void onAddClick(int position) {
                ItemPedido itemPedido = itensCarrinho.get(position);
                //Toast.makeText(CompradorPedidos.this, itemPedido.getNomeProduto(), Toast.LENGTH_SHORT).show();
                if (itemPedido.getQuantidade()==1&&itensCarrinho.size()==1){
                    itensCarrinho.clear();
                    pedidoRecuperado.remover();
                    adapterPedidoComprador.notifyDataSetChanged();
                }
                else if (itemPedido.getQuantidade()>1){
                    itemPedido.setQuantidade( itemPedido.getQuantidade() - 1);
                    pedidoRecuperado.setItens(itensCarrinho);
                    pedidoRecuperado.salvar();
                }else if (itemPedido.getQuantidade()==1&&itensCarrinho.size()>1){
                    itensCarrinho.remove(position);
                    pedidoRecuperado.setItens(itensCarrinho);
                    pedidoRecuperado.salvar();
                }

            }
        });


        recuperarItemPedidoJamilton();

    }

    @Override
    protected void onStart() {
        super.onStart();
        //recuperarItemPedidoJamilton();
    }

    private void recuperarItemPedidoJamilton() {
        DatabaseReference produtoref = firebaseRef
                .child("pedidos_usuario")
                .child(idUsuarioLogado);

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

    public List<ItemPedido> retornarlista() {
        DatabaseReference produtoref = firebaseRef
                .child("pedidos_usuario")
                .child(idUsuarioLogado);

        produtoref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    pedidoRecuperado = dataSnapshot.getValue(Pedido.class);
                    itensCarrinho2 = pedidoRecuperado.getItens();
                    adapterPedidoComprador.notifyDataSetChanged();

                    System.out.println(String.format("quantidade que está no carrinhoJamilton:  %d nesse exato momento", itensCarrinho2.size()));
                } else {
                    //Toast.makeText(CompradorPedidos.this, "Seu carrinho está vazio", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Error
            }

        });
        System.out.println(String.format("quantidade que está no carrinhoJamiltonfora:  %d nesse exato momento", itensCarrinho.size()));

        return null;
    }
    public void abrirLocalEntrega(View view){

        Intent intent = new Intent(CompradorPedidos.this, CompradorLocalEntrega.class);
        startActivity(intent);
    }


}