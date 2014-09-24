package usbong.android.builder.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.activeandroid.query.Select;
import rx.Observer;
import usbong.android.builder.R;
import usbong.android.builder.activities.ScreenActivity;
import usbong.android.builder.activities.ScreenDetailActivity;
import usbong.android.builder.adapters.ScreenAdapter;
import usbong.android.builder.controllers.ScreenListController;
import usbong.android.builder.controllers.UtreeListController;
import usbong.android.builder.fragments.dialogs.DeleteConfirmationDialogFragment;
import usbong.android.builder.models.Screen;
import usbong.android.builder.models.Utree;
import usbong.android.builder.utils.IntentUtils;
import usbong.android.builder.utils.StringUtils;

import java.io.File;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * interface.
 */
public class ScreenListFragment extends Fragment implements Observer<List<Screen>> {

    private static final String TAG = ScreenListFragment.class.getSimpleName();
    public static final String EXTRA_TREE_ID = "EXTRA_TREE_ID";
    public static final String EXTRA_SCREEN_ID = "EXTRA_SCREEN_ID";

    /**
     * The fragment's ListView/GridView.
     */
    @InjectView(android.R.id.list)
    AbsListView listView;
    @InjectView(android.R.id.empty)
    TextView emptyView;
    @InjectView(R.id.search)
    EditText search;

    private ScreenListController controller;
    private UtreeListController utreeListController;
    private Screen selectedScreen;
    private ActionMode actionMode;
    private ActionMode.Callback selectedScreenCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.selected_screen, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_edit:
                    mode.finish();
                    editScreen();
                    return true;
                case R.id.action_delete:
                    mode.finish();
                    showDeleteConfirmationDialog();
                    return true;
                case R.id.action_mark_as_start:
                    mode.finish();
                    markAsStart();
                    return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
        }
    };
    private Utree utree;

    private void showDeleteConfirmationDialog() {
        DeleteConfirmationDialogFragment dialog = DeleteConfirmationDialogFragment.newInstance();
        dialog.setCallback(new DeleteConfirmationDialogFragment.Callback() {
            @Override
            public void onYes() {
                deleteScreen();
            }

            @Override
            public void onNo() {

            }
        });
        dialog.show(getFragmentManager(), "DIALOG");
    }

    private void deleteScreen() {
        controller.deleteScreen(selectedScreen, new Observer<Object>() {

            @Override
            public void onCompleted() {
                selectedScreen = null;
                controller.fetchScreens(treeId, ScreenListFragment.this);
                Toast.makeText(getActivity(), "Screen deleted", Toast.LENGTH_SHORT).show();
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

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ScreenAdapter adapter;
    private long treeId = -1;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param args
     * @return A new instance of fragment UtreeFragment.
     */
    public static Fragment newInstance(Bundle args) {
        ScreenListFragment fragment = new ScreenListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ScreenListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            treeId = getArguments().getLong(EXTRA_TREE_ID);
            if (treeId == -1) {
                throw new IllegalArgumentException("tree is required");
            }
            utree = new Select().from(Utree.class)
                    .where(Utree._ID + " = ?", treeId)
                    .executeSingle();
        }
        setHasOptionsMenu(true);
        controller = new ScreenListController();
        utreeListController = new UtreeListController();
        adapter = new ScreenAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_screen_list_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.inject(this, view);

        ((AdapterView<ListAdapter>) listView).setAdapter(adapter);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        controller.fetchScreens(treeId, this);
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

    public void editScreen() {
        Intent intent = new Intent(getActivity(), ScreenDetailActivity.class);
        intent.putExtra(ScreenDetailFragment.EXTRA_SCREEN_ID, selectedScreen.getId().longValue());
        intent.putExtra(ScreenDetailFragment.EXTRA_TREE_ID, treeId);
        startActivity(intent);
        selectedScreen = null;
    }

    @OnItemClick(android.R.id.list)
    public void onItemClick(View view, int position) {
        if (actionMode != null) {
            if (selectedScreen != null && selectedScreen.getId().equals(adapter.getItem(position).getId())) {
                actionMode.finish();
                editScreen();
            }
            view.setSelected(true);
            selectedScreen = adapter.getItem(position);
            return;
        }
        view.setSelected(true);
        selectedScreen = adapter.getItem(position);
        actionMode = getActivity().startActionMode(selectedScreenCallback);
    }

    private void markAsStart() {
        controller.markAsStart(selectedScreen, new Observer<Screen>() {
            @Override
            public void onCompleted() {
                controller.fetchScreens(treeId, ScreenListFragment.this);
                Toast.makeText(getActivity(), getString(R.string.screen_saved), Toast.LENGTH_SHORT).show();
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
        selectedScreen = null;
    }

    @Override
    public void onCompleted() {
        if (adapter.getCount() == 0) {
            setEmptyText(getString(R.string.empty_screens));
        } else {
            setEmptyText(StringUtils.EMPTY);
        }
    }

    @Override
    public void onError(Throwable e) {
        Log.e(TAG, e.getMessage(), e);
        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNext(List<Screen> screens) {
        adapter.clear();
        adapter.addAll(screens);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.screen_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            Intent intent = new Intent(getActivity(), ScreenActivity.class);
            intent.putExtra(ScreenFragment.EXTRA_TREE_ID, treeId);
            startActivity(intent);
        }
        if (item.getItemId() == R.id.action_export) {
            exportTree();
        }
        return super.onOptionsItemSelected(item);
    }

    private void exportTree() {
        Intent intent = IntentUtils.getSelectFolderIntent(getActivity());
        startActivityForResult(intent, IntentUtils.CHOOSE_FOLDER_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IntentUtils.CHOOSE_FOLDER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            String outputFolderLocation = data.getData().getPath();
            String treeFolderLocation = getActivity().getFilesDir() + File.separator + "trees" + File.separator + utree.name + File.separator;
            String tempFolderLocation = getActivity().getFilesDir() + File.separator + "temp" + File.separator;
            utreeListController.exportTree(utree, outputFolderLocation, treeFolderLocation, tempFolderLocation, new Observer<String>() {

                @Override
                public void onCompleted() {
                    Toast.makeText(getActivity(), ".utree exported", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(Throwable e) {
                    Log.e(TAG, e.getMessage(), e);
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNext(String s) {

                }
            });
        }
    }

}
