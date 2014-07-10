package usbong.android.builder.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.activeandroid.query.Select;
import com.dd.processbutton.iml.ActionProcessButton;
import usbong.android.builder.R;
import usbong.android.builder.adapters.ChildrenScreensAdapter;
import usbong.android.builder.adapters.ParentsScreensAdapter;
import usbong.android.builder.controllers.ScreenDetailController;
import usbong.android.builder.enums.ScreenType;
import usbong.android.builder.events.OnNeedRefreshScreen;
import usbong.android.builder.events.OnScreenDetailsSave;
import usbong.android.builder.events.OnScreenSave;
import usbong.android.builder.fragments.screens.DecisionFragment;
import usbong.android.builder.fragments.screens.TextDisplayFragment;
import usbong.android.builder.models.Screen;
import usbong.android.builder.models.ScreenRelation;
import usbong.android.builder.utils.ScreenUtils;
import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import java.io.File;
import java.util.List;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Use the {@link ScreenDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScreenDetailFragment extends Fragment {

    public static final String TAG = ScreenDetailFragment.class.getSimpleName();
    public static final String EXTRA_SCREEN_ID = "EXTRA_SCREEN_ID";

    private long screenId = -1;
    private Screen currentScreen;

    private ParentsScreensAdapter parentScreenAdapter;
    private ChildrenScreensAdapter childrenScreenAdapter;

    private ScreenDetailController controller;

    @InjectView(R.id.screen_contents)
    LinearLayout screenContainer;
    @InjectView(R.id.parent_list)
    ListView parentListView;
    @InjectView(R.id.child_list)
    ListView childListView;

    @InjectView(android.R.id.button1)
    ActionProcessButton saveButton;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ScreenDetailFragment.
     */
    public static ScreenDetailFragment newInstance(Bundle args) {
        ScreenDetailFragment fragment = new ScreenDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public ScreenDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            screenId = getArguments().getLong(EXTRA_SCREEN_ID, -1);
        }
        if (screenId == -1) {
            throw new IllegalArgumentException("screen id is required");
        }
        parentScreenAdapter = new ParentsScreensAdapter(getActivity());
        childrenScreenAdapter = new ChildrenScreensAdapter(getActivity());

        controller = new ScreenDetailController();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_screen_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.inject(this, view);
        EventBus.getDefault().register(this);
        saveButton.setMode(ActionProcessButton.Mode.PROGRESS);
        parentListView.setAdapter(parentScreenAdapter);
        childListView.setAdapter(childrenScreenAdapter);
        initializeScreen();
    }

    private void initializeScreen() {
        controller.loadScreen(screenId, new Observer<Screen>() {

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, e.getMessage(), e);
            }

            @Override
            public void onNext(Screen screen) {
                currentScreen = screen;
                Fragment fragment = null;
                if (ScreenType.TEXT_DISPLAY.getName().equals(screen.screenType)) {
                    fragment = TextDisplayFragment.newInstance(getArguments());
                } else if (ScreenType.DECISION.getName().equals(screen.screenType)) {
                    fragment = DecisionFragment.newInstance(getArguments());
                } else {
                    throw new IllegalArgumentException("unhandled screen type: " + screen.screenType);
                }
                if (fragment != null) {
                    FragmentManager fragmentManager = getChildFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.screen_contents, fragment)
                            .commit();
                }
            }
        });
        refreshRelations();
    }

    @OnClick(android.R.id.button1)
    public void onSave() {
        saveButton.setProgress(1);
        EventBus.getDefault().post(OnScreenSave.EVENT);
    }

    public void onEvent(final OnNeedRefreshScreen event) {
        refreshRelations();
    }

    private void refreshRelations() {
        controller.loadParentScreens(screenId, new Observer<List<ScreenRelation>>() {

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, e.getMessage(), e);
            }

            @Override
            public void onNext(List<ScreenRelation> screens) {
                parentScreenAdapter.clear();
                parentScreenAdapter.addAll(screens);
            }
        });
        controller.loadChildrenScreens(screenId, new Observer<List<ScreenRelation>>() {

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, e.getMessage(), e);
            }

            @Override
            public void onNext(List<ScreenRelation> screenRelations) {
                childrenScreenAdapter.clear();
                childrenScreenAdapter.addAll(screenRelations);
            }
        });
    }

    public void onEvent(final OnScreenDetailsSave event) {
        Log.d(TAG, "onEvent(OnScreenDetailsSave event): " + event.getName() + ", " + event.getContent());
        currentScreen.name = event.getName();
        currentScreen.details = event.getContent();
        controller.saveScreen(currentScreen, screenContainer, new Observer<Screen>() {

            @Override
            public void onCompleted() {
                saveButton.setProgress(100);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, e.getMessage(), e);
                saveButton.setProgress(-1);
            }

            @Override
            public void onNext(Screen screen) {
                Log.d(TAG, "saved: " + screen.details);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + ", " + resultCode + ", Intent data)");
        for (Fragment fragment : getChildFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
