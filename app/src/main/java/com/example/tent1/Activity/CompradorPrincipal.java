package com.example.tent1.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tent1.Model.Categoria;
import com.example.tent1.Model.ItemPedido;
import com.example.tent1.Model.Itens;
import com.example.tent1.Model.Notificacao;
import com.example.tent1.Model.NotificacaoDados;
import com.example.tent1.Model.Pedido;
import com.example.tent1.Model.Produto;
import com.example.tent1.Model.Usuario;
import com.example.tent1.R;
import com.example.tent1.adapter.AdapterCompradorCarrinho;
import com.example.tent1.adapter.AdapterProduto;
import com.example.tent1.adapter.AdapterProdutoComprador;
import com.example.tent1.adapter.AdapterSpinner;
import com.example.tent1.api.NotificacaoService;
import com.example.tent1.config.ConfiguracaoFirebase;
import com.example.tent1.helper.Base64Custom;
import com.example.tent1.helper.UsuarioFirebase;
import com.example.tent1.listener.RecyclerItemClickListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CompradorPrincipal extends AppCompatActivity {

    private Toolbar toolbar;
    private int qtdItensCarrinho;
    private Double totalCarrinho;
    private MaterialSearchView searchView;
    private Spinner spinner;
    private TextView textviewCategoria;
    private TextView textCompradorPrincipalUsuario, textCompradorPrincipalQtd, textCompradorPrincipalRS, textAbrirPedidos;
    private FloatingActionButton floatingbuttonn;
    private LinearLayout linearLayout;
    private FirebaseAuth autenticacao;
    private RecyclerView listaProdutosComprador;
    private List<Itens> usuariosAvaliacao = new ArrayList<>();
    private List<String> nomesVendedor = new ArrayList<>();
    private List<Produto> produtos  = new ArrayList<>();
    private List<ItemPedido> itensCarrinho  = new ArrayList<>();
    private List<ItemPedido> itensCarrinho2 = new ArrayList<>();
    private DatabaseReference firebaseRef;
    private StorageReference storageRef;
    private AdapterProduto adapterProduto;
    private Usuario comprador;
    private String usuarioNome;
    private String idUsuarioLogado;
    private DatabaseReference referencia = FirebaseDatabase.getInstance().getReference();
    private String categoriaProduto = "";
    private String cateTodos = "Todos", cateOutros = "Outros", cateFru = "Frutas", cateHor = "Hortaliças";

    //Configuração Classificações
    private Produto produtoSelecionado;

    //Spinner
    private Categoria cate, testando;
    private AdapterSpinner adapterSpinner;
    private ArrayList<Categoria> listaCategoria;

    //AdapterCompradorProdutos
    private AdapterProdutoComprador adapterProdutoComprador;

    //Adapter para realizar o Carrinho de compras;
    private AdapterCompradorCarrinho adapterCompradorCarrinho;

    //Criando Pedido
    private Pedido pedidoRecuperado;
    private int verificadorPagamento=0;
    int verificadoritens = 0, verificadorDesabilitar = 0, veriId = 0;
    private Itens itens;
    private List<String> idProdutoDesabilitar = new ArrayList<>();

    //contadores itensConfirmar
    private int contadorNomesVendedor = 0;

    //Opções para o Spinner
    String[] mOptions = { "Todos","Hortaliças", "Frutas", "Outros"};
    private List<Integer> listaNumeros = new ArrayList<Integer>();

    //Coisas para notificação
    private Retrofit retrofit;
    private String baseUrl;

    //Configurando local para entrega
    private String localEntrega;

    //Configurações para as configurações do avaliador do produto
    private int verificadorUsuarioAvaliador=0, verificadorParaEntrarAvaliacoes=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comprador_principal);

        //dados da Toolbar
        toolbar = findViewById(R.id.toolbarteste);
        //toolbar.setTitle("Página Inicial");
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Página Inicial");

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_principal);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navigation_pedidos:
                        startActivity(new Intent(getApplicationContext(), CompradorTodosPedidos.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navigation_localentrega:
                        startActivity(new Intent(getApplicationContext(), CompradorLocalEntrega.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navigation_principal:
                        return true;
                }
                return false;
            }
        });

        idUsuarioLogado = UsuarioFirebase.getIdUsuario();
        searchView = findViewById(R.id.materialSearchView);
        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        listaProdutosComprador = findViewById(R.id.listaProdutosComprador);
        //textCompradorPrincipalUsuario = findViewById(R.id.textCompradorPrincipalUsuario);
        textCompradorPrincipalRS = findViewById(R.id.TextRSPrincipal);
        textCompradorPrincipalQtd = findViewById(R.id.TextQtdPrincipal);
        textAbrirPedidos = findViewById(R.id.idAbrirPedidos);
        floatingbuttonn = findViewById(R.id.floatingAdicionarPedido);
        spinner = findViewById(R.id.SpinnerCategoriaComprador);

        recuperarUsuario();

        //Configurando RecyclerView
        listaProdutosComprador.setLayoutManager(new LinearLayoutManager(this));
        listaProdutosComprador.setHasFixedSize(true);
        adapterProdutoComprador = new AdapterProdutoComprador(produtos, this);
        //listaProdutosComprador.setAdapter(adapterProdutoComprador);

        //Configurando RecyclerView para o carrinho
        ///*
        adapterCompradorCarrinho = new AdapterCompradorCarrinho(produtos);
        listaProdutosComprador.setAdapter(adapterCompradorCarrinho);
        adapterCompradorCarrinho.setOnItemClickListener(new AdapterCompradorCarrinho.OnItemClickListener() {
            @Override
            public void onAddClick(int position) {
                int quantidade = 1;
                Produto produtoselecionado = produtos.get(position);
                //Toast.makeText(CompradorPrincipal.this, produtoselecionado.getNome(), Toast.LENGTH_SHORT).show();
                ///*
                ItemPedido itemPedido = new ItemPedido();
                itemPedido.setIdProduto(produtoselecionado.getIdProduto());
                itemPedido.setNomeProduto(produtoselecionado.getNome());
                itemPedido.setPreco(produtoselecionado.getPreco());
                itemPedido.setQuantidade(quantidade);
                itemPedido.setTokenVendedor(produtoselecionado.getTokenVendedor());
                itemPedido.setURLImage(produtoselecionado.getUrlImagem());
                itemPedido.setIdVendedor(produtoselecionado.getIdUsuario());
                itemPedido.setDescricao(produtoselecionado.getDescricao());
                ///*
                if (itensCarrinho.isEmpty()){
                    itensCarrinho.add(itemPedido);
                    Toast.makeText(CompradorPrincipal.this, "primeiro item", Toast.LENGTH_SHORT).show();
                }else if (!itensCarrinho.isEmpty()){
                    for (int i = 0; i<itensCarrinho.size(); i++){
                        if (itensCarrinho.get(i).getIdProduto().equals(produtoselecionado.getIdProduto())){
                            itensCarrinho.get(i).setQuantidade(itensCarrinho.get(i).getQuantidade()+1);
                            verificadoritens = 0;
                            Toast.makeText(CompradorPrincipal.this, "repetiu", Toast.LENGTH_SHORT).show();
                        }else{
                            verificadoritens += 1;
                        }
                    } if (verificadoritens == itensCarrinho.size()){
                        itensCarrinho.add(itemPedido);
                        Toast.makeText(CompradorPrincipal.this, "item novo", Toast.LENGTH_SHORT).show();
                        verificadoritens = 0;
                    }
                    verificadoritens = 0;

                }
                // */
                /*
                if (itensCarrinho.isEmpty()){
                    itensCarrinho.add(itemPedido);
                }else {
                    for (ItemPedido item : itensCarrinho) {
                        // Verifica se o produto ja existe no carrinho
                        if (!(item.getIdProduto().equals(produtoselecionado.getIdProduto()))) {
                            itensCarrinho.add(itemPedido);
                        }if ((item.getIdProduto().equals(produtoselecionado.getIdProduto()))) {
                            item.setQuantidade(item.getQuantidade()+1);
                        }
                    }
                }
                */

                if (pedidoRecuperado == null){
                    pedidoRecuperado = new Pedido(idUsuarioLogado);
                }
                pedidoRecuperado.setNome(comprador.getNome());
                pedidoRecuperado.setCidade(comprador.getCidade());
                pedidoRecuperado.setTelefoneComprador(comprador.getTelefone());
                pedidoRecuperado.setItens(itensCarrinho);
                pedidoRecuperado.salvar();
                //Toast.makeText(CompradorPrincipal.this, produtoselecionado.getNome() + " foi adicionado", Toast.LENGTH_SHORT).show();
                 //*/
            }
        });
        //*/
        ///*
        listaProdutosComprador.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this,
                        listaProdutosComprador, new RecyclerItemClickListener.OnItemClickListener() {

                    public void onItemClick(View view, int position) {

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        produtoSelecionado = produtos.get(position);
                        recuperarUsuarioAvaliador(produtoSelecionado.getIdProduto());
                        metodoAvaliacoes(produtoSelecionado, view, position);


                        //Toast.makeText(CompradorPrincipal.this, produtoselecionado.getNome() + " foi clicado", Toast.LENGTH_SHORT).show();

                    }

                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }

                }
                ));
        //*/
        recuperarProduto();

        //Configurando Adapter para o spinner
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, mOptions);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

          listaCategoria = new ArrayList<>();
          listaCategoria.add(new Categoria(cateTodos));
          listaCategoria.add(new Categoria(cateHor));
          listaCategoria.add(new Categoria(cateFru));
          listaCategoria.add(new Categoria(cateOutros));
