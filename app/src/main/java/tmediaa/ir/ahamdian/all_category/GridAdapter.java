package tmediaa.ir.ahamdian.all_category;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tmediaa.ir.ahamdian.R;
import tmediaa.ir.ahamdian.model.GridItem;

/**
 * Created by tmediaa on 11/28/2017.
 */

public class GridAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<GridItem> all_grid_list = new ArrayList<>();

    // Constructor
    public GridAdapter(Context c, List<GridItem> results){
        mContext = c;
        all_grid_list.addAll(results);
        mInflater = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return all_grid_list.size();
    }

    @Override
    public Object getItem(int position) {
        return all_grid_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(R.layout.grid_item_view, null);

        convertView.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, 400));

        ImageView imageview = (ImageView) convertView.findViewById(R.id.img);
        TextView title_tv = (TextView) convertView.findViewById(R.id.title);
        View border = (View) convertView.findViewById(R.id.border);

        GridItem item = all_grid_list.get(position);
        title_tv.setText(item.get_name());
        imageview.setBackgroundResource(item.get_path());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            border.setBackgroundColor(mContext.getColor(item.get_color()));
        }else{
            border.setBackgroundColor(mContext.getResources().getColor(item.get_color()));
        }

        return convertView;
    }


}
