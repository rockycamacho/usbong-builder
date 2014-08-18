package usbong.android.builder.fragments.dialogs;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Use the {@link AddingChildToItselfWarningDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddingChildToItselfWarningDialogFragment extends DialogFragment {

    public static final String TAG = AddingChildToItselfWarningDialogFragment.class.getSimpleName();
    private Callback callback;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AddingChildToItselfWarningDialogFragment.
     */
    public static AddingChildToItselfWarningDialogFragment newInstance() {
        AddingChildToItselfWarningDialogFragment fragment = new AddingChildToItselfWarningDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public AddingChildToItselfWarningDialogFragment() {
        // Required empty public constructor
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setMessage("Note: You are connecting a node to itself. Proceed?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (callback != null) {
                            callback.onYes();
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (callback != null) {
                            callback.onNo();
                        }
                    }
                })
                .create();
    }

    public interface Callback {
        void onYes();

        void onNo();
    }
}
