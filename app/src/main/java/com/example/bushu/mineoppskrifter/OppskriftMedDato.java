package com.example.bushu.mineoppskrifter;



public class OppskriftMedDato {
    private long Id;
    private String OppskriftMedDatoTittel;
    private long OppskriftIKalenderId;
    private String OppskriftDato;

    public OppskriftMedDato(long id, long oppskriftIKalenderId, String oppskriftMedDatoTittel, String oppskriftDato) {
        Id = id;
        OppskriftIKalenderId = oppskriftIKalenderId;
        OppskriftMedDatoTittel = oppskriftMedDatoTittel;
        OppskriftDato = oppskriftDato;
    }

    public long getId() {
        return Id;
    }

    public long getOppskriftIKalenderId() {
        return OppskriftIKalenderId;
    }

    public String getOppskriftMedDatoTittel() {
        return OppskriftMedDatoTittel;
    }

    public String getOppskriftDato() {
        return OppskriftDato;
    }


}
