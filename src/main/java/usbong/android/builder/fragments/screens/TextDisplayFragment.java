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
import usbong.android.builder.R;
import usbong.android.builder.activities.SelectDecisionActivity;
import usbong.android.builder.activities.SelectScreenActivity;
import usbong.android.builder.controllers.ScreenDetailController;
import usbong.android.builder.events.OnNeedRefreshScreen;
import usbong.android.builder.events.OnScreenDetailsSave;
import usbong.android.builder.events.OnScreenSave;
import usbong.android.builder.fragments.ScreenDetailFragment;
import usbong.android.builder.fragments.SelectScreenFragment;
import usbong.android.builder.models.Screen;
import usbong.android.builder.models.ScreenRelation;
import de.greenrobot.event.EventBus;
import rx.Observer;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Use the {@link TextDisplayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TextDisplayFragment extends Fragment {

    private static final String TAG = TextDisplayFragment.class.getSimpleName();
    private static final String EXTRA_SCREEN_ID = "EXTRA_SCREEN_ID";
    public static final int ADD_CHILD_REQUEST_CODE = 101;
    public static final String RELATION_CONDITION = "DEFAULT";

    private long screenId = -1;
    private long treeId = -1;
    private Screen currentScreen;

    private ScreenDetailController controller;
    @InjectView(R.id.name)
    EditText name;
    @InjectView(R.id.content)
//    RichEditText textDisplay;
    EditText textDisplay;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TextDisplayFragment.
     */
    public static TextDisplayFragment newInstance(Bundle args) {
        TextDisplayFragment fragment = new TextDisplayFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public TextDisplayFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            screenId = getArguments().getLong(ScreenDetailFragment.EXTRA_SCREEN_ID, -1);
            treeId = getArguments().getLong(ScreenDetailFragment.EXTRA_TREE_ID, -1);
        }
        if (screenId == -1) {
            throw new IllegalArgumentException("screen id is required");
        }
        if (treeId == -1) {
            throw new IllegalArgumentException("tree id is required");
        }
        Log.d(TAG, "currentScreen id: " + screenId);
        setHasOptionsMenu(true);
        controller = new ScreenDetailController();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_screen_type_text_display, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.inject(this, view);
//        textDisplay.enableActionModes(true);

        EventBus.getDefault().register(this);

        controller.loadScreen(screenId, new Observer<Screen>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, e.getMessage(), e);
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(Screen screen) {
                currentScreen = screen;
                Log.d(TAG, "currentScreen id3: " + currentScreen.getId());
                name.setText(currentScreen.name);
                String details = "";
                if(currentScreen.details != null) {
                    details = currentScreen.details;
                }
                SpannableString text = new SpannableString(Html.fromHtml(details));
                textDisplay.setText(text);
            }
        });
    }

    public void onEvent(OnScreenSave event) {
        Log.d(TAG, "onEvent(OnScreenSave event): " + textDisplay.getText().toString());

        String screenName = name.getText().toString();
        String screenContent = textDisplay.getText().toString().replaceAll("\n", "<br>");
        EventBus.getDefault().post(new OnScreenDetailsSave(screenName, screenContent));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.screen_detail, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_add_child) {
            Intent data = new Intent(getActivity(), SelectScreenActivity.class);
            data.putExtra(SelectScreenFragment.EXTRA_SCREEN_RELATION, SelectScreenFragment.CHILD);
            data.putExtra(SelectScreenFragment.EXTRA_SCREEN_ID, screenId);
            data.putExtra(SelectScreenFragment.EXTRA_TREE_ID, treeId);
            getParentFragment().startActivityForResult(data, ADD_CHILD_REQUEST_CODE);
        }
        if(item.getItemId() == R.id.action_remove_child) {
            controller.deleteAllChildScreens(currentScreen.getId(), new Observer<Object>() {
                @Override
                public void onCompleted() {
                    Toast.makeText(getActivity(), "Screen navigation removed", Toast.LENGTH_SHORT).show();
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
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult("+ requestCode +", "+ resultCode +", Intent data)");
        if(requestCode == ADD_CHILD_REQUEST_CODE) {
            Log.d(TAG, "requestCode == ADD_CHILD_REQUEST_CODE");
            if(resultCode == Activity.RESULT_OK) {
                updateChildren(data);
            }
        }
    }

    private void updateChildren(Intent data) {
        Log.d(TAG, "resultCode == Activity.RESULT_OK");
        long childScreenId = data.getLongExtra(SelectScreenFragment.EXTRA_SELECTED_SCREEN_ID, -1);

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
                replaceChildren(childScreen);
            }
        });
    }

    private void replaceChildren(final Screen childScreen) {
        controller.deleteAllChildScreens(currentScreen.getId(), new Observer<Object>() {
            @Override
            public void onCompleted() {
                saveChildScreen(childScreen);
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

    private void saveChildScreen(Screen childScreen) {
        ScreenRelation screenRelation = new ScreenRelation();
        screenRelation.parent = currentScreen;
        screenRelation.child = childScreen;
        screenRelation.condition = RELATION_CONDITION;
        final List<ScreenRelation> screenRelations = new ArrayList<ScreenRelation>();
        screenRelations.add(screenRelation);
        controller.add(screenRelations, new Observer<Object>() {

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
            public void onNext(Object o) {

            }
        });
    }
}
