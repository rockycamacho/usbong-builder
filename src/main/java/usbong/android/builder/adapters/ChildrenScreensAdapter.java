package usbong.android.builder.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import usbong.android.builder.R;
import usbong.android.builder.models.Screen;
import usbong.android.builder.models.ScreenRelation;
import com.squareup.picasso.Picasso;
import usbong.android.builder.models.Utree;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rocky Camacho on 6/26/2014.
 */
public class ChildrenScreensAdapter extends BaseAdapter implements Filterable {

    public static final int LAYOUT_RES_ID = R.layout.list_item_screen_relation;
    private final Context context;
    private List<ScreenRelation> allItems;
    private List<ScreenRelation> items;
    private ScreenRelationFilter filter;

    public ChildrenScreensAdapter(Context context) {
        super();
        this.context = context;
        this.allItems = new ArrayList<ScreenRelation>();
        this.items = new ArrayList<ScreenRelation>();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public ScreenRelation getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
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
        viewHolder.name.setText(screenRelation.child.name);
        if(screenRelation.child.isStart == 1) {
            viewHolder.name.setTextColor(context.getResources().getColor(R.color.usbong_color));
        }
        else {
            viewHolder.name.setTextColor(context.getResources().getColor(android.R.color.primary_text_light));
        }
        Picasso.with(context)
                .load(context.getFileStreamPath(screenRelation.child.getScreenshotPath()))
                .fit()
                .into(viewHolder.image);
        viewHolder.condition.setText(screenRelation.condition);
        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new ScreenRelationFilter();
        }
        return filter;
    }

    public void clear() {
        allItems.clear();
        items.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<ScreenRelation> relations) {
        allItems.addAll(relations);
        items.addAll(relations);
        notifyDataSetChanged();
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

    private class ScreenRelationFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint == null || constraint.length() == 0) {
                results.values = allItems;
                results.count = allItems.size();
            } else {
                List<ScreenRelation> filteredItems = new ArrayList<ScreenRelation>();
                for (ScreenRelation entry : allItems) {
                    if (entry.child.name.toUpperCase().contains(constraint.toString().toUpperCase()) ||
                            entry.child.details.toUpperCase().contains(constraint.toString().toUpperCase())) {
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
                items = (List<ScreenRelation>) results.values;
                notifyDataSetChanged();
            }
        }
    }
}
