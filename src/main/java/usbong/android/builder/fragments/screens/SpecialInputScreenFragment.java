package usbong.android.builder.fragments.screens;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.widget.*;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;
import rx.Observer;
import usbong.android.builder.R;
import usbong.android.builder.adapters.SpecialInputTypeAdapter;
import usbong.android.builder.exceptions.FormInputException;
import usbong.android.builder.models.Screen;
import usbong.android.builder.models.details.SpecialInputScreenDetails;
import usbong.android.builder.utils.IntentUtils;
import usbong.android.builder.utils.JsonUtils;
import usbong.android.builder.utils.StringUtils;

import java.io.File;
import java.util.Arrays;

/**
 * Created by Rocky Camacho on 8/10/2014.
 */
public class SpecialInputScreenFragment extends BaseScreenFragment {

    private static final String TAG = SpecialInputScreenFragment.class.getSimpleName();
    @InjectView(R.id.content)
    FloatLabeledEditText content;
    @InjectView(R.id.input_type)
    Spinner inputType;
    private SpecialInputTypeAdapter adapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SpecialInputScreenFragment.
     */
    public static SpecialInputScreenFragment newInstance(Bundle args) {
        SpecialInputScreenFragment fragment = new SpecialInputScreenFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public SpecialInputScreenFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_screen_type_special_input;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new SpecialInputTypeAdapter(getActivity());
    }

    @Override
    protected void onScreenLoad(Screen screen) {
        name.setText(screen.name);
        SpecialInputScreenDetails specialInputScreenDetails = JsonUtils.fromJson(screen.details, SpecialInputScreenDetails.class);
        String details = StringUtils.EMPTY;
        if (currentScreen.details != null) {
            details = specialInputScreenDetails.getText();
        }
        content.setText(new SpannableString(Html.fromHtml(details)));
        SpecialInputScreenDetails.InputType selectedInputType = SpecialInputScreenDetails.InputType.from(specialInputScreenDetails.getInputType());
        inputType.setSelection(adapter.getPosition(selectedInputType));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter.addAll(Arrays.asList(SpecialInputScreenDetails.InputType.values()));
        inputType.setAdapter(adapter);
    }

    @Override
    protected String convertFormDataToScreenDetails() throws Exception {
        SpecialInputScreenDetails details = new SpecialInputScreenDetails();
        String screenContent = content.getText().toString().replaceAll("\n", "<br>");
        details.setText(screenContent);
        SpecialInputScreenDetails.InputType selectedInputType = adapter.getItem(inputType.getSelectedItemPosition());
        details.setInputType(selectedInputType.getName());
        return JsonUtils.toJson(details);
    }
}
