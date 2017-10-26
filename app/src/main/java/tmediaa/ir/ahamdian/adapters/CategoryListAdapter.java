package tmediaa.ir.ahamdian.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tmediaa.ir.ahamdian.R;
import tmediaa.ir.ahamdian.model.CategoryItem;

/**
 * Created by tmediaa on 9/29/2017.
 */

public class CategoryListAdapter extends BaseAdapter{
    private ArrayList<CategoryItem> lists;
    private Context mContext;
    private LinearLayout item_row;
    public CategoryListAdapter(@NonNull Context context,  ArrayList<CategoryItem> items) {
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

            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.category_list_item_row, null);

            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv);
            viewHolder.arrow = (ImageView) convertView.findViewById(R.id.arrowView);

            viewHolder.tvName.setText(item.getName());

            if(item.getChilds().size() < 1){
                viewHolder.arrow.setVisibility(View.GONE);
            }

        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }



        return convertView;
    }

    public void updateList(ArrayList<CategoryItem> childs) {
        lists.clear();
        lists.addAll(childs);
        notifyDataSetChanged();
        notifyDataSetInvalidated();
    }

    private static class ViewHolder {

        TextView tvName;
        ImageView arrow;

    }


}
