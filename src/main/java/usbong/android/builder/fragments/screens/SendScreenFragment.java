package usbong.android.builder.fragments.screens;

import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.view.View;
import android.widget.Spinner;
import butterknife.InjectView;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;
import usbong.android.builder.R;
import usbong.android.builder.adapters.SendTypeAdapter;
import usbong.android.builder.models.Screen;
import usbong.android.builder.models.details.SendScreenDetails;
import usbong.android.builder.utils.JsonUtils;
import usbong.android.builder.utils.StringUtils;

import java.util.Arrays;

/**
 * Created by Rocky Camacho on 8/10/2014.
 */
public class SendScreenFragment extends BaseScreenFragment {

    private static final String TAG = SendScreenFragment.class.getSimpleName();
    @InjectView(R.id.content)
    FloatLabeledEditText content;
    @InjectView(R.id.type)
    Spinner processingType;

    private SendTypeAdapter adapter;

    public static SendScreenFragment newInstance(Bundle args) {
        SendScreenFragment fragment = new SendScreenFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private SendScreenFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_screen_type_send;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new SendTypeAdapter(getActivity());
    }

    @Override
    protected void onScreenLoad(Screen screen) {
        name.setText(screen.name);
        SendScreenDetails sendScreenDetails = JsonUtils.fromJson(screen.details, SendScreenDetails.class);
        String details = StringUtils.EMPTY;
        if (currentScreen.details != null) {
            details = sendScreenDetails.getText();
        }
        content.setText(new SpannableString(Html.fromHtml(details)));
        SendScreenDetails.Type selectedType = SendScreenDetails.Type.from(sendScreenDetails.getType());
        processingType.setSelection(adapter.getPosition(selectedType));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter.addAll(Arrays.asList(SendScreenDetails.Type.values()));
        processingType.setAdapter(adapter);
    }

    @Override
    protected String convertFormDataToScreenDetails() throws Exception {
        SendScreenDetails details = new SendScreenDetails();
        String screenContent = content.getText().toString().replaceAll("\n", "<br>");
        details.setText(screenContent);
        SendScreenDetails.Type selectedType = adapter.getItem(processingType.getSelectedItemPosition());
        details.setType(selectedType.getName());

        return JsonUtils.toJson(details);
    }
}
