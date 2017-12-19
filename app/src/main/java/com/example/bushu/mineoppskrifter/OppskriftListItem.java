package com.example.bushu.mineoppskrifter;

/**
 * Created by bushu on 21.11.2017.
 */

public class OppskriftListItem {
    private long Id;
    private String OppskriftTittel;
    private byte[] Bildet;

    public OppskriftListItem(long id, String oppskriftTittel, byte[] bildet) {
        Id = id;
        OppskriftTittel = oppskriftTittel;
        Bildet = bildet;
    }

    public OppskriftListItem() {
    }

    public long getId() {
        return Id;
    }

    public String getOppskriftTittel() {
        return OppskriftTittel;
    }

    public byte[] getBildet() {
        return Bildet;
    }

    public void setId(long id) {
        Id = id;
    }

    public void setOppskriftTittel(String oppskriftTittel) {
        OppskriftTittel = oppskriftTittel;
    }

    public void setBildet(byte[] bildet) {
        Bildet = bildet;
    }
}
