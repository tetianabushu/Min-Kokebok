package com.example.bushu.mineoppskrifter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.ArrayList;



public class HeleOppskrift extends AppCompatActivity{
    private long id;
    private DBHandler db;
    private ViewFlipper flipper;
    private int bilderCount = 0;
    private int currentBildet = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hele_oppskrift);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Intent i = getIntent();
        String resultId = i.getExtras().getString(getResources().getString(R.string.valgt_id));
        id = Long.parseLong(resultId);

        setBildetFlipper();

        db = new DBHandler(this);
        hentOppskriftFraDb(id);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_hele_oppskr, menu);

        return true;
    }
    @Override


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.kalender_item:
                leggeTilKalendar(item.getActionView());
                break;
            case R.id.rediger_item:
                oppdaterOppskrift(item.getActionView());
                break;
            case R.id.delete_item:
                slettOpskrift(item.getActionView());
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void hentOppskriftFraDb(long oppskriftId) {
        Oppskrift oppskrift = db.finnOppskriftIdb(oppskriftId);
        TextView tittel = (TextView)findViewById(R.id.oppskrift_title);
        tittel.setText(oppskrift.getOppskriftTittel());
        TextView tekst = (TextView)findViewById(R.id.hele_oppskrift_text);
        tekst.setText(oppskrift.getOppskriftText());
        tekst.setMovementMethod(new ScrollingMovementMethod());

        flipper.removeAllViews();
        ArrayList<Bildet> bilder = db.finnBilderForEnOppskrift(oppskriftId);
        for (Bildet b : bilder) {
            byte[] bildetArr = b.getBildet();
            Bitmap bmp = BitmapFactory.decodeByteArray(bildetArr, 0, bildetArr.length);
            ImageView v = new ImageView(this);
            v.setImageBitmap(bmp);
            flipper.addView(v);
        }

        bilderCount = bilder.size();
        if(bilderCount > 0) currentBildet = 1;
        updateBilderCounter();
    }

    private void updateBilderCounter() {
        TextView bilderCountView = (TextView) findViewById(R.id.bildet_counter);
        bilderCountView.setText(currentBildet + "/" + bilderCount);
    }

    public void slettOpskrift(View v){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        Intent intent = new Intent(HeleOppskrift.this, OppskriftListe.class);
                        db.slettOppskrift(id);
                        intent.putExtra(getResources().getString(R.string.oppskrift_slettet), true);
                        setResult(RESULT_OK, intent);
                        Toast.makeText(getApplicationContext(), R.string.oppskrift_slettet_warning, Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.alert_slette_oppskrift)).setPositiveButton("Ja", dialogClickListener)
                .setNegativeButton("Nei", dialogClickListener).show();

    }

    public void oppdaterOppskrift(View v){
       Intent intent = new Intent(this, AddOppskrift.class);
        intent.putExtra(getResources().getString(R.string.hele_oppsk_id), id);
        startActivityForResult(intent,23);
    }

    public void leggeTilKalendar(View v){
        Intent intent = new Intent(this, KalenderAktivitet.class);
        intent.putExtra(getResources().getString(R.string.hele_oppskrift_id) , id);
        startActivityForResult(intent, 24);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode== RESULT_OK) {
            if(data.hasExtra(getResources().getString(R.string.oppskrift_slettet)) &&
                    data.getExtras().getBoolean(getResources().getString(R.string.oppskrift_slettet)) == true) {
                setResult(RESULT_OK, data);
                finish();
            }
            else if (data.hasExtra(getResources().getString(R.string.valgt_id))) {
                Intent intent = new Intent(this, HeleOppskrift.class);// New activity
                String valgtId = data.getExtras().getString(getResources().getString(R.string.valgt_id));
                intent.putExtra(getResources().getString(R.string.valgt_id), valgtId);
                startActivity(intent);
                finish();
            }
            else {
                hentOppskriftFraDb(id);
            }
        }
    }

    private void setBildetFlipper() {
        flipper = (ViewFlipper)findViewById(R.id.heleoppskrift_flipper);
        flipper.setOnTouchListener(new SwipeListener() {
            @Override
            public void onRightToLeftSwipe(View v) {
                flipper.setInAnimation(v.getContext(), R.anim.slide_in_right);
                flipper.setOutAnimation(v.getContext(), R.anim.slide_out_left);
                flipper.showNext();
                if(bilderCount > 0) {
                    currentBildet++;
                    if (currentBildet > bilderCount)
                        currentBildet = 1;
                    updateBilderCounter();
                }
            }

            @Override
            public void onLeftToRightSwipe(View v) {
                flipper.setInAnimation(v.getContext(), android.R.anim.slide_in_left);
                flipper.setOutAnimation(v.getContext(), android.R.anim.slide_out_right);
                flipper.showPrevious();
                if(bilderCount > 0) {
                    currentBildet--;
                    if (currentBildet == 0)
                        currentBildet = bilderCount;
                    updateBilderCounter();
                }
            }
        });
    }
}
