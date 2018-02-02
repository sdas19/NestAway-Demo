package soumyajit.org.nestawaydemo.Utility;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import soumyajit.org.nestawaydemo.TenantListScreen;

import static soumyajit.org.nestawaydemo.Adapter.TenantListAdapter.MY_PERMISSION_CALL;
import static soumyajit.org.nestawaydemo.TenantListScreen.MY_PERMISSION_WRITE_EXTERNAL_STORAGE;

/**
 * Created by Soumyajit Das on 02-02-2018.
 */

public class Utility {

    private static String TAG=Utility.class.getSimpleName();


                         /*If permission not granted this alertdialog will come up*/

    public static void createDialogWrite(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("This application requires Write to external storage. Please give the permission..")
                .setCancelable(false)
                .setPositiveButton("Ok! Granted", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ActivityCompat.requestPermissions((Activity) context,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},MY_PERMISSION_WRITE_EXTERNAL_STORAGE);
                        Log.e(TAG,"again request permission");
                    }
                })
                .setNegativeButton("Not required", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        Log.e(TAG,"never ask again");
                        dialog.cancel();
                    }
                });

        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Alert!!!!!!");
        alert.show();
    }


    public static void createDialogCall(final Context context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("This application requires calling. Please give the permission..")
                .setCancelable(false)
                .setPositiveButton("Ok! Granted", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ActivityCompat.requestPermissions((Activity) context,new String[]{Manifest.permission.CALL_PHONE},MY_PERMISSION_CALL);
                        Log.e(TAG,"again request permission");
                    }
                })
                .setNegativeButton("Not required", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Log.e(TAG,"never ask again");
                        dialog.cancel();
                    }
                });


        AlertDialog alert = builder.create();
        alert.setTitle("Alert!!!!!!");
        alert.show();
    }
}
