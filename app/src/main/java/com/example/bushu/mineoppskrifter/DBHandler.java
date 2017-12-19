package com.example.bushu.mineoppskrifter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;


public class DBHandler extends SQLiteOpenHelper {
    static int DATABASE_VERSION = 1;
    static String DATABASE_NAME = "MineOppskrifter";
    static String TABLE_OPPSKRIFTER = "Oppskrifter";
    static String KEY_ID = "_ID";
    static String KEY_OPPSKRIFT_TITTEL = "oppskriftTittel";
    static String KEY_OPPSKR_TEKST = "OppskriftText";

    static String TABLE_OPPSKRIFTER_I_KALENDER = "OppskrifterIKalender";
    static String EVENT_ID = "ID";
    static String DATO_I_KALENDER = "DatoIKalender";
    static String OPPSKR_ID = "OppskriftID";

    static String TABLE_BILDER_I_OPPSKRIFT = "BilderIOppskrift";
    static String BILDER_ID = "Id";
    static String KEY_IMG = "Bildet";

    Context context;

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            String createImgTable = "CREATE TABLE " + TABLE_OPPSKRIFTER + "(" + KEY_ID +
                    " INTEGER PRIMARY KEY," + KEY_OPPSKRIFT_TITTEL + " TEXT," + KEY_OPPSKR_TEKST + " TEXT" + ")";
            Log.d("SQL", createImgTable);
            db.execSQL(createImgTable);

            String createKalenderEventsTable = "CREATE TABLE " + TABLE_OPPSKRIFTER_I_KALENDER + "("
                    + EVENT_ID +" INTEGER PRIMARY KEY,"
                    + DATO_I_KALENDER + " TEXT,"
                    + OPPSKR_ID + " INTEGER,"
                    + " FOREIGN KEY ("+OPPSKR_ID+") REFERENCES "+TABLE_OPPSKRIFTER +"("+KEY_ID+")" + ")";
            Log.d("SQL", createKalenderEventsTable);
            db.execSQL(createKalenderEventsTable);

