package com.example.tent1.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.tent1.Model.Produto;
import com.example.tent1.Model.Usuario;
import com.example.tent1.R;
import com.example.tent1.config.ConfiguracaoFirebase;
import com.example.tent1.helper.Base64Custom;
import com.example.tent1.helper.UsuarioFirebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import dmax.dialog.SpotsDialog;

public class VendedorProdutosEditar extends AppCompatActivity {

    private Toolbar toolbar;
    private FirebaseAuth autenticacao;
    private DatabaseReference referencia = FirebaseDatabase.getInstance().getReference();
    private Usuario comprador;
    private AlertDialog dialog;
    private EditText editNomeEditar, editDescricaoEditar, editPrecoEditar;
    private Button botaoSalvarEditar;
    private Spinner spinnerEditar;
    private ImageView imagemProdutosEditar;
    private static final int SELECAO_GALERIA = 200;
    private DatabaseReference firebaseRef;
    private String idUsuarioLogado;
    private Produto produtoAtual;
    private StorageReference storageRef;
    private String urlImagemSelecionadaEditar = "";
    private static byte[] dadosImagemEditar;
    private String idProdutoteste;
    private int verificadorImagemEditar, verificadoralterarimagem;
    private String categoriaProdutoEditar = "";
    private String categoria = "";

    //Opções para o Spinner
    String[] mOptions = {"Hortaliças", "Frutas", "Outros"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendedor_produtos_editar);

        idUsuarioLogado = UsuarioFirebase.getIdUsuario();
        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();

        produtoAtual = (Produto) getIntent().getSerializableExtra("produtoSelecionado");

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Alterar Produto");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recuperarUsuario();

        editNomeEditar = findViewById(R.id.editNomeProdutosEditar);
        editDescricaoEditar = findViewById(R.id.editDescricaoProdutosEditar);
        editPrecoEditar = findViewById(R.id.editPrecoProdutosEditar);
        imagemProdutosEditar = findViewById(R.id.profile_imageEditar);
        botaoSalvarEditar = findViewById(R.id.buttonsalvarProdutosEditar);
        spinnerEditar = findViewById(R.id.SpinnerCategoriaProdutosEditar);

