package com.example.tent1.Model;

import com.example.tent1.config.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class Pedido{

    private String idComprador;
    private String idPedido;
    private String nome;
    private String telefoneComprador;
    private String cidade;
    private List<ItemPedido> itens;
    private String localEntrega;
    private Double total;
    private String key;
    private String data;
    private String status = "pendente";
    private int metodoPagamento;
    private String observacao;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTelefoneComprador() {
        return telefoneComprador;
    }

    public void setTelefoneComprador(String telefoneComprador) {
        this.telefoneComprador = telefoneComprador;
    }

    public Pedido() {
    }
    public Pedido(String idUsu){
        setIdComprador(idUsu);

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference pedidoRef = firebaseRef
                .child("pedidos_usuario")
                .child(idComprador);
        setIdPedido(UUID.randomUUID().toString());
        //setIdPedido(pedidoRef.push().getKey());
    }
    public void salvar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference pedidoRef = firebaseRef
                .child("pedidos_usuario")
                .child(getIdComprador());
        pedidoRef.setValue( this );
    }
    public void remover(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference pedidoRef = firebaseRef
                .child("pedidos_usuario")
                .child(getIdComprador());
        pedidoRef.removeValue();
    }
    public void confirmar(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference pedidoRef = firebaseRef
                .child("pedidos")
                .child(getIdComprador())
                .push();
        pedidoRef.setValue(this);

    }
    public void itensConfirmar(String idVendedor){
        DatabaseReference firebase = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference itensRef = firebase.child("itensVendedor")
                .child(idVendedor)
                //.child(getIdComprador())
                //Geralmente utilizo esse aqui debaixo:
                //.child(getIdPedido());
                .push();
        itensRef.setValue(this);
    }
    public void itensRemover(String idVendedor, String push){
        DatabaseReference firebase = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference itensRef = firebase.child("itensVendedor")
                .child(idVendedor)
                //.child(getIdComprador())
                //.child(getIdPedido());
                .child(push);
        itensRef.removeValue();
    }

    public String getLocalEntrega() {
        return localEntrega;
    }

    public void setLocalEntrega(String localEntrega) {
        this.localEntrega = localEntrega;
    }

    public String getIdComprador() {
        return idComprador;
    }

    public void setIdComprador(String idComprador) {
        this.idComprador = idComprador;
    }


    public String getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(String idPedido) {
        this.idPedido = idPedido;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public List<ItemPedido> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedido> itens) {
        this.itens = itens;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(int metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
