package tmediaa.ir.ahamdian.all_category;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import tmediaa.ir.ahamdian.R;
import tmediaa.ir.ahamdian.model.CategoryItem;

/**
 * Created by tmediaa on 12/13/2017.
 */

public class CategoryButtonListAdpter extends ArrayAdapter<CategoryItem> {

    public CategoryButtonListAdpter(Context context, ArrayList<CategoryItem> datas) {
        super(context, 0, datas);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        CategoryItem user = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.category_button_item, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.label);
        ImageView arrow = (ImageView) convertView.findViewById(R.id.arrow);
        // Populate the data into the template view using the data object
        if(user.getChilds().size()>0){
            arrow.setVisibility(View.VISIBLE);
        }else{
            arrow.setVisibility(View.GONE);
        }
        tvName.setText(user.getName());

        // Return the completed view to render on screen
        return convertView;
    }
}
