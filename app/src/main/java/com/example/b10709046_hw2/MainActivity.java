package com.example.b10709046_hw2;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.Shape;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.b10709046_hw2.data.WaitlistContract;
import com.example.b10709046_hw2.data.WaitlistDbHelper;

import org.xmlpull.v1.XmlPullParser;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private WaitlistAdapter waitlistAdapter;
    private SQLiteDatabase sqLiteDatabase;
    RecyclerView waitlistRecyclerView;

    @ColorInt
    private static int shapeColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        waitlistRecyclerView = (RecyclerView) this.findViewById(R.id.waitlist_recyclerview);
        waitlistRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        setupSharedPreferences();

        WaitlistDbHelper dbHelper = new WaitlistDbHelper(this);
        sqLiteDatabase = dbHelper.getWritableDatabase();

        Cursor cursor = getAllGuests();
        waitlistAdapter = new WaitlistAdapter(this, cursor);
        waitlistRecyclerView.setAdapter(waitlistAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                final RecyclerView.ViewHolder vh = viewHolder;
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Note");
                alertDialog.setMessage("確定要刪除資料嗎？");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        long id = (long) vh.itemView.getTag();
                        removeGuest(id);
                        waitlistAdapter.swapCursor(getAllGuests());
                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        waitlistAdapter.swapCursor(getAllGuests());
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        }).attachToRecyclerView(waitlistRecyclerView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        } else if (id == R.id.action_add_guest) {
            Intent startAddGuestActivity = new Intent(this, AddGuestActivity.class);
            startActivity(startAddGuestActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Cursor getAllGuests() {
        return sqLiteDatabase.query(WaitlistContract.WaitlistEntry.TABLE_NAME,
                null, null, null, null, null,
                WaitlistContract.WaitlistEntry.COLUMN_TIMESTAMP);
    }

    private boolean removeGuest(long id) {
        return sqLiteDatabase.delete(WaitlistContract.WaitlistEntry.TABLE_NAME, WaitlistContract.WaitlistEntry._ID + "=" + id, null) > 0;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_color_key))) {
            loadColorFromPreferences(sharedPreferences);
        }
    }

    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        loadColorFromPreferences(sharedPreferences);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    private void loadColorFromPreferences(SharedPreferences sharedPreferences) {
        setColor(sharedPreferences.getString(getString(R.string.pref_color_key), getString(R.string.pref_color_red_value)));
    }

    public void setColor(String newColorKey) {
        if (newColorKey.equals(waitlistRecyclerView.getContext().getString(R.string.pref_color_blue_value))) {
            shapeColor = ContextCompat.getColor(waitlistRecyclerView.getContext(), R.color.shapeBlue);
        } else if (newColorKey.equals(waitlistRecyclerView.getContext().getString(R.string.pref_color_green_value))) {
            shapeColor = ContextCompat.getColor(waitlistRecyclerView.getContext(), R.color.shapeGreen);
        } else {
            shapeColor = ContextCompat.getColor(waitlistRecyclerView.getContext(), R.color.shapeRed);
        }
    }

    public static int getShapeColor() {
        return shapeColor;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

}
