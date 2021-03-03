package com.example.tent1.Model;

import com.example.tent1.config.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class Itens {

    private String idItemProduto;
    private String idVendedor;
    private String idComprador;
    private String idItemPedido;
    private String nomeComprador;
    private String cidadeComprador;
    private String telefoneComprador;
    private String nome;
    private Double preco;
    private int quantidade;

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public Itens() {
    }
    public void SalvarUsuarioAvaliacao(String idProduto, String idUsuarioLogado){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference pedidoRef = firebaseRef
                .child("usuarios_avaliacao")
                .child(idProduto)
                .child(idUsuarioLogado);
        pedidoRef.setValue(this);
    }
    public void salvar(String idpedido){
        DatabaseReference firebase = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference itensRef = firebase.child("itens")
                .child(getIdVendedor())
                .child(getIdComprador())
                .child(getIdItemPedido());
        itensRef.setValue(this);
    }
    public void itensRemover(String idVendedor, String idPedido, List<ItemPedido> itens){
        DatabaseReference firebase = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference itensRef = firebase.child("itensVendedor")
                .child(idVendedor)
                .child(idComprador)
                .child(idPedido)
                .child("itens");
        for (int i = 0; i< itens.size(); i++){
            if (itens.get(i).getIdVendedor()!= idVendedor){
                itensRef.removeValue();
            }
        }
    }

    public String getIdItemPedido() {
        return idItemPedido;
    }

    public void setIdItemPedido(String idItemPedido) {
        this.idItemPedido = idItemPedido;
    }

    public String getIdComprador() {
        return idComprador;
    }

    public void setIdComprador(String idComprador) {
        this.idComprador = idComprador;
    }

    public String getNomeComprador() {
        return nomeComprador;
    }

    public void setNomeComprador(String nomeComprador) {
        this.nomeComprador = nomeComprador;
    }

    public String getCidadeComprador() {
        return cidadeComprador;
    }

    public void setCidadeComprador(String cidadeComprador) {
        this.cidadeComprador = cidadeComprador;
    }

    public String getTelefoneComprador() {
        return telefoneComprador;
    }

    public void setTelefoneComprador(String telefoneComprador) {
        this.telefoneComprador = telefoneComprador;
    }

    public String getIdItemProduto() {
        return idItemProduto;
    }

    public void setIdItemProduto(String idItem) {
        this.idItemProduto = idItem;
    }

    public String getIdVendedor() {
        return idVendedor;
    }

    public void setIdVendedor(String idVendedor) {
        this.idVendedor = idVendedor;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }
}
