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
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AccountBalance extends AppCompatActivity {
    TextView balance;

    private final OkHttpClient client = new OkHttpClient();
    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_balance);
        balance = findViewById(R.id.acct_info);
        preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        String access_token = preferences.getString("a_token","");
        new AccountBalance.Mybackground().execute("http://192.168.1.31:8084/api/check_balance?access_token="+access_token);
    }

    class Mybackground extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... voids) {

            Request.Builder builder = new Request.Builder();
            builder.url(voids[0]);
            Request request = builder.build();

            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            try{
            if(Integer.parseInt(result) >= 0){

                balance.setText(result);

            }else {
                Toast.makeText(AccountBalance.this, "Unknown Error Occurred", Toast.LENGTH_LONG).show();
                Log.e(CreditAccount.Mybackground.class.getSimpleName(), "body: " + result);
            }

            }catch (Exception e){
                Toast.makeText(AccountBalance.this, "Unknown Error Occurred", Toast.LENGTH_LONG).show();
                Log.e(CreditAccount.Mybackground.class.getSimpleName(), "body: " + result);
            }
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
