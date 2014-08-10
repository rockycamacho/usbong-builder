package usbong.android.builder.fragments.screens;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.SpannableString;
import android.util.Log;
import android.view.*;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;
import de.greenrobot.event.EventBus;
import rx.Observer;
import usbong.android.builder.R;
import usbong.android.builder.activities.SelectDecisionActivity;
import usbong.android.builder.controllers.ScreenDetailController;
import usbong.android.builder.events.OnNeedRefreshScreen;
import usbong.android.builder.events.OnScreenDetailsSave;
import usbong.android.builder.events.OnScreenSave;
import usbong.android.builder.fragments.ScreenDetailFragment;
import usbong.android.builder.fragments.SelectScreenFragment;
import usbong.android.builder.models.Screen;
import usbong.android.builder.models.ScreenRelation;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Use the {@link DecisionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DecisionFragment extends BaseScreenFragment {

    private static final String TAG = DecisionFragment.class.getSimpleName();
    @InjectView(R.id.content)
    FloatLabeledEditText textDisplay;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TextDisplayFragment.
     */
    public static DecisionFragment newInstance(Bundle args) {
        DecisionFragment fragment = new DecisionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public DecisionFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_screen_type_decision;
    }

    @Override
    protected void onScreenLoad(Screen screen) {
        name.setText(currentScreen.name);
        String details = "";
        if (currentScreen.details != null) {
            details = currentScreen.details;
        }
        SpannableString text = new SpannableString(Html.fromHtml(details));
        textDisplay.setText(text);
    }

    @Override
    protected String convertFormDataToScreenDetails() throws Exception {
        return textDisplay.getText().toString().replaceAll("\n", "<br>");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.decision, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_child) {
            Intent data = new Intent(getActivity(), SelectDecisionActivity.class);
            data.putExtra(SelectDecisionActivity.EXTRA_SCREEN_ID, screenId);
            data.putExtra(SelectDecisionActivity.EXTRA_TREE_ID, treeId);
            getParentFragment().startActivityForResult(data, ADD_CHILD_REQUEST_CODE);
        }
        //TODO: change this to remove a single child selected by the user
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
        Log.d(TAG, "onActivityResult(" + requestCode + ", " + resultCode + ", Intent data)");
        if (requestCode == ADD_CHILD_REQUEST_CODE) {
            Log.d(TAG, "requestCode == ADD_CHILD_REQUEST_CODE");
            if (resultCode == Activity.RESULT_OK) {
                updateChildren(data);
            }
        }
    }

    @Override
    protected void updateChildren(Intent data) {
        Log.d(TAG, "resultCode == Activity.RESULT_OK");
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
                addDecision(childScreen, condition);
            }
        });
    }

    private void addDecision(Screen childScreen, String condition) {
        controller.addRelation(currentScreen, childScreen, condition, new Observer<ScreenRelation>() {
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