//        cate.setNome(cateTodos);
//        listaCategoria.add(cate);
//        cate.setNome(cateHor);
//        listaCategoria.add(cate);
//        cate.setNome(cateFru);
//        listaCategoria.add(cate);
//        cate.setNome(cateOutros);
//        listaCategoria.add(cate);
        adapterSpinner = new AdapterSpinner(this, listaCategoria);
        spinner.setAdapter(adapterSpinner);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                testando =  listaCategoria.get(position);
                categoriaProduto = testando.getNome();
                if (categoriaProduto.equals("Todos")){
                    recuperarProduto();
                } else {
                    Categoria(categoriaProduto);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Configurando o search View
        searchView.setHint("Pesquisar Produtos");
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                pesquisarProdutos(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                pesquisarProdutos(newText);
                return true;
            }
        });

        textAbrirPedidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirPedidosComprador(v);

            }
        });

        floatingbuttonn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                metodoPagamento();
                //Toast.makeText(CompradorPrincipal.this, "EAI MEU CHAPA", Toast.LENGTH_SHORT).show();

            }
        });

        baseUrl="https://fcm.googleapis.com/fcm/";
        retrofit=new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }

    //TODOS OS MÉTODOS
    //=====================================================================================================================================

    public void enviarNotificacoes(String token){
        String to = "";
        to = token;
        Notificacao notificacao = new Notificacao("Pedido Recebido", "Você recebeu um novo pedido");
        NotificacaoDados notificacaoDados = new NotificacaoDados(to, notificacao);
        NotificacaoService service = retrofit.create(NotificacaoService.class);
        Call<NotificacaoDados> call = service.salvarNotificacao(notificacaoDados);

        call.enqueue(new Callback<NotificacaoDados>() {
            @Override
            public void onResponse(Call<NotificacaoDados> call, Response<NotificacaoDados> response) {
                if (response.isSuccessful()){

                    //Toast.makeText(CompradorPrincipal.this, "codigo "+response.code(), Toast.LENGTH_SHORT).show();


                }
            }

            @Override
            public void onFailure(Call<NotificacaoDados> call, Throwable t) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarPedido();
    }

    private void pesquisarProdutos(String pesquisa){
        DatabaseReference produtosRef = firebaseRef.child("produtos");
        Query query = produtosRef.orderByChild("nomefiltro")
                .startAt(pesquisa)
                .endAt(pesquisa + "\uf8ff");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                produtos.clear();

                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    if (categoriaProduto.equals("Todos") && ds.getValue(Produto.class).getDesabilitar()==0){
                        produtos.add(ds.getValue(Produto.class));}
                    else if (categoriaProduto.equals(cateHor) && ds.getValue(Produto.class).getCategoria().equals(categoriaProduto) && ds.getValue(Produto.class).getDesabilitar()==0){
                        produtos.add(ds.getValue(Produto.class));
                    }else if (categoriaProduto.equals(cateFru) && ds.getValue(Produto.class).getCategoria().equals(categoriaProduto) && ds.getValue(Produto.class).getDesabilitar()==0){
                        produtos.add(ds.getValue(Produto.class));
                    }else if (categoriaProduto.equals(cateOutros) && ds.getValue(Produto.class).getCategoria().equals(categoriaProduto) && ds.getValue(Produto.class).getDesabilitar()==0){
                        produtos.add(ds.getValue(Produto.class));
                    }

                }//Termina o for
                Collections.sort(produtos);
                adapterCompradorCarrinho.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menucomprador, menu);

        //Configurando pesquisa
        MenuItem item = menu.findItem(R.id.iconPesquisar);
        searchView.setMenuItem(item);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.iconSairComprador:
                autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
                autenticacao.signOut();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                Toast.makeText(CompradorPrincipal.this, "Sucesso ao deslogar usuário!!", Toast.LENGTH_SHORT).show();
                finish();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void recuperarProduto(){
        DatabaseReference produtoref = firebaseRef.child("produtos");
        produtoref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                produtos.clear();

                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    if (ds.getValue(Produto.class).getDesabilitar()==0) {
                        produtos.add(ds.getValue(Produto.class));
                    }else{

                    }
                }
                Collections.sort(produtos);
                adapterCompradorCarrinho.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
                recuperarPedido();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void Categoria(final String filtro){
        DatabaseReference produtoref = firebaseRef.child("produtos");
        produtoref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                produtos.clear();

                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    if (ds.getValue(Produto.class).getCategoria().equals(filtro) && ds.getValue(Produto.class).getDesabilitar()==0) {
                        produtos.add(ds.getValue(Produto.class));
                    }
                }
                Collections.sort(produtos);
                adapterCompradorCarrinho.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void recuperarPedido(){
        DatabaseReference pedidoRef = firebaseRef
                .child("pedidos_usuario")
                .child(idUsuarioLogado);
        pedidoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                qtdItensCarrinho = 0;
                totalCarrinho = 0.0;
                itensCarrinho = new ArrayList<>();

                if (dataSnapshot.getValue()!= null){
                    pedidoRecuperado = dataSnapshot.getValue(Pedido.class);
                    itensCarrinho = pedidoRecuperado.getItens();


                    for (ItemPedido itemPedido: itensCarrinho){
                        int qtde = itemPedido.getQuantidade();
                        Double preco = itemPedido.getPreco();

                        totalCarrinho += (qtde * preco);
                        qtdItensCarrinho += qtde;
                    }
                }
                DecimalFormat df = new DecimalFormat("0.00");
                textCompradorPrincipalQtd.setText("qtd: " + String.valueOf(qtdItensCarrinho));
                textCompradorPrincipalRS.setText("R$ " + df.format(totalCarrinho));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void confirmarpedido(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("ATENÇÃO!");
        dialog.setMessage("Confirmar pedido?");
        dialog.setCancelable(false);

        //opções
        dialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                idProdutoDesabilitar.clear();
                for (int i=0; i<itensCarrinho.size(); i++) {
                    for (int j = 0; j <produtos.size(); j++) {
                        if (!itensCarrinho.get(i).getIdProduto().equals(produtos.get(j).getIdProduto())) {
                            verificadorDesabilitar += 1;
                        }else {
                            verificadorDesabilitar = 0;
                        }
                    }
                    if (verificadorDesabilitar==produtos.size()){
                        veriId = 1;
                        idProdutoDesabilitar.add(itensCarrinho.get(i).getIdProduto());
                        verificadorDesabilitar=0;
                    }else{
                        verificadorDesabilitar=0;
                    }
                }
                if (qtdItensCarrinho == 0){
                    verificadorDesabilitar=0;
                    veriId=0;
                    Toast.makeText(CompradorPrincipal.this, "Carrinho vazio", Toast.LENGTH_SHORT).show();
                }else if (veriId == 1){
                    for (int i = 0; i<idProdutoDesabilitar.size(); i++) {
                        for (int j = 0; j<itensCarrinho.size(); j++) {
                            if (idProdutoDesabilitar.get(i).equals(itensCarrinho.get(j).getIdProduto())){
                                itensCarrinho.remove(j);
                            }
                        }
                    }
                    Toast.makeText(CompradorPrincipal.this, "Foram removidos itens que não estavam mais disponíveis", Toast.LENGTH_SHORT).show();
                    pedidoRecuperado.setItens(itensCarrinho);
                    pedidoRecuperado.salvar();
                    verificadorDesabilitar=0;
                    veriId=0;
                }
                else {
                    if (verificadorPagamento==1){
                        pedidoRecuperado.setMetodoPagamento(0);
                    }else if (verificadorPagamento==2){
                        pedidoRecuperado.setMetodoPagamento(1);
                    }
                    Date horaAtual = Calendar.getInstance().getTime();
                    String hora = String.valueOf(horaAtual);
                    String[] horaSeparada = hora.split(" ");
                    String data = horaSeparada[2]+ "/"+horaSeparada[1]+"/"+horaSeparada[5];
                    System.out.println(data);
                    pedidoRecuperado.setData(data);
                    Itens itens = new Itens();
                    pedidoRecuperado.setStatus("pendente");
                    pedidoRecuperado.confirmar();
                    //=================================================
                    Itens usuarioAvaliador = new Itens();
                    for (int i=0; i<itensCarrinho.size(); i++){
                        recuperarUsuarioAvaliador(itensCarrinho.get(i).getIdProduto());
                        if (usuariosAvaliacao==null){
                            usuarioAvaliador.setIdComprador(idUsuarioLogado);
                            usuarioAvaliador.SalvarUsuarioAvaliacao(itensCarrinho.get(i).getIdProduto(), idUsuarioLogado);
                        }else{
                            for (int j = 0; j<usuariosAvaliacao.size(); j++){
                                if (usuariosAvaliacao.get(j).getIdComprador().equals(idUsuarioLogado)){
                                    verificadorUsuarioAvaliador=1;
                                }

                            }
                            if (verificadorUsuarioAvaliador==1){
                                verificadorUsuarioAvaliador = 0;
                            }else if (verificadorUsuarioAvaliador==0){
                                usuarioAvaliador.setIdComprador(idUsuarioLogado);
                                usuarioAvaliador.SalvarUsuarioAvaliacao(itensCarrinho.get(i).getIdProduto(), idUsuarioLogado);
                                verificadorUsuarioAvaliador=0;
                            }
                        }
                    }
                    //=================================================
                    nomesVendedor.clear();
                    for (int i = 0; i<itensCarrinho.size(); i++){
                        //if (itensCarrinho.get(i).getIdVendedor() == )

                        /*itens.setIdItemProduto(itensCarrinho.get(i).getIdProduto());
                        itens.setIdComprador(pedidoRecuperado.getIdComprador());
                        itens.setIdVendedor(itensCarrinho.get(i).getIdVendedor());
                        itens.setIdItemPedido(pedidoRecuperado.getIdPedido());
                        itens.setNomeComprador(comprador.getNome());
                        itens.setCidadeComprador(comprador.getCidade());
                        itens.setTelefoneComprador(comprador.getTelefone());
                        itens.setNome(itensCarrinho.get(i).getNomeProduto());
                        itens.setPreco(itensCarrinho.get(i).getPreco());
                        itens.setQuantidade(itensCarrinho.get(i).getQuantidade());
                        itens.setTelefoneComprador(comprador.getTelefone());*/
                        if (nomesVendedor.isEmpty()){
                            nomesVendedor.add(itensCarrinho.get(i).getIdVendedor());
                        }else if(!nomesVendedor.isEmpty()){
                            for (int j=0; j<nomesVendedor.size();j++){
                                if (itensCarrinho.get(i).getIdVendedor().equals(nomesVendedor.get(j))){
                                    contadorNomesVendedor = 1;
                                }
                            }
                            if (contadorNomesVendedor==0){
                                nomesVendedor.add(itensCarrinho.get(i).getIdVendedor());
                            }
                            contadorNomesVendedor=0;
                        }

                        //itens.salvar(pedidoRecuperado.getIdPedido());
                        //pedidoRecuperado.itensConfirmar(itensCarrinho.get(i).getIdVendedor());
                        //itens.itensRemover(itensCarrinho.get(i).getIdVendedor(), pedidoRecuperado.getIdPedido(), itensCarrinho);

                    }

                    for (int i=0; i<nomesVendedor.size(); i++){
                        for (int j=0; j<itensCarrinho.size(); j++){
                            if (nomesVendedor.get(i).equals(itensCarrinho.get(j).getIdVendedor())){
                                itensCarrinho2.add(itensCarrinho.get(j));
                                enviarNotificacoes(itensCarrinho.get(j).getTokenVendedor());
                                System.out.println(itensCarrinho.get(j).getTokenVendedor());
                            }
                        }
                        pedidoRecuperado.setItens(itensCarrinho2);
                        pedidoRecuperado.itensConfirmar(nomesVendedor.get(i));
                        itensCarrinho2.clear();
                    }
                    //nomesVendedor.clear();
                    itensCarrinho2.clear();
                    veriId=0;
                    verificadorDesabilitar = 0;
                    verificadorPagamento=0;

                    Toast.makeText(CompradorPrincipal.this, "Pedido Realizado com Sucesso", Toast.LENGTH_SHORT).show();

                    pedidoRecuperado.remover();
                    pedidoRecuperado = null;
                    //itens.confirmar(pedidoRecuperado.getIdPedido());
                }

            }
        });

        dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    verificadorPagamento=0;
            }
        });

        dialog.create();
        dialog.show();
    }
    private void metodoPagamento(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("ATENÇÃO!");
        dialog.setMessage("Método de Pagamento:");
        dialog.setCancelable(false);

        //opções
        dialog.setPositiveButton("Dinheiro", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                verificadorPagamento = 1;
                LocaldeEntrega();
            }
        });

        dialog.setNegativeButton("Cartão de Crédito", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                verificadorPagamento=2;
                LocaldeEntrega();
            }
        });

        dialog.create();
        dialog.show();
    }
    public void abrirPedidosComprador(View view){

        Intent intent = new Intent(CompradorPrincipal.this, CompradorPedidos.class);
        startActivity(intent);
    }
    public void abrirAvaliacoesComprador(View view){

        Intent intent = new Intent(CompradorPrincipal.this, CompradorAvaliacoes.class);
        startActivity(intent);
    }
    public void recuperarUsuario2(){
        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        DatabaseReference usuarioRef = referencia.child("usuarios").child(idUsuario);

        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    private void metodoAvaliacoes(Produto produtoSelecionado, View v,final int position){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Avaliações!");
        dialog.setMessage(produtoSelecionado.getNome());
        dialog.setCancelable(false);
        //opções
        dialog.setPositiveButton("Ver Avaliações", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Produto produtolegal = produtos.get(position);
                Intent intent = new Intent(CompradorPrincipal.this, CompradorAvaliacoes.class);
                intent.putExtra("produtoSelecionado", produtolegal);
                startActivity(intent);

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
    private void LocaldeEntrega(){
        recuperarPedido();
        if (pedidoRecuperado==null || qtdItensCarrinho==0){
            Toast.makeText(CompradorPrincipal.this, "Adicione um pedido", Toast.LENGTH_SHORT).show();
        }else if (pedidoRecuperado.getLocalEntrega()!=null){
            System.out.println(pedidoRecuperado.getLocalEntrega());
            confirmarpedido();
        }
        else{
            Toast.makeText(CompradorPrincipal.this, "Adicione um local para entrega", Toast.LENGTH_SHORT).show();
        }
        /*
        AlertDialog.Builder dialogEntrega = new AlertDialog.Builder(this);
        dialogEntrega.setTitle("Local de entrega");
        dialogEntrega.setMessage("Insira o local e a hora: ");
        dialogEntrega.setCancelable(false);

        EditText editTextLocal = new EditText(this);
        LinearLayout.LayoutParams linearLayoutLocal = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        );
        editTextLocal.setLayoutParams(linearLayoutLocal);

        dialogEntrega.setView(editTextLocal);

        dialogEntrega.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                localEntrega = String.valueOf(editTextLocal.getText());
                System.out.println(localEntrega);
                confirmarpedido();
            }
        });
        dialogEntrega.create().show();

         */
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
                adapterCompradorCarrinho.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