        //Configurando Adapter para o spinner
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, mOptions);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEditar.setAdapter(aa);

        recuperarDadosProduto();

        spinnerEditar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoria = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        imagemProdutosEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i  = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                );
                //Intent i2 = new Intent( MediaStore.ACTION_IMAGE_CAPTURE);

                if (i.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(i, SELECAO_GALERIA);

                }


            }
        });


        //Recuperar os dados do produto selecionado
        botaoSalvarEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String textoNome = editNomeEditar.getText().toString();
                String textoDescricao = editDescricaoEditar.getText().toString();
                String textoPreco = editPrecoEditar.getText().toString();


                if (!textoNome.isEmpty()){
                    if (!textoDescricao.isEmpty()){
                        if (!textoPreco.isEmpty()){
                            if (verificadorImagemEditar == 1){

                                Produto produto = new Produto();
                                produto.setIdUsuario(idUsuarioLogado);
                                produto.setIdProduto(idProdutoteste);
                                produto.setNome(textoNome);
                                produto.setTokenVendedor(comprador.getToken());
                                produto.setCategoria(categoria);
                                produto.setNomefiltro(produto.getNome().toLowerCase());
                                produto.setDescricao(textoDescricao);
                                produto.setPreco(Double.parseDouble(textoPreco));
                                uploadImagem(produto);

                                editNomeEditar.setEnabled(false);
                                editDescricaoEditar.setEnabled(false);
                                editPrecoEditar.setEnabled(false);
                                imagemProdutosEditar.setEnabled(false);
                                spinnerEditar.setEnabled(false);

                                //dialogConfirm(produto);
                                DialogMensagens();

                                //Toast.makeText(VendedorProdutosEditar.this, "Alterando produto no sistema, AGUARDE!", Toast.LENGTH_SHORT).show();

                            }else{
                                Toast.makeText(VendedorProdutosEditar.this, "Insira uma imagem!", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(VendedorProdutosEditar.this, "Preencha o campo com o Preço", Toast.LENGTH_SHORT).show();
                        }

                    }else {
                        Toast.makeText(VendedorProdutosEditar.this, "Preencha o campo com a Descrição", Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(VendedorProdutosEditar.this, "Preencha o campo com o Nome", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

    private void recuperarDadosProduto(){
        DatabaseReference produtoRef = firebaseRef
                .child("produtos")
                //.child(idUsuarioLogado)
                .child(produtoAtual.getIdProduto());
        produtoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    Produto produto = dataSnapshot.getValue(Produto.class);
                    editNomeEditar.setText(produto.getNome());
                    editDescricaoEditar.setText(produto.getDescricao());
                    editPrecoEditar.setText(produto.getPreco().toString());
                    categoriaProdutoEditar = produto.getCategoria();
                    if (categoriaProdutoEditar.equals("Hortaliças")){
                        spinnerEditar.setSelection(0);
                    }else if (categoriaProdutoEditar.equals("Frutas")){
                        spinnerEditar.setSelection(1);
                    }else if (categoriaProdutoEditar.equals("Outros")){
                        spinnerEditar.setSelection(2);
                    }
                    idProdutoteste = produto.getIdProduto();
                    urlImagemSelecionadaEditar = produto.getUrlImagem();
                    if(urlImagemSelecionadaEditar != ""){
                        Picasso.get().load(urlImagemSelecionadaEditar).into(imagemProdutosEditar);
                        verificadorImagemEditar = 1;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            Bitmap imagem = null;

            try {

                switch (requestCode){
                    case SELECAO_GALERIA:
                        Uri localImagem = data.getData();
                        imagem = MediaStore.Images
                                .Media
                                .getBitmap(
                                        getContentResolver(),
                                        localImagem
                                );
                        break;
                }

                if (imagem != null){
                    verificadorImagemEditar = 1;
                    verificadoralterarimagem = 1;
                    imagemProdutosEditar.setImageBitmap(imagem);

                    //RECUPERAR DADOS PARA O FIREBASE
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    dadosImagemEditar = baos.toByteArray();

                    /*storageRef = ConfiguracaoFirebase.getFirebaseStorage();
                    final StorageReference imagemRef = storageRef.child(idUsuarioLogado)
                            .child(idProdutoteste)
                            .child("image.jpeg");
                    UploadTask uploadTask = imagemRef.putBytes(dadosImagemEditar);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(VendedorProdutosEditar.this, "Erro ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            urlImagemSelecionadaEditar = String.valueOf(imagemRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    verificadorImagemEditar = 1;
                                    Uri url = uri;
                                    urlImagemSelecionadaEditar = url.toString();
                                }
                            }));
                            Toast.makeText(VendedorProdutosEditar.this, "Sucesso ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                        }
                    });*/
                }

            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }
    public void uploadImagem(final Produto produto){
        storageRef = ConfiguracaoFirebase.getFirebaseStorage();
        final StorageReference imagemRef = storageRef.child(idUsuarioLogado)
                .child(idProdutoteste)
                .child("image.jpeg");
        if (verificadoralterarimagem == 1){
        UploadTask uploadTask = imagemRef.putBytes(dadosImagemEditar);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(VendedorProdutosEditar.this, "Erro ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                urlImagemSelecionadaEditar = String.valueOf(imagemRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        verificadorImagemEditar = 1;
                        Uri url = uri;
                        urlImagemSelecionadaEditar = url.toString();
                        //Toast.makeText(VendedorProdutos.this, "Sucesso ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                    }
                }));
                dialog.setMessage("Carregando imagem");
                //Toast.makeText(VendedorProdutosEditar.this, "Sucesso ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        produto.setUrlImagem(urlImagemSelecionadaEditar);
                        produto.Salvar();
                        dialog.dismiss();
                        //Toast.makeText(VendedorProdutosEditar.this, "Sucesso ao alterar produto", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }, 3000);
            }
        });}else {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                produto.setUrlImagem(urlImagemSelecionadaEditar);
                produto.Salvar();
                dialog.dismiss();
                //Toast.makeText(VendedorProdutosEditar.this, "Sucesso ao alterar produto", Toast.LENGTH_SHORT).show();
                finish();
            }
        }, 3000);}
    }
    public void DialogMensagens(){
        dialog = new SpotsDialog.Builder().setContext(this)
                .setMessage("Alterando produto no Sistema")
                .setCancelable(false)
                .build();
        dialog.show();
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
                //textCompradorPrincipalUsuario.setText("Olá, " + comprador.getNome());
                //FirebaseMessaging.getInstance().subscribeToTopic(comprador.getIdUsuario());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
