package usbong.android.builder.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import usbong.android.builder.R;
import usbong.android.builder.fragments.ScreenDetailFragment;

public class ScreenDetailActivity extends ActionBarActivity {

    public static final String TAG = ScreenDetailActivity.class.getSimpleName();

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    protected CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_detail);
        mTitle = getTitle();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, ScreenDetailFragment.newInstance(getIntent().getExtras()))
                .commit();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

}
