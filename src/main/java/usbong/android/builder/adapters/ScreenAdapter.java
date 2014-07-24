package usbong.android.builder.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.picasso.Picasso;
import usbong.android.builder.R;
import usbong.android.builder.models.Screen;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rocky Camacho on 6/26/2014.
 */
public class ScreenAdapter extends BaseAdapter implements Filterable {

    public static final int LAYOUT_RES_ID = R.layout.list_item_screen;
    private final Context context;
    private List<Screen> allItems;
    private List<Screen> items;
    private ScreenFilter filter;

    public ScreenAdapter(Context context) {
        super();
        this.context = context;
        this.allItems = new ArrayList<Screen>();
        this.items = new ArrayList<Screen>();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Screen getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
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
        Screen screen = getItem(position);
        viewHolder.name.setText(screen.name);
        if (screen.isStart == 1) {
            viewHolder.name.setTextColor(context.getResources().getColor(R.color.usbong_color));
        } else {
            viewHolder.name.setTextColor(context.getResources().getColor(android.R.color.primary_text_light));
        }
        Picasso.with(context)
                .load(context.getFileStreamPath(screen.getScreenshotPath()))
                .fit()
                .into(viewHolder.image);
        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new ScreenFilter();
        }
        return filter;
    }

    public void clear() {
        allItems.clear();
        items.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Screen> screens) {
        allItems.addAll(screens);
        items.addAll(screens);
        notifyDataSetChanged();
    }

    static class ViewHolder {

        @InjectView(android.R.id.icon)
        ImageView image;

        @InjectView(android.R.id.text1)
        TextView name;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

    }

    private class ScreenFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint == null || constraint.length() == 0) {
                results.values = allItems;
                results.count = allItems.size();
            } else {
                List<Screen> filteredItems = new ArrayList<Screen>();
                for (Screen entry : allItems) {
                    if (entry.name.toUpperCase().contains(constraint.toString().toUpperCase()) ||
                            entry.details.toUpperCase().contains(constraint.toString().toUpperCase())) {
                        filteredItems.add(entry);
                    }
                }
                results.values = filteredItems;
                results.count = filteredItems.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.count == 0) {
                notifyDataSetInvalidated();
            } else {
                items = (List<Screen>) results.values;
                notifyDataSetChanged();
            }
        }
    }

}
