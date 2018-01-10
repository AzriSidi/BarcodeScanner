package app.num.barcodescannerproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity{
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onBackPressed() {
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
        finish();
    }

    public void QrScanner(View v){
        intent = new Intent(this,OpenCamera.class);
        startActivity(intent);
    }

    public void ShowResult(View view) {
        intent = new Intent(this,ShowResult.class);
        startActivity(intent);
    }
}
