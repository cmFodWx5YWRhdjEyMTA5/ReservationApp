package app.adie.reservation.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import app.adie.reservation.JSONParser;
import app.adie.reservation.R;
import app.adie.reservation.SessionManager;
import app.adie.reservation.entity.User;
import app.adie.reservation.utils.ConnectionDetector;

public class Register extends BaseActivity {

    private Toolbar toolbar;
    private EditText inputUsername,inputName, inputEmail, inputPassword,inputPhone,inputAlamat;
    private TextInputLayout inputLayoutUsername,inputLayoutName, inputLayoutEmail, inputLayoutPassword,inputLayoutPhone,inputLayoutAlamat;
    private Button btnSignUp;
    private ProgressDialog pDialog;
    private String nama,email;
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private JSONParser jsonParser = new JSONParser();
    public SessionManager session;
    private static String url = "http://krakalineshuttle.xyz/api/register.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        toolbar = (Toolbar) findViewById(R.id.toolbarr);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle extras = getIntent().getExtras();
        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        session = new SessionManager(getApplicationContext());
        inputLayoutAlamat = (TextInputLayout) findViewById(R.id.input_layout_alamat);
        inputLayoutUsername = (TextInputLayout) findViewById(R.id.input_layout_uname);
        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        inputLayoutPhone = (TextInputLayout) findViewById(R.id.input_layout_phone);

        inputAlamat = (EditText)findViewById(R.id.input_alamat);
        inputUsername = (EditText)findViewById(R.id.input_uname);
        inputName = (EditText) findViewById(R.id.input_name);
        inputEmail = (EditText) findViewById(R.id.input_email);
        inputPassword = (EditText) findViewById(R.id.input_password);
        inputPhone = (EditText) findViewById(R.id.input_phone);
        btnSignUp = (Button) findViewById(R.id.btn_signup);

        inputAlamat.addTextChangedListener(new MyTextWatcher(inputAlamat));
        inputUsername.addTextChangedListener(new MyTextWatcher(inputUsername));
        inputName.addTextChangedListener(new MyTextWatcher(inputName));
        inputEmail.addTextChangedListener(new MyTextWatcher(inputEmail));
        inputPhone.addTextChangedListener(new MyTextWatcher((inputPhone)));
        inputPassword.addTextChangedListener(new MyTextWatcher(inputPassword));

