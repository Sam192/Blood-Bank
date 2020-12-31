package com.sam.bloodbank.Activites;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.sam.bloodbank.R;
import com.sam.bloodbank.Utils.Endpoints;
import com.sam.bloodbank.Utils.VolleySingleton;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private EditText mUserNumber, mPassword;
    private TextView mSignUp;
    private Button mLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();

        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Redirect to Register page
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserNumber.setError(null);
                mPassword.setError(null);
                String number =mUserNumber.getText().toString();
                String password = mPassword.getText().toString();

                if (isValid(number, password)){
                    login(number, password);
                }

            }
        });

    }

    private void login(final String number, final String password){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Endpoints.login_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!response.equals("Invalid Credentials")) {
                    //open Main Activity
                    Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit()
                            .putString("number", number).apply();
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit()
                            .putString("city", response).apply();
                    LoginActivity.this.finish();
                } else {
                    Toast.makeText(LoginActivity.this, " " + response, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginActivity.this, "Something Went Wrong :(", Toast.LENGTH_SHORT).show();
                Log.d("VOLLEY", error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("password", password);
                params.put("number", number);
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);


    }

    //Check Validation
    private boolean isValid(String number, String password){
        if (number.isEmpty() || number.length() != 10){
            showMessage("Please Enter a valid Mobile Number");
            mUserNumber.setError("Enter valid Mobile Number");
            return false;
        }else if (password.isEmpty()){
            showMessage("Please Enter Password");
            mPassword.setError("Empty Password");
            return false;
        }

        return true;
    }


    private void showMessage(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }


    private void initViews(){
        mUserNumber = findViewById(R.id.etxtUserNumber);
        mPassword = findViewById(R.id.etxtPwd);
        mLogin = findViewById(R.id.btnLogin);
        mSignUp = findViewById(R.id.txtSignUp);
    }
}
