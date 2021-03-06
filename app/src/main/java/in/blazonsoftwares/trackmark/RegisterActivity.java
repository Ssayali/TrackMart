package in.blazonsoftwares.trackmark;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.blazonsoftwares.trackmark.model.WebServicesAPI;


public class RegisterActivity extends AppCompatActivity {

    Button btnsignin;
    EditText txt_email,txt_add,txt_cno,txt_pass,txt_conpass;

    //firebase varibels
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    String user, pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnsignin=(Button) findViewById(R.id.btnsignin);
        txt_email=(EditText) findViewById(R.id.txt_email);
        txt_add=(EditText) findViewById(R.id.txt_add);
        txt_cno=(EditText) findViewById(R.id.txt_cno);
        txt_pass=(EditText) findViewById(R.id.txt_pass);
        txt_conpass=(EditText) findViewById(R.id.txt_conpass);


        //Firebase  initialization
        Firebase.setAndroidContext(this);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        btnsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                  if(TextUtils.isEmpty(txt_email.getText())) {
                        txt_email.requestFocus();
                        txt_email.setError("FIELD CANNOT BE EMPTY");
                    }
                    else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(txt_email.getText()).matches())
                    {
                        txt_email.setError("Invalid Email ADDRESS");
                        return;
                    }

                    else if(TextUtils.isEmpty(txt_add.getText())) {
                        txt_add.requestFocus();
                        txt_add.setError("FIELD CANNOT BE EMPTY");
                    }

                    else if(TextUtils.isEmpty(txt_cno.getText())) {
                        txt_cno.requestFocus();
                        txt_cno.setError("FIELD CANNOT BE EMPTY");
                    }
                    else if(!TextUtils.isDigitsOnly(txt_cno.getText()))
                    {
                        txt_cno.setText("");
                        txt_cno.requestFocus();
                        txt_cno.setError("Invalid Mobile No.");
                    }
                    else if(TextUtils.isEmpty(txt_pass.getText())) {
                        txt_pass.requestFocus();
                        txt_pass.setError("FIELD CANNOT BE EMPTY");
                    }
                    else if(TextUtils.isEmpty(txt_conpass.getText())) {
                        txt_conpass.requestFocus();
                        txt_conpass.setError("FIELD CANNOT BE EMPTY");
                    }
                    else if(!txt_pass.getText().toString().equals(txt_conpass.getText().toString()))
                    {
                        txt_pass.setText("");
                        txt_conpass.setText("");
                        txt_pass.requestFocus();
                        txt_pass.setError("Password Does Not Match.");
                    }

                    else {


                      String urlpre = WebServicesAPI.deployment_api+"shop/LoginAuthentication?username=" + txt_email.getText().toString() + "&password=" + txt_pass.getText().toString();

                      StringRequest stringRequest = new StringRequest(urlpre, new Response.Listener<String>() {
                          @Override
                          public void onResponse(String response) {
                              showJSON(response);
                          }
                      },
                              new Response.ErrorListener() {
                                  @Override
                                  public void onErrorResponse(VolleyError error) {
                                  }
                              });
                      RequestQueue requestQueue = Volley.newRequestQueue(RegisterActivity.this);
                      requestQueue.add(stringRequest);

                    }
                }
                catch (Exception ex){
                    Toast.makeText(RegisterActivity.this, "exception error..." + ex, Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void showJSON(String response){
        try {
            JSONArray jsonObject = new JSONArray(response);

            if(jsonObject.length()==0)
            {
                storedetails();
                String url = WebServicesAPI.deployment_api+"shop/UserRegistration?User_Email=" + txt_email.getText().toString() + "&User_password=" + txt_pass.getText().toString() + "&User_Address=" + txt_add.getText().toString() + "&User_cno=" + txt_cno.getText().toString() + "";
                        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                //Toast.makeText(RegisterActivity.this, "Registration Save Successfully..." + response, Toast.LENGTH_LONG).show();
                                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(i);
                            }
                        },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(RegisterActivity.this, "error..." + error, Toast.LENGTH_LONG).show();
                                    }
                                });
                        RequestQueue requestQueue = Volley.newRequestQueue(RegisterActivity.this);
                        requestQueue.add(stringRequest);
            }
            else {
                Toast.makeText(RegisterActivity.this, "Registration Failed, You entered Details Is Already Exist ..",
                        Toast.LENGTH_SHORT).show();
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void storedetails(){
        user = txt_email.getText().toString();
        pass = txt_pass.getText().toString();

        firebaseAuth.createUserWithEmailAndPassword(user, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Sorry This User Already Exist..",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else {

                            final ProgressDialog pd = new ProgressDialog(RegisterActivity.this);
                            pd.setMessage("Loading...");
                            pd.show();

                            String url = "https://trackmark-2a9be.firebaseio.com/users.json";
                            final String newuser= user.replace(".", "-");
                            StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
                                @Override
                                public void onResponse(String s) {
                                    Firebase reference = new Firebase("https://trackmark-2a9be.firebaseio.com/users");

                                    if(s.equals("null")) {
                                        reference.child(newuser).child("password").setValue(pass);
                                        Toast.makeText(RegisterActivity.this, "Registration Save Successfully...", Toast.LENGTH_LONG).show();
                                    }
                                    else {
                                        try {
                                            JSONObject obj = new JSONObject(s);

                                            if (!obj.has(newuser)) {
                                                reference.child(newuser).child("password").setValue(pass);
                                                Toast.makeText(RegisterActivity.this, "New User Create Successfully...",
                                                        Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(RegisterActivity.this, "username already exists", Toast.LENGTH_LONG).show();
                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }



                                    pd.dismiss();
                                }

                            },new Response.ErrorListener(){
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {
                                    System.out.println("" + volleyError );
                                    pd.dismiss();
                                }
                            });
                            RequestQueue rQueue = Volley.newRequestQueue(RegisterActivity.this);
                            rQueue.add(request);



                        }

                    }
                });


    }

}
