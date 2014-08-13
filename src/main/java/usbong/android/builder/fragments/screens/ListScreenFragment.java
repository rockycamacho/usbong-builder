package usbong.android.builder.fragments.screens;

import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.view.View;
import android.widget.Spinner;
import butterknife.InjectView;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;
import usbong.android.builder.R;
import usbong.android.builder.adapters.ListTypeAdapter;
import usbong.android.builder.adapters.ProcessingTypeAdapter;
import usbong.android.builder.enums.ImagePosition;
import usbong.android.builder.models.Screen;
import usbong.android.builder.models.details.ImageScreenDetails;
import usbong.android.builder.models.details.ListScreenDetails;
import usbong.android.builder.models.details.ProcessingScreenDetails;
import usbong.android.builder.utils.JsonUtils;
import usbong.android.builder.utils.StringUtils;

import java.util.Arrays;

/**
 * Created by Rocky Camacho on 8/13/2014.
 */
public class ListScreenFragment extends BaseScreenFragment {

    @InjectView(R.id.content)
    FloatLabeledEditText mContent;
    @InjectView(R.id.type)
    Spinner mType;
    @InjectView(R.id.items)
    FloatLabeledEditText mItems;

    private ListTypeAdapter adapter;

    public static ListScreenFragment newInstance(Bundle args) {
        ListScreenFragment fragment = new ListScreenFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private ListScreenFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_screen_type_list;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ListTypeAdapter(getActivity());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter.setNotifyOnChange(false);
        adapter.add(ListScreenDetails.ListType.NO_RESPONSE);
        adapter.add(ListScreenDetails.ListType.SINGLE_RESPONSE);
        adapter.notifyDataSetChanged();
        mType.setAdapter(adapter);
    }

    @Override
    protected void onScreenLoad(Screen screen) {
        name.setText(currentScreen.name);
        String details = StringUtils.EMPTY;
        if (currentScreen.details != null) {
            ListScreenDetails listScreenDetails = JsonUtils.fromJson(currentScreen.details, ListScreenDetails.class);
            details = listScreenDetails.getText();

            if (!StringUtils.isEmpty(listScreenDetails.getType())) {
                ListScreenDetails.ListType listType = ListScreenDetails.ListType.from(listScreenDetails.getType());
                mType.setSelection(adapter.getPosition(listType));
            }
            StringBuilder sb = new StringBuilder();
            for(String item : listScreenDetails.getItems()) {
                if(sb.length() > 0) {
                    sb.append("\n");
                }
                sb.append(item);
            }
            mItems.setText(sb.toString());
        }
        mContent.setText(new SpannableString(Html.fromHtml(details)));
    }

    @Override
    protected String convertFormDataToScreenDetails() throws Exception {
        ListScreenDetails listScreenDetails = new ListScreenDetails();
        String screenContent = mContent.getText().toString().replaceAll("\n", "<br>");
        listScreenDetails.setText(screenContent);
        String[] items = mItems.getText().toString().split("\n");
        listScreenDetails.setItems(Arrays.asList(items));

        ListScreenDetails.ListType selectedListType = adapter.getItem(mType.getSelectedItemPosition());
        listScreenDetails.setType(selectedListType.getName());

        return JsonUtils.toJson(listScreenDetails);
    }
}
