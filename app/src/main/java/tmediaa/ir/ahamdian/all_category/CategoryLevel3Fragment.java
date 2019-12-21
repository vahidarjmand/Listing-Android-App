package tmediaa.ir.ahamdian.all_category;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.santalu.emptyview.EmptyView;

import java.util.ArrayList;

import tmediaa.ir.ahamdian.R;
import tmediaa.ir.ahamdian.adapters.AllItemAdapter;
import tmediaa.ir.ahamdian.itemView.ShowOrder;
import tmediaa.ir.ahamdian.model.CategoryItem;
import tmediaa.ir.ahamdian.model.OrderItem;
import tmediaa.ir.ahamdian.tools.ApiCallTools;
import tmediaa.ir.ahamdian.tools.AppSharedPref;
import tmediaa.ir.ahamdian.tools.CONST;

/**
 * Created by tmediaa on 12/13/2017.
 */

public class CategoryLevel3Fragment extends Fragment {
    private View rootView;
    private ListView category_btn_list;
    private XRecyclerView category_item_lv;
    private EmptyView error_view;
    private ProgressDialog progressDialog;
    private int current_page = 1;
    private Drawable dividerDrawable;
    private ArrayList<OrderItem> listData = new ArrayList<>();
    private AllItemAdapter mAdapter;
    private Integer target_city_id;
    private ApiCallTools apiCall = new ApiCallTools();
    private CategoryButtonListAdpter adpter;
    public static CategoryLevel3Fragment newInstance(String title) {
        CategoryLevel3Fragment fragmentFirst = new CategoryLevel3Fragment();
        Bundle args = new Bundle();
        //args.putInt("name", title);
        args.putString("name", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.category_level3_layout, container, false);

        category_btn_list = (ListView) rootView.findViewById(R.id.category_btn_list);
        category_item_lv = (XRecyclerView) rootView.findViewById(R.id.recyclerview);
        error_view = (EmptyView) rootView.findViewById(R.id.error_view3);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("در حال بارگذاری");


        return rootView;
    }

    public void fillList(int m, ArrayList<CategoryItem> lists) {
        adpter = new CategoryButtonListAdpter(getContext(),lists);

        adpter.notifyDataSetChanged();
        category_btn_list.setAdapter(adpter);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adpter.notifyDataSetChanged();
            }
        });
        category_btn_list.setAdapter(adpter);
        category_btn_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CategoryItem item = (CategoryItem) parent.getItemAtPosition(position);
                loadItems(item.getId());
            }
        });


    }

    private void loadItems(final int id) {
        if(listData.size() > 0){
            listData.clear();
            mAdapter.notifyDataSetChanged();
        }

        progressDialog.setCancelable(false);
        progressDialog.show();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        category_item_lv.setLayoutManager(layoutManager);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dividerDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.item_row_drawable, null);
        } else {
            dividerDrawable = getResources().getDrawable(R.drawable.item_row_drawable);
        }

        category_item_lv.addItemDecoration(category_item_lv.new DividerItemDecoration(dividerDrawable));
        category_item_lv.setRefreshProgressStyle(ProgressStyle.BallBeat);
        category_item_lv.setLoadingMoreProgressStyle(ProgressStyle.BallBeat);

        mAdapter = new AllItemAdapter(getContext(), listData);
        category_item_lv.setAdapter(mAdapter);
        category_item_lv.refresh();

        mAdapter.setOnItemClickListener(new AllItemAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, OrderItem data) {
                Intent i =new Intent(getActivity(), ShowOrder.class);
                i.putExtra("id",data.getId());
                i.putExtra("mode",false);
                startActivity(i);
            }
        });

        target_city_id = Integer.valueOf(AppSharedPref.read("CITY_ID", "0"));
        apiCall.getOrdersWithID(getContext(), CONST.GET_ORDERS_WITH_ID, target_city_id, current_page,id, new ApiCallTools.onOrderLoad() {
            @Override
            public void onOrdersLoad(ArrayList<OrderItem> items, boolean status) {
                progressDialog.dismiss();
                listData.addAll(items);
                mAdapter.notifyDataSetChanged();
                category_item_lv.refreshComplete();



                if(items.size() < 1){
                    category_item_lv.bringToFront();
                    error_view.showEmpty();
                }else{
                    error_view.showContent();
                }
            }
        });

        category_item_lv.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                current_page = 1;
                apiCall.getOrdersWithID(getContext(), CONST.GET_ORDERS_WITH_ID, target_city_id, current_page,id, new ApiCallTools.onOrderLoad() {
                    @Override
                    public void onOrdersLoad(ArrayList<OrderItem> items, boolean status) {
                        progressDialog.dismiss();
                        listData.clear();
                        listData.addAll(items);
                        mAdapter.notifyDataSetChanged();
                        category_item_lv.refreshComplete();
                    }
                });
            }

            @Override
            public void onLoadMore() {
                current_page++;
                apiCall.getOrdersWithID(getContext(), CONST.GET_ORDERS_WITH_ID, target_city_id, current_page,id, new ApiCallTools.onOrderLoad() {
                    @Override
                    public void onOrdersLoad(ArrayList<OrderItem> items, boolean status) {
                        progressDialog.dismiss();

                        if (status) {
                            category_item_lv.setNoMore(true);
                            mAdapter.notifyDataSetChanged();
                        } else {
                            listData.addAll(items);
                            mAdapter.notifyDataSetChanged();
                            category_item_lv.refreshComplete();
                        }
                    }
                });
            }
        });

    }
}
