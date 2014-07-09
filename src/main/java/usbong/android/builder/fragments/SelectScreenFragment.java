package usbong.android.builder.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import usbong.android.builder.R;
import usbong.android.builder.adapters.ScreenAdapter;
import usbong.android.builder.controllers.SelectScreenController;
import usbong.android.builder.models.Screen;
import com.squareup.picasso.Picasso;
import rx.Observer;

import java.util.List;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Use the {@link SelectScreenFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class SelectScreenFragment extends Fragment {

    public static final String EXTRA_SCREEN_ID = "EXTRA_SCREEN_ID";
    public static final String EXTRA_SCREEN_RELATION = "EXTRA_SCREEN_RELATION";
    public static final String PARENT = "PARENT";
    public static final String CHILD = "CHILD";
    private static final String TAG = SelectScreenFragment.class.getSimpleName();
    public static final String EXTRA_SELECTED_SCREEN_ID = "EXTRA_SELECTED_SCREEN_ID";

    private long screenId = -1;
    private Screen screen;

    private ScreenAdapter adapter;

    private String screenRelation;

    private SelectScreenController controller;

    @InjectView(android.R.id.list)
    ListView listView;

    @InjectView(R.id.image)
    ImageView image;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SelectScreenFragment.
     */
    public static SelectScreenFragment newInstance(Bundle args) {
        SelectScreenFragment fragment = new SelectScreenFragment();
        fragment.setArguments(args);
        return fragment;
    }
    public SelectScreenFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            screenId = getArguments().getLong(EXTRA_SCREEN_ID, -1);
        }
        if(screenId == -1) {
            throw new IllegalArgumentException("screen id is required");
        }
        controller = new SelectScreenController();
        adapter = new ScreenAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_screen, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.inject(this, view);
        listView.setAdapter(adapter);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        controller.fetchScreens(new Observer<List<Screen>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, e.getMessage(), e);
            }

            @Override
            public void onNext(List<Screen> screens) {
                adapter.clear();
                adapter.addAll(screens);
            }
        });
    }

    @OnItemClick(android.R.id.list)
    public void onItemClick(int position) {
        Screen screen = adapter.getItem(position);
        Picasso.with(getActivity())
                .load(getActivity().getFileStreamPath(screen.getScreenshotPath()))
                .fit()
                .into(image);
    }

    @OnClick(android.R.id.button1)
    public void onSelectScreen() {
        if(listView.getSelectedItem() != null) {
            Toast.makeText(getActivity(), "Select a screen first", Toast.LENGTH_SHORT).show();
            return;
        }
        Screen screen = adapter.getItem(listView.getCheckedItemPosition());
        Intent data = new Intent();
        data.putExtra(EXTRA_SELECTED_SCREEN_ID, screen.getId().longValue());
        getActivity().setResult(Activity.RESULT_OK, data);
        getActivity().finish();
    }
}
