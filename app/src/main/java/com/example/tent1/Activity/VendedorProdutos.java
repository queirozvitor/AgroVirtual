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

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import dmax.dialog.SpotsDialog;

public class VendedorProdutos extends AppCompatActivity {

    private Toolbar toolbar;
    private AlertDialog dialog;
    private EditText editNome, editDescricao, editPreco;
    private Button botaoSalvar;
    private ImageView imagemProdutos;
    private Spinner spinner;
    private FirebaseAuth autenticacao;
    private DatabaseReference referencia = FirebaseDatabase.getInstance().getReference();
    private Usuario comprador;
    private static final int SELECAO_GALERIA = 200;
    private static final int SELECAO_CAMERA = 100;
    private StorageReference storageRef;
    private String idUsuarioLogado;
    private String urlImagemSelecionada = "";
    private static byte[] dadosImagem;
    private String idProdutoteste = UUID.randomUUID().toString();
    private int verificadorImagem;
    private int verificadorUpload = 0;
    private String categoriaProduto = "";
    private DatabaseReference firebaseRef;

    //Opções para o Spinner
    String[] mOptions = {"Hortaliças", "Frutas", "Outros"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendedor_produtos);

        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Produtos");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recuperarUsuario();

        editNome = findViewById(R.id.editNomeProdutos);
        editDescricao = findViewById(R.id.editDescricaoProdutos);
        editPreco = findViewById(R.id.editPrecoProdutos);
        imagemProdutos = findViewById(R.id.profile_image);
        botaoSalvar = findViewById(R.id.buttonsalvarProdutos);
        spinner = findViewById(R.id.SpinnerCategoriaProdutos);

        //Configurando Adapter para o spinner
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, mOptions);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(aa);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoriaProduto = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Iniciar a programar
        imagemProdutos.setOnClickListener(new View.OnClickListener() {
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




        botaoSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String textoNome = editNome.getText().toString();
                String textoDescricao = editDescricao.getText().toString();
                String textoPreco = editPreco.getText().toString();


                if (!textoNome.isEmpty()){
                    if (!textoDescricao.isEmpty()){
                        if (!textoPreco.isEmpty()){
                            if (verificadorImagem == 1){

                                Produto produto = new Produto();
                                produto.setIdUsuario(idUsuarioLogado);
                                produto.setIdProduto(idProdutoteste);
                                produto.setNome(textoNome);
                                produto.setCategoria(categoriaProduto);
                                produto.setTokenVendedor(comprador.getToken());
                                produto.setNomefiltro(produto.getNome().toLowerCase());
                                produto.setDescricao(textoDescricao);
                                produto.setQtdcarrinho(0);
                                produto.setDesabilitar(0);
                                produto.setPreco(Double.parseDouble(textoPreco));
                                uploadImagem(produto);
                                DialogMensagens();

                                //dialogConfirm(produto);

                                //editNome.setText("");
                                editNome.setEnabled(false);
                                //editDescricao.setText("");
                                editDescricao.setEnabled(false);
                                //editPreco.setText("");
                                editPreco.setEnabled(false);
                                //imagemProdutos.setImageBitmap(null);
                                imagemProdutos.setEnabled(false);
                                idProdutoteste = UUID.randomUUID().toString();
                                botaoSalvar.setEnabled(false);
                                spinner.setEnabled(false);
                                verificadorImagem = 0;
                                //Toast.makeText(VendedorProdutos.this, "Salvando produto no sistema, AGUARDE!", Toast.LENGTH_SHORT).show();
                           }else{
                                Toast.makeText(VendedorProdutos.this, "Insira uma imagem!", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(VendedorProdutos.this, "Preencha o campo com o Preço", Toast.LENGTH_SHORT).show();
                        }

                    }else {
                        Toast.makeText(VendedorProdutos.this, "Preencha o campo com a Descrição", Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(VendedorProdutos.this, "Preencha o campo com o Nome", Toast.LENGTH_SHORT).show();
                }


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
                    case SELECAO_CAMERA:
                        imagem =(Bitmap) data.getExtras().get("data");
                        break;
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
                    //Tem que ser = 0
                    verificadorImagem = 1;
                    imagemProdutos.setImageBitmap(imagem);

                    //RECUPERAR DADOS PARA O FIREBASE
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    dadosImagem = baos.toByteArray();
                    /*
                    storageRef = ConfiguracaoFirebase.getFirebaseStorage();
                    final StorageReference imagemRef = storageRef.child(idUsuarioLogado)
                            .child(idProdutoteste)
                            .child("image.jpeg");
                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(VendedorProdutos.this, "Erro ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            urlImagemSelecionada = String.valueOf(imagemRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    verificadorImagem = 1;
                                    Uri url = uri;
                                    urlImagemSelecionada = url.toString();
                                }
                            }));
                            Toast.makeText(VendedorProdutos.this, "Sucesso ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
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
        UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(VendedorProdutos.this, "Erro ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                botaoSalvar.setEnabled(true);
                editNome.setEnabled(true);
                editDescricao.setEnabled(true);
                editPreco.setEnabled(true);
                spinner.setEnabled(true);
                imagemProdutos.setEnabled(true);
                dialog.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                urlImagemSelecionada = String.valueOf(imagemRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        verificadorImagem = 1;
                        verificadorUpload = 1;
                        Uri url = uri;
                        urlImagemSelecionada = url.toString();
                        dialog.setMessage("Produto Salvo");
                        //Toast.makeText(VendedorProdutos.this, "Sucesso ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                    }
                }));
                dialog.setMessage("Carregando Imagem");
                //Toast.makeText(VendedorProdutos.this, "Sucesso ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        produto.setUrlImagem(urlImagemSelecionada);
                        produto.Salvar();
                        editNome.setText("");
                        editDescricao.setText("");
                        editPreco.setText("");
                        imagemProdutos.setImageResource(R.drawable.iconegaleria);
                        verificadorImagem = 0;
                        verificadorUpload = 0;
                        botaoSalvar.setEnabled(true);
                        editNome.setEnabled(true);
                        editDescricao.setEnabled(true);
                        spinner.setEnabled(true);
                        editPreco.setEnabled(true);
                        imagemProdutos.setEnabled(true);
                        dialog.dismiss();
                        //Toast.makeText(VendedorProdutos.this, "Sucesso ao salvar produto", Toast.LENGTH_SHORT).show();
                    }
                }, 1000);
            }
        });
    }
    public void DialogMensagens(){
        dialog = new SpotsDialog.Builder().setContext(this)
                .setMessage("Salvando produto no Sistema")
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
