package usbong.android.builder.fragments.screens;

import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.view.View;
import android.widget.Spinner;
import butterknife.InjectView;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;
import usbong.android.builder.R;
import usbong.android.builder.adapters.MiscTypeAdapter;
import usbong.android.builder.models.Screen;
import usbong.android.builder.models.details.MiscScreenDetails;
import usbong.android.builder.utils.JsonUtils;
import usbong.android.builder.utils.StringUtils;

import java.util.Arrays;

/**
 * Created by Rocky Camacho on 8/10/2014.
 */
public class MiscScreenFragment extends BaseScreenFragment {

    private static final String TAG = MiscScreenFragment.class.getSimpleName();
    @InjectView(R.id.content)
    FloatLabeledEditText content;
    @InjectView(R.id.type)
    Spinner type;
    private MiscTypeAdapter adapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SpecialInputScreenFragment.
     */
    public static MiscScreenFragment newInstance(Bundle args) {
        MiscScreenFragment fragment = new MiscScreenFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public MiscScreenFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_screen_type_misc;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new MiscTypeAdapter(getActivity());
    }

    @Override
    protected void onScreenLoad(Screen screen) {
        name.setText(screen.name);
        MiscScreenDetails miscScreenDetails = JsonUtils.fromJson(screen.details, MiscScreenDetails.class);
        String details = StringUtils.EMPTY;
        if (currentScreen.details != null) {
            details = miscScreenDetails.getText();
        }
        content.setText(new SpannableString(Html.fromHtml(details)));
        MiscScreenDetails.Type selectedType = MiscScreenDetails.Type.from(miscScreenDetails.getType());
        type.setSelection(adapter.getPosition(selectedType));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter.addAll(Arrays.asList(MiscScreenDetails.Type.values()));
        type.setAdapter(adapter);
    }

    @Override
    protected String convertFormDataToScreenDetails() throws Exception {
        MiscScreenDetails details = new MiscScreenDetails();
        String screenContent = content.getText().toString().replaceAll("\n", "<br>");
        details.setText(screenContent);
        MiscScreenDetails.Type selectedType = adapter.getItem(type.getSelectedItemPosition());
        details.setType(selectedType.getName());
        return JsonUtils.toJson(details);
    }
}
