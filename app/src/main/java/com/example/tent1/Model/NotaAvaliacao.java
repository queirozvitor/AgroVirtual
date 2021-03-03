package com.example.tent1.Model;

import com.example.tent1.config.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

public class NotaAvaliacao {

    private String comentario;
    private double nota;
    private String idUsuario;
    private String idProduto;
    private String nomeUsuario;

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(String idProduto) {
        this.idProduto = idProduto;
    }

    public NotaAvaliacao(){

    }
    public void salvar(String idProduto, String idUsuario){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference pedidoRef = firebaseRef
                .child("avaliacoes")
                .child(idProduto)
                .child(idUsuario);
        pedidoRef.setValue( this );
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public double getNota() {
        return nota;
    }

    public void setNota(double nota) {
        this.nota = nota;
    }
}
