package usbong.android.builder.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.activeandroid.query.Select;
import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import usbong.android.builder.R;
import usbong.android.builder.activities.ScreenListActivity;
import usbong.android.builder.activities.UtreeActivity;
import usbong.android.builder.adapters.UtreeAdapter;
import usbong.android.builder.controllers.UtreeListController;
import usbong.android.builder.events.OnNeedRefreshTrees;
import usbong.android.builder.events.OnScreenDetailsSave;
import usbong.android.builder.models.Screen;
import usbong.android.builder.models.Utree;
import rx.Observer;
import usbong.android.builder.utils.IntentUtils;
import usbong.android.builder.utils.ScreenUtils;

import java.io.File;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p />
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p />
 * interface.
 */
public class UtreeListFragment extends Fragment implements AbsListView.OnItemClickListener, Observer<List<Utree>> {

    public static final String TAG = UtreeListFragment.class.getSimpleName();

    /**
     * The fragment's ListView/GridView.
     */
    @InjectView(android.R.id.list)
    AbsListView listView;
    @InjectView(android.R.id.empty)
    TextView emptyView;

    private UtreeListController controller;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private UtreeAdapter adapter;

    public static UtreeListFragment newInstance() {
        UtreeListFragment fragment = new UtreeListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public UtreeListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        adapter = new UtreeAdapter(getActivity());
        controller = new UtreeListController();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        activity.setTitle(getString(R.string.select_a_tree));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_utree_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.inject(this, view);
        EventBus.getDefault().register(this);
        ((AdapterView<ListAdapter>) listView).setAdapter(adapter);

        // Set OnItemClickListener so we can be notified on item clicks
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        controller.fetchUtrees(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Utree utree = adapter.getItem(position);
        Intent intent = new Intent(getActivity(), ScreenListActivity.class);
        intent.putExtra(ScreenListFragment.EXTRA_TREE_ID, utree.getId().longValue());
        startActivity(intent);
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        emptyView.setText(emptyText);
        emptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCompleted() {
        if(adapter.getCount() == 0) {
            setEmptyText(getString(R.string.empty_utrees));
        }
    }

    @Override
    public void onError(Throwable e) {
        Log.e(TAG, e.getMessage(), e);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.utree_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_add) {
            Intent intent = new Intent(getActivity(), UtreeActivity.class);
            startActivity(intent);
        }
        else if(item.getItemId() == R.id.action_import) {
            try {
                Intent fileDialogIntent = IntentUtils.getFileDialogIntent(getActivity(), "file/*.utree");
                startActivityForResult(fileDialogIntent, IntentUtils.CHOOSE_FILE_REQUESTCODE);
            } catch (android.content.ActivityNotFoundException e) {
                Log.e(TAG, e.getMessage(), e);
                Toast.makeText(getActivity(), getString(R.string.no_file_managers_found), Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNext(List<Utree> utrees) {
        adapter.clear();
        adapter.addAll(utrees);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IntentUtils.CHOOSE_FILE_REQUESTCODE) {
            if(resultCode == Activity.RESULT_OK) {
                String fileLocation = data.getData().getPath();
                String outputFolderLocation = getActivity().getFilesDir() + File.separator + "trees";
                controller.importTree(fileLocation, outputFolderLocation, new Observer<String>() {

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, e.getMessage(), e);
                    }

                    @Override
                    public void onNext(String o) {

                    }
                });
            }
        }
    }

    public void onEvent(final OnNeedRefreshTrees event) {
        controller.fetchUtrees(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        EventBus.getDefault().unregister(this);
    }
}
