package com.example.tent1.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.tent1.Model.Usuario;
import com.example.tent1.R;
import com.example.tent1.config.ConfiguracaoFirebase;
import com.example.tent1.helper.Base64Custom;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class cadastro extends AppCompatActivity {

    private DatabaseReference referencia = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth autenticacao = FirebaseAuth.getInstance();

    private Toolbar toolbar;
    private RadioGroup radiogroup;
    private EditText cadastrarEmail, cadastrarSenha, cadastrarNome, cadastrarTelefone, cadastrarCidade;
    private RadioButton radiobutton, comprador, vendedor;
    private Button buttonCadastrar;
    private Usuario usuario;
    private String tokenn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Cadastro");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        radiogroup = findViewById(R.id.cadastrarradiogroup);
        buttonCadastrar = findViewById(R.id.cadastrarBotao);
        cadastrarEmail = findViewById(R.id.cadastrarEmail);
        cadastrarSenha = findViewById(R.id.cadastrarSenha);
        cadastrarNome = findViewById(R.id.cadastrarNome);
        cadastrarTelefone = findViewById(R.id.cadastrarTelefone);
        cadastrarCidade = findViewById(R.id.cadastrarCidade);

        comprador = findViewById(R.id.cadastrarbotaocomprador);
        vendedor = findViewById(R.id.cadastrarbotaovendedor);

        //Máscara
        SimpleMaskFormatter smfTelefone = new SimpleMaskFormatter("(NN)NNNNN-NNNN");
        MaskTextWatcher mtwTelefone = new MaskTextWatcher(cadastrarTelefone, smfTelefone);
        cadastrarTelefone.addTextChangedListener(mtwTelefone);



        buttonCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pegando valor selecionado do radiogroup
                int radioId = radiogroup.getCheckedRadioButtonId();

                radiobutton = findViewById(radioId);

                String textoEmail = cadastrarEmail.getText().toString();
                String textoSenha = cadastrarSenha.getText().toString();
                String textoNome = cadastrarNome.getText().toString();
                String textoCidade = cadastrarCidade.getText().toString();
                String textoTelefone = cadastrarTelefone.getText().toString();
                String verificador = radiobutton.getText().toString();


                if (!textoEmail.isEmpty()){
                    if (!textoSenha.isEmpty()){
                        if (!textoNome.isEmpty()){
                            if (!textoCidade.isEmpty()){
                                if(!textoTelefone.isEmpty()){
                                    if (textoTelefone.length()==14){

                                usuario = new Usuario();
                                usuario.setEmail(textoEmail);
                                usuario.setSenha(textoSenha);
                                usuario.setNome(textoNome);
                                usuario.setCidade(textoCidade);
                                usuario.setTelefone(textoTelefone);
                                if (verificador.equals("Vendedor")){
                                    usuario.setVerificador(2);
                                }else if(verificador.equals("Comprador")){
                                    usuario.setVerificador(1);
                                }
                                        FirebaseMessaging.getInstance().getToken()
                                                .addOnCompleteListener(new OnCompleteListener<String>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<String> task) {

                                                        if ( task.isSuccessful() ){
                                                            tokenn = task.getResult();
                                                            usuario.setToken(tokenn);
                                                        }



                                                        Log.i("Token", tokenn);

                                                    }
                                                });
//                                DatabaseReference ref = referencia.child("usuarios");
//                                ref.child(usuario.getId()).setValue(usuario);

                                cadastrarUsuario(v);

                                    }else{
                                        Toast.makeText(cadastro.this, "Número de telefone inválido", Toast.LENGTH_SHORT).show(); }
                                }else{
                                    Toast.makeText(cadastro.this, "Preencha o campo com o Telefone", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(cadastro.this, "Preencha o campo com a Cidade", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(cadastro.this, "Preencha o campo com o Nome", Toast.LENGTH_SHORT).show();
                        }

                    }else {
                        Toast.makeText(cadastro.this, "Preencha o campo com o Senha", Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(cadastro.this, "Preencha o campo com o Email", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
    public void checkbutton(View v){
        int radioId = radiogroup.getCheckedRadioButtonId();

        radiobutton = findViewById(radioId);
    }

    public void abrirMain(View view){

        Intent intent = new Intent(cadastro.this, MainActivity.class);
        startActivity(intent);
    }
    public void cadastrarUsuario(final View view){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(usuario.getEmail(), usuario.getSenha())
                .addOnCompleteListener(cadastro.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){

                            String idUsuario = Base64Custom.codificarBase64(usuario.getEmail());
                            usuario.setIdUsuario(idUsuario);
                            usuario.Salvar();

                            Toast.makeText(cadastro.this, "Sucesso ao cadastrar usuário", Toast.LENGTH_SHORT).show();
                            cadastrarEmail.setText("");
                            cadastrarSenha.setText("");
                            cadastrarNome.setText("");
                            cadastrarCidade.setText("");
                            cadastrarTelefone.setText("");
                            autenticacao.signOut();

                            Log.i("CreateUser", "Sucesso ao cadastrar usuário");
                        }else{
                            String excessao = "";
                            try {
                                throw task.getException();
                            }catch (FirebaseAuthWeakPasswordException e){
                                excessao = "Digite uma senha mais forte!";
                                cadastrarSenha.setText("");
                            }catch (FirebaseAuthInvalidCredentialsException e){
                                excessao = "Por favor, digite um email válido!";
                                cadastrarEmail.setText("");
                            }catch (FirebaseAuthUserCollisionException e){
                                excessao = "Essa conta já existe!";
                                cadastrarEmail.setText("");
                                cadastrarSenha.setText("");
                                cadastrarNome.setText("");
                                cadastrarTelefone.setText("");
                            }catch (Exception e){
                                excessao = "Erro ao cadastrar usuário: " + e.getMessage();
                                e.printStackTrace();
                            }

                            Toast.makeText(cadastro.this, excessao, Toast.LENGTH_SHORT).show();
                            Log.i("CreateUser", "Erro ao cadastrar usuário");
                        }

                    }
                });
    }


}

