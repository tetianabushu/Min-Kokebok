package com.example.bushu.mineoppskrifter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

/**
 * Created by bushu on 14.11.2017.
 */

public class KalenderAktivitet extends AppCompatActivity {
    private Date selectedDate;
    CompactCalendarView compactCalendar;

    private SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMMM- yyyy", Locale.getDefault());
    private DBHandler dbhandler;
    private List<OppskriftMedDato>  oppskrifterMedDato;
    private boolean fromHeleOppskrift = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kalender);
        Toolbar t = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(t);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        /*************************************************/

        dbhandler = new DBHandler(this);

        final Context _this = this;
        final Toolbar monthtoolbar = (Toolbar) findViewById(R.id.moned_dato_toolbar);
        monthtoolbar.setTitle(dateFormatMonth.format(new Date()));
        compactCalendar = (CompactCalendarView) findViewById(R.id.kalender_view);
        compactCalendar.setUseThreeLetterAbbreviation(true);

        setDatoerFraDb();

        compactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                selectedDate = dateClicked;
                updateOppskrifterOnSelectedDate(_this);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                monthtoolbar.setTitle(dateFormatMonth.format(firstDayOfNewMonth));
            }
        });

        if(getIntent().hasExtra(getResources().getString(R.string.hele_oppskrift_id)))
            fromHeleOppskrift = true;
    }

    @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            super.onCreateOptionsMenu(menu);
            getMenuInflater().inflate(R.menu.menu_kalender, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    if(getIntent().hasExtra(getResources().getString(R.string.hele_oppskrift_id))) {
                        finish();
                    } else {
                        NavUtils.navigateUpFromSameTask(this);
                    }
                    return true;
                case R.id.save_i_kalender:
                    try {
                        leggOppskriftIKalender(item.getActionView());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;

                default:
                    return super.onOptionsItemSelected(item);
            }
            return true;
        }

    private List<OppskriftMedDato> finnOppskrifterForDato(String dateString) {
        List<OppskriftMedDato> listetoReturn = new ArrayList<>();
        for (OppskriftMedDato opd:this.oppskrifterMedDato) {
            if(opd.getOppskriftDato().equals(dateString)){
                listetoReturn.add(opd);
            }
        }
        return listetoReturn;
    }

    public void leggOppskriftIKalender(View view) throws ParseException {
        Intent i = getIntent();
        if(i.hasExtra(getResources().getString(R.string.hele_oppskrift_id))){

            String helrOppskriftString = getResources().getString(R.string.hele_oppskrift_id);
            long oppskriftId = i.getLongExtra(helrOppskriftString, 0);

            if(oppskriftId != 0) {
                DateFormat df = new SimpleDateFormat(getResources().getString(R.string.simple_date_format_kal));
                String dateString = df.format(selectedDate);

                OppskriftIKalender kalenderOppskrift = new OppskriftIKalender(dateString, oppskriftId);
                dbhandler.leggTilOppskriftIKalender(kalenderOppskrift);

                Event ev = new Event(Color.RED, selectedDate.getTime(), "");
                compactCalendar.addEvent(ev, true);
                String oppskrifterLagtString = getResources().getString(R.string.toast_opprsk_lagt_til);
                Toast.makeText(getApplicationContext(),oppskrifterLagtString + dateString, Toast.LENGTH_SHORT).show();
                this.oppskrifterMedDato = dbhandler.finnOppskrifterIKalender();
                updateOppskrifterOnSelectedDate(this);
            }
        }
    }

    public void sletteFraKalender(View v){
        long oppskriftIKalenderId = (long)v.getTag();
        dbhandler.slettOppskriftFraKalender(oppskriftIKalenderId);
        setDatoerFraDb();
        updateOppskrifterOnSelectedDate(this);
    }

    public void openOppskrift(View v){
        String oppskriftId = v.getTag().toString();
        Intent intent = new Intent(this, HeleOppskrift.class);// New activity
        intent.putExtra(getResources().getString(R.string.valgt_id), oppskriftId);
        if(fromHeleOppskrift)
            setResult(RESULT_OK, intent);
        else startActivity(intent);
        finish();
    }
    private void setDatoerFraDb(){
        compactCalendar.removeAllEvents();
        this.oppskrifterMedDato= dbhandler.finnOppskrifterIKalender();
        for (OppskriftMedDato op:oppskrifterMedDato) {
            DateFormat df = new SimpleDateFormat(getResources().getString(R.string.simple_date_format_kal));
            try {
                Date dato = df.parse(op.getOppskriftDato());
                Event ev = new Event(Color.RED, dato.getTime(), "");
                compactCalendar.addEvent(ev, true);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateOppskrifterOnSelectedDate(Context _this) {
        DateFormat df = new SimpleDateFormat(getResources().getString(R.string.simple_date_format_kal));
        String dateString = df.format(selectedDate);
        List<OppskriftMedDato> oppskrifterForCurrentDato = finnOppskrifterForDato(dateString); // returnerer datoer liste
        if (oppskrifterForCurrentDato.size() != 0) {
            ArrayAdapter<OppskriftMedDato> adapter = new KalenderItemAdapter(_this, oppskrifterForCurrentDato);
            ListView kalenderList = (ListView) findViewById(R.id.vise_oppskrift_tittel);
            kalenderList.setAdapter(adapter);
        } else {
            ArrayAdapter<OppskriftMedDato> adapter = new KalenderItemAdapter(_this, new ArrayList<OppskriftMedDato>()); // oppretter ny liste, for å tomme den forrige
            ListView kalenderList = (ListView) findViewById(R.id.vise_oppskrift_tittel);
            kalenderList.setAdapter(adapter);
        }
    }
}

