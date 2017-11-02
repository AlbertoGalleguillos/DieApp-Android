package cl.marcer.die_app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.bluejamesbond.text.DocumentView;
import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;
import me.leolin.shortcutbadger.ShortcutBadger;

public class DetailActivity extends AppCompatActivity {
    private int badgeCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String author = intent.getStringExtra(MainActivity.EXTRA_AUTHOR);
        String title = intent.getStringExtra(MainActivity.EXTRA_TITLE);
        String body = intent.getStringExtra(MainActivity.EXTRA_BODY);
        String url = intent.getStringExtra(MainActivity.EXTRA_PHOTO_URL);

        TextView mAuthor = (TextView) findViewById(R.id.detail_author);
        TextView mTitle = (TextView) findViewById(R.id.detail_title);
        DocumentView mBody = (DocumentView) findViewById(R.id.detail_body);
        CircleImageView mProfileImage = (CircleImageView) findViewById(R.id.profile_image);

        mAuthor.setText(author);
        mTitle.setText(title);
        mBody.setText(body);

        Glide.with(getApplicationContext()).load(url).centerCrop().crossFade().into(mProfileImage);

        //Set Icon Number - 1
        final SharedPreferences mSharedPreferences = getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE);
        badgeCount = mSharedPreferences.getInt(getString(R.string.unread), 0);

        if (badgeCount > 0) {
            badgeCount -= 1;
            ShortcutBadger.applyCount(getApplicationContext(), badgeCount);
            SharedPreferences.Editor mEditor = mSharedPreferences.edit();
            mEditor.putInt(getString(R.string.unread), badgeCount).apply();
        }

    }

}
