package in.blazonsoftwares.trackmark;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private EditText username;
    private EditText password;
    private FirebaseAuth firebaseAuth;
    ProgressDialog pd;

    // Alert Dialog Manager & session object
    SessionManagement session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        firebaseAuth = FirebaseAuth.getInstance();

        username = (EditText) findViewById(R.id.txt_uname);
        password = (EditText) findViewById(R.id.txt_password);

        session = new SessionManagement(getApplicationContext());

    }

    public void btnRegistrationUser_Click(View v){

        Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(i);
       /* try
        {
            String user=username.getText().toString();
            String pass=password.getText().toString();
            if(user.equals("") || pass.equals(""))
            {

                Toast.makeText(LoginActivity.this, "Sorry username and password is compulsory...",
                        Toast.LENGTH_SHORT).show();
                username.setText("");
                password.setText("");
                username.requestFocus();

            }
            else
            {
                pd = new ProgressDialog(LoginActivity.this);
                pd.setMessage("Loading");
                pd.show();


             firebaseAuth.createUserWithEmailAndPassword(user, pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            pd.dismiss();
                            if (!task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Sorry This User Already Exist..",
                                        Toast.LENGTH_SHORT).show();
                            }
                            else {

                                Toast.makeText(LoginActivity.this, "New User Create Successfully...",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

            }


      }
        catch (Exception ex)
        {
            Toast.makeText(LoginActivity.this, "catch error ..." + ex,
                    Toast.LENGTH_SHORT).show();
        }*/



    }
    public void btnUserLogin_Click(View v) {
        String user=username.getText().toString();
        String pass=password.getText().toString();

        if(user.equals("") || pass.equals(""))
        {

            Toast.makeText(LoginActivity.this, "Sorry username and password is compulsory...",
                    Toast.LENGTH_SHORT).show();
            username.setText("");
            password.setText("");
            username.requestFocus();

        }

        else {
            String url = "http://trackmark.in/shop/LoginAuthentication?username=" + user + "&password=" + pass;
            //String url="http://trackmark.in/shop/LoginAuthentication?username=om@gmail.com&password=123456";
            StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    showJSON(response);

                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            int socketTimeout = 30000; // 30 seconds. You can change it
                            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                            Toast.makeText(LoginActivity.this,"error call...."+error,Toast.LENGTH_LONG).show();
                        }
                    });
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);

        }



     /*   try
        {
            final String user=username.getText().toString();
            final String pass=password.getText().toString();

            if(user.equals("") || pass.equals(""))
            {

                Toast.makeText(LoginActivity.this, "Sorry username and password is compulsory...",
                        Toast.LENGTH_SHORT).show();
                username.setText("");
                password.setText("");
                username.requestFocus();

            }
            else {
                    pd = new ProgressDialog(LoginActivity.this);
                    pd.setMessage("Loading");
                    pd.show();
                    firebaseAuth.signInWithEmailAndPassword(user, pass)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    pd.dismiss();
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this, "Login Failed, You entered an incorrect username or password...",
                                                Toast.LENGTH_SHORT).show();
                                        username.setText("");
                                        password.setText("");
                                        username.requestFocus();

                                    } else {
                                        session.KEY_EMAIL = "" + user;
                                        session.createLoginSession("Blazon Test", "info@blazonsoftwares.in");
                                        Intent i = new Intent(LoginActivity.this, MapsActivity.class);
                                        i.putExtra("emialname", user);
                                        startActivity(i);
                                        finish();
                                    }
                                }
                            });
            }
        }
        catch (Exception ex)
        {
            Toast.makeText(LoginActivity.this, "catch error ..." + ex,
                    Toast.LENGTH_SHORT).show();
        }*/



    }


    private void showJSON(String response){
        try {
            JSONArray jsonObject = new JSONArray(response);

            if(jsonObject.length()==0)
            {
                Toast.makeText(LoginActivity.this, "Login Failed, You entered an incorrect username or password...",
                        Toast.LENGTH_SHORT).show();
                username.setText("");
                password.setText("");
                username.requestFocus();
            }
            else {
                session.KEY_EMAIL = "" + username.getText();
                session.createLoginSession("Blazon Test", "info@blazonsoftwares.in");
                Intent i = new Intent(LoginActivity.this, MapsActivity.class);
                i.putExtra("emialname", username.getText());
                startActivity(i);
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}