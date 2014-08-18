package usbong.android.builder.fragments.screens;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.widget.*;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;
import rx.Observer;
import usbong.android.builder.R;
import usbong.android.builder.adapters.SpecialInputTypeAdapter;
import usbong.android.builder.exceptions.FormInputException;
import usbong.android.builder.models.Screen;
import usbong.android.builder.models.details.SpecialInputScreenDetails;
import usbong.android.builder.models.details.VideoScreenDetails;
import usbong.android.builder.utils.IntentUtils;
import usbong.android.builder.utils.JsonUtils;
import usbong.android.builder.utils.StringUtils;

import java.io.File;
import java.util.Arrays;

/**
 * Created by Rocky Camacho on 8/10/2014.
 */
public class VideoScreenFragment extends BaseScreenFragment {

    private static final String TAG = VideoScreenFragment.class.getSimpleName();
    @InjectView(R.id.content)
    FloatLabeledEditText content;
    @InjectView(R.id.upload)
    Button upload;
    @InjectView(R.id.video)
    TextView video;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SpecialInputScreenFragment.
     */
    public static VideoScreenFragment newInstance(Bundle args) {
        VideoScreenFragment fragment = new VideoScreenFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public VideoScreenFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_screen_type_video;
    }

    @Override
    protected void onScreenLoad(Screen screen) {
        name.setText(screen.name);
        VideoScreenDetails videoScreenDetails = JsonUtils.fromJson(screen.details, VideoScreenDetails.class);
        String details = StringUtils.EMPTY;
        if (currentScreen.details != null) {
            details = videoScreenDetails.getText();
        }
        content.setText(new SpannableString(Html.fromHtml(details)));
        video.setText(videoScreenDetails.getVideo());
    }

    @Override
    protected String convertFormDataToScreenDetails() throws Exception {
        SpecialInputScreenDetails details = new SpecialInputScreenDetails();
        String screenContent = content.getText().toString().replaceAll("\n", "<br>");
        details.setText(screenContent);
        if (StringUtils.isEmpty(video.getText().toString())) {
            throw new FormInputException("Please upload a video");
        }
        details.setVideo(video.getText().toString());

        return JsonUtils.toJson(details);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IntentUtils.CHOOSE_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            final String outputFolderLocation = getActivity().getFilesDir() + File.separator + "trees" + File.separator + currentScreen.utree.name + File.separator + "res";
            controller.uploadVideo(getActivity(), data.getData(), outputFolderLocation, new Observer<File>() {

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
                    video.setText(uploadedFile.getName());
                }
            });
        }
    }

    @OnClick(R.id.upload)
    public void onUploadClicked() {
        Intent fileDialogIntent = IntentUtils.getSelectFileIntent(getActivity(), "file/*.mp4");
        try {
            startActivityForResult(
                    Intent.createChooser(fileDialogIntent, getString(R.string.select_a_video)),
                    IntentUtils.CHOOSE_FILE_REQUEST_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity(), getString(R.string.please_install_file_manager), Toast.LENGTH_SHORT).show();
        }
    }
}
