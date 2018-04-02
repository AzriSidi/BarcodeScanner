package app.num.barcodescannerproject;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class OpenCamera extends MainActivity implements ZXingScannerView.ResultHandler{
    ZXingScannerView mScannerView;
    String TAG = "OpenCameraTag";
    Intent intent;
    AlertDialog.Builder alertDialog;
    Context context;
    DatabaseHandler db;
    private static final int REQUEST_CAMERA_RESULT = 1;
    String CAMERA_PERMISSSION = Manifest.permission.CAMERA;
    String title,mgs;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
        context = OpenCamera.this;
        db = new DatabaseHandler(this);
        cameraPermission();
    }

    @Override
    protected void onResume(){
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    protected void onStart(){
        super.onStart();
        cameraPermission();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void onBackPressed() {
        intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        startActivity(intent);
    }

    @Override
    public void handleResult(Result rawResult) {
        Constant.getResult = rawResult.getText();
        Constant.getFormat = rawResult.getBarcodeFormat().toString();
        Log.e(TAG, "Result: "+Constant.getResult);
        Log.e(TAG, "Format: "+Constant.getFormat);
        if(!Constant.getResult.equals("")){
            alertBuilder();
        }

        if(Constant.getFormat.equals("QR_CODE")){
            Constant.getFormat = "QR Code";
        }else{
            Constant.getFormat = "Barcode";
        }

        if(db.checkContact(Constant.getResult)){
            updateData();
        }else {
            insertData();
        }
    }

    public void alertBuilder(){
        alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Scan Result:");
        alertDialog.setMessage(Constant.getResult);
        alertDialog.setCancelable(false);
        final AlertDialog ad = alertDialog.create();
        alertDialog.setPositiveButton("View", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                intent = new Intent(context, ViewDetail.class);
                intent.putExtra("result",Constant.getResult);
                startActivity(intent);
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ad.dismiss();
                mScannerView.resumeCameraPreview(OpenCamera.this);
            }
        });
        alertDialog.show();
    }

    private void insertData(){
        Log.d(TAG, "Inserting ..");
        db.addContact(new Contact(Constant.getResult,Constant.getFormat));
    }

    private void updateData(){
        Log.d(TAG, "Updating ..");
        db.updateContact(new Contact(Constant.getResult,Constant.getFormat));
    }

    private void cameraPermission(){
        if (ContextCompat.checkSelfPermission(this, CAMERA_PERMISSSION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{CAMERA_PERMISSSION},REQUEST_CAMERA_RESULT);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for(String permission: permissions){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, permission)){
                Log.e(TAG, "denied: "+permission);
                denyBox();
            }else{
                if(ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED){
                    Log.e(TAG, "allowed: "+permission);
                    Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show();
                } else{
                    Log.e(TAG, "set to never ask again: "+permission);
                    dontAskBox();
                }
            }
        }
    }

    public void denyBox(){
        title = "Warning your denied permission.";
        mgs = "Your need to grant camera permission in order to use Qr Scanner. " +
                "Click the qr scanner button again and choose allow.";
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        intent = new Intent(OpenCamera.this, MainActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(mgs)
                .setPositiveButton("Ok", dialogClickListener)
                .setCancelable(false)
                .show();
    }

    public void dontAskBox(){
        title = "Warning your pick don't ask again.";
        mgs = "Your need to grant camera permission in order to use Qr Scanner. " +
                "Click permissions then choose camera to grant permission.";
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_CAMERA_RESULT);
                    break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(mgs).
                setPositiveButton("Ok", dialogClickListener)
                .setCancelable(false)
                .show();
    }
}
