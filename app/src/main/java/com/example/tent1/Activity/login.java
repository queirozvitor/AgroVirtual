package com.example.tent1.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tent1.Model.Usuario;
import com.example.tent1.R;
import com.example.tent1.config.ConfiguracaoFirebase;
import com.example.tent1.helper.Base64Custom;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class login extends AppCompatActivity {

    private DatabaseReference referencia = FirebaseDatabase.getInstance().getReference();
    private EditText loginEmail, loginSenha;
    private Button buttonEntrar;
    private Toolbar toolbar;
    private Usuario usuario;
    private FirebaseAuth autenticacao;
    private int verificarUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Faça seu login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loginEmail = findViewById(R.id.loginEmail);
        loginSenha = findViewById(R.id.loginSenha);
        buttonEntrar = findViewById(R.id.loginbutton);


        buttonEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String textoEmail = loginEmail.getText().toString();
                String textoSenha = loginSenha.getText().toString();

                if (!textoEmail.isEmpty()){
                        if (!textoSenha.isEmpty()) {

                            usuario = new Usuario();
                            usuario.setEmail(textoEmail);
                            usuario.setSenha(textoSenha);
                            validarLogin();


                        } else {
                            Toast.makeText(login.this, "Preencha o campo senha!", Toast.LENGTH_SHORT).show();
                        }
                }else{
                    Toast.makeText(login.this, "Preencha o campo email!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void validarLogin(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        autenticacao.signInWithEmailAndPassword(usuario.getEmail(), usuario.getSenha())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            recuperarUsuario();
                            Log.i("signIn", "Sucesso ao logar usuario");

                        }else{

                            String excessao = "";
                            try {
                                throw task.getException();
                            }catch (FirebaseAuthInvalidUserException e) {
                                excessao = "Usuário não existe!";
                                loginEmail.setText("");
                                loginSenha.setText("");
                            }catch (FirebaseAuthInvalidCredentialsException e){
                                excessao = "email e senha não correspondem a um usuário existente!";
                                loginSenha.setText("");
                                loginEmail.setText("");
                            }catch (Exception e){
                                excessao = "Erro ao cadastrar usuário: " + e.getMessage();
                                e.printStackTrace();
                            }

                            Toast.makeText(login.this, excessao, Toast.LENGTH_SHORT).show();
                            Log.i("signIn", "Erro ao logar usuario");
                        }
                    }
                });
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
                    Intent intent = new Intent(login.this, VendedorPrincipal.class);
                    startActivity(intent);
                    finish();
                }else if (verificarUsuario==1){
                    Intent intent = new Intent(login.this, CompradorPrincipal.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void telaVendedorPrincipal(View view){

        Intent intent = new Intent(this, VendedorPrincipal.class);
        startActivity(intent);
    }
}
