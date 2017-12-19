package com.example.bushu.mineoppskrifter;



public class OppskriftIKalender {
    private long Id;
    private String Dato_i_kalender;
    private long OppskriftId;

    public OppskriftIKalender(String dato_i_kalender, long oppskriftId){
        this.Dato_i_kalender = dato_i_kalender;
        this.OppskriftId = oppskriftId;
    }

    public long getId() {
        return Id;
    }

    public String getDato_i_kalender() {
        return Dato_i_kalender;
    }

    public long getOppskriftId() {
        return OppskriftId;
    }

    public void setId(long id) {
        Id = id;
    }

    public void setDato_i_kalender(String dato_i_kalender) {
        Dato_i_kalender = dato_i_kalender;
    }

    public void setOppskriftId(long oppskriftId) {
        OppskriftId = oppskriftId;
    }
}