        if (extras != null){
            this.nama = extras.getString("nama");
            this.email = extras.getString("email");
            this.inputEmail.setText(this.email);
            this.inputName.setText(this.nama);
        }

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });
    }

    /**
     * Validating form
     */
    private void submitForm() {
        if (!validateUsName()) {
            return;
        }
        if (!validateLimPassword()) {
            return;
        }
        if (!validateLimName()) {
            return;
        }
        if (!validateLimAlamat()) {
            return;
        }
        if (!validateLimPhone()) {
            return;
        }
        if (!validateUName()) {
            return;
        }
        if (!validateName()) {
            return;
        }
        if (!validateAlamat()) {
            return;
        }
        if (!validateEmail()) {
            return;
        }

        if (!validatePassword()) {
            return;
        }
        if (!validatePhone()) {
            return;
        }
        if (isInternetPresent) {
            new DaftarAku().execute();
        } else {
            // Internet connection is not present
            // Ask user to connect to Internet
            showSnackbar(btnSignUp, (int) R.string.message_no_network_connection, true);
        }

    }

    public class DaftarAku extends AsyncTask<String, String, String> {

        String success;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Register.this);
            pDialog.setMessage("Signing Up");
            pDialog.setIndeterminate(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            @SuppressLint("WrongThread") String struname = inputUsername.getText().toString();
            @SuppressLint("WrongThread") String strnama = inputName.getText().toString();
            @SuppressLint("WrongThread") String stremail = inputEmail.getText().toString();
            @SuppressLint("WrongThread") String strpassword = inputPassword.getText().toString();
            @SuppressLint("WrongThread") String stralamat = inputAlamat.getText().toString();
            @SuppressLint("WrongThread") String strphone = inputPhone.getText().toString();
            List<NameValuePair> nvp = new ArrayList<NameValuePair>();
            nvp.add(new BasicNameValuePair("uname", struname));
            nvp.add(new BasicNameValuePair("nama", strnama));
            nvp.add(new BasicNameValuePair("alamat", stralamat));
            nvp.add(new BasicNameValuePair("email", stremail));
            nvp.add(new BasicNameValuePair("password", strpassword));
            nvp.add(new BasicNameValuePair("phone", strphone));

            JSONObject json = jsonParser.makeHttpRequest(url, "POST", nvp);
            try {
                success = json.getString("success");
                if(Objects.equals(success, "1")){
                    String iduser = json.getString("id");
                    User user = new User(json.getString("id"),
                            strnama,
                            stremail,
                            strphone,stralamat);
                    session.storeUser(user);
                    Log.e("ok", iduser);
                }else if(success.equals("0")){
                    showSnackbar(btnSignUp, (int) R.string.eror_pesan, false);
                }

            } catch (Exception e) {
               // showSnackbar(btnSignUp, (int) R.string.eror_login, false);
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();

            if (Objects.equals(success, "1")) {
                Intent a = new Intent(Register.this, LoadingLanding.class);
                startActivity(a);
                finish();

            } else if (Objects.equals(success, "0")){
                showSnackbar(btnSignUp, (int) R.string.eror_pesan, false);
            }else if(Objects.equals(success, "3")){
                showSnackbar(btnSignUp, (int) R.string.eror_reg, false);
            }
        }

    }

    private boolean validateName() {
        if (inputName.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError(getString(R.string.err_msg_name));
            requestFocus(inputName);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }

        return true;
    }
    private boolean validateLimName() {
        if (inputName.getText().toString().length()<6) {
            inputLayoutName.setError("Jumlah karakter nama lengkap minimal 6 karakter");
            requestFocus(inputName);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }

        return true;
    }
    private boolean validateUsName() {
        if (inputUsername.getText().toString().length()<3) {
            inputLayoutUsername.setError("Jumlah karakter username minimal 3 karakter");
            requestFocus(inputUsername);
            return false;
        } else {
            inputLayoutUsername.setErrorEnabled(false);
        }

        return true;
    }
    private boolean validateLimPassword() {
        if (inputPassword.getText().toString().length()<4) {
            inputLayoutPassword.setError("Jumlah karakter password minimal 4 karakter");
            requestFocus(inputPassword);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }
    private boolean validateLimAlamat() {
        if (inputAlamat.getText().toString().length()<8) {
            inputLayoutAlamat.setError("Jumlah karakter alamat minimal 8 karakter");
            requestFocus(inputAlamat);
            return false;
        } else {
            inputLayoutAlamat.setErrorEnabled(false);
        }

        return true;
    }
    private boolean validateAlamat() {
        if (inputAlamat.getText().toString().trim().isEmpty()) {
            inputLayoutAlamat.setError(getString(R.string.err_msg_alm));
            requestFocus(inputAlamat);
            return false;
        } else {
            inputLayoutAlamat.setErrorEnabled(false);
        }

        return true;
    }
    private boolean validateUName() {
        if (inputUsername.getText().toString().trim().isEmpty()) {
            inputLayoutUsername.setError(getString(R.string.err_msg_uname));
            requestFocus(inputUsername);
            return false;
        } else {
            inputLayoutUsername.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateEmail() {
        String email = inputEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus(inputEmail);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {
        if (inputPassword.getText().toString().trim().isEmpty()) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            requestFocus(inputPassword);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }
    private boolean validateLimPhone() {
        if (inputPhone.getText().toString().length()<10) {
            inputLayoutPhone.setError("Jumlah karakter no telp minimal 10 karakter");
            requestFocus(inputPhone);
            return false;
        } else {
            inputLayoutPhone.setErrorEnabled(false);
        }

        return true;
    }
    private boolean validatePhone() {
        if (inputPhone.getText().toString().trim().isEmpty()) {
            inputLayoutPhone.setError(getString(R.string.err_msg_phone));
            requestFocus(inputPhone);
            return false;
        } else {
            inputLayoutPhone.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_uname:
                    validateUName();
                    validateUsName();
                    break;
                case R.id.input_name:
                    validateName();
                    validateLimName();
                    break;
                case R.id.input_alamat:
                    validateAlamat();
                    validateLimAlamat();
                    break;
                case R.id.input_email:
                    validateEmail();
                    break;
                case R.id.input_password:
                    validatePassword();
                    validateLimPassword();
                    break;
                case R.id.input_phone:
                    validatePhone();
                    validateLimPhone();
                    break;
            }
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
}
