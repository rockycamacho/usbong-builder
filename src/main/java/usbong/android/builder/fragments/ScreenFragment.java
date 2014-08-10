package usbong.android.builder.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.activeandroid.query.Select;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;
import rx.Observer;
import usbong.android.builder.R;
import usbong.android.builder.activities.ScreenDetailActivity;
import usbong.android.builder.adapters.ScreenTypeAdapter;
import usbong.android.builder.controllers.ScreenController;
import usbong.android.builder.enums.UsbongBuilderScreenType;
import usbong.android.builder.models.Screen;
import usbong.android.builder.models.Utree;
import usbong.android.builder.models.details.ScreenDetailsFactory;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Use the {@link UtreeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScreenFragment extends Fragment {

    public static final String TAG = ScreenFragment.class.getSimpleName();
    public static final String EXTRA_ID = "EXTRA_ID";
    public static final String EXTRA_TREE_ID = "EXTRA_TREE_ID";
    private static final int NEW_SCREEN = -1;

    private long id = NEW_SCREEN;
    private long treeId = -1;
    private ScreenController controller;
    private ScreenTypeAdapter adapter;

    @InjectView(R.id.name)
    FloatLabeledEditText name;
    @InjectView(R.id.screen_type)
    Spinner spinner;
    @InjectView(android.R.id.button1)
    Button save;
    @InjectView(android.R.id.button2)
    Button saveAndEdit;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param args
     * @return A new instance of fragment UtreeFragment.
     */
    public static ScreenFragment newInstance(Bundle args) {
        ScreenFragment fragment = new ScreenFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public ScreenFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            id = arguments.getLong(EXTRA_ID, NEW_SCREEN);
            treeId = arguments.getLong(EXTRA_TREE_ID, -1);
        }
        if (treeId == -1) {
            throw new IllegalArgumentException("tree is required");
        }
        Log.d(TAG, "tree id:  " + treeId);
        int titleResId = R.string.new_screen;
        if (id != NEW_SCREEN) {
            titleResId = R.string.edit_screen;
        }
        getActivity().setTitle(getString(titleResId));

        controller = new ScreenController();
        adapter = new ScreenTypeAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_screen, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.inject(this, view);

        adapter.addAll(UsbongBuilderScreenType.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    @OnClick(android.R.id.button1)
    public void onSave() {
        saveScreen(new Observer<Screen>() {
            @Override
            public void onCompleted() {
                //TODO: implement better transition?
                Toast.makeText(getActivity(), getString(R.string.screen_saved), Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, e.getMessage(), e);
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(Screen screen) {

            }
        });
    }

    private void saveScreen(final Observer<Screen> callback) {
        //TODO: improve code
        controller.fetchScreen(id, new Observer<Screen>() {
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
                screen.name = name.getText().toString().trim();
                screen.utree = new Select().from(Utree.class).where(Utree._ID + " = ?", treeId).executeSingle();
                screen.screenType = adapter.getItem(spinner.getSelectedItemPosition()).getName();
                screen.details = ScreenDetailsFactory.create(screen);
                controller.save(screen, callback);
            }
        });
    }

    @OnClick(android.R.id.button2)
    public void onSaveAndExit() {
        saveScreen(new Observer<Screen>() {
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
                Toast.makeText(getActivity(), getString(R.string.screen_saved), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), ScreenDetailActivity.class);
                intent.putExtra(ScreenDetailFragment.EXTRA_SCREEN_ID, screen.getId().longValue());
                intent.putExtra(ScreenDetailFragment.EXTRA_TREE_ID, treeId);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }

}
