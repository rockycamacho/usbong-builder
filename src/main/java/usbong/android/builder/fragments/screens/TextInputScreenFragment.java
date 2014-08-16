package usbong.android.builder.fragments.screens;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.util.Log;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Toast;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;
import de.greenrobot.event.EventBus;
import rx.Observer;
import usbong.android.builder.R;
import usbong.android.builder.activities.SelectDecisionActivity;
import usbong.android.builder.activities.SelectScreenActivity;
import usbong.android.builder.events.OnNeedRefreshScreen;
import usbong.android.builder.exceptions.FormInputException;
import usbong.android.builder.fragments.SelectScreenFragment;
import usbong.android.builder.models.Screen;
import usbong.android.builder.models.ScreenRelation;
import usbong.android.builder.models.details.TextInputScreenDetails;
import usbong.android.builder.utils.JsonUtils;
import usbong.android.builder.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Created by Rocky Camacho on 8/7/2014.
 */
public class TextInputScreenFragment extends BaseScreenFragment {

    private static final Pattern VARIABLE_NAME = Pattern.compile("[a-z][a-zA-Z0-9]*");
    private static final int ADD_CHILD_FOR_ANSWER_REQUEST_CODE = 200;
    public static final String TAG = TextInputScreenFragment.class.getSimpleName();
    @InjectView(R.id.content)
    FloatLabeledEditText content;
    @InjectView(R.id.alphanumeric)
    RadioButton alphanumeric;
    @InjectView(R.id.has_multiple_lines)
    CheckBox hasMultipleLines;
    @InjectView(R.id.numeric)
    RadioButton numeric;
    @InjectView(R.id.has_unit)
    CheckBox hasUnit;
    @InjectView(R.id.unit)
    FloatLabeledEditText unit;
    @InjectView(R.id.should_store_variable)
    CheckBox shouldStoreVariable;
    @InjectView(R.id.variable)
    FloatLabeledEditText variable;
    @InjectView(R.id.has_answer)
    CheckBox hasAnswer;
    @InjectView(R.id.answers)
    FloatLabeledEditText answers;

    public static TextInputScreenFragment newInstance(Bundle args) {
        TextInputScreenFragment fragment = new TextInputScreenFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private TextInputScreenFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_screen_type_text_input;
    }

    @Override
    protected void onScreenLoad(Screen screen) {
        name.setText(screen.name);
        TextInputScreenDetails textInputScreenDetails = JsonUtils.fromJson(screen.details, TextInputScreenDetails.class);
        String details = "";
        if (currentScreen.details != null) {
            details = textInputScreenDetails.getText();
        }
        content.setText(new SpannableString(Html.fromHtml(details)));
        setInputType(TextInputScreenDetails.ALPHA_NUMERIC);  //Default
        if (TextInputScreenDetails.NUMERIC.equals(textInputScreenDetails.getInputType())) {
            setInputType(TextInputScreenDetails.NUMERIC);
        }
        hasMultipleLines.setChecked(textInputScreenDetails.isMultiLine());
        hasUnit.setChecked(textInputScreenDetails.isHasUnit());
        hasAnswer.setChecked(textInputScreenDetails.isHasAnswer());
        StringBuilder sb = new StringBuilder();
        if(textInputScreenDetails.getAnswers() != null) {
            for (String answer : textInputScreenDetails.getAnswers()) {
                if (sb.length() > 0) {
                    sb.append("\n");
                }
                sb.append(answer);
            }
        }
        answers.setText(sb.toString());
        shouldStoreVariable.setChecked(textInputScreenDetails.isStoreVariable());
        unit.setText(textInputScreenDetails.getUnit());
        variable.setText(textInputScreenDetails.getVariableName());
    }

