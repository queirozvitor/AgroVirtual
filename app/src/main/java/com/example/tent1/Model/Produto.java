package com.example.tent1.Model;

import com.example.tent1.config.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

public class Produto implements Serializable, Comparable<Produto> {

    private String idUsuario;
    private String idProduto;
    private String tokenVendedor;
    private String urlImagem;
    private String nomefiltro;
    private String categoria;
    private String nome;
    private String descricao;
    private int qtdcarrinho;

    public String getTokenVendedor() {
        return tokenVendedor;
    }

    public void setTokenVendedor(String tokenVendedor) {
        this.tokenVendedor = tokenVendedor;
    }

    private int desabilitar;

    public int getQtdcarrinho() {
        return qtdcarrinho;
    }

    public int getDesabilitar() {
        return desabilitar;
    }

    public void setDesabilitar(int desabilitar) {
        this.desabilitar = desabilitar;
    }

    public void setQtdcarrinho(int qtdcarrinho) {
        this.qtdcarrinho = qtdcarrinho;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getNomefiltro() {
        return nomefiltro;
    }

    public void setNomefiltro(String nomefiltro) {
        this.nomefiltro = nomefiltro;
    }

    private Double preco;

    public String getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(String idProduto) {
        this.idProduto = idProduto;
    }



    public Produto(){
    }


    public void Salvar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference produtoRef = firebaseRef.child("produtos")
                //.child(getIdUsuario())
                .child(getIdProduto());

        produtoRef.setValue(this);
    }
    public void remover(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference produtoRef = firebaseRef.child("produtos")
                //.child(getIdUsuario())
                .child(getIdProduto());
        produtoRef.removeValue();
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }


    public String getUrlImagem() {
        return urlImagem;
    }

    public void setUrlImagem(String urlImagem) {
        this.urlImagem = urlImagem;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    @Override
    public int compareTo(Produto o) {
        return getNome().compareToIgnoreCase(o.getNome());
    }
}
