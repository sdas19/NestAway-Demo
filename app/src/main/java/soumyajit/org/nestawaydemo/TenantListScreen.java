package soumyajit.org.nestawaydemo;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import soumyajit.org.nestawaydemo.Adapter.TenantListAdapter;
import soumyajit.org.nestawaydemo.JsonModel.TenantModel;
import soumyajit.org.nestawaydemo.Utility.Utility;

import static android.support.v4.app.ActivityCompat.requestPermissions;

public class TenantListScreen extends AppCompatActivity {

    public static final int MY_PERMISSION_WRITE_EXTERNAL_STORAGE=1;
    public static final String MY_PREFS_NAME = "MyPrefsFile";

    Boolean wroteToExternal=false;
                                    /*Arraylist of the model class to pass to the adapter*/
    ArrayList<TenantModel.Details> tenantModelList;
                                    /*This is used to create the text file and this is the conversion from the arraylist of model to string*/
    String tenantObjectString;
    private static String TAG=TenantListScreen.class.getSimpleName();
    TextView date;
    RecyclerView tenantRecyclerView;
    FloatingActionButton floatingActionButton;
    Gson gson;
    public static String actualDate;
    SwipeRefreshLayout swipeRefreshLayout;
    TenantListAdapter tenantListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_list_screen);
        gson=new Gson();
                            /*Used to track whether tenantlist file is created in external memory or not*/
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        wroteToExternal = prefs.getBoolean("writeToExternal", false);
        Log.e(TAG,"wrote "+wroteToExternal);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                Log.e(TAG,"inside rationalle");
                Utility.createDialogWrite(TenantListScreen.this);

            } else {

                Log.e(TAG,"inside requestpermission");

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSION_WRITE_EXTERNAL_STORAGE);

            }
        }



        date=(TextView) findViewById(R.id.date_textview);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        actualDate=formatter.format(new Date());
        date.setText(String.valueOf(actualDate));

        swipeRefreshLayout=(SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        tenantRecyclerView=(RecyclerView) findViewById(R.id.tenant_recyclerview);
        floatingActionButton=(FloatingActionButton) findViewById(R.id.fab);

        tenantModelList=new ArrayList<>();

                                /*Used to add new tenant*/
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(TenantListScreen.this,AddUpdateTenant.class);
                intent.putExtra("comingFromFAB",true);
                intent.putExtra("tenantList",tenantModelList);
                startActivity(intent);
            }
        });






        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });
                    /*If already file is created then it will read from the file and set up the adapter*/
        if(wroteToExternal==true)
        {
            startReadingFromExternalStorage();
        }



    }

                /*SwipeRefresh layout used to refresh the recyclerview to get the updated tenantlist and this method is called when user swipes*/
    private void refreshItems() {

        startReadingFromExternalStorage();
                                            /*New data will come up if the new entry is there or any previous data is updated*/
        tenantListAdapter.notifyDataSetChanged();
        onItemsLoadComplete();
    }

    private void onItemsLoadComplete() {
        // Update the adapter and notify data set changed
        // ...

        // Stop refresh animation
        swipeRefreshLayout.setRefreshing(false);
    }







    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

                        /*This permission is asked in this activity and required to create file in external storage*/
            case MY_PERMISSION_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.e(TAG, "permission granted");
                    Toast.makeText(this, "permission granted", Toast.LENGTH_LONG).show();
                    Log.e(TAG,"insideagain2");
                    createFileAndPutInExternalStorage();



                    // permission was granted, yay! Do the
                    // task you need to do.

                } else {

                    Log.e(TAG, "permission not granted");
                    Toast.makeText(this, "permission not granted", Toast.LENGTH_LONG).show();
                    Utility.createDialogWrite(TenantListScreen.this);
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
                                        /*This permission is asked from adapter and required to call the tenant*/
            case TenantListAdapter.MY_PERMISSION_CALL: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.e(TAG, "permission granted");


                    Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", TenantListAdapter.phoneNumberToCall, null));
                    startActivity(callIntent);

                } else {

                    Log.e(TAG, "permission not granted");
                    Toast.makeText(TenantListScreen.this, "permission not granted", Toast.LENGTH_LONG).show();
                    Utility.createDialogCall(TenantListScreen.this);
                }
                return;
            }
        }
    }
                        /*To create the file for the first time these are the datas passed to arraylist of model object*/
    private void createJsonData() {
        TenantModel.Details detail1=new TenantModel.Details("Vishnu Agrawal","vishnu@gmm.com","3049495940","10:30 am",actualDate,"Not Visited");
        TenantModel.Details detail2=new TenantModel.Details("Jhon K.","kjohn@gmmm.com","4049495940","11:30 am",actualDate,"Visited");
        TenantModel.Details detail3=new TenantModel.Details("Prateek Roy","prateekR@gmm.com","2149495940","12:00 pm",actualDate,"Pending");
        TenantModel.Details detail4=new TenantModel.Details("Shobhit Sharma","shobhitS@gmm.com","5549495940","04:00 pm",actualDate,"Cancelled");
        TenantModel.Details detail5=new TenantModel.Details("Pooja Prakesh","poojaP@gmail.com","6649495940","04:30 pm",actualDate,"Pending");
        tenantModelList.add(detail1);
        tenantModelList.add(detail2);
        tenantModelList.add(detail3);
        tenantModelList.add(detail4);
        tenantModelList.add(detail5);
        TenantModel tenantModel=new TenantModel(tenantModelList);
        tenantObjectString=gson.toJson(tenantModel);
        Log.e(TAG,tenantObjectString);
    }
                    /* Shared preference used to check whether file is alread created. If once created it wont create again. For the first time file will be created*/
    private void createFileAndPutInExternalStorage() {

        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean("writeToExternal", true);
        editor.apply();


        createJsonData();
        String state= Environment.getExternalStorageState();
        String filename="TenantList.txt";
        if(Environment.MEDIA_MOUNTED.equals(state))
        {
            File root=Environment.getExternalStorageDirectory();
            File dir=new File(root.getAbsolutePath()+"/NestAwayTenantList");
            if(!dir.exists())
            {
                dir.mkdir();
            }
            //Toast.makeText(this,"directory"+ dir.getAbsolutePath().toString(),Toast.LENGTH_LONG).show();
            Log.e("directory",dir.getAbsolutePath().toString());
            File tenant = new File(dir, filename);
            try
            {
                FileOutputStream fop=new FileOutputStream(tenant);
                fop.write(tenantObjectString.getBytes());
                fop.close();
            }catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            Toast.makeText(this,"No memory",Toast.LENGTH_LONG).show();
        }

        startReadingFromExternalStorage();
    }
                    /*Reading the file from external storage and creating the list of Details object to pass to the adapter*/
    private void startReadingFromExternalStorage() {
        Log.e(TAG,"inside");
        File root=Environment.getExternalStorageDirectory();
        File dir=new File(root.getAbsolutePath()+"/NestAwayTenantList");
        File file = new File(dir,"TenantList.txt");
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            Log.e(TAG,e.getLocalizedMessage().toString());
        }

        String tenantJsonString=text.toString();
        Log.e(TAG,tenantJsonString);
        tenantModelList.clear();

        try
        {
            JSONObject slot_booking= new JSONObject(tenantJsonString);
            JSONArray array=slot_booking.getJSONArray("slot_booking");
            Log.e(TAG,""+array.length());
            for(int i=0;i<array.length();i++)
            {
                JSONObject object=array.getJSONObject(i);
                TenantModel.Details details=new TenantModel.Details(object.getString("name"),object.getString("email"),object.getString("phone"),object.getString("time"),object.getString("date"),object.getString("visit_status"));
                tenantModelList.add(details);
            }
        }catch (JSONException e)
        {
            Log.e(TAG,e.toString());
        }

                                            /*Sorting of the datas beofore passing to adapter to show in recyclerview*/
        Collections.sort(tenantModelList, new Comparator<TenantModel.Details>() {

            @Override
            public int compare(TenantModel.Details o1, TenantModel.Details o2) {
                try {
                    Log.e(TAG,"sort");
                    return new SimpleDateFormat("hh:mm a").parse(o1.getTime()).compareTo(new SimpleDateFormat("hh:mm a").parse(o2.getTime()));
                } catch (ParseException e) {
                    return 0;
                }
            }
        });


        tenantListAdapter=new TenantListAdapter(this,tenantModelList);
        tenantRecyclerView.setAdapter(tenantListAdapter);
        tenantRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        tenantRecyclerView.setHasFixedSize(true);

    }
}
