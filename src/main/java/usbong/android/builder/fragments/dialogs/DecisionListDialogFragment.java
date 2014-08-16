package usbong.android.builder.fragments.dialogs;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import usbong.android.builder.R;
import usbong.android.builder.activities.SelectDecisionActivity;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Use the {@link usbong.android.builder.fragments.dialogs.DecisionListDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DecisionListDialogFragment extends DialogFragment {

    private Callback callback;
    private ArrayList<String> decisions;
    private String[] items;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AddingChildToItselfWarningDialogFragment.
     * @param decisions
     */
    public static DecisionListDialogFragment newInstance(ArrayList<String> decisions) {
        DecisionListDialogFragment fragment = new DecisionListDialogFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(SelectDecisionActivity.EXTRA_POSSIBLE_DECISIONS, decisions);
        fragment.setArguments(args);
        return fragment;
    }

    public DecisionListDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        decisions = getArguments().getStringArrayList(SelectDecisionActivity.EXTRA_POSSIBLE_DECISIONS);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        items = getResources().getStringArray(R.array.decisions);
        if(decisions != null && !decisions.isEmpty()) {
            items = decisions.toArray(new String[decisions.size()]);
        }
        return new AlertDialog.Builder(getActivity())
                .setTitle("Decision template")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (callback != null) {
                            callback.onSelect(items[which]);
                            dialog.dismiss();
                        }
                    }
                })
                .create();
    }

    public interface Callback {
        void onSelect(String text);
    }
}
