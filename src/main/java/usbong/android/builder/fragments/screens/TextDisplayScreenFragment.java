package usbong.android.builder.fragments.screens;


import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import butterknife.InjectView;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;
import usbong.android.builder.R;
import usbong.android.builder.models.Screen;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Use the {@link TextDisplayScreenFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TextDisplayScreenFragment extends BaseScreenFragment {

    private static final String TAG = TextDisplayScreenFragment.class.getSimpleName();

    @InjectView(R.id.content)
    FloatLabeledEditText textDisplay;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TextDisplayFragment.
     */
    public static TextDisplayScreenFragment newInstance(Bundle args) {
        TextDisplayScreenFragment fragment = new TextDisplayScreenFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public TextDisplayScreenFragment() {
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
