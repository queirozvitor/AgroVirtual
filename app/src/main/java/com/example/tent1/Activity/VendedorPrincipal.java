package com.example.tent1.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tent1.Model.Produto;
import com.example.tent1.Model.Usuario;
import com.example.tent1.R;
import com.example.tent1.adapter.AdapterProduto;
import com.example.tent1.config.ConfiguracaoFirebase;
import com.example.tent1.helper.Base64Custom;
import com.example.tent1.helper.UsuarioFirebase;
import com.example.tent1.listener.RecyclerItemClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VendedorPrincipal extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView textPrincipalUsuario;
    private LinearLayout linearLayout;
    private FirebaseAuth autenticacao;
    private RecyclerView listaProdutosVendedor;
    private List<Produto> produtos  = new ArrayList<>();
    private DatabaseReference firebaseRef;
    private StorageReference storageRef;
    private AdapterProduto adapterProduto;
    private String usuarioNome;
    private String idUsuarioLogado;
    private DatabaseReference referencia = FirebaseDatabase.getInstance().getReference();
    private String situacaoDesabilitado="", outrasituacao = "";
    private int positionDesabilitar, verificadorDesabilitar=4;
    private String produtoDesabilitar="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendedor_principal);

        //dados da Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Página Inicial");

        /*
        ColorDrawable topBorder = new ColorDrawable(Color.BLACK);
        ColorDrawable leftBorder = new ColorDrawable(Color.WHITE);
        ColorDrawable rightBorder = new ColorDrawable(Color.WHITE);
        ColorDrawable bottomBorder = new ColorDrawable(Color.BLACK);
        ColorDrawable background = new ColorDrawable(Color.WHITE);
        Drawable[] layers = new Drawable[]{
                leftBorder,
                topBorder,
                rightBorder,
                bottomBorder,
                background
        };
        LayerDrawable layerDrawable = new LayerDrawable(layers);
        layerDrawable.setLayerInset(0,0,0,15,0);
        layerDrawable.setLayerInset(1,15,0,0,15);
        layerDrawable.setLayerInset(2,15,15,0,0);
        layerDrawable.setLayerInset(3,15,15,15,0);
        layerDrawable.setLayerInset(4,15,15,15,15);
        linearLayout = findViewById(R.id.LinearPrincipal);
        linearLayout.setBackground(layerDrawable);
         */

        idUsuarioLogado = UsuarioFirebase.getIdUsuario();
        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        listaProdutosVendedor = findViewById(R.id.listaProdutosVendedor);
        textPrincipalUsuario = findViewById(R.id.textPrincipalUsuario);
        swipe();

        recuperarUsuario();

        //Configurando RecyclerView
        listaProdutosVendedor.setLayoutManager(new LinearLayoutManager(this));
        listaProdutosVendedor.setHasFixedSize(true);
        adapterProduto = new AdapterProduto(produtos, this);
        listaProdutosVendedor.setAdapter(adapterProduto);

        recuperarProduto();

        //Evento de clique
        listaProdutosVendedor.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this,
                        listaProdutosVendedor, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Produto produtoSelecionado = produtos.get(position);

                        if (produtoSelecionado.getDesabilitar()==0){
                            produtoSelecionado.setDesabilitar(1);
                            produtoSelecionado.Salvar();
                        }else if (produtoSelecionado.getDesabilitar()==1){
                            produtoSelecionado.setDesabilitar(0);
                            produtoSelecionado.Salvar();
                        }
                        //desabilitarConfirm(position);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        //Produto produtoSelecionado = produtos.get(position);
                        dialogConfirm(position);
                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }
                ));


    }
    public void desabilitarConfirm(final int position){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        Produto produtoSelecionado = produtos.get(position);
        if (produtoSelecionado.getDesabilitar() == 0){
            situacaoDesabilitado = "Habilitado";
            outrasituacao = "Deseja Desabilitar? ";
        }else if (produtoSelecionado.getDesabilitar() == 1){
            situacaoDesabilitado = "Desabilitado";
            outrasituacao = "Deseja Habilitar? ";
        }

        dialog.setTitle(situacaoDesabilitado);
        dialog.setMessage(outrasituacao);
        //opções
        dialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //se confirmar os dados são salvos novamente, e sua imagem também
                Produto produtoSelecionado = produtos.get(position);
                if (produtoSelecionado.getDesabilitar()==0){
                    produtoSelecionado.setDesabilitar(1);
                    produtoSelecionado.Salvar();
                }else if (produtoSelecionado.getDesabilitar()==1){
                    produtoSelecionado.setDesabilitar(0);
                    produtoSelecionado.Salvar();
                }

            }
        });

        dialog.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        dialog.create();
        dialog.show();
    }

    public void dialogConfirm(final int position){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        Produto produtoSelecionado = produtos.get(position);

        dialog.setTitle(produtoSelecionado.getNome());
        //dialog.setMessage("Excluir ou Editar produto?");
        /*
        //opções
        dialog.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //se confirmar os dados são salvos novamente, e sua imagem também
                Produto produtoSelecionado = produtos.get(position);
                produtoSelecionado.remover();
                storageRef = ConfiguracaoFirebase.getFirebaseStorage();
                storageRef.child(idUsuarioLogado).child(produtoSelecionado.getIdProduto()).child("image.jpeg").delete();
                Toast.makeText(VendedorPrincipal.this, "Sucesso ao deletar produto !!", Toast.LENGTH_SHORT).show();

            }
        });
        
         */

        dialog.setNegativeButton("Avaliações", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /*
                Produto produtoSelecionado = produtos.get(position);
                Intent intent = new Intent(VendedorPrincipal.this, VendedorProdutosEditar.class);
                intent.putExtra("produtoSelecionado", produtoSelecionado);
                if (produtoSelecionado.getDesabilitar()==1){
                    positionDesabilitar = position;
                    verificadorDesabilitar = 2;
                }

                startActivity(intent);

                 */
                Produto produtolegal = produtos.get(position);
                Intent intent = new Intent(VendedorPrincipal.this, VendedorVerAvaliacoes.class);
                intent.putExtra("produtoSelecionado", produtolegal);
                startActivity(intent);

            }

        });dialog.setPositiveButton("Editar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Produto produtoSelecionado = produtos.get(position);
                Intent intent = new Intent(VendedorPrincipal.this, VendedorProdutosEditar.class);
                intent.putExtra("produtoSelecionado", produtoSelecionado);
                if (produtoSelecionado.getDesabilitar()==1){
                    positionDesabilitar = position;
                    produtoDesabilitar = produtoSelecionado.getIdProduto();
                    verificadorDesabilitar = 2;
                }

                startActivity(intent);



            }

        });


        dialog.create();
        dialog.show();
    }

    private void recuperarProduto(){
        DatabaseReference produtoref = firebaseRef.child("produtos");
                //.child(idUsuarioLogado);
        produtoref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                produtos.clear();

                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    //Retirar esse if caso queira salvar os produtos no firebase com nó de idUsuario
                    if (ds.getValue(Produto.class).getIdUsuario().equals(idUsuarioLogado)) {
                        produtos.add(ds.getValue(Produto.class));
                    }
                }
                Collections.sort(produtos);
                adapterProduto.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void abrirProdutos(View view){

        Intent intent = new Intent(this, VendedorProdutos.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.iconSair:
                autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
                autenticacao.signOut();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                Toast.makeText(VendedorPrincipal.this, "Sucesso ao deslogar usuário!!", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case R.id.iconRelatorio:
                Intent in = new Intent(this, PedidosVendedor.class);
                startActivity(in);

                break;
        }
        return super.onOptionsItemSelected(item);
    }
    public void abrirMain(View view){

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (verificadorDesabilitar==2){
            for (int i = 0; i<produtos.size();i++){
                if (produtos.get(i).getIdProduto().equals(produtoDesabilitar)){
                    System.out.println(produtoDesabilitar);
                    Produto produtoSelecionado = produtos.get(i);
                    produtoSelecionado.setDesabilitar(1);
                    produtoSelecionado.Salvar();
                }
            }

            verificadorDesabilitar=4;
        }
    }

    public void recuperarUsuario(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        DatabaseReference usuarioRef = referencia.child("usuarios").child(idUsuario);

        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                usuarioNome = usuario.getNome();
                textPrincipalUsuario.setText("Olá, " + usuarioNome);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void swipe(){

        ItemTouchHelper.Callback itemTouch = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int draFlags = ItemTouchHelper.ACTION_STATE_IDLE;
                int swipeFlags = ItemTouchHelper.START;
                return makeMovementFlags(draFlags, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                metodoExcluir(viewHolder);
            }
        };
        new ItemTouchHelper(itemTouch).attachToRecyclerView(listaProdutosVendedor);

    }
    private void metodoExcluir(final RecyclerView.ViewHolder viewHolder){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("ATENÇÃO!");
        dialog.setMessage("Excluir produto?");
        dialog.setCancelable(false);

        //opções
        dialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int position = viewHolder.getAdapterPosition();
                Produto produtoSelecionado = produtos.get(position);
                produtoSelecionado.remover();
                storageRef = ConfiguracaoFirebase.getFirebaseStorage();
                storageRef.child(idUsuarioLogado).child(produtoSelecionado.getIdProduto()).child("image.jpeg").delete();
                Toast.makeText(VendedorPrincipal.this, "Sucesso ao deletar produto !!", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                adapterProduto.notifyDataSetChanged();
            }
        });

        dialog.create();
        dialog.show();
    }

}
