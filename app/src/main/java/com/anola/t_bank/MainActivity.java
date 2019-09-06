package com.anola.t_bank;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.anola.t_bank.model.TokenModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {


    private Button login,regBtn;
    private EditText username,password;
   // private TextView text;

    private final OkHttpClient client = new OkHttpClient();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        login = findViewById(R.id.loginBtn);
        regBtn = findViewById(R.id.regBtn);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
       // text = findViewById(R.id.responseTxt);


        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent regPage = new Intent(MainActivity.this, Register.class);
                startActivity(regPage);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userText  = username.getText().toString();
                String passText  = password.getText().toString();
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("username", userText)
                        .addFormDataPart("password", passText)
                        .addFormDataPart("grant_type", "password")
                        .build();
                new Mybackground().execute(requestBody);
            }
        });

    }



    class Mybackground extends AsyncTask<RequestBody , Void, String>{

        String responseStr = "";
        @Override
        protected String doInBackground(RequestBody... voids) {

          RequestBody requestBody = voids[0];
            String credentials = "devglan-client" + ":" + "devglan-secret";
            String auth = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
            Request request = new Request.Builder()
                    .url("http://192.168.1.41:8084/oauth/token")
                    .header("Authorization", "Basic " + auth)
                    .post(requestBody)
                    .build();


            try{
//              if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                Response response = client.newCall(request).execute();

                //Log.e(Mybackground.class.getSimpleName(), "isSuccessful: " + response.isSuccessful());
                //Log.e(Mybackground.class.getSimpleName(), "body: " + response.body().string());
                responseStr = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseStr;
        }

        @Override
        protected void onPostExecute(String result) {
            //text.setText(result);
            TokenModel tokenInfo;
            try {
                JSONObject obj = new JSONObject(result);
                //System.out.println("Access Token : " + obj.get("access_token"));
                tokenInfo  = new TokenModel(obj.get("access_token").toString(),obj.get("refresh_token").toString());
                //System.out.println(tokenInfo.getAccess_token());
                Log.e(Mybackground.class.getSimpleName(), "My Info: " + tokenInfo.getAccess_token());


                Toast.makeText(MainActivity.this,"Login Success", Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                Toast.makeText(MainActivity.this,"Login Failed", Toast.LENGTH_LONG).show();
            }

                Log.e(Mybackground.class.getSimpleName(), "body: " + result);

        }
    }
}
