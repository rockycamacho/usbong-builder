package usbong.android.builder.models.details;

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by Rocky Camacho on 8/13/2014.
 */
public class ListScreenDetails {

    public static enum ListType {
        NO_RESPONSE("NO_RESPONSE"),
        SINGLE_RESPONSE("SINGLE_RESPONSE"),
        MULTIPLE_RESPONSE("MULTIPLE_RESPONSE");

        private final String name;

        private ListType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static ListType from(String name) {
            for (ListType listType : ListType.values()) {
                if (listType.getName().equals(name)) {
                    return listType;
                }
            }
            return null;
        }
    }

    @Expose
    private String text;
    @Expose
    private List<String> items;
    @Expose
    private String type;
    @Expose
    private boolean hasAnswer;
    @Expose
    private List<String> answers;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isHasAnswer() {
        return hasAnswer;
    }

    public void setHasAnswer(boolean hasAnswer) {
        this.hasAnswer = hasAnswer;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }
}
