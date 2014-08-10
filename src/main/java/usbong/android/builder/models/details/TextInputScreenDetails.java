package usbong.android.builder.models.details;

import com.google.gson.annotations.Expose;

/**
 * Created by Rocky Camacho on 8/7/2014.
 */
public class TextInputScreenDetails {

    public static final String NUMERIC = "NUMERIC";
    public static final String ALPHA_NUMERIC = "ALPHA_NUMERIC";
    @Expose
    private String text;
    @Expose
    private String inputType;
    @Expose
    private boolean isMultiLine;
    @Expose
    private boolean hasUnit;
    @Expose
    private String unit;
    @Expose
    private boolean storeVariable;
    @Expose
    private String variableName;

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public boolean isMultiLine() {
        return isMultiLine;
    }

    public void setMultiLine(boolean isMultiLine) {
        this.isMultiLine = isMultiLine;
    }

    public boolean isHasUnit() {
        return hasUnit;
    }

    public void setHasUnit(boolean hasUnit) {
        this.hasUnit = hasUnit;
    }

    public boolean isStoreVariable() {
        return storeVariable;
    }

    public void setStoreVariable(boolean storeVariable) {
        this.storeVariable = storeVariable;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
