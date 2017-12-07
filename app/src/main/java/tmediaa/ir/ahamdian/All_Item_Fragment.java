package tmediaa.ir.ahamdian;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import tmediaa.ir.ahamdian.adapters.AllItemAdapter;
import tmediaa.ir.ahamdian.itemView.ShowOrder;
import tmediaa.ir.ahamdian.model.OrderItem;
import tmediaa.ir.ahamdian.otto.AppEvents;
import tmediaa.ir.ahamdian.otto.GlobalBus;
import tmediaa.ir.ahamdian.tools.ApiCallTools;
import tmediaa.ir.ahamdian.tools.AppSharedPref;
import tmediaa.ir.ahamdian.tools.CONST;

/**
 * Created by tmediaa on 9/14/2017.
 */

public class All_Item_Fragment extends Fragment {

    private Context context;
    private View rootView;
    private int current_page = 1;

    private XRecyclerView mRecyclerView;
    private AllItemAdapter mAdapter;
    private ArrayList<OrderItem> listData;
    private ApiCallTools apiCall = new ApiCallTools();
    private boolean hasNetWork = true;
    private Drawable dividerDrawable;
    private ProgressDialog progressDialog;

    private int target_city_id = 0;

    public static All_Item_Fragment newInstance() {
        All_Item_Fragment fragment = new All_Item_Fragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        return inflater.inflate(R.layout.all_item_fragment_layout, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootView = view;

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("در حال بارگذاری");
        progressDialog.show();

        target_city_id = Integer.valueOf(AppSharedPref.read("CITY_ID", "0"));

        if (target_city_id != 0) {
            loadItem(Integer.valueOf(target_city_id));
        } else {
            loadItem(1);
        }


        ((MainActivity) getActivity()).setOnBackClickListener(new MainActivity.OnBackClickListener() {
            @Override
            public boolean onBackClick() {
                return false;
            }
        });
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

    @Subscribe
    public void getCityID(final AppEvents.UpdateLocation events) {
        listData.clear();
        mAdapter.notifyDataSetChanged();
    }

    private void loadItem(final int id) {

        progressDialog.dismiss();
        listData = new ArrayList<OrderItem>();

        mRecyclerView = (XRecyclerView) rootView.findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        //GridLayoutManager layoutManager = new GridLayoutManager(context,2);

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

        mAdapter = new AllItemAdapter(context, listData);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.refresh();

        mAdapter.setOnItemClickListener(new AllItemAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, OrderItem data) {
                Intent i =new Intent(getContext(), ShowOrder.class);
                i.putExtra("id",data.getId());
                i.putExtra("mode",false);
                startActivity(i);
            }
        });


        apiCall.getOrders(context, CONST.GET_ORDERS, id, current_page, new ApiCallTools.onOrderLoad() {
            @Override
            public void onOrdersLoad(ArrayList<OrderItem> items, boolean status) {
                listData.addAll(items);
                mAdapter.notifyDataSetChanged();
                // mRecyclerView.refreshComplete();
            }
        });

        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                current_page = 1;
                apiCall.getOrders(context, CONST.GET_ORDERS, id, current_page, new ApiCallTools.onOrderLoad() {
                    @Override
                    public void onOrdersLoad(ArrayList<OrderItem> items, boolean status) {
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
                apiCall.getOrders(context, CONST.GET_ORDERS, id, current_page, new ApiCallTools.onOrderLoad() {
                    @Override
                    public void onOrdersLoad(ArrayList<OrderItem> items, boolean status) {
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

}