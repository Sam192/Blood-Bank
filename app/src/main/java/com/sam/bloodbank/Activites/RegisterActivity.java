package com.sam.bloodbank.Activites;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.RegexValidator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.sam.bloodbank.R;
import com.sam.bloodbank.Utils.Endpoints;
import com.sam.bloodbank.Utils.VolleySingleton;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.ErrorListener;

public class RegisterActivity extends AppCompatActivity {
    private EditText mName, mCity, mContact, mBloodGroup, mPassword;
    private Button mSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name, city, blood_group, contact, password;

                name = mName.getText().toString();
                city = mCity.getText().toString();
                blood_group = mBloodGroup.getText().toString();
                contact = mContact.getText().toString();
                password = mPassword.getText().toString();

//
                if (isValid(name, city, blood_group, contact, password)) {
                    // if validation is successful then proceed
//                    showMessage(name + "\n" + city + "\n" + blood_group + "\n" + contact + "\n" + password);
                    showMessage(name + " Registered Successfully..!!!!");
                    register(name, city, blood_group, contact, password);

                }
            }
        });
    }

    //Register Data and redirect to MainActivity
    private void register(final String name, final String city, final String blood_group, final String contact, final String password) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Endpoints.register_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!response.equals("Invalid Credentials")) {
                    //open Main Activity
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit()
                            .putString("city", city).apply();
                    Toast.makeText(RegisterActivity.this, response, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    RegisterActivity.this.finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "something miss :" + response, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RegisterActivity.this, "Something Went Wrong :(", Toast.LENGTH_SHORT).show();
                Log.d("VOLLEY", error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("city", city);
                params.put("blood_group", blood_group);
                params.put("contact", contact);
                params.put("password", password);
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    //Verify Data before inserting into DB
    private boolean isValid(String name, String city, String blood_group, String contact, String password) {
        List<String> valid_blood_group = new ArrayList<>();
        valid_blood_group.add("A+");
        valid_blood_group.add("B+");
        valid_blood_group.add("AB+");
        valid_blood_group.add("A-");
        valid_blood_group.add("B-");
        valid_blood_group.add("AB-");
        valid_blood_group.add("O+");
        valid_blood_group.add("O-");

        if (name.isEmpty()) {
            showMessage("Name is required");
            return false;
        } else if (city.isEmpty()) {
            showMessage("City name is required");
            return false;
        } else if (!valid_blood_group.contains(blood_group)) {
            showMessage("Blood Group is invalid, Choose from these :\n" + valid_blood_group);
            return false;
        } else if (contact.length() != 10) {
            showMessage("Invalid mobile number, number should be of 10 digits");
            return false;
        } else if (password.length() < 4) {
            showMessage("Password character must be greater than 4 digit");
            return false;
        }

        return true;
    }

    private void initViews() {
        mName = findViewById(R.id.etxtName);
        mCity = findViewById(R.id.etxtCity);
        mContact = findViewById(R.id.etxtContact);
        mBloodGroup = findViewById(R.id.etxtBloodGroup);
        mPassword = findViewById(R.id.etxtPassword);
        mSubmit = findViewById(R.id.btnSubmit);
    }

    private void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}


