package usbong.android.builder.fragments.screens;

import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.widget.CheckBox;
import android.widget.RadioButton;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;
import usbong.android.builder.R;
import usbong.android.builder.exceptions.FormInputException;
import usbong.android.builder.models.Screen;
import usbong.android.builder.models.details.TextInputScreenDetails;
import usbong.android.builder.utils.JsonUtils;
import usbong.android.builder.utils.StringUtils;

import java.util.regex.Pattern;

/**
 * Created by Rocky Camacho on 8/7/2014.
 */
public class TextInputScreenFragment extends BaseScreenFragment {

    private static final Pattern VARIABLE_NAME = Pattern.compile("[a-z][a-zA-Z0-9]*");
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
        } else if (TextInputScreenDetails.NUMERIC.equals(inputType)) {
            numeric.setChecked(true);
            alphanumeric.setChecked(false);
            hasMultipleLines.setEnabled(false);
            hasUnit.setEnabled(true);
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
        textInputScreenDetails.setVariableName(variableName);
        return JsonUtils.toJson(textInputScreenDetails);
    }
}
