package usbong.android.builder.fragments.screens;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import butterknife.InjectView;
import butterknife.OnItemSelected;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;
import de.greenrobot.event.EventBus;
import rx.Observer;
import usbong.android.builder.R;
import usbong.android.builder.activities.SelectDecisionActivity;
import usbong.android.builder.activities.SelectScreenActivity;
import usbong.android.builder.adapters.ListTypeAdapter;
import usbong.android.builder.events.OnNeedRefreshScreen;
import usbong.android.builder.exceptions.FormInputException;
import usbong.android.builder.fragments.SelectScreenFragment;
import usbong.android.builder.models.Screen;
import usbong.android.builder.models.ScreenRelation;
import usbong.android.builder.models.details.ListScreenDetails;
import usbong.android.builder.utils.JsonUtils;
import usbong.android.builder.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Rocky Camacho on 8/13/2014.
 */
public class ListScreenFragment extends BaseScreenFragment {

    public static final String TAG = ListScreenFragment.class.getSimpleName();
    public static final int ADD_CHILD_FOR_ANSWER_REQUEST_CODE = 200;
    @InjectView(R.id.content)
    FloatLabeledEditText mContent;
    @InjectView(R.id.type)
    Spinner mType;
    @InjectView(R.id.items)
    FloatLabeledEditText mItems;
    @InjectView(R.id.has_answer)
    CheckBox hasAnswer;
    @InjectView(R.id.answer)
    FloatLabeledEditText answer;
    @InjectView(R.id.number_of_checks_needed)
    FloatLabeledEditText numberOfChecksNeeded;

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
        adapter.add(ListScreenDetails.ListType.ANY_ANSWER);
        adapter.add(ListScreenDetails.ListType.SINGLE_ANSWER);
        adapter.add(ListScreenDetails.ListType.MULTIPLE_ANSWERS);
        adapter.notifyDataSetChanged();
        mType.setAdapter(adapter);
        numberOfChecksNeeded.setText("1");
    }

    @OnItemSelected(R.id.type)
    public void onTypeSelected() {
        ListScreenDetails.ListType selectedListType = adapter.getItem(mType.getSelectedItemPosition());
        if(ListScreenDetails.ListType.ANY_ANSWER.equals(selectedListType)) {
            hasAnswer.setVisibility(View.GONE);
            answer.setVisibility(View.GONE);
            numberOfChecksNeeded.setVisibility(View.GONE);
        }
        else if(ListScreenDetails.ListType.SINGLE_ANSWER.equals(selectedListType)) {
            hasAnswer.setVisibility(View.VISIBLE);
            answer.setVisibility(View.VISIBLE);
            numberOfChecksNeeded.setVisibility(View.GONE);
        }
        else if(ListScreenDetails.ListType.MULTIPLE_ANSWERS.equals(selectedListType)) {
            hasAnswer.setVisibility(View.GONE);
            answer.setVisibility(View.GONE);
            numberOfChecksNeeded.setVisibility(View.VISIBLE);
        }
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
            for (String item : listScreenDetails.getItems()) {
                if (sb.length() > 0) {
                    sb.append("\n");
                }
                sb.append(item);
            }
            mItems.setText(sb.toString());
            hasAnswer.setChecked(listScreenDetails.isHasAnswer());
            answer.setText(String.valueOf(listScreenDetails.getAnswer()));
            numberOfChecksNeeded.setText(String.valueOf(listScreenDetails.getNumberOfChecksNeeded()));

            onTypeSelected();
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

        listScreenDetails.setHasAnswer(hasAnswer.isChecked());
        String[] answersArray = answer.getTextString().trim().split("\n");
        int answerIndex = Integer.valueOf(answer.getTextString());
        if (hasAnswer.isChecked() && items.length <= answerIndex) {
            throw new FormInputException("Invalid answer position");
        }
        if (hasAnswer.isChecked() && answerIndex < 0) {
            throw new FormInputException("Invalid answer position");
        }
        listScreenDetails.setAnswer(answerIndex);
        if(ListScreenDetails.ListType.MULTIPLE_ANSWERS.equals(selectedListType)) {
            try {
                int checksNeeded = Integer.parseInt(numberOfChecksNeeded.getTextString());
                if(checksNeeded > listScreenDetails.getItems().size()) {
                    throw new FormInputException("Number of checks is bigger than number of items in list");
                }
                if(checksNeeded <= 0) {
                    throw new FormInputException("Invalid value for number of checks");
                }
                listScreenDetails.setNumberOfChecksNeeded(checksNeeded);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
                throw new FormInputException("Invalid value for number of checks");
            }
        }
        return JsonUtils.toJson(listScreenDetails);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_child) {
            ListScreenDetails.ListType selectedListType = adapter.getItem(mType.getSelectedItemPosition());
            if((ListScreenDetails.ListType.SINGLE_ANSWER.equals(selectedListType) && hasAnswer.isChecked()) ||
                    ListScreenDetails.ListType.MULTIPLE_ANSWERS.equals(selectedListType)) {
                Intent data = new Intent(getActivity(), SelectDecisionActivity.class);
                ArrayList<String> decisions = new ArrayList<String>();
                decisions.add("Correct");
                decisions.add("Incorrect");
                data.putStringArrayListExtra(SelectDecisionActivity.EXTRA_POSSIBLE_DECISIONS, decisions);
                data.putExtra(SelectDecisionActivity.EXTRA_SCREEN_ID, screenId);
                data.putExtra(SelectDecisionActivity.EXTRA_TREE_ID, treeId);
                data.putExtra(SelectDecisionActivity.EXTRA_CONDITION_PREFIX, "ANSWER");
                getParentFragment().startActivityForResult(data, ADD_CHILD_FOR_ANSWER_REQUEST_CODE);
            }
            else {
                Intent data = new Intent(getActivity(), SelectScreenActivity.class);
                data.putExtra(SelectScreenFragment.EXTRA_SCREEN_RELATION, SelectScreenFragment.CHILD);
                data.putExtra(SelectScreenFragment.EXTRA_SCREEN_ID, screenId);
                data.putExtra(SelectScreenFragment.EXTRA_TREE_ID, treeId);
                getParentFragment().startActivityForResult(data, ADD_CHILD_REQUEST_CODE);
            }
        }
        if (item.getItemId() == R.id.action_remove_child) {
            Intent data = new Intent(getActivity(), SelectScreenActivity.class);
            data.putExtra(SelectScreenFragment.EXTRA_SCREEN_ID, screenId);
            data.putExtra(SelectScreenFragment.EXTRA_TREE_ID, treeId);
            data.putExtra(SelectScreenFragment.EXTRA_IS_FOR_DELETE_CHILD, true);
            getParentFragment().startActivityForResult(data, DELETE_SELECTED_CHILD_REQUEST_CODE);
        }
        return true;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult(" + requestCode + ", " + resultCode + ", Intent data)");
        if (requestCode == ADD_CHILD_FOR_ANSWER_REQUEST_CODE) {
            Log.d(TAG, "requestCode == ADD_CHILD_FOR_ANSWER_REQUEST_CODE");
            if (resultCode == Activity.RESULT_OK) {
                updateChildrenForAnswers(data);
            }
        }
    }

    private void updateChildrenForAnswers(Intent data) {
        long childScreenId = data.getLongExtra(SelectScreenFragment.EXTRA_SELECTED_SCREEN_ID, -1);
        final String condition = data.getStringExtra(SelectDecisionActivity.EXTRA_CONDITION);
        controller.loadScreen(childScreenId, new Observer<Screen>() {

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, e.getMessage(), e);
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(Screen childScreen) {
                addOrUpdateDecision(childScreen, condition);
            }
        });
    }

    private void addOrUpdateDecision(Screen childScreen, String condition) {
        controller.addOrUpdateRelation(currentScreen, childScreen, condition, new Observer<ScreenRelation>() {
            @Override
            public void onCompleted() {
                Toast.makeText(getActivity(), "Screen navigation saved", Toast.LENGTH_SHORT).show();
                EventBus.getDefault().post(OnNeedRefreshScreen.EVENT);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, e.getMessage(), e);
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(ScreenRelation screenRelation) {

            }
        });
    }


}
