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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.koushikdutta.async.http.BasicNameValuePair;
import com.koushikdutta.async.http.NameValuePair;
import com.santalu.emptyview.EmptyView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import tmediaa.ir.ahamdian.adapters.AllItemAdapter;
import tmediaa.ir.ahamdian.itemView.ShowOrder;
import tmediaa.ir.ahamdian.model.OrderItem;
import tmediaa.ir.ahamdian.otto.AppEvents;
import tmediaa.ir.ahamdian.otto.GlobalBus;
import tmediaa.ir.ahamdian.seach.SearchFilterActivity;
import tmediaa.ir.ahamdian.tools.ApiCallTools;
import tmediaa.ir.ahamdian.tools.AppSharedPref;
import tmediaa.ir.ahamdian.tools.CONST;

import static android.app.Activity.RESULT_OK;

/**
 * Created by tmediaa on 9/14/2017.
 */

public class Search_Fragment extends Fragment {

    private EmptyView error_view;
    private ProgressDialog progressDialog;
    private static final int FILTER_ACTIVITY_RESULT_CODE = 101;
    private View rootView;
    private EditText search_txt;
    private Button search_start_btn;
    private Button showFilterDialog;

    private List<NameValuePair> send_paramas = new ArrayList<NameValuePair>();
    private ArrayList<OrderItem> listData = new ArrayList<>();
    private int current_page = 1;
    private XRecyclerView mRecyclerView;
    private Drawable dividerDrawable;
    private AllItemAdapter mAdapter;
    private ApiCallTools apiCall = new ApiCallTools();
    private Integer target_city_id;
    boolean is_search_by_str = false;

    public static Search_Fragment newInstance() {
        Search_Fragment fragment = new Search_Fragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rootView = view;

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("در حال بارگذاری");
        progressDialog.setCancelable(false);

        mRecyclerView = (XRecyclerView) rootView.findViewById(R.id.search_recyclerview);
        error_view = (EmptyView) view.findViewById(R.id.error_view);
        search_txt = (EditText) rootView.findViewById(R.id.search_txt);
        search_start_btn = (Button) rootView.findViewById(R.id.search_start_btn);
        showFilterDialog = (Button) rootView.findViewById(R.id.showFilterDialog);

        showFilterDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), SearchFilterActivity.class);

                startActivityForResult(i, FILTER_ACTIVITY_RESULT_CODE);
            }
        });
        ((MainActivity) getActivity()).setOnBackClickListener(new MainActivity.OnBackClickListener() {
            @Override
            public boolean onBackClick() {
                return false;
            }
        });


        target_city_id = Integer.valueOf(AppSharedPref.read("CITY_ID", "0"));


        search_start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(search_txt.getText().toString().trim().length() > 0){
                    //is_search_by_str = !is_search_by_str;

                    is_search_by_str = !is_search_by_str;
                    if(is_search_by_str){
                        search_txt.setEnabled(false);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            search_start_btn.setBackground(getContext().getDrawable(R.drawable.search_icon_clear));
                        }else{
                            search_start_btn.setBackground(getResources().getDrawable(R.drawable.search_icon_clear));
                        }
                        searchByName(search_txt.getText().toString().trim());
                    }else{
                        search_txt.setEnabled(true);
                        search_txt.setText("");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            search_start_btn.setBackground(getContext().getDrawable(R.drawable.search_icon_img));
                        }else{
                            search_start_btn.setBackground(getResources().getDrawable(R.drawable.search_icon_img));
                        }
                        searchByName("");
                    }
                }else{
                    Toasty.info(getContext(),"متن مورد نظر جهت جستجو را وارد کنید.", Toast.LENGTH_LONG).show();
                }
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
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

        mAdapter = new AllItemAdapter(getContext(), listData);
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




    }

    private void searchByName(final String search) {
        listData.clear();
        progressDialog.show();

        current_page = 1;
        apiCall.getOrdersByName(getContext(), CONST.SEARCHSTR, target_city_id, search,current_page, new ApiCallTools.onOrderLoad() {
            @Override
            public void onOrdersLoad(ArrayList<OrderItem> items, boolean status) {
                progressDialog.dismiss();

                if (items.size() > 0) {
                    error_view.showContent();
                    listData.addAll(items);
                    mAdapter.notifyDataSetChanged();
                    // mRecyclerView.refreshComplete();
                } else {
                    error_view.showEmpty("هیچ آگهی در این مورد وجود ندارد.");
                }
            }
        });


        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                current_page = 1;
                apiCall.getOrdersByName(getContext(), CONST.SEARCHSTR, target_city_id, search,current_page, new ApiCallTools.onOrderLoad() {
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

                apiCall.getOrdersByName(getContext(), CONST.SEARCHSTR, target_city_id, search,current_page, new ApiCallTools.onOrderLoad() {
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILTER_ACTIVITY_RESULT_CODE) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                progressDialog.show();
                send_paramas.clear();
                if (bundle != null) {
                    for (String key : bundle.keySet()) {
                        Object value = bundle.get(key);
                        Log.d(CONST.APP_LOG, String.format("%s %s (%s)", key, value.toString(), value.getClass().getName()));
                        send_paramas.add(new BasicNameValuePair(key, value.toString()));
                    }
                }





                listData.clear();
                mAdapter.notifyDataSetChanged();
                apiCall.getOrdersByBundle(getContext(), CONST.SEARCH_API, target_city_id, current_page, send_paramas, new ApiCallTools.onOrderLoad() {
                    @Override
                    public void onOrdersLoad(ArrayList<OrderItem> items, boolean status) {
                        progressDialog.dismiss();
                        if (items.size() > 0) {
                            error_view.showContent();
                            listData.addAll(items);
                            mAdapter.notifyDataSetChanged();
                        } else {
                            error_view.showEmpty("هیچ آگهی در این مورد وجود ندارد.");
                        }
                    }
                });


                mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
                    @Override
                    public void onRefresh() {
                        current_page = 1;
                        apiCall.getOrdersByBundle(getContext(), CONST.SEARCH_API, target_city_id, current_page, send_paramas, new ApiCallTools.onOrderLoad() {
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

                        apiCall.getOrdersByBundle(getContext(), CONST.SEARCH_API, target_city_id, current_page, send_paramas, new ApiCallTools.onOrderLoad() {
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
    }
}