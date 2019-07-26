package app.adie.reservation.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import app.adie.reservation.Fragment.DialogPembayaranFragment;
import app.adie.reservation.R;
import app.adie.reservation.SessionManager;
import app.adie.reservation.entity.AndroidMultiPartEntity;
import app.adie.reservation.utils.DateUtils;
import app.adie.reservation.utils.RequestHandler;
import app.adie.reservation.utils.StringUtils;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class PembayaranActivity extends BaseActivity implements View.OnClickListener {
    public static final String UPLOAD_URL = "http://krakalineshuttle.xyz/api/upload.php";
    public static final String UPLOAD_KEY = "image";
    public static final String FILE_UPLOAD_URL = "http://krakalineshuttle.xyz/api/fileUpload.php";
    public static final String IMAGE_DIRECTORY_NAME = "Krakaline";
    private int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 133;
    private static final String TAG = PembayaranActivity.class.getSimpleName();
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static String kode;
    private static String total,poeayeuna,jamayeuna,tgl,batas,id_pem,iduser;
    private ImageView imageView;
    private Bitmap bitmap;
    private Uri filePath;
    SessionManager session;
    private int statusBarColor;
    private int b=2;
    private Toolbar toolbar;
    private static int harga,diskon;
    public static AppCompatActivity activity;
    private DialogPembayaranFragment mDialogFragment;
    private EditText inputkode, inputharga, inputdiskon,inputtot;
    private TextView buk;
    private Button btnkonfirmasi,btnpilih,btnupload,btncam;
    LinearLayout cont;
    long totalSize = 0;
    private Uri fileUri;
    String pathToFile;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pembayaran);
        setIsLastActivities(true);
        Bundle extras = this.getIntent().getExtras();
        activity = PembayaranActivity.this;
        toolbar = findViewById(R.id.toolbarf);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Konfirmasi Pembayaran");
        imageView = findViewById(R.id.photoResult);
        inputkode = findViewById(R.id.inp_kodebooking);
        inputharga = findViewById(R.id.inp_harga);
        inputdiskon = findViewById(R.id.inp_diskon);
        inputtot = findViewById(R.id.inp_total);
        btncam = findViewById(R.id.btn_cam);
        btnupload = findViewById(R.id.btn_upload);
        btnkonfirmasi = findViewById(R.id.btn_pembayaran);
        btnpilih = findViewById(R.id.btn_pilih);
        buk = findViewById(R.id.bukti);
        cont = findViewById(R.id.container);
        btnkonfirmasi.setOnClickListener(this);
        btnpilih.setOnClickListener(this);
        btncam.setOnClickListener(this);
        btnupload.setOnClickListener(this);

        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        HashMap<String, String> user = session.getUserDetails();
        iduser = user.get(SessionManager.KEY_ID);
        if (extras != null) {
            id_pem = extras.getString("id_pemesan");
            kode = extras.getString("kode");
            total = extras.getString("total");
            harga = extras.getInt("harga");
            diskon = extras.getInt("diskon");
            tgl = extras.getString("tgl");
            batas = extras.getString("batas");
            inputkode.setText(kode);
            inputharga.setText(StringUtils.toRupiahFormat(harga));
            inputtot.setText(StringUtils.toRupiahFormat(total));
            inputdiskon.setText(StringUtils.toRupiahFormat(diskon));
        }


        if (savedInstanceState == null) {
            this.mDialogFragment = DialogPembayaranFragment.newInstance(tgl,batas);
            getSupportFragmentManager().beginTransaction().replace(R.id.containerterms, this.mDialogFragment).commit();
            return;
        }
        this.mDialogFragment = (DialogPembayaranFragment) getSupportFragmentManager().getFragment(savedInstanceState, "mDialogFragment");

        if (Build.VERSION.SDK_INT >= 23){
            requestPermissions(new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},2);
        }
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (requestCode == 1){
                Bitmap bitmap = BitmapFactory.decodeFile(pathToFile);
                imageView.setImageBitmap(bitmap);
            }
        }
    }*/

    /**private void isDeviceSupportCamera() {
        try {
            if (getPackageManager().hasSystemFeature(
                    PackageManager.FEATURE_CAMERA)) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);


                startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);

            } else showSnackbar(btnkonfirmasi, R.string.eror_cam, true);
        } catch (Exception ignored){
            ignored.printStackTrace();
        }
    }*/

    //Take Photo's Code


    private void dispatchPictureTakenAction(){
        Intent takenPict = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takenPict.resolveActivity(getPackageManager()) != null){
            File photoFile = null;
            photoFile = createPhotoFile();

            if (photoFile != null){
                pathToFile = photoFile.getAbsolutePath();
                Uri photoUri = FileProvider.getUriForFile(PembayaranActivity.this, "app.adie.reservation.fileprovider", photoFile);
                takenPict.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takenPict, 1);
            }
        }
    }

    private File createPhotoFile(){
        @SuppressLint("SimpleDateFormat") String name = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(name, ".jpg", storageDir);
        } catch (IOException e) {
            Log.d("my log", "Except : "+e.toString());
        }
        return image;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            btnkonfirmasi.setVisibility(View.VISIBLE);
            btnupload.setVisibility(View.GONE);
            cont.setVisibility(View.VISIBLE);

            this.buk.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_bill_on, 0, 0, 0);
            filePath = data.getData();
            String pat = data.getDataString();
            Log.d("Tes: ", String.valueOf(pat));
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
       else if (requestCode == 1 ) {
            if (resultCode == RESULT_OK) {
                try {

                    launchUploadActivity();
                } catch (Exception ignore){
                    ignore.printStackTrace();
                }


            }  else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private void launchUploadActivity(){
        btnkonfirmasi.setVisibility(View.VISIBLE);
        btnupload.setVisibility(View.GONE);
        cont.setVisibility(View.VISIBLE);

        BitmapFactory.Options options = new BitmapFactory.Options();

        Bitmap bitmap = BitmapFactory.decodeFile(pathToFile);

        imageView.setImageBitmap(bitmap);
    }

    private int getStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return getWindow().getStatusBarColor();
        }
        return 0;
    }

    private void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(color);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }
    private void uploadImage(){

        class UploadImage extends AsyncTask<Bitmap,Void,String> {

            private ProgressDialog loading;
            private RequestHandler rh = new RequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = new ProgressDialog(PembayaranActivity.this);
                loading.setMessage("Mohon Tunggu Sedang Dalam Proses Upload");
                loading.setIndeterminate(false);
                loading.setCancelable(false);
                loading.show();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                if(s.equals("success")) {
                    Intent a = new Intent(PembayaranActivity.this, MainActivity.class);
                    a.putExtra("get", b);
                    startActivityForResult(a, 20);
                    finish();
                }else {
                    showSnackbar(btnkonfirmasi, R.string.eror_upload, true);
                }
            }

            @Override
            protected String doInBackground(Bitmap... params) {
                Bitmap bitmap = params[0];

                if (bitmap!=null) {
                    Date date = new Date();
                    poeayeuna = DateUtils.getDateNow(date);
                    jamayeuna = DateUtils.getTimeNow(date);
                    String uploadImage = getStringImage(bitmap);
                    HashMap<String, String> data = new HashMap<>();
                    data.put("tanggal",poeayeuna);
                    data.put("jam",jamayeuna);
                    data.put("id_pemesanan",id_pem);
                    data.put("id_user",iduser);
                    data.put(UPLOAD_KEY, uploadImage);

                    return rh.sendPostRequest(UPLOAD_URL, data);
                }else {
                    Toast.makeText(getApplicationContext(),"Ee" , Toast.LENGTH_SHORT).show();
                }
                return null;
            }
        }
        UploadImage ui = new UploadImage();
        ui.execute(bitmap);
    }

    @Override
    public void onClick(View v) {
        if (v == btnpilih) {
            showFileChooser();
        }

        if(v == btnkonfirmasi){
            uploadImage();
        }
        if(v==btncam) {
            dispatchPictureTakenAction();
        }
        if(v==btnupload){
            new UploadFileToServer().execute();
        }
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "bukti");
        } else {
            return null;
        }

        return mediaFile;
    }

    @SuppressLint("StaticFieldLeak")
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {

        ProgressDialog loading;
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero

            super.onPreExecute();
            loading = new ProgressDialog(PembayaranActivity.this);
            loading.setMessage("Mohon Tunggu Sedang Dalam Proses Upload");
            loading.setIndeterminate(false);
            loading.setCancelable(false);
            loading.show();
        }



        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(FILE_UPLOAD_URL);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });
                Date date = new Date();
                poeayeuna = DateUtils.getDateNow(date);
                jamayeuna = DateUtils.getTimeNow(date);
                File sourceFile = new File(Objects.requireNonNull(fileUri.getPath()));

                // Adding file data to http body
                entity.addPart("image", new FileBody(sourceFile));

                // Extra parameters if you want to pass to server
                entity.addPart("id_pemesanan", new StringBody(id_pem));
                entity.addPart("tanggal", new StringBody(poeayeuna));
                entity.addPart("jam", new StringBody(jamayeuna));
                entity.addPart("id_user", new StringBody(iduser));
                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            loading.dismiss();
            if(!Objects.equals(result, "error")) {
                Intent a = new Intent(PembayaranActivity.this, MainActivity.class);
                a.putExtra("get", b);
                startActivityForResult(a, 20);
                finish();
            }else {
                showSnackbar(btnkonfirmasi, R.string.eror_upload, true);
            }

        }

    }

}
