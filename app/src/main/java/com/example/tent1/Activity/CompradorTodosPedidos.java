package com.example.tent1.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.example.tent1.Model.ItemPedido;
import com.example.tent1.Model.Pedido;
import com.example.tent1.Model.Produto;
import com.example.tent1.R;
import com.example.tent1.adapter.AdapterCompradorPedidos;
import com.example.tent1.adapter.AdapterPedido;
import com.example.tent1.adapter.AdapterPedidoComprador;
import com.example.tent1.config.ConfiguracaoFirebase;
import com.example.tent1.helper.UsuarioFirebase;
import com.example.tent1.listener.RecyclerItemClickListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompradorTodosPedidos extends AppCompatActivity {

    private Toolbar toolbar;
    private MaterialSearchView searchView;
    private RecyclerView listaTodosPedidos;
    private AdapterCompradorPedidos adapterPedido;
    private FirebaseAuth autenticacao;
    private List<Pedido> pedidos = new ArrayList<>();
    private List<String> pushs = new ArrayList<>();
    private DatabaseReference firebaseRef;
    private String idUsuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comprador_todos_pedidos);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_pedidos);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navigation_pedidos:

                        return true;
                    case R.id.navigation_localentrega:
                        startActivity(new Intent(getApplicationContext(), CompradorLocalEntrega.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navigation_principal:
                        startActivity(new Intent(getApplicationContext(), CompradorPrincipal.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        listaTodosPedidos = findViewById(R.id.listaCompradorTodosPedidos);
        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        toolbar = findViewById(R.id.toolbarteste);
        //toolbar.setTitle("Página Inicial");
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Pedidos");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Configurando RecyclerView
        listaTodosPedidos.setLayoutManager(new LinearLayoutManager(this));
        listaTodosPedidos.setHasFixedSize(true);
        adapterPedido = new AdapterCompradorPedidos(pedidos, idUsuarioLogado);
        listaTodosPedidos.setAdapter(adapterPedido);

        recuperarPedidos();

        listaTodosPedidos.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this, listaTodosPedidos,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Pedido pedidoSelecionado = pedidos.get(position);
                                Intent intent = new Intent(CompradorTodosPedidos.this, CompradorTodosPedidosEspecifico.class);
                                intent.putExtra("StringKey", pedidoSelecionado.getKey());
                                startActivity(intent);

                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                                Pedido pedidoSelecionado = pedidos.get(position);
                                //Toast.makeText(PedidosVendedor.this, pedidoSelecionado.getNome(), Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );

    }
    private void recuperarPedidos(){

        DatabaseReference pedidoRef = firebaseRef.child("pedidos").child(idUsuarioLogado);
        pedidoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pedidos.clear();

                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    Pedido pedido = ds.getValue(Pedido.class);
                    pedido.setKey(ds.getKey());
                    pedidos.add(pedido);
                    //pedidos.add(ds.getValue(Pedido.class));

                }
                Collections.reverse(pedidos);
                adapterPedido.notifyDataSetChanged();
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}