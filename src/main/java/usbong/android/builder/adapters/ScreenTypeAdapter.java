package usbong.android.builder.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import usbong.android.builder.enums.ScreenType;

/**
 * Created by Rocky Camacho on 6/27/2014.
 */
public class ScreenTypeAdapter extends ArrayAdapter<ScreenType> {

    public static final int LAYOUT_RES_ID = android.R.layout.simple_list_item_1;
    private final Context context;

    public ScreenTypeAdapter(Context context) {
        super(context, LAYOUT_RES_ID);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(LAYOUT_RES_ID, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ScreenType screenType = getItem(position);
        viewHolder.name.setText(screenType.getName());
        return convertView;
    }

    static class ViewHolder {

        @InjectView(android.R.id.text1)
        TextView name;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

    }
}
