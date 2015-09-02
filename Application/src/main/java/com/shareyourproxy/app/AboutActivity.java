package com.shareyourproxy.app;

import android.os.Bundle;
import android.view.MenuItem;

import com.shareyourproxy.R;
import com.shareyourproxy.app.fragment.AboutFragment;

import timber.log.Timber;

/**
 * Display an {@link AboutFragment} that has an Apache II license for this project.
 */
public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.activity_about_container,
                    AboutFragment.newInstance()).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                Timber.e("Menu Item ID unknown");
                break;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.slide_out_bottom);
    }
}
