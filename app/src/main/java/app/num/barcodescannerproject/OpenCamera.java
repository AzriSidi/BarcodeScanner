package app.num.barcodescannerproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class OpenCamera extends MainActivity implements ZXingScannerView.ResultHandler{
    ZXingScannerView mScannerView;
    String TAG = "OpenCameraTag";
    Intent intent;
    AlertDialog.Builder alertDialog;
    Context context;
    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
        context = OpenCamera.this;
        db = new DatabaseHandler(this);
    }

    @Override
    protected void onResume(){
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
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
}
