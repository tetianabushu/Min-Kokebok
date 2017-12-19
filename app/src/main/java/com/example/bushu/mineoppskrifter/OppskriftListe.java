package com.example.bushu.mineoppskrifter;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.List;


public class OppskriftListe extends AppCompatActivity {
    DBHandler db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oppskrift_liste);
        db = new DBHandler(this);
        hentOppskriftListeDb();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton add_oppskriftbtn = (FloatingActionButton) findViewById(R.id.add_oppskriftbtn);
        add_oppskriftbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i  = new Intent(OppskriftListe.this, AddOppskrift.class);
                startActivityForResult(i, 10);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        this.getMenuInflater().inflate(R.menu.menu_hoved_list, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.kalender_item:
                Intent im = new Intent(this,KalenderAktivitet.class);
                startActivity(im);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
    public void seHeleOppskrift(View v){
        Intent intent = new Intent(this, HeleOppskrift.class);// New activity
        Object itemId = v.getTag();
        intent.putExtra(getResources().getString(R.string.valgt_id),itemId.toString());
        startActivityForResult(intent, 22);
    }
    private void hentOppskriftListeDb(){
        List<OppskriftListItem> oppskrifter = db.finnOppskrifterIDb();
        ArrayAdapter<OppskriftListItem> adapter = new MinListAdapter(this, oppskrifter);
        ListView oppskriftListView = (ListView) findViewById(R.id.list_av_oppskrifter);
        oppskriftListView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            hentOppskriftListeDb();
        }
    }
}
