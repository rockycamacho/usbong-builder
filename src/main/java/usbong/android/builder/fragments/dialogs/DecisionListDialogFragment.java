package usbong.android.builder.fragments.dialogs;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import usbong.android.builder.R;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Use the {@link usbong.android.builder.fragments.dialogs.DecisionListDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DecisionListDialogFragment extends DialogFragment {

    private Callback callback;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AddingChildToItselfWarningDialogFragment.
     */
    public static DecisionListDialogFragment newInstance() {
        DecisionListDialogFragment fragment = new DecisionListDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public DecisionListDialogFragment() {
        // Required empty public constructor
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String[] items = getResources().getStringArray(R.array.decisions);
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
