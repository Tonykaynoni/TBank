package com.anola.t_bank;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.anola.t_bank.model.TokenModel;

public class AllPagesList extends AppCompatActivity{

    String pages[] = {"Credit Account","Withdraw","Check Balance","Take Loan","Buy Credit","Pay Bills","Transaction History"};
    ListView pageList;
    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_pages_list);
        preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        pageList = findViewById(R.id.myList);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, pages);
        pageList.setAdapter(adapter);

         pageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             @Override
             public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                 if(position == 0){
                     Intent creditPage = new Intent(AllPagesList.this,CreditAccount.class);
                     startActivity(creditPage);
                 }else if(position == 1){
                     Intent withdrawPage = new Intent(AllPagesList.this,Withdraw.class);
                     startActivity(withdrawPage);
                 }else if(position == 2){
                     Intent acctBalPage = new Intent(AllPagesList.this,AccountBalance.class);
                     startActivity(acctBalPage);
                 }else if(position == 3){
                     Intent takeLoanPage = new Intent(AllPagesList.this,TakeLoan.class);
                     startActivity(takeLoanPage);
                 }else if(position == 4){
                     Intent buyLoanPage = new Intent(AllPagesList.this,BuyCredit.class);
                     startActivity(buyLoanPage);
                 }else if(position == 6){
                     Intent tranSac = new Intent(AllPagesList.this,TransactionHistory.class);
                     startActivity(tranSac);
                 }else if(position == 5){
                     Intent payB = new Intent(AllPagesList.this,PayBills.class);
                     startActivity(payB);
                 }else{
                     TokenModel tinfo = new TokenModel();
                     String token = preferences.getString("a_token","");
                     //String token  = tinfo.getAccess_token();
                     Toast.makeText(AllPagesList.this,"Page Not Available",Toast.LENGTH_SHORT).show();
                 }
             }
         });




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