    private void setInputType(String inputType) {
        if (TextInputScreenDetails.ALPHA_NUMERIC.equals(inputType)) {
            alphanumeric.setChecked(true);  //Default
            numeric.setChecked(false);
            hasMultipleLines.setEnabled(true);
            hasUnit.setEnabled(false);
            unit.setEnabled(false);
            hasAnswer.setEnabled(true);
            if (hasAnswer.isChecked()) {
                answers.setEnabled(true);
            }
        } else if (TextInputScreenDetails.NUMERIC.equals(inputType)) {
            numeric.setChecked(true);
            alphanumeric.setChecked(false);
            hasMultipleLines.setEnabled(false);
            hasUnit.setEnabled(true);
            hasAnswer.setEnabled(false);
            answers.setEnabled(false);
            if (hasUnit.isChecked()) {
                unit.setEnabled(true);
            }
        }
    }

    @OnCheckedChanged(R.id.has_unit)
    public void onUnitCheckChange(boolean isChecked) {
        unit.setEnabled(isChecked);
        unit.requestFieldFocus();
    }

    @OnCheckedChanged(R.id.has_answer)
    public void onHasAnswerChange(boolean isChecked) {
        answers.setEnabled(isChecked);
        answers.requestFieldFocus();
    }

    @OnCheckedChanged(R.id.should_store_variable)
    public void onStoreVariableCheckChange(boolean isChecked) {
        variable.setEnabled(isChecked);
        variable.requestFieldFocus();
    }

    @OnClick(R.id.alphanumeric)
    public void onClickAlphaNumeric() {
        setInputType(TextInputScreenDetails.ALPHA_NUMERIC);
    }

    @OnClick(R.id.numeric)
    public void onClickNumeric() {
        setInputType(TextInputScreenDetails.NUMERIC);
    }

    @Override
    protected String convertFormDataToScreenDetails() throws Exception {
        TextInputScreenDetails textInputScreenDetails = new TextInputScreenDetails();
        textInputScreenDetails.setHasUnit(hasUnit.isChecked());
        if (alphanumeric.isChecked()) {
            textInputScreenDetails.setInputType(TextInputScreenDetails.ALPHA_NUMERIC);
        }
        if (numeric.isChecked()) {
            textInputScreenDetails.setInputType(TextInputScreenDetails.NUMERIC);
        }
        textInputScreenDetails.setMultiLine(hasMultipleLines.isChecked());
        textInputScreenDetails.setStoreVariable(shouldStoreVariable.isChecked());
        textInputScreenDetails.setText(content.getTextString());
        if (hasUnit.isChecked() && StringUtils.isEmpty(unit.getTextString())) {
            throw new FormInputException("Please input a unit of measure");
        }
        textInputScreenDetails.setUnit(unit.getTextString());
        String variableName = variable.getTextString();
        if (shouldStoreVariable.isChecked() && StringUtils.isEmpty(variableName)) {
            throw new FormInputException("Please input a variable name");
        }
        if (shouldStoreVariable.isChecked() && !VARIABLE_NAME.matcher(variableName).matches()) {
            throw new FormInputException("Variable names should begin with a letter and must be followed by letters or numbers");
        }
        textInputScreenDetails.setHasAnswer(hasAnswer.isChecked());
        String[] answersArray = answers.getTextString().trim().split("\n");
        if (answersArray.length == 0 && textInputScreenDetails.isHasAnswer()) {
            throw new FormInputException("Please input at least one answer");
        }
        textInputScreenDetails.setAnswers(Arrays.asList(answersArray));
        textInputScreenDetails.setVariableName(variableName);
        return JsonUtils.toJson(textInputScreenDetails);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_child) {
            if(hasAnswer.isChecked()) {
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
            controller.deleteAllChildScreens(currentScreen.getId(), new Observer<Object>() {
                @Override
                public void onCompleted() {
                    Toast.makeText(getActivity(), "Screen navigation removed", Toast.LENGTH_SHORT).show();
                    EventBus.getDefault().post(OnNeedRefreshScreen.EVENT);
                }

                @Override
                public void onError(Throwable e) {
                    Log.e(TAG, e.getMessage(), e);
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNext(Object o) {

                }
            });
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
