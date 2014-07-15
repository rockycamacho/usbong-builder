package usbong.android.builder.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import usbong.android.builder.R;
import usbong.android.builder.fragments.SelectScreenFragment;

public class SelectDecisionActivity extends Activity {

    private static final String TAG = SelectDecisionActivity.class.getSimpleName();
    private static final int ADD_CHILD_REQUEST_CODE = 101;
    public static final String EXTRA_SCREEN_ID = "EXTRA_SCREEN_ID";
    public static final String EXTRA_TREE_ID = "EXTRA_TREE_ID";
    public static final String EXTRA_CONDITION = "EXTRA_CONDITION";
    @InjectView(R.id.decision)
    EditText decision;
    @InjectView(android.R.id.button1)
    Button selectScreen;

    private long screenId = -1;
    private long treeId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_decision);

        ButterKnife.inject(this);

        if(getIntent() != null) {
            screenId = getIntent().getLongExtra(EXTRA_SCREEN_ID, -1);
            treeId = getIntent().getLongExtra(EXTRA_TREE_ID, -1);
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
        Intent data = new Intent(this, SelectScreenActivity.class);
        data.putExtra(SelectScreenFragment.EXTRA_SCREEN_RELATION, SelectScreenFragment.CHILD);
        data.putExtra(SelectScreenFragment.EXTRA_SCREEN_ID, screenId);
        data.putExtra(SelectScreenFragment.EXTRA_TREE_ID, treeId);
        startActivityForResult(data, ADD_CHILD_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == ADD_CHILD_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK) {
                data.putExtra(EXTRA_CONDITION, "DECISION" + "~" + decision.getText().toString());
                setResult(Activity.RESULT_OK, data);
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
