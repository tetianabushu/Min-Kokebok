package com.example.bushu.mineoppskrifter;

public class Bildet {
    private long Id;
    private byte[] Bildet;
    private long oppskrift_id;

    public Bildet(long id, byte[] bildet, long oppskrift_id) {
        Id = id;
        Bildet = bildet;
        this.oppskrift_id = oppskrift_id;
    }

    public Bildet() {

    }

    public long getId() {
        return Id;
    }

    public byte[] getBildet() {
        return Bildet;
    }

    public long getOppskrift_id() {
        return oppskrift_id;
    }

    public void setId(long id) {
        Id = id;
    }

    public void setBildet(byte[] bildet) {
        Bildet = bildet;
    }

    public void setOppskrift_id(long oppskrift_id) {
        this.oppskrift_id = oppskrift_id;
    }
}
