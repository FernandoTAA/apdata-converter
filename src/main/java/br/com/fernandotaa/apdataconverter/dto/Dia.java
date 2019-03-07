package br.com.fernandotaa.apdataconverter.dto;

import java.time.LocalTime;
import java.util.List;

public class Dia {

    private List<LocalTime> pontos;
    private boolean feriado;
    private boolean atestado;
    private boolean abonado;


    public List<LocalTime> getPontos() {
        return pontos;
    }

    public void setPontos(List<LocalTime> pontos) {
        this.pontos = pontos;
    }

    public boolean isFeriado() {
        return feriado;
    }

    public void setFeriado(boolean feriado) {
        this.feriado = feriado;
    }

    public boolean isAtestado() {
        return atestado;
    }

    public void setAtestado(boolean atestado) {
        this.atestado = atestado;
    }

    public boolean isAbonado() {
        return abonado;
    }

    public void setAbonado(boolean abonado) {
        this.abonado = abonado;
    }
}
