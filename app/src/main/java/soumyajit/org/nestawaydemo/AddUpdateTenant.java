package soumyajit.org.nestawaydemo;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import soumyajit.org.nestawaydemo.JsonModel.TenantModel;

public class AddUpdateTenant extends AppCompatActivity implements View.OnClickListener{

    ImageView edit;
    EditText name;
    EditText phoneNo;
    EditText emailId;
    EditText time;
    Spinner timeSpinner;
    Spinner visitStatusSpinner;
    EditText visitStatus;
    Button save;
    Button sync;
    String[] timeSlots={"- Select One -","10:00 am","10:30 am","11:00 am","11:30 am","12:00 pm","12:30 pm","01:00 pm","01:30 pm","02:00 pm","02:30 pm","03:00 pm","03:30 pm","04:00 pm","04:30 pm","05:00 pm","05:30 pm","06:00 pm","06:30 pm","07:00 pm","07:30 pm","08:00 pm"};
    String[] visitStatusArray={"- Select One -","Not Visited","Visited","Pending","Cancelled"};
    ArrayList<TenantModel.Details> tenantList;
    Gson gson;
    Boolean comingFromFAB=false;

    private static String TAG=AddUpdateTenant.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_update_tenant);
        edit=(ImageView) findViewById(R.id.edit);
        name=(EditText) findViewById(R.id.name);
        phoneNo=(EditText) findViewById(R.id.phone);
        emailId=(EditText) findViewById(R.id.email);
        time=(EditText) findViewById(R.id.time);
        visitStatus=(EditText) findViewById(R.id.visit_status);
        visitStatusSpinner=(Spinner) findViewById(R.id.visit_status_spinner);
        visitStatusSpinner.setEnabled(false);
        timeSpinner=(Spinner) findViewById(R.id.time_spinner);
        timeSpinner.setEnabled(false);
        save=(Button) findViewById(R.id.save_button);
        sync=(Button) findViewById(R.id.sync_button);
        gson=new Gson();
        tenantList=new ArrayList<>();

        Intent pastIntent=getIntent();
                                /*The previous arraylist is passed here to update from this activity*/
        tenantList= (ArrayList<TenantModel.Details>) pastIntent.getSerializableExtra("tenantList");
        Log.e(TAG,tenantList.toString());

        Bundle pastData=pastIntent.getBundleExtra("data");
                                        /*This will keep track whether it is new entry or update because from FAB its new entry*/
        comingFromFAB=pastIntent.getBooleanExtra("comingFromFAB",false);

        if(pastData!=null)
        {
            setDataInFields(pastData);
        }

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //name.setEnabled(true);
                phoneNo.setEnabled(true);
                emailId.setEnabled(true);
                time.setEnabled(true);
                timeSpinner.setClickable(true);
                timeSpinner.setEnabled(true);
                visitStatusSpinner.setEnabled(true);
                visitStatusSpinner.setClickable(true);
                save.setEnabled(true);
                visitStatus.setEnabled(true);
                if(comingFromFAB)
                {
                    name.setEnabled(true);
                }
            }
        });


        save.setOnClickListener(this);
        sync.setOnClickListener(this);


        ArrayAdapter<String> visitStatusAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,visitStatusArray);
        visitStatusSpinner.setAdapter(visitStatusAdapter);

        visitStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=0)
                {
                    visitStatus.setText(parent.getSelectedItem().toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,
                        timeSlots){

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View mView=super.getDropDownView(position, convertView, parent);

                        /*Disabling the timeslots which are already taken*/

                final TextView mTextView = (TextView) mView;
                Boolean alreadySelected=false;
                for(TenantModel.Details details :tenantList)
                {
                    String json=gson.toJson(details);
                    String timeCompare;
                    try
                    {
                        JSONObject jsonObject=new JSONObject(json);
                        timeCompare=jsonObject.getString("time");

                        if (timeCompare.equals(mTextView.getText().toString()))
                        {
                            mTextView.setEnabled(false);
                            mTextView.setClickable(false);
                            alreadySelected=true;
                            break;
                        }

                    }catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
                if(alreadySelected==false)
                {
                    mTextView.setEnabled(true);
                }
                return mView;
            }
        };

        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        timeSpinner.setAdapter(spinnerArrayAdapter);
        timeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=0)
                {
                    time.setText(parent.getSelectedItem().toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
                    /*In case of update Data coming from last activity is placed in consecutive fields*/

    private void setDataInFields(Bundle pastData) {
        name.setText(pastData.getString("name","Name"));
        phoneNo.setText(pastData.getString("phone","Phone No"));
        emailId.setText(pastData.getString("email","Email Id"));
        time.setText(pastData.getString("time","Time"));
        visitStatus.setText(pastData.getString("visit_status"));

    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.save_button:
                if(emailId.getText().toString().contains("@")&&emailId.getText().toString().contains(".com")&&phoneNo.getText().toString().length()==10)
                {
                    sync.setEnabled(true);
                    Toast.makeText(AddUpdateTenant.this,"Please sync to get the updated list of tenants",Toast.LENGTH_LONG).show();
                }
                else
                {
                    if(!emailId.getText().toString().contains("@")||!emailId.getText().toString().contains(".com"))
                    {
                        Toast.makeText(AddUpdateTenant.this,"Please make sure you provide valid email id",Toast.LENGTH_LONG).show();

                    }
                    else if(phoneNo.getText().toString().length()!=10)
                    {
                        Toast.makeText(AddUpdateTenant.this,"Please make sure you provide valid phone no",Toast.LENGTH_LONG).show();

                    }
                }

            case R.id.sync_button:
                startSyncTask();
        }
    }



                /*If tenant details are already in the list then last detail is deleted and new entry is inserted in the list*/
    private void startSyncTask() {

        String nameToCheck= name.getText().toString();
        Boolean alreadyInList=false;
        for(TenantModel.Details details :tenantList)
        {
            String json=gson.toJson(details);
            try
            {
                JSONObject jsonObject=new JSONObject(json);

                if (jsonObject.getString("name").equals(nameToCheck))
                {
                    alreadyInList=true;
                    tenantList.remove(details);
                    tenantList.add(new TenantModel.Details(name.getText().toString(),emailId.getText().toString(),phoneNo.getText().toString(),time.getText().toString(),TenantListScreen.actualDate,visitStatus.getText().toString()));
                    break;
                }

            }catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        if(alreadyInList==false)
        {
            tenantList.add(new TenantModel.Details(name.getText().toString(),emailId.getText().toString(),phoneNo.getText().toString(),time.getText().toString(),TenantListScreen.actualDate,visitStatus.getText().toString()));

        }
        updateTextFileInExternalMemory(tenantList);
    }
                /*Deleting the previous file from external memory and creating new file in the external memory
                * now to see the change swipe the recyclerview in the last activity*/
    private void updateTextFileInExternalMemory(ArrayList<TenantModel.Details> tenantList) {

        deletePreviousFile();

        TenantModel tenantModel=new TenantModel(tenantList);
        String tenantObjectString=gson.toJson(tenantModel);
        Log.e(TAG,tenantObjectString);

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

    }

    private void deletePreviousFile()
    {
        String path = Environment.getExternalStorageDirectory().toString()+"/NestAwayTenantList";
        Log.e(TAG, "Path: " + path);
        File file = new File(path,"TenantList.txt");
        boolean deleted = file.delete();
        Log.e(TAG,"deleted ->"+deleted);
    }

}
