package tmediaa.ir.ahamdian.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

import tmediaa.ir.ahamdian.R;
import tmediaa.ir.ahamdian.model.CategoryItem;
import tmediaa.ir.ahamdian.tools.CONST;

public class CategoryMenuPagerAdapter extends PagerAdapter implements AdapterView.OnItemClickListener {
    private Context mContext;
    protected ArrayList<CategoryItem> mList1 = new ArrayList<>();
    ;
    protected ArrayList<CategoryItem> mList2 = new ArrayList<>();
    protected ArrayList<CategoryItem> mList3 = new ArrayList<>();
    ;
    private LayoutInflater mLayoutInflater;
    private ListView listView1;
    private ListView listView2;
    private ListView listView3;
    private PagerCallback mCallback;
    private CategoryListAdapter adapter1;
    private CategoryListAdapter adapter2;
    private CategoryListAdapter adapter3;

    public CategoryMenuPagerAdapter(Context context, ArrayList<CategoryItem> lists, PagerCallback callback) {
        mContext = context;
        mList1 = lists;
        mCallback = callback;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        View itemView = null;
        int ResId = 0;

        switch (position) {
            case 0:
                ResId = R.layout.category_pager1_item;
                itemView = mLayoutInflater.inflate(ResId, container, false);
                listView1 = (ListView) itemView.findViewById(R.id.category_1_list);
                adapter1 = new CategoryListAdapter(mContext, mList1);
                listView1.setAdapter(adapter1);
                listView1.setOnItemClickListener(this);
                break;
            case 1:
                ResId = R.layout.category_pager2_item;
                itemView = mLayoutInflater.inflate(ResId, container, false);
                listView2 = (ListView) itemView.findViewById(R.id.category_2_list);
                adapter2 = new CategoryListAdapter(mContext, mList1);
                //listView2.setAdapter(adapter2);
                listView2.setOnItemClickListener(this);
                break;
            case 2:
                ResId = R.layout.category_pager3_item;
                itemView = mLayoutInflater.inflate(ResId, container, false);
                listView3 = (ListView) itemView.findViewById(R.id.category_3_list);
                adapter3 = new CategoryListAdapter(mContext, mList1);
               // listView3.setAdapter(adapter3);
                listView3.setOnItemClickListener(this);
                break;
        }

        /*search_view = (SearchView) itemView.findViewById(R.id.search_view);
        search_view.setActivated(true);
        search_view.setQueryHint("عنوان دسته را بنویسید.");
        search_view.onActionViewExpanded();
        search_view.setIconified(false);
        search_view.clearFocus();
        search_view.setFocusable(false);
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(search_view.getWindowToken(), 0);
        */
        container.addView(itemView);



        return itemView;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CategoryItem item = (CategoryItem) parent.getItemAtPosition(position);

        Log.d(CONST.APP_LOG,"depth: " + item.getDepth() + " -- : name: " + item.getName());
        if (item.getDepth() == 0) {
            adapter2.getData().clear();
            adapter2.getData().addAll(item.getChilds());
            adapter2.notifyDataSetChanged();
            listView2.setAdapter(adapter2);

            mCallback.onChangePager(item.getDepth());
        } else if (item.getDepth() == 1) {

            adapter3.getData().clear();
            adapter3.getData().addAll(item.getChilds());
            adapter3.notifyDataSetChanged();
            listView3.setAdapter(adapter3);
            if (item.getChilds().size() > 0) {
                mCallback.onChangePager(item.getDepth());
            }
        }

        if(item.getChilds().size() == 0){
            mCallback.closeMenu(item.getId(),item.getName());
        }

    }

    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public void loadRoot() {
        adapter1 = new CategoryListAdapter(mContext, mList1);
        listView1.setAdapter(adapter1);
    }

    public interface PagerCallback {
        void onChangePager(int page);
        void closeMenu(int selected_id, String breadcrumb);
    }
}