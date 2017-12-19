package com.example.bushu.mineoppskrifter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;



public class KalenderItemAdapter extends ArrayAdapter<OppskriftMedDato> {

    public KalenderItemAdapter(@NonNull Context context, @NonNull List<OppskriftMedDato> objects) {
        super(context, 0, objects);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final OppskriftMedDato oppskrift = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.kalender_item, parent, false);
        }

        final TextView tittel = (TextView) convertView.findViewById(R.id.kalender_list_item_tittel);
        ImageView deleteImage = (ImageView) convertView.findViewById(R.id.slett_oppskrift_fra_kalender);
        tittel.setText(oppskrift.getOppskriftMedDatoTittel());
        tittel.setTag(oppskrift.getId());
        deleteImage.setTag(oppskrift.getOppskriftIKalenderId());
        return convertView;
    }

}
