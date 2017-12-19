package com.example.bushu.mineoppskrifter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by bushu on 09.11.2017.
 */

public class MinListAdapter extends ArrayAdapter<OppskriftListItem> {
    public MinListAdapter(@NonNull Context context, @NonNull List<OppskriftListItem> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        OppskriftListItem oppskrift = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.oppskrift_list_item, parent, false);
        }
        TextView tittel = (TextView) convertView.findViewById(R.id.oppskrift_tittel_list);
        tittel.setText(oppskrift.getOppskriftTittel());
        convertView.setTag(oppskrift.getId());

        ImageView im = (ImageView)convertView.findViewById(R.id.oppskrift_bildet_list);
        byte[] bildetArr = oppskrift.getBildet();
        if(bildetArr != null) {
            Bitmap bmp = BitmapFactory.decodeByteArray(bildetArr, 0, bildetArr.length);
            im.setImageBitmap(bmp);
        }



        return convertView;
    }

}
