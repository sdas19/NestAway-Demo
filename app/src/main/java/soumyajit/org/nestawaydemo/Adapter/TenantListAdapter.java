package soumyajit.org.nestawaydemo.Adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import soumyajit.org.nestawaydemo.AddUpdateTenant;
import soumyajit.org.nestawaydemo.JsonModel.TenantModel;
import soumyajit.org.nestawaydemo.R;
import soumyajit.org.nestawaydemo.TenantListScreen;
import soumyajit.org.nestawaydemo.Utility.Utility;

import static android.support.v4.app.ActivityCompat.requestPermissions;

/**
 * Created by Soumyajit Das on 30-01-2018.
 */


public class TenantListAdapter extends RecyclerView.Adapter<TenantListAdapter.TenantListScreenViewHolder>  {

    private Context context;
    private ArrayList<TenantModel.Details> tenantDetailsList;
    private static String TAG = TenantListAdapter.class.getSimpleName();
    public static final int MY_PERMISSION_CALL = 3;
    public static String phoneNumberToCall;

    public TenantListAdapter(Context context, ArrayList<TenantModel.Details> tenantDetailsList) {
        this.context = context;
        this.tenantDetailsList = tenantDetailsList;
    }

    @Override
    public TenantListScreenViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_tenant_details, parent, false);
        TenantListScreenViewHolder tenantListScreenViewHolder = new TenantListScreenViewHolder(view);
        return tenantListScreenViewHolder;
    }

    @Override
    public void onBindViewHolder(TenantListScreenViewHolder holder, int position) {
        TenantModel.Details details = tenantDetailsList.get(position);
        holder.tenant_name.setText(details.getName());
        holder.tenant_phone.setText(details.getPhone());
        holder.tenant_email.setText(details.getEmail());
        holder.tenant_time.setText(details.getTime());
        holder.tenant_visit_status.setText(details.getVisit_status());
        if (details.getVisit_status().equals("Not Visited")) {
            holder.tenant_visit_status.setBackgroundResource(R.color.Red);
        } else if (details.getVisit_status().equals("Visited")) {
            holder.tenant_visit_status.setBackgroundResource(R.color.Green);
        } else if (details.getVisit_status().equals("Pending")) {
            holder.tenant_visit_status.setBackgroundResource(R.color.Yellow);
        } else {
            holder.tenant_visit_status.setBackgroundResource(R.color.Black);
        }

    }

    @Override
    public int getItemCount() {
        return tenantDetailsList.size();
    }


    private void call(String number) {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                    Manifest.permission.CALL_PHONE)) {
                Log.e(TAG, "inside rationalle");
                                                /*Dialog to ask permission again created*/
                Utility.createDialogCall(context);

            } else {
                Log.e(TAG, "inside requestpermission");
                ActivityCompat.requestPermissions( (Activity) context, new String[]{Manifest.permission.CALL_PHONE},MY_PERMISSION_CALL);
            }
        } else {
            Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null));
            context.startActivity(callIntent);
        }
    }



    public class TenantListScreenViewHolder extends RecyclerView.ViewHolder{

        TextView tenant_name;
        TextView tenant_time;
        TextView tenant_phone;
        TextView tenant_email;
        ImageView call;
        TextView tenant_visit_status;

        public TenantListScreenViewHolder(View itemView) {
            super(itemView);
            tenant_name=(TextView) itemView.findViewById(R.id.tenant_name);
            tenant_email=(TextView) itemView.findViewById(R.id.tenant_email);
            tenant_time=(TextView) itemView.findViewById(R.id.tenant_time);
            tenant_phone=(TextView) itemView.findViewById(R.id.tenant_phone);
            tenant_visit_status=(TextView) itemView.findViewById(R.id.tenant_visit_status);
            call=(ImageView) itemView.findViewById(R.id.call_tenant);

            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    phoneNumberToCall=tenant_phone.getText().toString();
                    call(phoneNumberToCall);
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context, AddUpdateTenant.class);
                    Bundle data=new Bundle();
                    data.putString("name",tenant_name.getText().toString());
                    data.putString("email",tenant_email.getText().toString());
                    data.putString("phone",tenant_phone.getText().toString());
                    data.putString("time",tenant_time.getText().toString());
                    data.putString("visit_status",tenant_visit_status.getText().toString());
                    intent.putExtra("data",data);
                    intent.putExtra("tenantList",tenantDetailsList);
                    context.startActivity(intent);
                }
            });
        }
    }
}
