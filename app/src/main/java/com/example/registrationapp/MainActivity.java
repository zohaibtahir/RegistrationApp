package com.example.registrationapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    EditText userName,userPassword;
    Button loginBtn;
    TextView registerText;
    AlertDialog.Builder builder;
    String url ="http://10.123.60.94/registration/login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userName = findViewById(R.id.user_name_txt);
        userPassword = findViewById(R.id.password_txt);
        registerText = findViewById(R.id.register_txt);
        loginBtn = findViewById(R.id.login_btn);
        builder = new AlertDialog.Builder(MainActivity.this);

        registerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,Register.class));
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String getUserName = userName.getText().toString();
                final String getPassword = userPassword.getText().toString();
                if(getUserName.equals("")||getPassword.equals("")){
                    builder.setTitle("Alert!");
                    builder.setMessage("Empty Fields");
                    String code = "empty";
                    displayDialog(code);
                }else{
                    final StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONArray jsonArray = new JSONArray(response);
                                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                                        String code = jsonObject.getString("code");
                                        if(code.equals("failed")){
                                            builder.setTitle("Alert!");
                                            builder.setMessage(jsonObject.getString("message"));
                                            displayDialog(code);
                                        }else{
                                            Intent intent = new Intent(MainActivity.this,Welcome.class);
                                            Bundle bundle = new Bundle();
                                            bundle.putString("name",jsonObject.getString("name"));
                                            bundle.putString("email",jsonObject.getString("email"));
                                            intent.putExtras(bundle);
                                            startActivity(intent);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(MainActivity.this,"Server Error!",Toast.LENGTH_SHORT).show();
                            error.printStackTrace();
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String,String> params = new HashMap<>();
                            params.put("user_name",getUserName);
                            params.put("password",getPassword);
                            return params;
                        }
                    };
                    MySingleton.getInstance(MainActivity.this).addRequestQueue(stringRequest);
                }
            }
        });
    }

    private void displayDialog(final String code) {
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(code.equals("empty")){
                    userName.setText("");
                    userPassword.setText("");
                }else if(code.equals("failed")){
                    userName.setText("");
                    userPassword.setText("");
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
