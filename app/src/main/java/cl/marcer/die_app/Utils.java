package cl.marcer.die_app;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Alberto Galleguillos on 4/3/17.
 */

public class Utils {
    private static FirebaseDatabase mDatabase;

    public static FirebaseDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }
        return mDatabase;
    }
}
