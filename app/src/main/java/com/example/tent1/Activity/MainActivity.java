package com.example.tent1.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.tent1.Model.Usuario;
import com.example.tent1.R;
import com.example.tent1.config.ConfiguracaoFirebase;
import com.example.tent1.helper.Base64Custom;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference referencia = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth autenticacao;
    private int verificarUsuario;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        //Logar Usuário
        /*

        vendedores.signInWithEmailAndPassword("joao@gmail.com", "123456")
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (vendedores.getCurrentUser() != null){
                            Log.i("signIn", "Sucesso ao logar usuario");

                        }else{
                            Log.i("signIn", "Erro ao logar usuario");
                        }
                    }
                });
        */

        //Deslogar Usuário atual
        //vendedores.signOut();

        //Verificar se o Usuário está logado
        /*
        if (vendedores.getCurrentUser() != null){
            Log.i("CreateUser", "Usuário logado");
            //vendedores.signOut();
        }else{
            Log.i("CreateUser", "Usuário não está logado");
        }
        */


        //Cadastrando Usuários
        /*
        vendedores.createUserWithEmailAndPassword("joao@gmail.com", "123456")
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){
                            Log.i("CreateUser", "Sucesso ao cadastrar vendedor");
                        }else{
                            Log.i("CreateUser", "Erro ao cadastrar vendedor");
                        }

                    }
                });

         */

    }

    @Override
    protected void onStart() {
        super.onStart();
        DialogMensagens();
        verificarUsuarioLogado();

    }

    public void telaVendedorPrincipal(View view){

        Intent intent = new Intent(this, VendedorPrincipal.class);
        startActivity(intent);
    }

    public void telacadastro(View view){

        Intent intent = new Intent(this, cadastro.class);
        startActivity(intent);
    }
    public void telalogin(View view){

        Intent intent = new Intent(this, login.class);
        startActivity(intent);
    }
    public void verificarUsuarioLogado(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        //Deslogar do usuário atual
        //autenticacao.signOut();
        if (autenticacao.getCurrentUser()!=null){
            recuperarUsuario();
            Log.i("CreateUser", "Usuário está logado");
        }else {
            dialog.dismiss();
            Log.i("CreateUser", "Usuário não está logado");
        }

    }
    public void recuperarUsuario(){
        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        DatabaseReference usuarioRef = referencia.child("usuarios").child(idUsuario);

        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                verificarUsuario = usuario.getVerificador();
                if (verificarUsuario==2){
                    dialog.dismiss();
                    Intent intent = new Intent(MainActivity.this, VendedorPrincipal.class);
                    startActivity(intent);
                    finish();
                }else if (verificarUsuario==1){
                    dialog.dismiss();
                    Intent intent = new Intent(MainActivity.this, CompradorPrincipal.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    public void DialogMensagens(){
        dialog = new SpotsDialog.Builder().setContext(this)
                .setMessage("Verificando login")
                .setCancelable(false)
                .build();
        dialog.show();
    }
}
