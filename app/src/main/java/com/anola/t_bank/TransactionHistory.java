package com.anola.t_bank;

import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.LruCache;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.anola.t_bank.adapter.TransactionAdapter;
import com.anola.t_bank.model.TransactionModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TransactionHistory extends AppCompatActivity {
    private final OkHttpClient client = new OkHttpClient();
    private SharedPreferences preferences;
    private TransactionAdapter adapter;
    private ArrayList<TransactionModel> myListData = new ArrayList<>();
    private Button downloadPdf,searchBtn;
    private Bitmap bitmap;
    public EditText fromdate,todate;
    private RecyclerView recyclerView;
    public String access_token;
    Context context;
    DatePickerDialog datePickerDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        downloadPdf = findViewById(R.id.downloadBtnPdf);

        searchBtn = findViewById(R.id.searchBtn);
        recyclerView = findViewById(R.id.myRecyclerList);
        adapter = new TransactionAdapter(myListData);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        access_token = preferences.getString("a_token","");
        new TransactionHistory.Mybackground().execute("http://192.168.1.31:8084/api/account_history?access_token="+access_token);


        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(TransactionHistory.this);
                dialog.setContentView(R.layout.serach_dialog);
                Button dialogButton = (Button) dialog.findViewById(R.id.myDialogSearch);
                fromdate = dialog.findViewById(R.id.fromDate);
                todate = dialog.findViewById(R.id.toDate);
                fromdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // calender class's instance and get current date , month and year from calender
                        //datePickerDialog;
                        final Calendar c = Calendar.getInstance();
                        int mYear = c.get(Calendar.YEAR); // current year
                        int mMonth = c.get(Calendar.MONTH); // current month
                        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                        // date picker dialog
                        datePickerDialog = new DatePickerDialog(TransactionHistory.this,
                                new DatePickerDialog.OnDateSetListener() {

                                    @Override
                                    public void onDateSet(DatePicker view, int year,
                                                          int monthOfYear, int dayOfMonth) {
                                        // set day of month , month and year value in the edit text
                                        fromdate.setText(year + "-"
                                                + (monthOfYear + 1) + "-" + dayOfMonth);

                                    }
                                }, mYear, mMonth, mDay);
                        datePickerDialog.show();
                    }
                });

                todate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // calender class's instance and get current date , month and year from calender
                        //datePickerDialog;
                        final Calendar c = Calendar.getInstance();
                        int mYear = c.get(Calendar.YEAR); // current year
                        int mMonth = c.get(Calendar.MONTH); // current month
                        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                        // date picker dialog
                        datePickerDialog = new DatePickerDialog(TransactionHistory.this,
                                new DatePickerDialog.OnDateSetListener() {

                                    @Override
                                    public void onDateSet(DatePicker view, int year,
                                                          int monthOfYear, int dayOfMonth) {
                                        // set day of month , month and year value in the edit text
                                        todate.setText(year + "-"
                                                + (monthOfYear + 1) + "-" + dayOfMonth);


                                    }
                                }, mYear, mMonth, mDay);
                        datePickerDialog.show();
                    }
                });


                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newfrom = fromdate.getText().toString();
                        String newto = todate.getText().toString();

                        new TransactionHistory.Mybackground().execute(newfrom,newto);
                        //new TransactionHistory.Mybackground2().execute("http://192.168.1.44:8084/api/account_history/"+newfrom+"/"+newto+"/?access_token="+access_token);
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Dismissed..!!",Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.show();
            }
        });
        downloadPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPdf();
               // Toast.makeText(getApplicationContext(),"Pdf Created Successfully",Toast.LENGTH_SHORT).show();


            }
        });

    }


    class Mybackground extends AsyncTask<String, Void, String> {


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
                myListData.clear();
                String access_token = preferences.getString("a_token","");
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("fromdate", voids[0])
                        .addFormDataPart("todate", voids[1])
                        .build();
                Request request = new Request.Builder()
                        .url("http://192.168.1.31:8084/api/search_bydate?access_token="+access_token)
                        .post(requestBody)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    return response.body().string();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            return null;

        }

        @Override
        protected void onPostExecute(String result) {

            try{
                Log.e(TransactionHistory.Mybackground.class.getSimpleName(), "My Error: " + result);
                JSONArray infos = new JSONArray(result);
                Log.e(TransactionHistory.Mybackground.class.getSimpleName(), "My Error: " + infos);
                  for(int a=0; a<= infos.length() - 1; a++){
                      JSONObject myinf =  infos.getJSONObject(a);
                      TransactionModel m = new TransactionModel(myinf.get("transTime").toString(),myinf.get("transaction_type").toString(),myinf.get("amount").toString());
                      //Log.e(TransactionHistory.Mybackground.class.getSimpleName(), "Each List: " + m.toString());
                      myListData.add(m);
                      adapter.notifyDataSetChanged();

                  }




                Log.e(TransactionHistory.Mybackground.class.getSimpleName(), "My List: " + myListData.toString());
                //String token = preferences.getString("token","");


                }catch (JSONException e) {
                Log.e(MainActivity.Mybackground.class.getSimpleName(), "My Info: " + e.getMessage());
                Toast.makeText(TransactionHistory.this,"Error Occurred Failed", Toast.LENGTH_LONG).show();
            }

        }
    }

    public static Bitmap loadBitmapFromView(View v, int width, int height) {
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);
        //saveImage(bm);
        return b;
    }

    private void createPdf(){
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        //  Display display = wm.getDefaultDisplay();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float hight = displaymetrics.heightPixels ;
        float width = displaymetrics.widthPixels ;

        int convertHighet = (int) hight, convertWidth = (int) width;

//        Resources mResources = getResources();
//        Bitmap bitmap = BitmapFactory.decodeResource(mResources, R.drawable.screenshot);

        PdfDocument document = new PdfDocument();

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHighet, 3).create();
        PdfDocument.Page page = document.startPage(pageInfo);

         Canvas canvas = page.getCanvas();
