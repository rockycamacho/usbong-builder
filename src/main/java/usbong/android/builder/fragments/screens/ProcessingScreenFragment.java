package usbong.android.builder.fragments.screens;

import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.view.View;
import android.widget.Spinner;
import butterknife.InjectView;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;
import usbong.android.builder.R;
import usbong.android.builder.adapters.ProcessingTypeAdapter;
import usbong.android.builder.models.Screen;
import usbong.android.builder.models.details.ProcessingScreenDetails;
import usbong.android.builder.utils.JsonUtils;
import usbong.android.builder.utils.StringUtils;

import java.util.Arrays;

/**
 * Created by Rocky Camacho on 8/10/2014.
 */
public class ProcessingScreenFragment extends BaseScreenFragment {

    private static final String TAG = ProcessingScreenFragment.class.getSimpleName();
    @InjectView(R.id.content)
    FloatLabeledEditText content;
    @InjectView(R.id.type)
    Spinner processingType;

    private ProcessingTypeAdapter adapter;

    public static ProcessingScreenFragment newInstance(Bundle args) {
        ProcessingScreenFragment fragment = new ProcessingScreenFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private ProcessingScreenFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_screen_type_processing;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ProcessingTypeAdapter(getActivity());
    }

    @Override
    protected void onScreenLoad(Screen screen) {
        name.setText(screen.name);
        ProcessingScreenDetails processingScreenDetails = JsonUtils.fromJson(screen.details, ProcessingScreenDetails.class);
        String details = StringUtils.EMPTY;
        if (currentScreen.details != null) {
            details = processingScreenDetails.getText();
        }
        content.setText(new SpannableString(Html.fromHtml(details)));
        ProcessingScreenDetails.ProcessingType selectedProcessingType = ProcessingScreenDetails.ProcessingType.from(processingScreenDetails.getProcessingType());
        processingType.setSelection(adapter.getPosition(selectedProcessingType));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter.addAll(Arrays.asList(ProcessingScreenDetails.ProcessingType.values()));
        processingType.setAdapter(adapter);
    }

    @Override
    protected String convertFormDataToScreenDetails() throws Exception {
        ProcessingScreenDetails details = new ProcessingScreenDetails();
        String screenContent = content.getText().toString().replaceAll("\n", "<br>");
        details.setText(screenContent);
        ProcessingScreenDetails.ProcessingType selectedProcessingType = adapter.getItem(processingType.getSelectedItemPosition());
        details.setProcessingType(selectedProcessingType.getName());

        return JsonUtils.toJson(details);
    }
}
