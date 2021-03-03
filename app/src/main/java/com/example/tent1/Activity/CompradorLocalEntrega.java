package com.example.tent1.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.tent1.Model.ItemPedido;
import com.example.tent1.Model.LocalEntrega;
import com.example.tent1.Model.NotaAvaliacao;
import com.example.tent1.Model.Pedido;
import com.example.tent1.R;
import com.example.tent1.adapter.AdapterCompradorAvaliacoes;
import com.example.tent1.adapter.AdapterCompradorLocalEntrega;
import com.example.tent1.adapter.AdapterPedidoComprador;
import com.example.tent1.config.ConfiguracaoFirebase;
import com.example.tent1.helper.UsuarioFirebase;
import com.example.tent1.listener.RecyclerItemClickListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CompradorLocalEntrega extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView listaLocalEntrega;
    private Pedido pedidoRecuperado;
    private String idUsuarioLogado;
    private DatabaseReference firebaseRef;
    private AdapterCompradorLocalEntrega adapterCompradorLocalEntrega;
    private List<LocalEntrega> locais  = new ArrayList<>();
    private LocalEntrega localEntregaSelecionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comprador_local_entrega);

        toolbar = findViewById(R.id.toolbarteste);
        //toolbar.setTitle("Página Inicial");
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Local de Entrega");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_localentrega);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navigation_pedidos:
                        startActivity(new Intent(getApplicationContext(), CompradorTodosPedidos.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navigation_localentrega:

                        return true;
                    case R.id.navigation_principal:
                        startActivity(new Intent(getApplicationContext(), CompradorPrincipal.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        listaLocalEntrega = findViewById(R.id.listaLocalEntrega);
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();
        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();

        listaLocalEntrega.setLayoutManager(new LinearLayoutManager(this));
        listaLocalEntrega.setHasFixedSize(true);
        adapterCompradorLocalEntrega = new AdapterCompradorLocalEntrega(locais, this);
        listaLocalEntrega.setAdapter(adapterCompradorLocalEntrega);
        listaLocalEntrega.addOnItemTouchListener(new RecyclerItemClickListener(this, listaLocalEntrega, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                localEntregaSelecionado = locais.get(position);
                System.out.println(localEntregaSelecionado.getRua());
                if (pedidoRecuperado!=null){
                    pedidoRecuperado.setLocalEntrega(localEntregaSelecionado.getDia()
                            + " - "+localEntregaSelecionado.getRua()
                            + " - "+ localEntregaSelecionado.getHora());
                    pedidoRecuperado.salvar();
                    Toast.makeText(CompradorLocalEntrega.this, "Local para entrega foi inserido", Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    Toast.makeText(CompradorLocalEntrega.this, "Adicione itens ao carrinho", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));

        recuperarPedido();
        recuperarLocais();


    }
    public void recuperarPedido(){
        DatabaseReference pedidoRef = firebaseRef
                .child("pedidos_usuario")
                .child(idUsuarioLogado);
        pedidoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue()!= null){
                    pedidoRecuperado = dataSnapshot.getValue(Pedido.class);
                    System.out.println(pedidoRecuperado.getNome());
                    adapterCompradorLocalEntrega.notifyDataSetChanged();
                }else{
                    System.out.println("Não há pedidos");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void recuperarLocais(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference produtoref = firebaseRef.child("localEntrega");
        //.child(idUsuarioLogado);
        produtoref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                locais.clear();

                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    //Retirar esse if caso queira salvar os produtos no firebase com nó de idUsuario
                    locais.add(ds.getValue(LocalEntrega.class));

                }

                adapterCompradorLocalEntrega.notifyDataSetChanged();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}