package usbong.android.builder.fragments.screens;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoUtils;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;
import rx.Observer;
import usbong.android.builder.R;
import usbong.android.builder.enums.ImagePosition;
import usbong.android.builder.models.Screen;
import usbong.android.builder.models.details.ImageScreenDetails;
import usbong.android.builder.utils.IntentUtils;
import usbong.android.builder.utils.JsonUtils;
import usbong.android.builder.utils.StringUtils;

import java.io.File;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Use the {@link usbong.android.builder.fragments.screens.ImageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImageFragment extends BaseScreenFragment {

    private static final String TAG = ImageFragment.class.getSimpleName();
    @InjectView(R.id.image)
    ImageView image;
    @InjectView(R.id.hasCaption)
    Switch hasCaption;
    @InjectView(R.id.caption)
    FloatLabeledEditText caption;
    private String imagePath = StringUtils.EMPTY;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TextDisplayFragment.
     */
    public static ImageFragment newInstance(Bundle args) {
        ImageFragment fragment = new ImageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public ImageFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_screen_type_image;
    }

    @Override
    protected void onScreenLoad(Screen screen) {
        name.setText(currentScreen.name);
        if (currentScreen.details != null) {
            ImageScreenDetails textImageDetails = JsonUtils.fromJson(currentScreen.details, ImageScreenDetails.class);
            imagePath = textImageDetails.getImagePath();

            hasCaption.setChecked(textImageDetails.isHasCaption());
            caption.setEnabled(textImageDetails.isHasCaption());
            caption.setText(textImageDetails.getImageCaption());
        }
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
        String screenContent = StringUtils.EMPTY;
        details.setText(screenContent);
        details.setImagePath(imagePath);
        details.setHasCaption(hasCaption.isChecked());
        details.setImageCaption(caption.getTextString());
        ImagePosition position = ImagePosition.ABOVE_TEXT;
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
