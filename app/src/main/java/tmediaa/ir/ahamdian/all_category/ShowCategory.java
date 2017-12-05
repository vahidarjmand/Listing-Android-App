package tmediaa.ir.ahamdian.all_category;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.santalu.emptyview.EmptyView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import tmediaa.ir.ahamdian.R;
import tmediaa.ir.ahamdian.adapters.AllItemAdapter;
import tmediaa.ir.ahamdian.adapters.CategoryButtonListPagerAdapter;
import tmediaa.ir.ahamdian.model.CategoryItem;
import tmediaa.ir.ahamdian.model.OrderItem;
import tmediaa.ir.ahamdian.otto.AppEvents;
import tmediaa.ir.ahamdian.otto.GlobalBus;
import tmediaa.ir.ahamdian.tools.ApiCallTools;
import tmediaa.ir.ahamdian.tools.AppSharedPref;
import tmediaa.ir.ahamdian.tools.CONST;

/**
 * Created by tmediaa on 11/29/2017.
 */

public class ShowCategory extends Fragment {
    private View rootView;
    private ProgressDialog progressDialog;
    private ArrayList<CategoryItem> lists = new ArrayList<>();
    private ViewPager category_pager;
    private CategoryButtonListPagerAdapter adapter;

    private EmptyView error_view ;
    private int current_page = 1;
    private int total_page = 0;
    private XRecyclerView mRecyclerView;
    private AllItemAdapter mAdapter;
    private ArrayList<OrderItem> listData = new ArrayList<OrderItem>();

    private ApiCallTools apiCall = new ApiCallTools();
    private boolean hasNetWork = true;
    private Drawable dividerDrawable;
    private int is_recived = 0;
    private int target_city_id = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.category_inner_show, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rootView = view;
        error_view = (EmptyView) view.findViewById(R.id.error_view);
        error_view.showContent();

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("در حال بارگذاری");
        progressDialog.show();
        mRecyclerView = (XRecyclerView) view.findViewById(R.id.category_recyclerview);

        target_city_id = Integer.valueOf(AppSharedPref.read("CITY_ID", "0"));
        getCategoriyes(getArguments().getInt("selected_category"));




    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        GlobalBus.getBus().register(this);
    }

    @Override
    public void onDetach() {
        GlobalBus.getBus().unregister(this);
        super.onDetach();
    }

    private void getCategoriyes(final int target_id) {

        AppEvents.ChangeToolbarOrder update_category = new AppEvents.ChangeToolbarOrder();
        GlobalBus.getBus().post(update_category);

        Ion.with(getContext())
                .load(CONST.CATEGORIES_URL)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        progressDialog.dismiss();
                        if (e == null) {
                            JsonParser parser = new JsonParser();
                            JsonObject root = parser.parse(result).getAsJsonObject();
                            JsonArray items = root.get("items").getAsJsonArray();
                            for (JsonElement element : items) {
                                JsonObject obj = element.getAsJsonObject();
                                if (obj.get("parent_id").isJsonNull()) {
                                    CategoryItem item = new CategoryItem();
                                    item.setId(obj.get("id").getAsInt());
                                    item.setName(obj.get("name").getAsString());
                                    item.setParent_id(null);
                                    item.setDepth(obj.get("depth").getAsInt());
                                    item.setChilds(returnChilds(items, item.getId()));
                                    lists.add(item);
                                }
                            }

                            category_pager = (ViewPager) rootView.findViewById(R.id.category_pager);
                            adapter = new CategoryButtonListPagerAdapter(getContext(), lists , target_id);
                            category_pager.setAdapter(adapter);

                            loadOrders(lists.get(target_id).getId(),target_city_id);




                        } else {
                            Toasty.error(getContext(),getString(R.string.connection_error), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void loadOrders(final int id, final int target_city_id) {

        progressDialog.show();
        if(listData.size() > 0){
            listData.clear();
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dividerDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.item_row_drawable, null);
        } else {
            dividerDrawable = getResources().getDrawable(R.drawable.item_row_drawable);
        }

        mRecyclerView.addItemDecoration(mRecyclerView.new DividerItemDecoration(dividerDrawable));
        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallBeat);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallBeat);

        mAdapter = new AllItemAdapter(getContext(), listData);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.refresh();

        mAdapter.setOnItemClickListener(new AllItemAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, OrderItem data) {
                Log.d(CONST.APP_LOG, "view: " + data.getId());
            }
        });


        apiCall.getOrdersWithID(getContext(), CONST.GET_ORDERS_WITH_ID, target_city_id, current_page,id, new ApiCallTools.onOrderLoad() {
            @Override
            public void onOrdersLoad(ArrayList<OrderItem> items, boolean status) {
                progressDialog.dismiss();
                listData.addAll(items);
                mAdapter.notifyDataSetChanged();
                mRecyclerView.refreshComplete();

                if(items.size() < 1){
                    error_view.showEmpty();
                }else{
                    error_view.showContent();
                }
            }
        });

        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
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
                        mRecyclerView.refreshComplete();
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
                            mRecyclerView.setNoMore(true);
                            mAdapter.notifyDataSetChanged();
                        } else {
                            listData.addAll(items);
                            mAdapter.notifyDataSetChanged();
                            mRecyclerView.refreshComplete();
                        }
                    }
                });
            }
        });
    }

    private ArrayList<CategoryItem> returnChilds(JsonArray items, int target_parent_id) {
        ArrayList<CategoryItem> child_items = new ArrayList<>();
        for (JsonElement element : items) {
            JsonObject obj = element.getAsJsonObject();
            if (!obj.get("parent_id").isJsonNull()) {
                if (obj.get("parent_id").getAsInt() == target_parent_id) {
                    CategoryItem item = new CategoryItem();
                    item.setId(obj.get("id").getAsInt());
                    item.setName(obj.get("name").getAsString());
                    item.setParent_id(obj.get("parent_id").getAsInt());
                    item.setDepth(obj.get("depth").getAsInt());
                    item.setChilds(returnChilds(items, item.getId()));
                    child_items.add(item);
                }
            }

        }
        return child_items;
    }

    @Subscribe
    public void ChangePager(final AppEvents.ChangePager events) {
        if(events.getChangeList().size() > 0){
            category_pager.setCurrentItem(1);
            adapter.updateLevel2(events.getChangeList());
        }
        loadOrders(events.getID(), target_city_id);
    }
}
