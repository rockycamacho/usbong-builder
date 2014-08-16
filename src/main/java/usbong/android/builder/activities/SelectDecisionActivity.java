package usbong.android.builder.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;
import usbong.android.builder.R;
import usbong.android.builder.fragments.SelectScreenFragment;
import usbong.android.builder.fragments.dialogs.DecisionListDialogFragment;
import usbong.android.builder.utils.StringUtils;

import java.util.ArrayList;

public class SelectDecisionActivity extends ActionBarActivity {

    private static final String TAG = SelectDecisionActivity.class.getSimpleName();
    private static final int ADD_CHILD_REQUEST_CODE = 101;
    public static final String EXTRA_SCREEN_ID = "EXTRA_SCREEN_ID";
    public static final String EXTRA_TREE_ID = "EXTRA_TREE_ID";
    public static final String EXTRA_CONDITION = "EXTRA_CONDITION";
    public static final String EXTRA_POSSIBLE_DECISIONS = "EXTRA_POSSIBLE_DECISIONS";
    public static final String EXTRA_CONDITION_PREFIX = "EXTRA_CONDITION_PREFIX";
    public static final String DEFAULT_CONDITION_PREFIX = "DECISION";
    @InjectView(R.id.decision)
    FloatLabeledEditText decision;
    @InjectView(android.R.id.button1)
    Button selectScreen;

    private ArrayList<String> decisions = new ArrayList<String>();
    private long screenId = -1;
    private long treeId = -1;
    private String conditionPrefix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_decision);

        ButterKnife.inject(this);

        if (getIntent() != null) {
            screenId = getIntent().getLongExtra(EXTRA_SCREEN_ID, -1);
            treeId = getIntent().getLongExtra(EXTRA_TREE_ID, -1);
            conditionPrefix = getIntent().getStringExtra(EXTRA_CONDITION_PREFIX);
            if(conditionPrefix == null) {
                conditionPrefix = DEFAULT_CONDITION_PREFIX;
            }
            decisions = getIntent().getStringArrayListExtra(EXTRA_POSSIBLE_DECISIONS);
        }
        if (screenId == -1) {
            throw new IllegalArgumentException("screen id is required");
        }
        if (treeId == -1) {
            throw new IllegalArgumentException("tree id is required");
        }
    }

    @OnClick(android.R.id.button1)
    public void onSelectScreen() {
        if (StringUtils.isEmpty(decision.getText().toString().trim())) {
            Log.w(TAG, "Empty decision text");
            Toast.makeText(this, "Please input a decision", Toast.LENGTH_SHORT).show();
            return;
        }
        if(decisions != null && !decisions.isEmpty()) {
            boolean isValidDecisionText = false;
            for(String decisionText : decisions) {
                if(decision.getTextString().trim().equals(decisionText)) {
                    isValidDecisionText = true;
                    break;
                }
            }
            if(!isValidDecisionText) {
                Log.w(TAG, "Invalid decision text");
                Toast.makeText(this, "Please choose a decision text by selecting one from the list", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Intent data = new Intent(this, SelectScreenActivity.class);
        data.putExtra(SelectScreenFragment.EXTRA_SCREEN_RELATION, SelectScreenFragment.CHILD);
        data.putExtra(SelectScreenFragment.EXTRA_SCREEN_ID, screenId);
        data.putExtra(SelectScreenFragment.EXTRA_TREE_ID, treeId);
        startActivityForResult(data, ADD_CHILD_REQUEST_CODE);
    }

    @OnClick(android.R.id.button2)
    public void onOpenDialog() {
        DecisionListDialogFragment dialog = DecisionListDialogFragment.newInstance(decisions);
        dialog.setCallback(new DecisionListDialogFragment.Callback() {
            @Override
            public void onSelect(String text) {
                decision.setText(text);
            }
        });
        dialog.show(getSupportFragmentManager(), "DIALOG");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_CHILD_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                data.putExtra(EXTRA_CONDITION, conditionPrefix + "~" + decision.getText().toString().trim());
                setResult(Activity.RESULT_OK, data);
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
