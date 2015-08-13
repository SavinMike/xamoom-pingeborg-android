package com.xamoom.android.xamoom_pingeborg_android;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.xamoom.android.APICallback;
import com.xamoom.android.XamoomEndUserApi;
import com.xamoom.android.mapping.Content;
import com.xamoom.android.mapping.ContentById;
import com.xamoom.android.mapping.ContentByLocationIdentifier;
import com.xamoom.android.xamoomcontentblocks.XamoomContentFragment;

import java.net.HttpURLConnection;
import java.net.URL;

import retrofit.RetrofitError;


public class ArtistDetailActivity extends AppCompatActivity implements XamoomContentFragment.OnXamoomContentFragmentInteractionListener {

    private ProgressBar mProgressbar;
    private String mContentId;
    private String mLocationIdentifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_detail);

        //set statusbar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(getResources().getColor(R.color.pingeborg_dark_yellow));
        }

        //setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //setup actionbar
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(Global.getInstance().getCurrentSystemName());
        }

        //get mContentId or mLocationIdentifier from intent
        Intent myIntent = getIntent();
        mContentId = myIntent.getStringExtra(XamoomContentFragment.XAMOOM_CONTENT_ID);
        mLocationIdentifier = myIntent.getStringExtra(XamoomContentFragment.XAMOOM_LOCATION_IDENTIFIER);

        mProgressbar = (ProgressBar) findViewById(R.id.artistDetailLoadingIndicator);

        if(mContentId != null || mLocationIdentifier != null) {
            loadData(mContentId, mLocationIdentifier);
        } else {
            onNewIntent(getIntent());
        }
    }

    protected void onNewIntent(Intent intent) {
        final String[] url = {intent.getDataString()};

        if(url[0].contains("pingeb.org")) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        //get the url redirected
                        URL url2 = new URL(url[0]);
                        HttpURLConnection ucon = (HttpURLConnection) url2.openConnection();
                        ucon.setInstanceFollowRedirects(false);
                        final String newUrl = ucon.getHeaderField("Location");
                        ucon.disconnect();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                url[0] = newUrl;

                                Uri mUri = Uri.parse(url[0]);
                                mLocationIdentifier = mUri.getLastPathSegment();

                                loadData(mContentId, mLocationIdentifier);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), getString(R.string.old_pingeborg_sticker_redirect_failure), Toast.LENGTH_LONG).show();
                    }
                }
            };
            thread.start();
        } else {
            Uri mUri = Uri.parse(url[0]);
            mLocationIdentifier = mUri.getLastPathSegment();
            loadData(mContentId, mLocationIdentifier);
        }
    }

    private void loadData(final String contentId, final String locationIdentifier) {
        //load data
        if (contentId != null) {
            if(Global.getInstance().getSavedArtists().contains(contentId)) {
                Analytics.getInstance(this).sendEvent("UX", "Open Artist Detail", "User opened artist detail activity with mContentId: " + contentId);
                XamoomEndUserApi.getInstance(this.getApplicationContext()).getContentbyIdFull(contentId, false, false, null, true, new APICallback<ContentById>() {
                    @Override
                    public void finished(ContentById result) {
                        setupXamoomContentFrameLayout(result.getContent());
                    }

                    @Override
                    public void error(RetrofitError error) {
                        Log.e(Global.DEBUG_TAG, "Error:" + error);
                        openInBrowser(contentId, locationIdentifier);
                    }
                });
            } else {
                Analytics.getInstance(this).sendEvent("UX", "Open Artist Detail", "User opened artist detail activity with mContentId: " + contentId);
                XamoomEndUserApi.getInstance(this.getApplicationContext()).getContentbyIdFull(contentId, false, false, null, false, new APICallback<ContentById>() {
                    @Override
                    public void finished(ContentById result) {
                        setupXamoomContentFrameLayout(result.getContent());
                    }

                    @Override
                    public void error(RetrofitError error) {
                        Log.e(Global.DEBUG_TAG, "Error:" + error);
                        openInBrowser(contentId, locationIdentifier);
                    }
                });
            }
        } else if (locationIdentifier != null) {
            Analytics.getInstance(this).sendEvent("UX", "Open Artist Detail", "User opened artist detail activity with mLocationIdentifier: " + locationIdentifier);
            XamoomEndUserApi.getInstance(this.getApplicationContext()).getContentByLocationIdentifier(locationIdentifier, false, false, null, new APICallback<ContentByLocationIdentifier>() {
                @Override
                public void finished(ContentByLocationIdentifier result) {
                    //save artist
                    Global.getInstance().saveArtist(result.getContent().getContentId());

                    setupXamoomContentFrameLayout(result.getContent());
                }

                @Override
                public void error(RetrofitError error) {
                    Log.e(Global.DEBUG_TAG, "Error:" + error);
                    openInBrowser(contentId, locationIdentifier);
                }
            });
        } else {
            Log.w(Global.DEBUG_TAG, "There is no mContentId or mLocationIdentifier");
            finish();
        }
    }

    private void setupXamoomContentFrameLayout(Content content) {
        //hide loading indicator
        mProgressbar.setVisibility(View.GONE);

        XamoomContentFragment fragment = XamoomContentFragment.newInstance(Integer.toHexString(getResources().getColor(R.color.pingeborg_green)).substring(2));
        fragment.setContent(content);

        try {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.XamoomContentFrameLayout, fragment)
                    .commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_artist_detail, menu);
        return true;
    }

    public void openInBrowser(String contentId, String locationIdentifier) {
        Intent browserIntent;
        if(contentId != null) {
            browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://xm.gl/content/" + contentId));
        } else {
            browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://xm.gl/" + locationIdentifier));
        }
        //finish this activity
        finish();

        //open url in browser
        startActivity(browserIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void clickedContentBlock(Content content) {
        //also discover this artist
        Global.getInstance().saveArtist(content.getContentId());

        XamoomContentFragment fragment = XamoomContentFragment.newInstance(Integer.toHexString(getResources().getColor(R.color.pingeborg_green)).substring(2));
        fragment.setContent(content);

        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.bottom_swipe_in, 0, 0, R.anim.bottom_swipe_out)
                .add(R.id.XamoomContentFrameLayout, fragment)
                .addToBackStack(null)
                .commit();
    }
}
