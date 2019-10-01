package com.example.registrationapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    EditText nameText,emailText,userNameText,passwordText,confermPasswordText;
    Button registerBtn;
    AlertDialog.Builder builder;
    String code ="";
    String url = "http://10.123.60.94/registration/registration.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameText = findViewById(R.id.name_box);
        emailText = findViewById(R.id.email_box);
        userNameText = findViewById(R.id.user_name_box);
        passwordText = findViewById(R.id.password_box);
        confermPasswordText = findViewById(R.id.conferm_password_box);
        registerBtn = findViewById(R.id.register_btn);
        builder = new AlertDialog.Builder(Register.this);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = nameText.getText().toString();
                final String email = emailText.getText().toString();
                final String user_name = userNameText.getText().toString();
                final String password = passwordText.getText().toString();
                final String confermPassword = confermPasswordText.getText().toString();
                if(name.equals("")||email.equals("")||user_name.equals("")||password.equals("")){
                    builder.setTitle("Note:");
                    builder.setMessage("Please fill all fields!");
                    code ="empty_fields";
                    displayDialog(code);
                }else{
                    if(!(password.equals(confermPassword))){
                        builder.setTitle("Alert!");
                        builder.setMessage("Password not match");
                        code ="not_match_password";
                        displayDialog(code);
                    }else{
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONArray jsonArray = new JSONArray(response);
                                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                                            String code = jsonObject.getString("code");
                                            String message = jsonObject.getString("message");
                                            builder.setTitle("Server Response:");
                                            builder.setMessage(message);
                                            displayDialog(code);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(Register.this,"Server Error",Toast.LENGTH_SHORT).show();
                                error.printStackTrace();
                            }
                        }){
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String,String> params = new HashMap<String,String>();
                                params.put("name",name);
                                params.put("email",email);
                                params.put("user_name",user_name);
                                params.put("password",password);
                                return params;
                            }
                        };
                        MySingleton.getInstance(Register.this).addRequestQueue(stringRequest);
                    }
                }
            }
        });
    }
    public void displayDialog(final String code){
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(code.equals("empty_fields")){
                    nameText.setText("");
                    emailText.setText("");
                    userNameText.setText("");
                    passwordText.setText("");
                }else if(code.equals("not_match_password")){
                    passwordText.setText("");
                    confermPasswordText.setText("");
                }else if(code.equals("reg_failed")){
                    nameText.setText("");
                    emailText.setText("");
                    userNameText.setText("");
                    passwordText.setText("");
                    confermPasswordText.setText("");
                }else if(code.equals("reg_success")){
                    finish();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
