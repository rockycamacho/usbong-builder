package usbong.android.builder.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import com.dd.processbutton.iml.ActionProcessButton;
import de.greenrobot.event.EventBus;
import rx.Observer;
import usbong.android.builder.R;
import usbong.android.builder.activities.ScreenDetailActivity;
import usbong.android.builder.adapters.ChildrenScreensAdapter;
import usbong.android.builder.adapters.ParentsScreensAdapter;
import usbong.android.builder.controllers.ScreenDetailController;
import usbong.android.builder.events.OnNeedRefreshScreen;
import usbong.android.builder.events.OnScreenDetailsSave;
import usbong.android.builder.events.OnScreenDetailsSaveError;
import usbong.android.builder.events.OnScreenSave;
import usbong.android.builder.fragments.screens.ScreenFragmentFactory;
import usbong.android.builder.models.Screen;
import usbong.android.builder.models.ScreenRelation;

import java.util.List;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Use the {@link ScreenDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScreenDetailFragment extends Fragment {

    public static final String TAG = ScreenDetailFragment.class.getSimpleName();
    public static final String EXTRA_SCREEN_ID = "EXTRA_SCREEN_ID";
    public static final String EXTRA_TREE_ID = "EXTRA_TREE_ID";
    @InjectView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @InjectView(R.id.left_drawer)
    LinearLayout mLeftDrawer;
    @InjectView(R.id.right_drawer)
    LinearLayout mRightDrawer;

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
    @InjectView(android.R.id.button2)
    ActionProcessButton saveAndExitButton;


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
                if(e.getMessage() != null) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNext(Screen screen) {
                currentScreen = screen;
                Fragment fragment = ScreenFragmentFactory.getFragment(screen.screenType, getArguments());
                FragmentManager fragmentManager = getChildFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.screen_contents, fragment)
                        .commit();
            }
        });
        refreshRelations();
    }

    @OnClick(android.R.id.button1)
    public void onSave() {
        saveButton.setProgress(1);
        saveAndExitButton.setEnabled(false);
        EventBus.getDefault().post(OnScreenSave.EVENT);
    }

    @OnClick(android.R.id.button2)
    public void onSaveAndExit() {
        saveAndExitButton.setProgress(1);
        saveButton.setEnabled(false);
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
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
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
        Log.d(TAG, "currentScreen: " + currentScreen.name + ", " + currentScreen.details);
        currentScreen.name = event.getName();
        currentScreen.details = event.getContent();
        Log.d(TAG, "currentScreen: " + currentScreen.name + ", " + currentScreen.details);
        controller.saveScreen(currentScreen, event, screenContainer, new Observer<Screen>() {

            @Override
            public void onCompleted() {
                saveButton.setProgress(100);
                if (saveButton.getProgress() > 0) {
                    saveButton.setProgress(100);
                    saveButton.setEnabled(true);
                    saveAndExitButton.setEnabled(true);
                }
                if (saveAndExitButton.getProgress() > 0) {
                    saveAndExitButton.setProgress(100);
                    saveButton.setEnabled(true);
                    saveAndExitButton.setEnabled(true);
                    getActivity().finish();
                }
            }

            @Override
            public void onError(Throwable e) {
                showErrorButton(e);
            }

            @Override
            public void onNext(Screen screen) {
                Log.d(TAG, "saved: " + screen.name + screen.details);
            }
        });
    }

    public void onEvent(OnScreenDetailsSaveError event) {
        Log.e(TAG, event.getException().getMessage(), event.getException());
        if (saveButton.getProgress() > 0) {
            saveButton.setProgress(-1);
        }
        if (saveAndExitButton.getProgress() > 0) {
            saveAndExitButton.setProgress(-1);
        }
        saveButton.setEnabled(true);
        saveAndExitButton.setEnabled(true);
    }

    private void showErrorButton(Throwable e) {
        Log.e(TAG, e.getMessage(), e);
        if (saveButton.getProgress() > 0) {
            saveButton.setProgress(-1);
        }
        if (saveAndExitButton.getProgress() > 0) {
            saveAndExitButton.setProgress(-1);
        }
        saveButton.setEnabled(true);
        saveAndExitButton.setEnabled(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + ", " + resultCode + ", Intent data)");
        List<Fragment> fragments = getChildFragmentManager().getFragments();
        if(fragments != null) {
            for (Fragment fragment : fragments) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnItemClick(R.id.parent_list)
    public void onClickParent(int position) {
        ScreenRelation screenRelation = parentScreenAdapter.getItem(position);
        Intent intent = new Intent(getActivity(), ScreenDetailActivity.class);
        intent.putExtra(ScreenDetailFragment.EXTRA_SCREEN_ID, screenRelation.parent.getId().longValue());
        intent.putExtra(ScreenDetailFragment.EXTRA_TREE_ID, getArguments().getLong(EXTRA_TREE_ID));
        startActivity(intent);
        getActivity().finish();
    }

    @OnItemClick(R.id.child_list)
    public void onClickChild(int position) {
        ScreenRelation screenRelation = childrenScreenAdapter.getItem(position);
        Intent intent = new Intent(getActivity(), ScreenDetailActivity.class);
        intent.putExtra(ScreenDetailFragment.EXTRA_SCREEN_ID, screenRelation.child.getId().longValue());
        intent.putExtra(ScreenDetailFragment.EXTRA_TREE_ID, getArguments().getLong(EXTRA_TREE_ID));
        startActivity(intent);
        getActivity().finish();
    }

    @OnClick(R.id.slide_left)
    public void slideLeft() {
        System.out.println("mDrawerLayout.openDrawer(mLeftDrawer)");
        mDrawerLayout.openDrawer(mLeftDrawer);
    }

    @OnClick(R.id.slide_right)
    public void slideRight() {
        System.out.println("mDrawerLayout.openDrawer(mRightDrawer)");
        mDrawerLayout.openDrawer(mRightDrawer);
    }
}
