package com.example.tent1.Model;

import com.example.tent1.config.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class ItemPedido {

    private String idProduto;
    private String nomeProduto;
    private String URLImage;
    private String idVendedor;
    private int quantidade;
    private String tokenVendedor;
    private String descricao;

    public String getTokenVendedor() {
        return tokenVendedor;
    }

    public void setTokenVendedor(String tokenVendedor) {
        this.tokenVendedor = tokenVendedor;
    }

    private Double preco;

    public ItemPedido() {
    }

    public String getIdProduto() {
        return idProduto;
    }

    public String getIdVendedor() {
        return idVendedor;
    }

    public String getURLImage() {
        return URLImage;
    }

    public void setURLImage(String URLImage) {
        this.URLImage = URLImage;
    }

    public void setIdVendedor(String idVendedor) {
        this.idVendedor = idVendedor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setIdProduto(String idProduto) {
        this.idProduto = idProduto;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }
}
