package com.example.tent1.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.tent1.Model.Itens;
import com.example.tent1.Model.Pedido;
import com.example.tent1.Model.Produto;
import com.example.tent1.R;
import com.example.tent1.adapter.AdapterPedido;
import com.example.tent1.adapter.AdapterProduto;
import com.example.tent1.config.ConfiguracaoFirebase;
import com.example.tent1.helper.Base64Custom;
import com.example.tent1.helper.UsuarioFirebase;
import com.example.tent1.listener.RecyclerItemClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PedidosVendedor extends AppCompatActivity {

    private Toolbar toolbar;
    private MaterialSearchView searchView;
    private RecyclerView recyclerPedidos;
    private AdapterPedido adapterPedido;
    private FirebaseAuth autenticacao;
    private List<Pedido> pedidos = new ArrayList<>();
    private List<String> pushs = new ArrayList<>();
    private DatabaseReference firebaseRef;
    private String idUsuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos_vendedor);

        searchView = findViewById(R.id.materialSearchView);
        recyclerPedidos = findViewById(R.id.recyclerPedidos);
        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        toolbar = findViewById(R.id.toolbarteste);
        //toolbar.setTitle("Página Inicial");
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Pedidos");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        

        //Configurando RecyclerView
        recyclerPedidos.setLayoutManager(new LinearLayoutManager(this));
        recyclerPedidos.setHasFixedSize(true);
        adapterPedido = new AdapterPedido(pedidos, idUsuarioLogado);
        recyclerPedidos.setAdapter(adapterPedido);

        recuperarPedidos();

        //Evento de clique para o RecyclerView
        recyclerPedidos.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this, recyclerPedidos,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Pedido pedidoSelecionado = pedidos.get(position);
                                String post_push = pushs.get(position);
                                System.out.println(post_push);
                                //pedidoSelecionado.itensRemover(idUsuarioLogado, post_push);
                                DatabaseReference pedidoRef = firebaseRef.child("itensVendedor")
                                        .child(idUsuarioLogado)
                                        .child(pedidoSelecionado.getKey());
                                if (pedidoSelecionado.getStatus().equals("pendente")){
                                    pedidoSelecionado.setStatus("finalizado");
                                    pedidoRef.setValue(pedidoSelecionado);
                                    //adapterPedido.notifyDataSetChanged();
                                }else if (pedidoSelecionado.getStatus().equals("finalizado")){
                                    pedidoSelecionado.setStatus("pendente");
                                    pedidoRef.setValue(pedidoSelecionado);
                                    //adapterPedido.notifyDataSetChanged();
                                }
                                //adapterPedido.notifyDataSetChanged();

                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                                Pedido pedidoSelecionado = pedidos.get(position);
                                metodoRemoverPedido(position, pedidoSelecionado);
                                //Toast.makeText(PedidosVendedor.this, pedidoSelecionado.getNome(), Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );
        /*
        recyclerPedidos.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // Botão foi pressionado
                    //Toast.makeText(VendedorProdutos.this, "Botão foi pressionado!", Toast.LENGTH_SHORT).show();

                    // Muda a cor do botão ao pressionar
                    //v.setBackground(getResources().getDrawable(R.drawable.botoeshover));
                    v.setBackgroundColor(Color.parseColor("#333333"));
                }

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Botão foi solto
                    //Toast.makeText(VendedorProdutos.this, "Botão foi solto!", Toast.LENGTH_SHORT).show();

                    // Muda a cor do botão ao soltar
                    //v.setBackground(getResources().getDrawable(R.drawable.botoes));
                    v.setBackgroundColor(Color.parseColor("#FFFFFF"));
                }
                return false;
            }
        });

         */

        //Configurando o search View
        searchView.setHint("Pesquisar Produtos");
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                pesquisarPedidos(query);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                pesquisarPedidos(newText);
                return true;
            }
        });

    }
    //==============================================================================================
    private void recuperarPedidos(){

        DatabaseReference pedidoRef = firebaseRef.child("itensVendedor").child(idUsuarioLogado);
        pedidoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pedidos.clear();
                pushs.clear();
                    for (DataSnapshot ds:dataSnapshot.getChildren()){
                            Pedido pedido = ds.getValue(Pedido.class);
                            pedido.setKey(ds.getKey());
                            pedidos.add(pedido);
                            //pedidos.add(ds.getValue(Pedido.class));
                            pushs.add(dataSnapshot.getKey());
                    }
                Collections.reverse(pushs);
                Collections.reverse(pedidos);
                    adapterPedido.notifyDataSetChanged();
                }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void pesquisarPedidos(String pesquisa){
        DatabaseReference pedidoRef = firebaseRef.child("itensVendedor").child(idUsuarioLogado);
        Query query = pedidoRef.orderByChild("nome")
                .startAt(pesquisa)
                .endAt(pesquisa + "\uf8ff");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pedidos.clear();

                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    Pedido pedido = ds.getValue(Pedido.class);
                    pedido.setKey(ds.getKey());
                    pedidos.add(pedido);


                }//Termina o for
                Collections.reverse(pedidos);
                adapterPedido.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menupedidos, menu);

        //Configurando pesquisa
        MenuItem item = menu.findItem(R.id.iconPesquisarPedidos);
        searchView.setMenuItem(item);

        return super.onCreateOptionsMenu(menu);
    }

    private void metodoRemoverPedido(final int position, Pedido pedidoSelecionado){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle(pedidoSelecionado.getNome());
        dialog.setCancelable(false);

        //opções
        dialog.setPositiveButton("Remover Pedido?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String post_push = pushs.get(position);
                System.out.println(post_push);
                //pedidoSelecionado.itensRemover(idUsuarioLogado, post_push);
                DatabaseReference pedidoRef = firebaseRef.child("itensVendedor")
                        .child(idUsuarioLogado);
                pedidoRef.child(pedidoSelecionado.getKey()).removeValue();
                //adapterPedido.notifyDataSetChanged();
                //adapterPedido.notifyItemRemoved(position);
                Toast.makeText(PedidosVendedor.this, "Pedido removido", Toast.LENGTH_SHORT).show();

            }
        });

        dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        dialog.create();
        dialog.show();
    }

}
