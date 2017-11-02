package cl.marcer.die_app;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    FirebaseRecyclerAdapter<Message, MessageHolder> mAdapter;

    public final static String EXTRA_PHOTO_URL = "cl.marcer.die_app.PHOTO_URL";
    public final static String EXTRA_AUTHOR = "cl.marcer.die_app.AUTHOR";
    public final static String EXTRA_TITLE = "cl.marcer.die_app.TITLE";
    public final static String EXTRA_BODY = "cl.marcer.die_app.BODY";
    private static final String TAG = "MainActivity";
    public static final String ANONYMOUS = "anonymous";

    private String mUsername;
    private String mUsermail;
    private String mUid;
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mDatabase;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        MainActivity.this.setTitle(R.string.app_name);

        mDatabase = Utils.getDatabase();

        /* Sign In */
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
            mUsermail = mFirebaseUser.getEmail();
            mUid = mFirebaseUser.getUid();
        }

        DatabaseReference ref = mDatabase.getReference("user_message/" + mUid);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        /* Sign In */


        /* FCM */
        final String fcmToken = FirebaseInstanceId.getInstance().getToken();
        DatabaseReference regIdRef = mDatabase.getReference("users");
        regIdRef.orderByChild("regID").equalTo(fcmToken).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, String.valueOf(dataSnapshot.getValue()));
                if (String.valueOf(dataSnapshot.getValue()).equals("null")) {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("regID", fcmToken);
                    params.put("name", mUsername);
                    params.put("mail", mUsermail);
                    params.put("device", "Android");
                    params.put("notification", "true");
                    params.put("uid", mUid);

                    new CallFCM().execute(params);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });
        /* FCM */

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv);
        // TextView emptyView = (TextView) findViewById(R.id.empty_view);

        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        mAdapter = new FirebaseRecyclerAdapter<Message, MessageHolder>(
                Message.class,
                R.layout.item,
                MessageHolder.class,
                ref
        ) {
            @Override
            protected void populateViewHolder(MessageHolder viewHolder, Message model, int position) {
                Glide.with(getApplicationContext()).load(model.getPhotoUrl()).crossFade().into(viewHolder.mImage);
                viewHolder.setmAuthor(model.getAuthor());
                viewHolder.setmTitle(model.getTitle());
                viewHolder.setmBody(model.getBody());
                viewHolder.setmUrl(model.getPhotoUrl());
            }

            @Override
            public MessageHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
                final MessageHolder messageHolder = super.onCreateViewHolder(parent, viewType);
                messageHolder.setOnClickListener(new MessageHolder.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(getApplicationContext(), DetailActivity.class);

                        TextView author = (TextView) view.findViewById(R.id.tv_author);
                        TextView title = (TextView) view.findViewById(R.id.tv_title);
                        TextView body = (TextView) view.findViewById(R.id.tv_body);
                        TextView url = (TextView) view.findViewById(R.id.tv_url);

                        String mAuthor = author.getText().toString();
                        String mTitle = title.getText().toString();
                        String mBody = body.getText().toString();
                        String mPhotoUrl = url.getText().toString();

                        intent.putExtra(EXTRA_AUTHOR, mAuthor);
                        intent.putExtra(EXTRA_TITLE, mTitle);
                        intent.putExtra(EXTRA_BODY, mBody);
                        intent.putExtra(EXTRA_PHOTO_URL, mPhotoUrl);

                        startActivity(intent);
                    }
                });
                return messageHolder;
            }
        };

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);
                int messageCount = mAdapter.getItemCount();
                int lastVisiblePosition = mLayoutManager.findLastCompletelyVisibleItemPosition();

                if (lastVisiblePosition == -1 || (positionStart >= (messageCount - 1) && lastVisiblePosition == (positionStart - 1))) {
                    recyclerView.scrollToPosition(positionStart);
                }
            }
        });

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.sign_out_menu) {
            mFirebaseAuth.signOut();
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            mFirebaseUser = null;
            mUsername = ANONYMOUS;
            mUsermail = null;
            startActivity(new Intent(this, SignInActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAdapter != null ) {
            mAdapter.cleanup();
        }
    }

    /* Sing Ig */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }
    /* Sing Ig */

}