package com.example.bushu.mineoppskrifter;

/**
 * Created by bushu on 09.11.2017.
 */

public class Oppskrift {
    private long Id;
    private String OppskriftTittel;

    private String OppskriftText;


    public Oppskrift(long id, String oppskriftTittel, String oppskriftText) {
        this.Id = id;
        this.OppskriftTittel = oppskriftTittel;

        this.OppskriftText = oppskriftText;
    }

    public Oppskrift(){

    }

    public long getId() {
        return Id;
    }
    public String getOppskriftTittel() {
        return OppskriftTittel;
    }



    public String getOppskriftText() {
        return OppskriftText;
    }

    public void setId(long id) {
        Id = id;
    }

    public void setOppskriftTittel(String oppskriftTittel) {
        OppskriftTittel = oppskriftTittel;
    }



    public void setOppskriftText(String oppskriftText) {
        OppskriftText = oppskriftText;
    }
}
