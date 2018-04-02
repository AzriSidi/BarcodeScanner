package app.num.barcodescannerproject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class ViewDetail extends AppCompatActivity {
    Intent intent;
    private String TAG = "ViewDetailTag";
    private ProgressDialog pDialog;
    String getNoSiri;
    JSONObject dataAsset,pembekal;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private ScrollView scrollView;
    private Toolbar toolbar;
    TableLayout tableLayout;
    TextView tv1;
    private ImageView image;
    String title,mgs;
    BroadcastReceiver networkStateReceiver;
    IntentFilter networkIntentFilter;
    String urlJson = "https://mpsppay.mpsp.gov.my/old_ebayar/asset_json.php?no_siri=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_detail);

        Constant.textTitle = findViewById(R.id.title);
        Constant.textNoBP = findViewById(R.id.noBp);
        Constant.textNoPesan = findViewById(R.id.pesan);
        Constant.textNoSiri = findViewById(R.id.noSiri);
        Constant.textJenisAsset = findViewById(R.id.jenis);
        Constant.textKate = findViewById(R.id.kate);
        Constant.textSubKate = findViewById(R.id.subKate);
        Constant.textModel = findViewById(R.id.model);
        Constant.textBuatan = findViewById(R.id.buatan);
        Constant.textHarga = findViewById(R.id.harga);
        Constant.textEnjin = findViewById(R.id.enjin);
        Constant.textTkhTerima = findViewById(R.id.tkhTerima);
        Constant.textCasis = findViewById(R.id.casis);
        Constant.textNoPendaftar = findViewById(R.id.noPendaftar);
        Constant.textTempoh = findViewById(R.id.tempoh);
        Constant.textAksesori = findViewById(R.id.aksesori);
        Constant.textNama = findViewById(R.id.nama);
        Constant.textNama1 = findViewById(R.id.nama1);
        Constant.textAlamat1 = findViewById(R.id.alamat1);
        Constant.textAlamat2 = findViewById(R.id.alamat2);
        Constant.textAlamat3 = findViewById(R.id.alamat3);
        Constant.textAlamat4 = findViewById(R.id.alamat4);
        Constant.textPenyenggara = findViewById(R.id.penyenggara);
        Constant.textJabPenempat = findViewById(R.id.jabPenempat);
        Constant.textPegPenempat = findViewById(R.id.pegPenempat);
        Constant.textLokasi = findViewById(R.id.lokasi);
        Constant.textTkhPenempat = findViewById(R.id.tkhPenempat);
        Constant.textKetuaJab = findViewById(R.id.ketuaJab);
        Constant.textTkh = findViewById(R.id.tkh);

        scrollView = findViewById(R.id.scrollView);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tableLayout = findViewById(R.id.tableLayout);

        swipeRefresh();
        loadDialog();
        jsonDataAsset();
    }

    @Override
    protected void onStart() {
        super.onStart();
        networkStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                queryNetwork();
            }
        };
        networkIntentFilter = new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkStateReceiver, networkIntentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(networkStateReceiver);
    }

    private void queryNetwork() {
        try {
            ConnectivityManager connMgr = (ConnectivityManager) this
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifi = connMgr
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mobile = connMgr
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            Constant.connected3G = mobile.isConnected();
            Constant.connectedToWiFi = wifi.isConnected();
            // added by amir on 2013-01-08

            if (Constant.connectedToWiFi) {
                jsonDataAsset();
            } else if (Constant.connected3G) {
                jsonDataAsset();
            } else if ((!Constant.connectedToWiFi) && (!Constant.connected3G)) {
                mySnackBar();
                tableLayout.setVisibility(View.INVISIBLE);
            }

           Log.d(TAG, "myStatus: "+Constant.connectedToWiFi);
        } catch (Exception e) {
            Log.e(TAG, "Exception: "+e.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.view_result_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main:
                intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EXIT", true);
                startActivity(intent);
                return true;
            case R.id.scan:
                intent = new Intent(this,OpenCamera.class);
                startActivity(intent);
                return true;
            case R.id.result:
                intent = new Intent(this,ShowResult.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void swipeRefresh(){
        mSwipeRefreshLayout = findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.blue);
    }

    private void refreshContent() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
                if((!Constant.connectedToWiFi) && (!Constant.connected3G)){
                    mySnackBar();
                }else {
                    jsonDataAsset();
                }
            }
        },1000);
    }

    private void jsonDataAsset() {
        intent = getIntent();
        getNoSiri = intent.getStringExtra("result");
        final String url = urlJson+getNoSiri;
        Log.d(TAG, "Full url: "+url);
        showpDialog();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "Response from url: "+response.toString());
                try {
                    dataAsset = response.getJSONObject("data_asset");
                    if(dataAsset.has("message")){
                        Constant.message = dataAsset.getString("message");
                        Log.e(TAG,Constant.message);
                    }

                    Constant.title = dataAsset.getString("title");
                    Constant.noBp = dataAsset.getString("no_bp");
                    Constant.noPesan = dataAsset.getString("no_pesanan");
                    Constant.noSiri = dataAsset.getString("no_siri");
                    Constant.jenisAsset = dataAsset.getString("jenis_asset");
                    Constant.kategori = dataAsset.getString("kategori");
                    Constant.subKategori = dataAsset.getString("sub_kategori");
                    Constant.model = dataAsset.getString("model");
                    Constant.buatan = dataAsset.getString("buatan");
                    Constant.harga = dataAsset.getString("harga");
                    Constant.enjin = dataAsset.getString("enjin");
                    Constant.tkhTerima = dataAsset.getString("tkh_terima");
                    Constant.casis = dataAsset.getString("casis");
                    Constant.noPendaftar = dataAsset.getString("no_pendaftaran");
                    Constant.tempoh = dataAsset.getString("tempoh_jaminan");
                    Constant.aksesori = dataAsset.getString("aksesori");

                    pembekal = dataAsset.getJSONObject("pembekal");
                    Constant.nama = pembekal.getString("nama");
                    Constant.nama1 = pembekal.getString("nama1");
                    Constant.alamat1 = pembekal.getString("alamat1");
                    Constant.alamat2 = pembekal.getString("alamat2");
                    Constant.alamat3 = pembekal.getString("alamat3");
                    Constant.alamat4 = pembekal.getString("alamat4");

                    Constant.penyenlengara = dataAsset.getString("penyelenggaraan");
                    Constant.jabPenempat = dataAsset.getString("jab_penempatan");
                    Constant.pegPenempat = dataAsset.getString("peg_penempatan");
                    Constant.lokasi = dataAsset.getString("lokasi");
                    Constant.tkhPenempat = dataAsset.getString("tkh_penempatan");
                    Constant.ketuaJab = dataAsset.getString("ketua_jabatan");
                    Constant.tkh = dataAsset.getString("tkh");
                    jsonTextView();

                } catch (JSONException e) {
                    Log.e(TAG, "JSONException: "+e);
                    alertDialog();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "ErrorResponse: "+error.getMessage());
                tableLayout.setVisibility(View.INVISIBLE);
                pDialog.dismiss();
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    public void dialogBox(){
        title = "Error message";
        mgs = "Failed to get data from server.";
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        onBackPressed();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(mgs).
                setPositiveButton("Ok", dialogClickListener)
                .setCancelable(false)
                .show();
    }

    private void alertDialog(){
        tableLayout.setVisibility(View.INVISIBLE);
        pDialog.dismiss();

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog);
        tv1 = dialog.findViewById(R.id.textView1);
        tv1.setText(Constant.message);
        image = dialog.findViewById(R.id.imageView1);
        image.setImageResource(R.drawable.ic_highlight_off_black);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        dialog.getWindow().setLayout(300, 270);
        dialog.show();
    }

    private void mySnackBar(){
        Snackbar snackbar = Snackbar
                .make(scrollView,R.string.snackBar_mgs, Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(getResources().getColor(R.color.white));
        snackbar.setAction("Retry", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jsonDataAsset();
            }
        });
        snackbar.show();
        View view = snackbar.getView();
        view.setBackgroundColor(getResources().getColor(R.color.colorAccent));
    }

    private void loadDialog(){
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Getting data from server...");
        pDialog.setCancelable(false);
        tableLayout.setVisibility(View.INVISIBLE);
    }

    private void showpDialog() {
        if(!pDialog.isShowing())
            pDialog.show();
        if(mSwipeRefreshLayout.isShown())
            pDialog.dismiss();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
            tableLayout.setVisibility(View.VISIBLE);
    }

    private void jsonTextView(){
        hidepDialog();
        Constant.textTitle.setText(Constant.title);
        Constant.textNoBP.setText(Constant.noBp);
        Constant.textNoPesan.setText(Constant.noPesan);
        Constant.textNoSiri.setText(Constant.noSiri);
        Constant.textJenisAsset.setText(Constant.jenisAsset);
        Constant.textKate.setText(Constant.kategori);
        Constant.textSubKate.setText(Constant.subKategori);
        Constant.textModel.setText(Constant.model);
        Constant.textBuatan.setText(Constant.buatan);
        Constant.textHarga.setText(Constant.harga);
        Constant.textEnjin.setText(Constant.enjin);
        Constant.textTkhTerima.setText(Constant.tkhTerima);
        Constant.textCasis.setText(Constant.casis);
        Constant.textNoPendaftar.setText(Constant.noPendaftar);
        Constant.textTempoh.setText(Constant.tempoh);
        Constant.textAksesori.setText(Constant.aksesori);
        Constant.textNama.setText(Constant.nama);
        Constant.textNama1.setText(Constant.nama1);
        Constant.textAlamat1.setText(Constant.alamat1);
        Constant.textAlamat2.setText(Constant.alamat2);
        Constant.textAlamat3.setText(Constant.alamat3);
        Constant.textAlamat4.setText(Constant.alamat4);
        Constant.textPenyenggara.setText(Constant.penyenlengara);
        Constant.textJabPenempat.setText(Constant.jabPenempat);
        Constant.textPegPenempat.setText(Constant.pegPenempat);
        Constant.textLokasi.setText(Constant.lokasi);
        Constant.textTkhPenempat.setText(Constant.tkhPenempat);
        Constant.textKetuaJab.setText(Constant.ketuaJab);
        Constant.textTkh.setText(Constant.tkh);
    }

    public void onBackPressed(){
        Constant.getResult = "";
        finish();
    }
}
