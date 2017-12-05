package tmediaa.ir.ahamdian.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;

import tmediaa.ir.ahamdian.R;
import tmediaa.ir.ahamdian.model.CategoryItem;

public class CategoryButtonListPagerAdapter extends PagerAdapter implements AdapterView.OnItemClickListener {
    private Context mContext;
    protected ArrayList<CategoryItem> mList1 = new ArrayList<>();
    ;
    protected ArrayList<CategoryItem> mList2 = new ArrayList<>();
    protected ArrayList<CategoryItem> mList3 = new ArrayList<>();

    private int selected_id;
    private LayoutInflater mLayoutInflater;
    private ListView listView1;
    private ListView listView2;
    private ListView listView3;

    private CategoryButtonListAdapter adapter1;
    private CategoryButtonListAdapter adapter2;
    private CategoryButtonListAdapter adapter3;
    private FillChildTools tools ;

    public CategoryButtonListPagerAdapter(Context context, ArrayList<CategoryItem> lists, int id) {
        mContext = context;
        mList1 = lists;
        selected_id = id;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((FlexboxLayout) object);
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        View itemView = null;
        int ResId = 0;

        switch (position) {
            case 0:
                ResId = R.layout.category_show_btnlist_1;
                itemView = mLayoutInflater.inflate(ResId, container, false);
                FlexboxLayout view1_root = (FlexboxLayout) itemView.findViewById(R.id.view1_root);
                ArrayList<CategoryItem> level_2_list = mList1.get(selected_id).getChilds();

                tools = new FillChildTools(mContext,view1_root,level_2_list);


                break;
            case 1:
                ResId = R.layout.category_show_btnlist_2;
                itemView = mLayoutInflater.inflate(ResId, container, false);
                FlexboxLayout view2_root = (FlexboxLayout) itemView.findViewById(R.id.view2_root);
                tools = new FillChildTools(mContext,view2_root,mList2);
                break;
        }

        container.addView(itemView);
        return itemView;
    }

    public void updateLevel2(ArrayList<CategoryItem> list){
       tools.updateList(list);
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}