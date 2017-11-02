package cl.marcer.die_app;

import android.os.AsyncTask;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

/**
 * Created by Alberto Galleguillos on 4/3/17.
 */

public class CallFCM extends AsyncTask<HashMap<String, String>, String, String> {

    @Override
    protected String doInBackground(HashMap<String, String>... params) {
        return doCall(params[0]);
    }

    private String doCall(HashMap<String, String> params) {
        FirebaseDatabase mDatabase = Utils.getDatabase();
        DatabaseReference ref = mDatabase.getReference("users");
        ref.push().setValue(params);
        return null;
    }
}

