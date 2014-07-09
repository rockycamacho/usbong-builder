package usbong.android.builder.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import usbong.android.builder.R;
import usbong.android.builder.models.ScreenRelation;
import com.squareup.picasso.Picasso;

/**
 * Created by Rocky Camacho on 6/26/2014.
 */
public class ParentsScreensAdapter extends ArrayAdapter<ScreenRelation> {

    public static final int LAYOUT_RES_ID = R.layout.list_item_screen_relation;
    private final Context context;

    //TODO: add filter?
    public ParentsScreensAdapter(Context context) {
        super(context, LAYOUT_RES_ID);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(LAYOUT_RES_ID, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ScreenRelation screenRelation = getItem(position);
        viewHolder.name.setText(screenRelation.parent.name);
        Picasso.with(context)
                .load(context.getFileStreamPath(screenRelation.parent.getScreenshotPath()))
                .fit()
                .into(viewHolder.image);
        viewHolder.condition.setText(screenRelation.condition);
        return convertView;
    }

    static class ViewHolder {

        @InjectView(android.R.id.icon)
        ImageView image;

        @InjectView(android.R.id.text1)
        TextView name;

        @InjectView(android.R.id.text2)
        TextView condition;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

    }
}
