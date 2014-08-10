package usbong.android.builder.fragments.screens;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.SpannableString;
import android.util.Log;
import android.view.*;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;
import de.greenrobot.event.EventBus;
import rx.Observer;
import usbong.android.builder.R;
import usbong.android.builder.activities.SelectScreenActivity;
import usbong.android.builder.controllers.ScreenDetailController;
import usbong.android.builder.events.OnNeedRefreshScreen;
import usbong.android.builder.events.OnScreenDetailsSave;
import usbong.android.builder.events.OnScreenSave;
import usbong.android.builder.fragments.ScreenDetailFragment;
import usbong.android.builder.fragments.SelectScreenFragment;
import usbong.android.builder.models.Screen;
import usbong.android.builder.models.ScreenRelation;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Use the {@link TextDisplayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TextDisplayFragment extends BaseScreenFragment {

    private static final String TAG = TextDisplayFragment.class.getSimpleName();

    @InjectView(R.id.content)
    FloatLabeledEditText textDisplay;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TextDisplayFragment.
     */
    public static TextDisplayFragment newInstance(Bundle args) {
        TextDisplayFragment fragment = new TextDisplayFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public TextDisplayFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_screen_type_text_display;
    }

    @Override
    protected void onScreenLoad(Screen screen) {
        name.setText(currentScreen.name);
        String details = "";
        if (currentScreen.details != null) {
            details = currentScreen.details;
        }
        SpannableString text = new SpannableString(Html.fromHtml(details));
        textDisplay.setText(text);
    }

    @Override
    protected String convertFormDataToScreenDetails() throws Exception {
        return textDisplay.getText().toString().replaceAll("\n", "<br>");
    }
}
