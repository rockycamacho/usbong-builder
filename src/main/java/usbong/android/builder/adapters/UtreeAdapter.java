package usbong.android.builder.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import usbong.android.builder.models.Screen;
import usbong.android.builder.models.Utree;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rocky Camacho on 6/26/2014.
 */
public class UtreeAdapter extends BaseAdapter implements Filterable {

    public static final int LAYOUT_RES_ID = android.R.layout.simple_list_item_1;
    private static final String TAG = UtreeAdapter.class.getSimpleName();
    private final Context context;
    private List<Utree> allItems;
    private List<Utree> items;
    private UtreeFilter filter;

    public UtreeAdapter(Context context) {
        super();
        this.context = context;
        this.allItems = new ArrayList<Utree>();
        this.items = new ArrayList<Utree>();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Utree getItem(int position) {
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
        Utree utree = getItem(position);
        viewHolder.name.setText(utree.name);
        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new UtreeFilter();
        }
        return filter;
    }

    public void clear() {
        allItems.clear();
        items.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Utree> utrees) {
        allItems.addAll(utrees);
        items.addAll(utrees);
        notifyDataSetChanged();
    }

    static class ViewHolder {

        @InjectView(android.R.id.text1)
        TextView name;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

    }

    private class UtreeFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            Log.d(TAG, "filter: " + constraint);
            if (constraint == null || constraint.length() == 0) {
                results.values = allItems;
                results.count = allItems.size();
            } else {
                List<Utree> filteredItems = new ArrayList<Utree>();
                for (Utree entry : allItems) {
                    if (entry.name.toUpperCase().contains(constraint.toString().toUpperCase())) {
                        filteredItems.add(entry);
                    }
                }
                results.values = filteredItems;
                results.count = filteredItems.size();
                Log.d(TAG, "results.count: " + results.count);
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.count == 0) {
                notifyDataSetInvalidated();
            } else {
                items = (List<Utree>) results.values;
                notifyDataSetChanged();
            }
        }
    }
}
