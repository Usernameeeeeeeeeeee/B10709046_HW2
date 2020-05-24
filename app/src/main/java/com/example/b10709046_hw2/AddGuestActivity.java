package com.example.b10709046_hw2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.b10709046_hw2.data.WaitlistContract;
import com.example.b10709046_hw2.data.WaitlistDbHelper;

public class AddGuestActivity extends AppCompatActivity {

    private WaitlistAdapter waitlistAdapter;
    private SQLiteDatabase sqLiteDatabase;
    private EditText newGuestNameEditText;
    private EditText newPartySizeEditText;

    private final static String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_guest);

        newGuestNameEditText = (EditText) this.findViewById(R.id.person_name_edit_text);
        newPartySizeEditText = (EditText) this.findViewById(R.id.party_size_edit_text);

        WaitlistDbHelper dbHelper = new WaitlistDbHelper(this);
        sqLiteDatabase = dbHelper.getWritableDatabase();
    }

    public void addToWaitlist(View view) {
        if (newGuestNameEditText.getText().length() == 0 || newGuestNameEditText.getText().length() == 0) {
            return;
        }
        int partySize = 1;
        try {
            partySize = Integer.parseInt(newPartySizeEditText.getText().toString());
        } catch (NumberFormatException ex) {
            Log.e(LOG_TAG, "Failed to parse party size text to number: " + ex.getMessage());
        }

        addNewGuest(newGuestNameEditText.getText().toString(), partySize);
        //waitlistAdapter.swapCursor(getAllGuests());
        //newPartySizeEditText.clearFocus();
        //newGuestNameEditText.getText().clear();
        //newPartySizeEditText.getText().clear();
        //onBackPressed();
        backToHome();
    }

    private Cursor getAllGuests() {
        return sqLiteDatabase.query(WaitlistContract.WaitlistEntry.TABLE_NAME,
                null, null, null, null, null,
                WaitlistContract.WaitlistEntry.COLUMN_TIMESTAMP);
    }

    private long addNewGuest(String name, int partySize) {
        ContentValues cv = new ContentValues();
        cv.put(WaitlistContract.WaitlistEntry.COLUMN_GUEST_NAME, name);
        cv.put(WaitlistContract.WaitlistEntry.COLUMN_PARTY_SIZE, partySize);
        return sqLiteDatabase.insert(WaitlistContract.WaitlistEntry.TABLE_NAME, null, cv);
    }

    private void backToHome() {
        Intent home = new Intent(this, MainActivity.class);
        startActivity(home);
    }

    public void backToHome(View view) {
        //onBackPressed();
        backToHome();
    }
}