//
        //Paint paint = new Paint();
          //canvas.drawPaint(paint);
//
//        bitmap = Bitmap.createScaledBitmap(bitmap, convertWidth, convertHighet, true);
//
//        paint.setColor(Color.BLUE);
        canvas.drawBitmap(getScreenshotFromRecyclerView(recyclerView), 0, 0 , null);
        //canvas.drawBitmap(getScreenshotFromRecyclerView(recyclerView), 0, 0 , null);
//

        getScreenshotFromRecyclerView(recyclerView);
        document.finishPage(page);
        // write the document content
        final String APPLICATION_PACKAGE_NAME = TransactionHistory.class.getPackage().getName();
        File path = new File( Environment.getExternalStorageDirectory() + "/pdffromScroll.pdf");

        //File path = new File( "/sdcard/pdffromScroll.pdf");
        //if ( !path.exists() ){ path.mkdir(); }
        //File filePath = new File(path, "pdffromScroll.pdf");



        try {
            document.writeTo(new FileOutputStream(path));

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
        }

        // close the document
        document.close();
        Toast.makeText(this, "PDF of Scroll is created!!!", Toast.LENGTH_SHORT).show();

        //openGeneratedPDF(path);

    }

    private void openGeneratedPDF(File file){
        //File file = new File("/sdcard/pdffromScroll.pdf");
        if (file.exists())
        {
            Intent intent=new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.fromFile(file);
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            try
            {
                startActivity(intent);
            }
            catch(ActivityNotFoundException e)
            {
                Toast.makeText(TransactionHistory.this, "No Application available to view pdf", Toast.LENGTH_LONG).show();
            }
        }
    }

    public  boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("info","Permission is granted1");
                return true;
            } else {

                Log.v("info","Permission is revoked1");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("info","Permission is granted1");
            return true;
        }
    }

    public  boolean isWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("info","Permission is granted2");
                return true;
            } else {

                Log.v("info","Permission is revoked2");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("info","Permission is granted2");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 2:
                Log.d("info", "External storage2");
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Log.v("info","Permission: "+permissions[0]+ "was "+grantResults[0]);
                    //resume tasks needing this permission
                   createPdf();
                }else{
                    //progress.dismiss();
                }
                break;

            case 3:
                Log.d("info", "External storage1");
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Log.v("info","Permission: "+permissions[0]+ "was "+grantResults[0]);
                    //resume tasks needing this permission
                    createPdf();
                }else{
                   // progress.dismiss();
                }
                break;
        }
    }

    public Bitmap getScreenshotFromRecyclerView(RecyclerView view) {
        RecyclerView.Adapter adapter = view.getAdapter();
        Bitmap bigBitmap = null;
        if (adapter != null) {
            int size = adapter.getItemCount();
            int height = 0;
            Paint paint = new Paint();
            int iHeight = 0;
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

            // Use 1/8th of the available memory for this memory cache.
            final int cacheSize = maxMemory / 8;
            LruCache<String, Bitmap> bitmaCache = new LruCache<>(cacheSize);
            for (int i = 0; i < size; i++) {
                RecyclerView.ViewHolder holder = adapter.createViewHolder(view, adapter.getItemViewType(i));
                adapter.onBindViewHolder(holder, i);
                holder.itemView.measure(View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                holder.itemView.layout(0, 0, holder.itemView.getMeasuredWidth(), holder.itemView.getMeasuredHeight());
                holder.itemView.setDrawingCacheEnabled(true);
                holder.itemView.buildDrawingCache();
                Bitmap drawingCache = holder.itemView.getDrawingCache();
                if (drawingCache != null) {

                    bitmaCache.put(String.valueOf(i), drawingCache);
                }
//                holder.itemView.setDrawingCacheEnabled(false);
//                holder.itemView.destroyDrawingCache();
                height += holder.itemView.getMeasuredHeight();
            }

            bigBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), height, Bitmap.Config.ARGB_8888);
            Canvas bigCanvas = new Canvas(bigBitmap);
            bigCanvas.drawColor(Color.WHITE);

            for (int i = 0; i < size; i++) {
                Bitmap bitmap = bitmaCache.get(String.valueOf(i));
                bigCanvas.drawBitmap(bitmap, 0f, iHeight, paint);
                iHeight += bitmap.getHeight();
                bitmap.recycle();
            }

        }
        return bigBitmap;
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
