package com.anola.t_bank;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
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

    private SharedPreferences preferences;
   // private TextView text;

    private final OkHttpClient client = new OkHttpClient();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkLogout();
       // String info = getIntent().getBooleanExtra("logout",false);
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
                new Mybackground().execute(userText,passText);
            }
        });

    }

    private boolean checkLogout(){
        if(getIntent().getBooleanExtra("logout",false)){
            preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
            String access_token = preferences.getString("a_token","");
            new Mybackground().execute("http://192.168.1.31:8084/api/user-logout?access_token="+access_token);
            return true;
        }
        return false;
    }

    class Mybackground extends AsyncTask<String , Void, String>{

        String responseStr = "";
        @Override
        protected String doInBackground(String... voids) {


            if(voids.length == 1) {
                Request.Builder builder = new Request.Builder();
                builder.url(voids[0]);
                Request request = builder.build();

                try {
                    Response response = client.newCall(request).execute();
                    return response.body().string();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
               // myListData.clear();
                String credentials = "devglan-client" + ":" + "devglan-secret";
                String auth = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("username", voids[0])
                        .addFormDataPart("password", voids[1])
                        .addFormDataPart("grant_type", "password")
                        .build();
                Request request = new Request.Builder()
                        .url("http://192.168.1.31:8084/oauth/token")
                        .header("Authorization", "Basic " + auth)
                        .post(requestBody)
                        .build();
                try{
                    //if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    Response response = client.newCall(request).execute();

                    //Log.e(Mybackground.class.getSimpleName(), "isSuccessful: " + response.isSuccessful());
                    //Log.e(Mybackground.class.getSimpleName(), "body: " + response.body().string());
                    responseStr = response.body().string();
                }catch (IOException e) {
                    e.printStackTrace();
                }

            }
            return responseStr;
        }

        @Override
        protected void onPostExecute(String result) {
            //text.setText(result);
            TokenModel tokenInfo;

                if(result.equals("logout_processed")){
                    Toast.makeText(MainActivity.this,"Logout Successful",Toast.LENGTH_SHORT).show();

                }else{
                            try{
                                JSONObject obj = new JSONObject(result);
                                //System.out.println("Access Token : " + obj.get("access_token"));
                                tokenInfo = new TokenModel(obj.get("access_token").toString(), obj.get("refresh_token").toString());
                                SharedPreferences preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
                                preferences.edit().putString("a_token", obj.get("access_token").toString()).commit();
                                preferences.edit().putString("r_token", obj.get("refresh_token").toString()).commit();
                                //System.out.println(tokenInfo.getAccess_token());
                                Log.e(Mybackground.class.getSimpleName(), "My Info: " + tokenInfo.getAccess_token());
                                //String token = preferences.getString("token","");

                                Toast.makeText(MainActivity.this, "Login Success", Toast.LENGTH_LONG).show();
                                Intent listPage = new Intent(MainActivity.this, AllPagesList.class);
                                startActivity(listPage);
                            }catch (JSONException e) {
                                Toast.makeText(MainActivity.this,"Login Failed", Toast.LENGTH_LONG).show();
                            }
                }


                Log.e(Mybackground.class.getSimpleName(), "body: " + result);

        }
    }
}
