package entidades.evento;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import entidades.ingresso.TipoIngresso;

public abstract class Evento {
    private String nome;
    private LocalDate data;
    private String local;
    private int ingressosMeia;
    private int ingressosInteira;
    private double precoCheio;

    public Evento(String nome, LocalDate data, String local, int ingressosMeia, int ingressosInteira, double precoCheio) {
        this.nome = nome;
        this.data = data;
        this.local = local;
        this.ingressosMeia = ingressosMeia;
        this.ingressosInteira = ingressosInteira;
        this.precoCheio = precoCheio;
    }

    public double getPrecoCheio() {
        return this.precoCheio;
    }

    public String getNome() {
        return this.nome;
    }

    public void SetLocal(String local){
        this.local = local;
    }

    public void SetData(LocalDate data){
        this.data = data;
    }

    public int getIngressosMeia() {
        return this.ingressosMeia;
    }

    public int getIngressosInteira() {
        return this.ingressosInteira;
    }

    public boolean isIngressoDisponivel(TipoIngresso tipo, int quantidade) {
        if (tipo.equals(TipoIngresso.MEIA)) {
            return quantidade <= this.ingressosMeia;
        }

        return quantidade <= this.ingressosInteira;
    }

    public void venderIngresso(TipoIngresso tipo, int quantidade) {
        if (this.isIngressoDisponivel(tipo, quantidade)) {
            if (tipo.equals(TipoIngresso.MEIA)) {
                this.ingressosMeia -= quantidade;
            } else {
                this.ingressosInteira -= quantidade;
            }
        }
    }

    @Override
public String toString() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    String dataFormatada = this.data.format(formatter);
    return this.nome + " - " + dataFormatada + " - " + this.local;
}
}
