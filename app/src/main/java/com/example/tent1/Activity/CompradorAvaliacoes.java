package com.example.tent1.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tent1.Model.Itens;
import com.example.tent1.Model.NotaAvaliacao;
import com.example.tent1.Model.Produto;
import com.example.tent1.R;
import com.example.tent1.adapter.AdapterCompradorAvaliacoes;
import com.example.tent1.adapter.AdapterProduto;
import com.example.tent1.config.ConfiguracaoFirebase;
import com.example.tent1.helper.UsuarioFirebase;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CompradorAvaliacoes extends AppCompatActivity {

    private Toolbar toolbar;
    private FloatingActionButton floatingbuttonn;
    private static Produto produtoAtual;
    private RecyclerView listaAvaliacoesComprador;
    private String idUsuarioLogado;
    private AdapterCompradorAvaliacoes adapterCompradorAvaliacoes;
    private List<NotaAvaliacao> produtos  = new ArrayList<>();
    private DatabaseReference firebaseRef;
    private List<Itens> usuariosAvaliacao = new ArrayList<>();
    private int verificadorUsuarioAvaliador=0, verificadorParaEntrarAvaliacoes=0;

    private String idproduto;

    private TextView testando;
    private TextView nota;

    private static int verificador = 0;
    private Double notao = 0.0;

    //Configuração colocar média das avaliações;
    private int quantidade;
    private Double media;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comprador_avaliacoes);

        floatingbuttonn = findViewById(R.id.floatingAdicionarAvaliacao);
        testando = findViewById(R.id.textteste);
        listaAvaliacoesComprador = findViewById(R.id.listaAvaliacoesComprador);
        nota = findViewById(R.id.TextNotaAvaliadores);
        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();

        if (getIntent().getSerializableExtra("produtoSelecionado")!=null){
            produtoAtual = (Produto) getIntent().getSerializableExtra("produtoSelecionado");

        }

        testando.setText(produtoAtual.getNome());
        idproduto = produtoAtual.getIdProduto();
        recuperarUsuarioAvaliador(idproduto);
        System.out.println(idproduto);
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();
        nota.setText("0.0");

        toolbar = findViewById(R.id.toolbarteste);
        //toolbar.setTitle("Página Inicial");
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Avaliações");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listaAvaliacoesComprador.setLayoutManager(new LinearLayoutManager(this));
        listaAvaliacoesComprador.setHasFixedSize(true);
        adapterCompradorAvaliacoes = new AdapterCompradorAvaliacoes(produtos, this);
        listaAvaliacoesComprador.setAdapter(adapterCompradorAvaliacoes);

        recuperarAvaliacoes();

        /*
        int quantidade = produtos.size();
        for (int i = 0; i<produtos.size(); i++){
            notao +=produtos.get(i).getNota();
        }
        Double resultado = (notao/quantidade);
        nota.setText(" " + produtos.get(0).getNota());
        */

        floatingbuttonn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (usuariosAvaliacao.size()==0){
                    Toast.makeText(CompradorAvaliacoes.this, "Você não está autorizado a ver Avaliações de "+produtoAtual.getNome(), Toast.LENGTH_SHORT).show();
                }else if (usuariosAvaliacao.size()>=1){
                    System.out.println(usuariosAvaliacao.size());
                    verificadorParaEntrarAvaliacoes=0;
                    for (int i=0; i<usuariosAvaliacao.size(); i++){
                        if (usuariosAvaliacao.get(i).getIdComprador().equals(idUsuarioLogado)){
                            verificadorParaEntrarAvaliacoes=1;
                            System.out.println(usuariosAvaliacao.get(i).getIdComprador());
                        }
                    }
                    if (verificadorParaEntrarAvaliacoes==1){
                        verificadorParaEntrarAvaliacoes=0;
                        Intent intent = new Intent(CompradorAvaliacoes.this, AdicionarAvaliacoes.class);
                        intent.putExtra("produtoSelecionado", produtoAtual);
                        startActivity(intent);
                    }else if (verificadorParaEntrarAvaliacoes==0){
                        Toast.makeText(CompradorAvaliacoes.this, "Você não está autorizado a ver Avaliações de "+produtoAtual.getNome(), Toast.LENGTH_SHORT).show();
                        verificadorParaEntrarAvaliacoes=0;
                    }
                }

                //Toast.makeText(CompradorPrincipal.this, "EAI MEU CHAPA", Toast.LENGTH_SHORT).show();

            }
        });

    }
    public void abrirPedidosComprador(View view){

        Intent intent = new Intent(CompradorAvaliacoes.this, AdicionarAvaliacoes.class);
        startActivity(intent);
    }
    private void recuperarAvaliacoes(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference produtoref = firebaseRef.child("avaliacoes").child(idproduto);
        //.child(idUsuarioLogado);
        produtoref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                produtos.clear();
                quantidade = 0;
                media = 0.0;
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    //Retirar esse if caso queira salvar os produtos no firebase com nó de idUsuario
                        produtos.add(ds.getValue(NotaAvaliacao.class));

                            quantidade += 1;
                            Double nota = ds.getValue(NotaAvaliacao.class).getNota();
                            media += nota;

                }
                DecimalFormat df = new DecimalFormat("0.0");
                nota.setText(" "+df.format(media/quantidade));
                adapterCompradorAvaliacoes.notifyDataSetChanged();
                if (quantidade==0){
                    nota.setText(" ");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                nota.setText("0.0");

            }
        });
    }
    private void recuperarUsuarioAvaliador(String idProduto){
        DatabaseReference produtoref = firebaseRef.child("usuarios_avaliacao").child(idProduto);
        produtoref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usuariosAvaliacao.clear();

                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    if (dataSnapshot.getValue()!= null){
                        usuariosAvaliacao.add(ds.getValue(Itens.class));
                    }
                }
                adapterCompradorAvaliacoes.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}