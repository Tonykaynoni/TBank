package com.anola.t_bank;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.anola.t_bank.R;
import com.anola.t_bank.model.TokenModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Register extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Button regBtn,loginBtn;
    EditText username,password,dob,address,fullname;
    Spinner account_type;
    String myUsername,myPassword,myDob,myAddress,fullName,myAccount_type;
    private final OkHttpClient client = new OkHttpClient();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username =findViewById(R.id.username);
        password = findViewById(R.id.password);
        dob = findViewById(R.id.dob);
        address = findViewById(R.id.address);
        fullname = findViewById(R.id.fullname);
        account_type = findViewById(R.id.acct_type);
        loginBtn = findViewById(R.id.loginBtn);
        regBtn = findViewById(R.id.regBtn);

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                myUsername = username.getText().toString();
                myPassword = password.getText().toString();
                myDob = dob.getText().toString();
                myAddress = address.getText().toString();
                fullName = fullname.getText().toString();


                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("username", myUsername)
                        .addFormDataPart("password", myPassword)
                        .addFormDataPart("dob", myDob)
                        .addFormDataPart("address", myAddress)
                        .addFormDataPart("fullname", fullName)
                        .addFormDataPart("account_type", "savings")
                        .build();
                new Register.Mybackground().execute(requestBody);

            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent regPage = new Intent(Register.this, MainActivity.class);
                startActivity(regPage);
            }
        });
    }

    class Mybackground extends AsyncTask<RequestBody, Void, String> {

        String responseStr = "";
        @Override
        protected String doInBackground(RequestBody... voids) {

            RequestBody requestBody = voids[0];
            String credentials = "devglan-client" + ":" + "devglan-secret";
            String auth = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
            Request request = new Request.Builder()
                    .url("http://192.168.1.41:8084/apiregister")
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
            //TokenModel tokenInfo;
            try {
                JSONObject obj = new JSONObject(result);
                //System.out.println("Access Token : " + obj.get("access_token"));

                 // String resp = obj.get("success").toString();
                  if(obj.get("response").equals("success")){
                      Toast.makeText(Register.this,"Registration Successful", Toast.LENGTH_LONG).show();
                  }else{
                      Toast.makeText(Register.this,"Registration Failed : " + obj.get("response").toString(), Toast.LENGTH_LONG).show();
                  }
                  //System.out.println(resp);
                // Log.e(Register.Mybackground.class.getSimpleName(), "My Info: " + resp);

            } catch (JSONException e) {
                //Toast.makeText(Register.this,"Registration Failed : Error On Form" + result, Toast.LENGTH_LONG).show();

                Toast.makeText(Register.this,"Registration Failed", Toast.LENGTH_LONG).show();
            }

            Log.e(MainActivity.Mybackground.class.getSimpleName(), "body: " + result);

        }
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        myAccount_type = (String) parent.getItemAtPosition(pos);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
