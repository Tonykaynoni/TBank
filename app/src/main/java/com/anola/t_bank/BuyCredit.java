package com.anola.t_bank;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BuyCredit extends AppCompatActivity {
    EditText amount,number;
    Button crdBtn;

    private final OkHttpClient client = new OkHttpClient();
    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_credit);
        preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);

        amount = findViewById(R.id.anountField);
        number = findViewById(R.id.anountField);
        crdBtn = findViewById(R.id.buyBtn);


        crdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amt = amount.getText().toString();
                String num = number.getText().toString();
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("phone", num)
                        .addFormDataPart("credit_amt", amt)
                        .build();
                new BuyCredit.Mybackground().execute(requestBody);
            }
        });
    }

    class Mybackground extends AsyncTask<RequestBody, Void, String> {

        String responseStr = "";
        @Override
        protected String doInBackground(RequestBody... voids) {
            String access_token = preferences.getString("a_token","");
            RequestBody requestBody = voids[0];
            Request request = new Request.Builder()
                    .url("http://192.168.1.31:8084/api/buycredit?access_token="+access_token)
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
            return responseStr;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.equals("Minimum_issue")){
                //Log.e(Mybackground.class.getSimpleName(), "isSuccessful: " + response.isSuccessful());
                //Log.e(Mybackground.class.getSimpleName(), "body 1: " + response.body().string());
                Toast.makeText(BuyCredit.this,"Operation Failed, You must add an amount greater than your minimum allowed amount",Toast.LENGTH_LONG).show();
            }else if(result.equals("Successful")){
                Toast.makeText(BuyCredit.this,"Operation Successful",Toast.LENGTH_LONG).show();
                amount.setText("0.0");
                number.setText("0");

            }
            Log.e(CreditAccount.Mybackground.class.getSimpleName(), "body: " + result);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.homenav:
                Intent homePage = new Intent(this,AllPagesList.class);
                startActivity(homePage);
                return true;
            case R.id.check_bal_nav:
                Intent checkPage = new Intent(this,AccountBalance.class);
                startActivity(checkPage);
                return true;
            case R.id.history_nav:
                Intent hist = new Intent(this,TransactionHistory.class);
                startActivity(hist);
                return true;
            case R.id.profile_nav:
                Intent editpro = new Intent(this,AllPagesList.class);
                startActivity(editpro);
                return true;
            case R.id.logout_nav:
                Intent logout = new Intent(this,MainActivity.class);
                logout.putExtra("logout",true);
                logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(logout);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
