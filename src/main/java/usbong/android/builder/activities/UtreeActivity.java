package usbong.android.builder.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import usbong.android.builder.R;
import usbong.android.builder.fragments.UtreeFragment;

public class UtreeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, UtreeFragment.newInstance(getIntent().getExtras()))
                .commit();
    }

}
