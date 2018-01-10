package app.num.barcodescannerproject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class ShowResult extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener{
    Intent intent;
    DatabaseHandler db;
    ArrayList<String> result,type,time;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    DataAdapter adapter;
    int totalResult;
    TextView countResult;
    AlertDialog.Builder alertDialog;
    String getResult;
    Toolbar toolbar;
    String deleteResult;
    int deletedIndex;
    TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_result);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        countResult = findViewById(R.id.resultCount);
        tableLayout = findViewById(R.id.result_table_layout);
        db = new DatabaseHandler(this);
        countResult();
        recyleViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_show_result, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clear:
                alertDeleteAll();
                return true;
            case R.id.scan:
                intent = new Intent(this,OpenCamera.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void recyleViews(){
        recyclerView = findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        result = db.getAllResults();
        type = db.getAllType();
        time = db.getAllTime();

        adapter = new DataAdapter(result,type,time);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(getApplicationContext(),
                    new GestureDetector.SimpleOnGestureListener() {
                        @Override public boolean onSingleTapUp(MotionEvent e) {
                            return true;
                        }

                    });

            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if(child != null && gestureDetector.onTouchEvent(e)) {
                    int position = rv.getChildAdapterPosition(child);
                    intent = new Intent(ShowResult.this, ViewDetail.class);
                    getResult = result.get(position);
                    intent.putExtra("result",getResult);
                    startActivity(intent);
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
    }

    public void alertDeleteAll(){
        alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Clear all results");
        alertDialog.setMessage("Are you sure ?");
        alertDialog.setCancelable(true);
        final AlertDialog ad = alertDialog.create();
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.deleteAllContact();
                finish();
                startActivity(getIntent());
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ad.dismiss();
            }
        });
        alertDialog.show();
    }

    public void alertDelete(final String getResult){
        alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Delete a result");
        alertDialog.setMessage("Are you sure ?");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.deleteContact(getResult);
                snackBar();
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(getIntent());
            }
        });
        alertDialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void countResult(){
        totalResult = db.getContactsCount();
        if (totalResult==1) {
            countResult.setText(totalResult+" Result");
            tableLayout.setVisibility(View.VISIBLE);
        }else if(totalResult==0) {
            countResult.setText("No Result");
            tableLayout.setVisibility(View.INVISIBLE);
        } else {
            countResult.setText(totalResult+" Results");
            tableLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        startActivity(intent);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof DataAdapter.ViewHolder) {
            deleteResult = result.get(viewHolder.getAdapterPosition());
            deletedIndex = viewHolder.getAdapterPosition();
            adapter.removeItem(viewHolder.getAdapterPosition());
            Log.e("ShowResult","Delete = "+deleteResult);
            alertDelete(deleteResult);
        }
    }

    private void snackBar(){
        countResult();
        Snackbar snackbar = Snackbar
                .make(toolbar, deleteResult + " was removed", Snackbar.LENGTH_LONG);
        TextView snackbarTV = snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        snackbarTV.setTextColor(Color.YELLOW);
        snackbar.show();
    }
}
