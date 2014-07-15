package usbong.android.builder.fragments.screens;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.SpannableString;
import android.util.Log;
import android.view.*;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoUtils;
import de.greenrobot.event.EventBus;
import rx.Observer;
import usbong.android.builder.R;
import usbong.android.builder.activities.SelectDecisionActivity;
import usbong.android.builder.activities.SelectScreenActivity;
import usbong.android.builder.adapters.ImagePositionAdapter;
import usbong.android.builder.controllers.ScreenDetailController;
import usbong.android.builder.enums.ImagePosition;
import usbong.android.builder.events.OnNeedRefreshScreen;
import usbong.android.builder.events.OnScreenDetailsSave;
import usbong.android.builder.events.OnScreenSave;
import usbong.android.builder.fragments.ScreenDetailFragment;
import usbong.android.builder.fragments.SelectScreenFragment;
import usbong.android.builder.models.Screen;
import usbong.android.builder.models.ScreenDetails;
import usbong.android.builder.models.ScreenRelation;
import usbong.android.builder.utils.IntentUtils;
import usbong.android.builder.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Use the {@link usbong.android.builder.fragments.screens.TextImageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TextImageFragment extends Fragment {

    private static final String TAG = TextImageFragment.class.getSimpleName();
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
    EditText textDisplay;
    @InjectView(R.id.image)
    ImageView image;
    @InjectView(R.id.image_position)
    Spinner imagePosition;
    private ImagePositionAdapter adapter;
    private String imagePath = StringUtils.EMPTY;
    private Gson gson;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TextDisplayFragment.
     */
    public static TextImageFragment newInstance(Bundle args) {
        TextImageFragment fragment = new TextImageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public TextImageFragment() {
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
        adapter = new ImagePositionAdapter(getActivity());
        gson = new Gson();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_screen_type_text_image, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.inject(this, view);
//        textDisplay.enableActionModes(true);

        EventBus.getDefault().register(this);

        adapter.add(ImagePosition.ABOVE_TEXT);
        adapter.add(ImagePosition.BELOW_TEXT);
        imagePosition.setAdapter(adapter);

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
                String details = StringUtils.EMPTY;
                if(currentScreen.details != null) {
                    ScreenDetails textImageDetails = gson.fromJson(currentScreen.details, ScreenDetails.class);
                    details = textImageDetails.getText();

                    imagePath = textImageDetails.getImagePath();

                    if(!StringUtils.isEmpty(textImageDetails.getImagePosition())) {
                        ImagePosition position = ImagePosition.from(textImageDetails.getImagePosition());
                        imagePosition.setSelection(adapter.getPosition(position));
                    }
                }
                SpannableString text = new SpannableString(Html.fromHtml(details));
                textDisplay.setText(text);
                if(!StringUtils.isEmpty(imagePath)) {
                    Picasso.with(getActivity())
                            .load(new File(imagePath))
                            .fit()
                            .into(image);
                }

            }
        });
    }

    public void onEvent(OnScreenSave event) {
        Log.d(TAG, "onEvent(OnScreenSave event): " + textDisplay.getText().toString());

        String screenName = name.getText().toString();
        ScreenDetails details = new ScreenDetails();
        //TODO: implement this
        String screenContent = textDisplay.getText().toString().replaceAll("\n", "<br>");

        details.setText(screenContent);
        details.setImagePath(imagePath);
        ImagePosition position = adapter.getItem(imagePosition.getSelectedItemPosition());
        details.setImagePosition(position.getName());
        EventBus.getDefault().post(new OnScreenDetailsSave(screenName, gson.toJson(details)));
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
        if(requestCode == ADD_CHILD_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            updateChildren(data);
        }
        else if (requestCode == IntentUtils.CHOOSE_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            String fileLocation = data.getData().getPath();
            final String outputFolderLocation = getActivity().getFilesDir() + File.separator + "trees" + File.separator + currentScreen.utree.name + File.separator + "res";
            controller.uploadImage(fileLocation, outputFolderLocation, new Observer<File>() {

                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    Log.e(TAG, e.getMessage(), e);
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNext(File uploadedFile) {
                    Picasso picasso = Picasso.with(getActivity());
                    PicassoUtils.clearCache(picasso);
                    picasso.load(uploadedFile).fit().into(image);
                    imagePath = uploadedFile.getAbsolutePath();
                }
            });
        }
    }

    private void updateChildren(Intent data) {
        Log.d(TAG, "resultCode == Activity.RESULT_OK");
        long childScreenId = data.getLongExtra(SelectScreenFragment.EXTRA_SELECTED_SCREEN_ID, -1);
        Toast.makeText(getActivity(), "screenId: " + childScreenId, Toast.LENGTH_SHORT).show();

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

    @OnClick(R.id.upload)
    public void onUploadClicked() {
        Intent fileDialogIntent = IntentUtils.getSelectFileIntent(getActivity(), "file/*.png");
        startActivityForResult(fileDialogIntent, IntentUtils.CHOOSE_FILE_REQUEST_CODE);
    }
}
