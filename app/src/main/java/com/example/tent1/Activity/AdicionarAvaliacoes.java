package com.example.tent1.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.tent1.Model.NotaAvaliacao;
import com.example.tent1.Model.Produto;
import com.example.tent1.Model.Usuario;
import com.example.tent1.R;
import com.example.tent1.config.ConfiguracaoFirebase;
import com.example.tent1.helper.Base64Custom;
import com.example.tent1.helper.UsuarioFirebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdicionarAvaliacoes extends AppCompatActivity {

    private Toolbar toolbar;
    private Produto produtoAtual;
    private String idUsuarioLogado;
    private FirebaseAuth autenticacao;
    private DatabaseReference referencia = FirebaseDatabase.getInstance().getReference();
    private Usuario comprador;
    private String idproduto;
    private NotaAvaliacao notaAvaliacao;

    //Configurações SeekBar
    private TextView textoprogresso;
    private SeekBar seekBar;
    private static double valordoSeekbar=0.0;

    //Configurando demais componentes;
    private EditText editComentario;
    private Button buttonSalvar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_avaliacoes);


        toolbar = findViewById(R.id.toolbarteste);
        //toolbar.setTitle("Página Inicial");
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Adicionar Avaliação");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        produtoAtual = (Produto) getIntent().getSerializableExtra("produtoSelecionado");
        idproduto = produtoAtual.getIdProduto();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();
        recuperarUsuario();
        recuperarAvaliacoes();
        textoprogresso = findViewById(R.id.textProgress);
        seekBar = findViewById(R.id.seekbarAvaliacao);
        editComentario = findViewById(R.id.editComentarioAvaliacao);
        buttonSalvar = findViewById(R.id.buttonsalvarAvaliacao);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double progressinho = (Double.parseDouble(String.valueOf(progress)));
                valordoSeekbar = progressinho/10;

                textoprogresso.setText(" "+ valordoSeekbar);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        buttonSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comentario = editComentario.getText().toString();

                if (!comentario.isEmpty()){
                    NotaAvaliacao notaAvaliacao = new NotaAvaliacao();
                    notaAvaliacao.setComentario(comentario);
                    notaAvaliacao.setNota(valordoSeekbar);
                    notaAvaliacao.setIdProduto(produtoAtual.getIdProduto());
                    notaAvaliacao.setIdUsuario(idUsuarioLogado);
                    notaAvaliacao.setNomeUsuario(comprador.getNome());

                    //Toast.makeText(AdicionarAvaliacoes.this, String.format("%s Nota: %f", notaAvaliacao.getComentario(), notaAvaliacao.getNota()), Toast.LENGTH_SHORT).show();
                    notaAvaliacao.salvar(produtoAtual.getIdProduto(), idUsuarioLogado);
                    Toast.makeText(AdicionarAvaliacoes.this, String.format("Avaliação salva!"), Toast.LENGTH_SHORT).show();
                    editComentario.setEnabled(false);
                    finish();

                }else{
                    Toast.makeText(AdicionarAvaliacoes.this, "Insira um comentário!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        valordoSeekbar=0.0;
    }
    public void recuperarUsuario(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        DatabaseReference usuarioRef = referencia.child("usuarios").child(idUsuario);

        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                comprador = dataSnapshot.getValue(Usuario.class);
                //Toast.makeText(AdicionarAvaliacoes.this, comprador.getNome(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void recuperarAvaliacoes(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference produtoref = firebaseRef.child("avaliacoes").child(idproduto).child(idUsuarioLogado);
        //.child(idUsuarioLogado);
        produtoref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    notaAvaliacao = new NotaAvaliacao();
                    //itensCarrinho2.add(pedidoRecuperado.getItens());
                    notaAvaliacao = dataSnapshot.getValue(NotaAvaliacao.class);
                    editComentario.setText(notaAvaliacao.getComentario());
                    Double notao = notaAvaliacao.getNota();
                    int notinha = (int) (notao*10);
                    //System.out.println(notinha);
                    seekBar.setProgress(notinha);
                } else {
                    //Toast.makeText(CompradorPedidos.this, "Seu carrinho está vazio", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}