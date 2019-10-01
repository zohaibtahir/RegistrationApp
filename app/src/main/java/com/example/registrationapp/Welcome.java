package com.example.registrationapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Welcome extends AppCompatActivity {
    TextView getName,getEmail;
    ImageView profileImageView;
    EditText imageName;
    Button imageChooseBtn,doneBtn;
    private int IMAGE_REQUEST_CODE = 1;
    private Bitmap bitmap;
    String url ="http://10.123.60.94/registration/image_upload.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        getName = findViewById(R.id.get_name_txt);
        getEmail = findViewById(R.id.get_email_txt);
        profileImageView = findViewById(R.id.profile_img_box);
        imageChooseBtn = findViewById(R.id.choose_img_btn);
        imageName = findViewById(R.id.image_name_txt);
        doneBtn = findViewById(R.id.upload_btn);

        Bundle bundle = getIntent().getExtras();
        getName.setText(bundle.getString("name"));
        getEmail.setText(bundle.getString("email"));

        imageChooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFromGallery();
            }
        });
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(profileImageView.equals(null)){

                }
                    uploadImage();
            }
        });
    }

    public void selectFromGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==IMAGE_REQUEST_CODE && resultCode==RESULT_OK && data!=null){
            Uri path = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),path);
                profileImageView.setImageBitmap(bitmap);
                profileImageView.setVisibility(View.VISIBLE);
                imageName.setVisibility(View.VISIBLE);
                doneBtn.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void uploadImage(){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String res = jsonObject.getString("response");
                            Toast.makeText(Welcome.this,res,Toast.LENGTH_SHORT).show();
                            profileImageView.setVisibility(View.GONE);
                            imageName.setVisibility(View.GONE);
                            doneBtn.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Welcome.this, "Server Error!", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("image_name",imageName.getText().toString());
                params.put("image",imageToString(bitmap));
                return params;
            }
        };
        MySingleton.getInstance(Welcome.this).addRequestQueue(stringRequest);

    }
    public String imageToString(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] imageByte = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imageByte,Base64.DEFAULT);
    }
}