            String createbilderTable= "CREATE TABLE " + TABLE_BILDER_I_OPPSKRIFT
                    + "(" + BILDER_ID + " INTEGER PRIMARY KEY,"
                    + KEY_IMG + " BLOB, "
                    + OPPSKR_ID + " INTEGER, "
                    + " FOREIGN KEY("+OPPSKR_ID+") REFERENCES "+TABLE_OPPSKRIFTER +"("+KEY_ID+")" + ")";
            Log.d("SQL", createbilderTable);
            db.execSQL(createbilderTable);
        }catch(SQLiteException ex){
            Log.e("create db",ex.getMessage());
        }

        addOppskrifter(db);
    }

    /***************************** Legger til default oppskrifter når app installeres ******************************/

    private void addOppskrifter(SQLiteDatabase db) {
        leggTillDefaultOppskrift(db, R.string.linsesuppe, R.string.linsesuppe_oppskrift, new int[]{R.drawable.linsesuppe} );
        leggTillDefaultOppskrift(db, R.string.ribbe, R.string.ribbe_oppskrift, new int[]{R.drawable.ribbe1, R.drawable.ribbe2, R.drawable.ribbe3 } );
    }

    private void leggTillDefaultOppskrift(SQLiteDatabase db, int oppskrifttittel, int oppskrifttekst, int [] oppskriftbilder) {
        Oppskrift oppskrift = new Oppskrift(0,
                this.context.getResources().getString(oppskrifttittel),
                this.context.getResources().getString(oppskrifttekst));
        long id = leggTilOppskrift(oppskrift, db);

        for (int bildet:oppskriftbilder) {
            Bitmap bitmap = BitmapFactory.decodeResource(this.context.getResources(), bildet);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
            byte[] bitmapdata = stream.toByteArray();

            Bildet b = new Bildet();
            b.setBildet(bitmapdata);
            b.setOppskrift_id(id); //kobler bildet med oppskrift_tabell, FK er oppskriftId
            leggTilBildet(b, db);
        }
    }

    /******************************************************************************/

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BILDER_I_OPPSKRIFT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OPPSKRIFTER_I_KALENDER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OPPSKRIFTER);
        onCreate(db);
    }

    public long leggTilOppskrift(Oppskrift oppskrift) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            long id = leggTilOppskrift(oppskrift, db);
            db.close();
            return id;
        } catch (Exception e) {
            Log.d((this.context.getResources().getString(R.string.legg_til_oppskr_db_ex)), e.getMessage());
            return -1;
        }
    }

    private long leggTilOppskrift(Oppskrift oppskrift, SQLiteDatabase database) {
            ContentValues values = new ContentValues();
            values.put(KEY_OPPSKRIFT_TITTEL, oppskrift.getOppskriftTittel());
            values.put(KEY_OPPSKR_TEKST, oppskrift.getOppskriftText());
            long newRowId = database.insertOrThrow(TABLE_OPPSKRIFTER, null, values);
            return newRowId;
    }

    public void leggTilOppskriftIKalender(OppskriftIKalender oppskriftIKalender) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(DATO_I_KALENDER, oppskriftIKalender.getDato_i_kalender());
            values.put(OPPSKR_ID, oppskriftIKalender.getOppskriftId());

            db.insertOrThrow(TABLE_OPPSKRIFTER_I_KALENDER, null, values);
            db.close();
        }catch(Exception e){
            Log.d(this.context.getResources().getString(R.string.legginn_oppskrift_i_kal_db),e.getMessage());
        }
    }

    public void leggTilBildet(Bildet bildet) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            leggTilBildet(bildet, db);
            db.close();
        }catch(Exception e){
            Log.d(this.context.getResources().getString(R.string.legginn_bildet_i_kal_db),e.getMessage());
        }
    }

    private void leggTilBildet(Bildet bildet, SQLiteDatabase db) {
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_IMG, bildet.getBildet());
            values.put(OPPSKR_ID, bildet.getOppskrift_id());
            db.insertOrThrow(TABLE_BILDER_I_OPPSKRIFT, null, values);
        } catch (Exception e) {
            Log.d(this.context.getResources().getString(R.string.legginn_bildet_i_kal_db), e.getMessage());
        }

    }

    public List<OppskriftListItem> finnOppskrifterIDb() {
        List<OppskriftListItem> oppskriftListe = new ArrayList();
        try {
            // Henter alle oppskrifter, men kun første bildet fra bildet tabell, for å vise i hovedliste
            String selectQuery =
                    "SELECT Oppskrifter._ID, Oppskrifter.oppskriftTittel, BilderIOppskrift.Bildet FROM Oppskrifter " +
                    " LEFT OUTER JOIN BilderIOppskrift " +
                        " ON BilderIOppskrift.Id = " +
                    "       ( SELECT  BilderIOppskrift.Id " +
                    "         FROM    BilderIOppskrift " +
                    "         WHERE   BilderIOppskrift.OppskriftID = Oppskrifter._ID LIMIT 1)";
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    long id = cursor.getLong(0);
                    String title = cursor.getString(1);
                    byte[] bildet = cursor.getBlob(2);
                    OppskriftListItem oppskrift = new OppskriftListItem(id, title, bildet);
                    oppskriftListe.add(oppskrift);
                } while (cursor.moveToNext());
                cursor.close();
                db.close();
            }
        }catch(Exception e){
        Log.e(this.context.getResources().getString(R.string.finne_oppskrift_db), e.getMessage());
        }
        return oppskriftListe;
    }

    public List<OppskriftMedDato> finnOppskrifterIKalender() {
        List<OppskriftMedDato> oppskriftDatoList = new ArrayList<>();
        try {
            String selectQuery = "SELECT "+KEY_ID + "," + EVENT_ID + "," + KEY_OPPSKRIFT_TITTEL +", "+ DATO_I_KALENDER+ " FROM " + TABLE_OPPSKRIFTER
                    + " INNER JOIN " + TABLE_OPPSKRIFTER_I_KALENDER +
                    " ON "+TABLE_OPPSKRIFTER+ "." + KEY_ID +" = " + TABLE_OPPSKRIFTER_I_KALENDER+ "."+OPPSKR_ID;
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    long oppskriftId = cursor.getLong(0);
                    long oppskriftIKalenderId = cursor.getLong(1);
                    String title = cursor.getString(2);
                    String dato = cursor.getString(3);
                    OppskriftMedDato oppskriftMedDato = new OppskriftMedDato(oppskriftId, oppskriftIKalenderId, title,dato);
                    oppskriftDatoList.add(oppskriftMedDato);
                } while (cursor.moveToNext());
                cursor.close();
                db.close();
            }
        }catch(Exception e){
            Log.e(this.context.getResources().getString(R.string.finne_oppskr_den_dato_db), e.getMessage());
        }
        return oppskriftDatoList;
    }

    public Oppskrift finnOppskriftIdb(long id) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_OPPSKRIFTER, new String[]{
                        KEY_ID, KEY_OPPSKRIFT_TITTEL, KEY_OPPSKR_TEKST}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        Oppskrift oppskriftfradb = new
                Oppskrift(cursor.getLong(0),cursor.getString(1), cursor.getString(2));
        cursor.close();
        db.close();
        return oppskriftfradb;
    }

    public ArrayList<Bildet> finnBilderForEnOppskrift(long oppskriftId) {

        ArrayList<Bildet> bilder = new ArrayList();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_BILDER_I_OPPSKRIFT, new String[]{
                        BILDER_ID, KEY_IMG, OPPSKR_ID}, OPPSKR_ID + "=?",
                new String[]{String.valueOf(oppskriftId)}, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Bildet b = new Bildet(cursor.getLong(0), cursor.getBlob(1), cursor.getLong(2));
                bilder.add(b);

            }
        }

        cursor.close();
        db.close();

        return bilder;
    }

    /*public OppskriftIKalender finnOppskriftIKalender(String dato) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_OPPSKRIFTER_I_KALENDER, new String[]{
                        EVENT_ID, DATO_I_KALENDER, OPPSKR_ID}, DATO_I_KALENDER + "=?",
                new String[]{String.valueOf(dato)}, null, null, null, null);

        if (cursor != null) cursor.moveToFirst();
        OppskriftIKalender kalenderOppskrift = new OppskriftIKalender(cursor.getString(1), Long.parseLong(cursor.getString(2)));
        kalenderOppskrift.setId(Long.parseLong(cursor.getString(0)));
        cursor.close();
        db.close();
        return kalenderOppskrift;
    }*/

    public void slettOppskrift(long id) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_OPPSKRIFTER_I_KALENDER, OPPSKR_ID + " =? ",
                new String[]{Long.toString(id)});
        db.delete(TABLE_BILDER_I_OPPSKRIFT, OPPSKR_ID + " =? ",
                new String[]{Long.toString(id)});
        db.delete(TABLE_OPPSKRIFTER, KEY_ID + " =? ",
                new String[]{Long.toString(id)});
        db.close();
    }

    public void slettOppskriftFraKalender(long id) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_OPPSKRIFTER_I_KALENDER, EVENT_ID + " =? ",
                new String[]{Long.toString(id)});
        db.close();
    }

    public void slettBildetFraOppskrift(long id) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BILDER_I_OPPSKRIFT, BILDER_ID + " =? ",
                new String[]{Long.toString(id)});
        db.close();
    }

    public int oppdatereOppskrift(Oppskrift oppskrift) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_OPPSKRIFT_TITTEL, oppskrift.getOppskriftTittel());
        values.put(KEY_OPPSKR_TEKST, oppskrift.getOppskriftText());
        int endret = db.update(TABLE_OPPSKRIFTER, values, KEY_ID + "= ?",
                new String[]{String.valueOf(oppskrift.getId())});
        db.close();
        return endret;
    }


}
