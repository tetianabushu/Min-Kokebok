package com.example.bushu.mineoppskrifter;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AddOppskrift extends AppCompatActivity {
    private static final int REQUEST_TAKE_PHOTO =1;
    private static final int REQUEST_GALLERY_PHOTO =2;

    private ArrayList<Long> bildetToDelete;
    private Uri file;
    private Menu menu;
    private ViewFlipper flipper;
    Oppskrift oppskrift;
    DBHandler db;
    private int bilderCount = 0;
    private int currentBildet = 0;
    private long id;

    public AddOppskrift() {
        bildetToDelete = new ArrayList();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_oppskrift);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        db = new DBHandler(this);
        setBildetFlipper();

        Toolbar t = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(t);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();
        if (intent.hasExtra(getResources().getString(R.string.hele_oppskr_id))) {
            id = intent.getExtras().getLong(getResources().getString(R.string.hele_oppskr_id));
            henteEksisterendeOppskriftOgBilder(id);
        } else {
            oppskrift = null;
        }

        ImageButton b = (ImageButton) findViewById(R.id.fjern_bildet);
        if(flipper.getChildCount() == 0) b.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_add_oppskr, menu);
        this.menu = menu;
        checkPermissions(menu);
        Intent i = getIntent();
        MenuItem del_item = menu.findItem(R.id.delete_item);
        if(!i.hasExtra(getResources().getString(R.string.hele_oppskr_id))) {
            del_item.setVisible(false);
        }
        else {
            del_item.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(id != 0){
                    finish();
                    return true;
                }
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.velg_gallery:
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_GALLERY_PHOTO);
                break;
            case R.id.ta_bildet:
                try {
                    taBildet(item.getActionView());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.save_item:
                largeOppskrift(item.getActionView());
                break;
            case R.id.delete_item:
                sletteOpskrift(item.getActionView());
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                MenuItem item = menu.findItem(R.id.ta_bildet);
                item.setEnabled(true);
            }
        }
    }
    public void taBildet(View view) throws IOException {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            try {
                file = Uri.fromFile(getOutputMediaFile());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, file);
                startActivityForResult(intent, REQUEST_TAKE_PHOTO);
            } catch (IOException ex) {

            }
        }
    }
    public void sletteOpskrift(View v){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        Intent intent = new Intent(AddOppskrift.this,OppskriftListe.class);
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
        builder.setMessage("Vil du slette denne oppskrift?").setPositiveButton("Ja", dialogClickListener)
                .setNegativeButton("Nei", dialogClickListener).show();



        /*Intent intent = new Intent(this,OppskriftListe.class);
        db.slettOppskrift(id);
        setResult(RESULT_OK, intent);
        Toast.makeText(getApplicationContext(), R.string.oppskrift_slettet_warning, Toast.LENGTH_SHORT).show();
        finish();
*/
    }

    public void slettBildet(View v){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        int current = flipper.getDisplayedChild();
                        ImageView v = (ImageView) flipper.getChildAt(current);
                        if(v.getTag() != null){
                            long id = (long)v.getTag();
                            bildetToDelete.add(id);
                        }
                        flipper.removeViewAt(current);
                        bilderCount--;
                        if(bilderCount == 0) currentBildet = 0;
                        if(currentBildet > bilderCount) currentBildet--;
                        updateBilderCounter();
                        Toast.makeText(getApplicationContext(), R.string.bildet_fjernet, Toast.LENGTH_SHORT).show();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Vil du fjerne bildet?").setPositiveButton("Ja", dialogClickListener)
                .setNegativeButton("Nei", dialogClickListener).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if((requestCode == REQUEST_TAKE_PHOTO || requestCode == REQUEST_GALLERY_PHOTO) && resultCode == RESULT_OK){
            try {
                ImageView v = new ImageView(this);
                Bitmap bitmap;
                if (requestCode == REQUEST_TAKE_PHOTO ) {
                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(file)); // hvis file er null, så ble ikke bildet oppdatert på Hele oppskrift aktivitet, da beholdes det gamle bildet
                    Matrix m = new Matrix();
                    m.postRotate(90);
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),bitmap.getHeight(), m,false);
                }else {
                    super.onActivityResult(requestCode, resultCode, data);
                    if(data!=null){
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(data.getData()));
                    }
                    else return;
                }

                v.setImageBitmap(bitmap);
                flipper.addView(v);
                bilderCount++;
                if(bilderCount == 1) currentBildet = 1;
                updateBilderCounter();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        if (requestCode ==23) {
            if (resultCode == RESULT_OK && data != null) {
                file = data.getData();
            }
        }
    }

    /*private Bildet getBildetFraBitmap(Bitmap bitmap, long oppskriftId) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);
        byte[] bArray = bos.toByteArray();

        Bildet b = new Bildet(0, bArray, oppskriftId);
        return b;
    }*/

    public void largeOppskrift(View v){
        EditText oppskriftTittel = (EditText)findViewById(R.id.oppskr_tittel);
        EditText oppskriftText = (EditText)findViewById(R.id.oppskrift_text);

        if(oppskrift ==null){
            oppskrift = new Oppskrift();
        }
        oppskrift.setOppskriftTittel(oppskriftTittel.getText().toString()); // setter oppskr tittel fra gui input
        oppskrift.setOppskriftText(oppskriftText.getText().toString()); //setter oppskri tekst fra gui input

        if(oppskrift.getOppskriftTittel().isEmpty() || oppskrift.getOppskriftText().isEmpty()) {
            Toast.makeText(getApplicationContext(),R.string.validering_av_larging, Toast.LENGTH_SHORT).show();
            return;
        }

        lagreOppskrift_i_db();
        finish();

    }

    private void checkPermissions(Menu menu) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            MenuItem item = menu.findItem(R.id.ta_bildet);
            item.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }

    private void henteEksisterendeOppskriftOgBilder(long id) {

        oppskrift = db.finnOppskriftIdb(id);
        EditText oppskriftTittel = (EditText) findViewById(R.id.oppskr_tittel);
        oppskriftTittel.setText(oppskrift.getOppskriftTittel());
        EditText oppskriftText = (EditText) findViewById(R.id.oppskrift_text);
        oppskriftText.setText(oppskrift.getOppskriftText());

        ArrayList<Bildet> bilder = db.finnBilderForEnOppskrift(id);
        for (Bildet b : bilder) {
            byte[] bildetArr = b.getBildet();
            Bitmap bmp = BitmapFactory.decodeByteArray(bildetArr, 0, bildetArr.length);
            ImageView v = new ImageView(this);
            v.setImageBitmap(bmp);
            v.setTag(b.getId());

            flipper.addView(v);
        }

        bilderCount = bilder.size();
        if(bilderCount > 0) currentBildet = 1;
        updateBilderCounter();
    }

    private void updateBilderCounter() {
        TextView bilderCountView = (TextView) findViewById(R.id.bildet_counter_add_opp);
        bilderCountView.setText(currentBildet + "/" + bilderCount);

        ImageButton b = (ImageButton) findViewById(R.id.fjern_bildet);
        if(bilderCount > 0) b.setVisibility(View.VISIBLE);
        else b.setVisibility(View.INVISIBLE);
    }

    private void setBildetFlipper() {
        flipper = (ViewFlipper) findViewById(R.id.bildetflipper);
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

    private void lagreOppskrift_i_db() {
        Intent intent;
        ArrayList<Bitmap> alleBilder = getBitmapBilderFraFlipper(flipper);

        if(oppskrift.getId() == 0){
            intent = new Intent(this,OppskriftListe.class );
            long newOppskriftId = db.leggTilOppskrift(oppskrift); //returnerer id av ny oppskr som ble lagret i db

            if(alleBilder.size() > 0 && newOppskriftId != -1) {
                lagreBilder(newOppskriftId, alleBilder);
            }
        }else{
            intent = new Intent(this,HeleOppskrift.class);
            db.oppdatereOppskrift(oppskrift);
            if(alleBilder.size() > 0) {
                lagreBilder(oppskrift.getId(), alleBilder);
            }
            for (long id:bildetToDelete) {
                db.slettBildetFraOppskrift(id);
            }
        }

        Log.d("Legg inn: ",(getResources().getString(R.string.legge_til_oppskrt_log)));
        setResult(RESULT_OK, intent);
    }

    @NonNull
    private ArrayList<Bitmap> getBitmapBilderFraFlipper(ViewFlipper f) {
        ArrayList<Bitmap> alleBilder = new ArrayList();
        for (int i=0; i<f.getChildCount(); i++){
            ImageView v = (ImageView) f.getChildAt(i);
            if(v.getTag() == null){
                BitmapDrawable drawable = (BitmapDrawable) v.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                alleBilder.add(bitmap);
            }
        }
        return alleBilder;
    }


    private void lagreBilder(long newOppskriftId, ArrayList<Bitmap> list) {
        for (Bitmap bitmap : list) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);
            byte[] bArray = bos.toByteArray();

            Bildet b = new Bildet();
            b.setBildet(bArray);
            b.setOppskrift_id(newOppskriftId); //kobler bildet med oppskrift_tabell, FK er oppskriftId
            db.leggTilBildet(b);
        }
    }

    private File getOutputMediaFile() throws IOException{
        File mediaLagringsMappe = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "CameraDemo");
        if (!mediaLagringsMappe.exists()){
            if (!mediaLagringsMappe.mkdirs()){
                return null;
            }
        }
        //lage navn image fil
        String timeStamp = new SimpleDateFormat(this.getResources().getString(R.string.simple_date_format)).format(new Date());
        return new File(mediaLagringsMappe.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg");
    }
}

