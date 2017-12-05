package tmediaa.ir.ahamdian.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tmediaa.ir.ahamdian.model.CategoryItem;

/**
 * Created by tmediaa on 11/29/2017.
 */

class CategoryButtonListAdapter extends BaseAdapter {
    private ArrayList<CategoryItem> lists;
    private Context mContext;
    private LinearLayout item_row;

    public CategoryButtonListAdapter(@NonNull Context context, ArrayList<CategoryItem> items) {
        this.lists = items;
        this.mContext = context;
    }
    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    public List<CategoryItem> getData() {
        return lists;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final CategoryItem item = lists.get(position);
        ViewHolder viewHolder = new ViewHolder();

        if (convertView == null) {



        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }



        return convertView;
    }


    private static class ViewHolder {

        TextView tvName;
        ImageView arrow;

    }
}
