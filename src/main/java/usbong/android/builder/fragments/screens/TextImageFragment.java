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
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoUtils;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;
import de.greenrobot.event.EventBus;
import rx.Observer;
import usbong.android.builder.R;
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
import usbong.android.builder.models.details.ImageScreenDetails;
import usbong.android.builder.models.ScreenRelation;
import usbong.android.builder.utils.IntentUtils;
import usbong.android.builder.utils.JsonUtils;
import usbong.android.builder.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Use the {@link usbong.android.builder.fragments.screens.TextImageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TextImageFragment extends BaseScreenFragment {

    private static final String TAG = TextImageFragment.class.getSimpleName();
    @InjectView(R.id.content)
    FloatLabeledEditText textDisplay;
    @InjectView(R.id.image)
    ImageView image;
    @InjectView(R.id.image_position)
    Spinner imagePosition;
    @InjectView(R.id.hasCaption)
    Switch hasCaption;
    @InjectView(R.id.caption)
    FloatLabeledEditText caption;
    private ImagePositionAdapter adapter;
    private String imagePath = StringUtils.EMPTY;

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
        adapter = new ImagePositionAdapter(getActivity());
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_screen_type_text_image;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter.add(ImagePosition.ABOVE_TEXT);
        adapter.add(ImagePosition.BELOW_TEXT);
        imagePosition.setAdapter(adapter);
    }

    @Override
    protected void onScreenLoad(Screen screen) {
        name.setText(currentScreen.name);
        String details = StringUtils.EMPTY;
        if (currentScreen.details != null) {
            ImageScreenDetails textImageDetails = JsonUtils.fromJson(currentScreen.details, ImageScreenDetails.class);
            details = textImageDetails.getText();

            imagePath = textImageDetails.getImagePath();

            if (!StringUtils.isEmpty(textImageDetails.getImagePosition())) {
                ImagePosition position = ImagePosition.from(textImageDetails.getImagePosition());
                imagePosition.setSelection(adapter.getPosition(position));
            }
            hasCaption.setChecked(textImageDetails.isHasCaption());
            caption.setEnabled(textImageDetails.isHasCaption());
            caption.setText(textImageDetails.getImageCaption());
        }
        SpannableString text = new SpannableString(Html.fromHtml(details));
        textDisplay.setText(text);
        if (!StringUtils.isEmpty(imagePath)) {
            Picasso.with(getActivity())
                    .load(new File(imagePath))
                    .fit()
                    .into(image);
        }
    }

    @Override
    protected String convertFormDataToScreenDetails() throws Exception {
        ImageScreenDetails details = new ImageScreenDetails();
        String screenContent = textDisplay.getText().toString().replaceAll("\n", "<br>");
        details.setText(screenContent);
        details.setImagePath(imagePath);
        details.setHasCaption(hasCaption.isChecked());
        details.setImageCaption(caption.getTextString());
        ImagePosition position = adapter.getItem(imagePosition.getSelectedItemPosition());
        details.setImagePosition(position.getName());

        return JsonUtils.toJson(details);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IntentUtils.CHOOSE_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            final String outputFolderLocation = getActivity().getFilesDir() + File.separator + "trees" + File.separator + currentScreen.utree.name + File.separator + "res";
            controller.uploadImage(getActivity(), data.getData(), outputFolderLocation, new Observer<File>() {

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

    @OnClick(R.id.upload)
    public void onUploadClicked() {
        Intent fileDialogIntent = IntentUtils.getSelectFileIntent(getActivity(), "file/*.png");
        try {
            startActivityForResult(
                    Intent.createChooser(fileDialogIntent, getString(R.string.select_an_image)),
                    IntentUtils.CHOOSE_FILE_REQUEST_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity(), getString(R.string.please_install_file_manager), Toast.LENGTH_SHORT).show();
        }
    }
}
