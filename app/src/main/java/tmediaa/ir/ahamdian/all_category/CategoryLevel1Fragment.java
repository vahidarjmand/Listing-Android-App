package tmediaa.ir.ahamdian.all_category;

import android.app.ProgressDialog;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import tmediaa.ir.ahamdian.R;
import tmediaa.ir.ahamdian.model.CategoryItem;
import tmediaa.ir.ahamdian.model.GridItem;
import tmediaa.ir.ahamdian.otto.AppEvents;
import tmediaa.ir.ahamdian.otto.GlobalBus;
import tmediaa.ir.ahamdian.tools.CONST;

/**
 * Created by tmediaa on 12/13/2017.
 */

public class CategoryLevel1Fragment extends Fragment {
    private View rootView;
    private ArrayList<GridItem> list_items;
    private XRecyclerView recyclerview;
    private GridAdapterView mAdapter;
    private GridLayoutManager layoutManager;
    private ProgressDialog progressDialog;
    private ArrayList<CategoryItem> lists = new ArrayList<>();

    public static CategoryLevel1Fragment newInstance(String title) {
        CategoryLevel1Fragment fragmentFirst = new CategoryLevel1Fragment();
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
        rootView = inflater.inflate(R.layout.category_level1_layout, container, false);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("در حال بارگذاری");

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int y = size.y;
        y = y / 5;

        recyclerview = (XRecyclerView) rootView.findViewById(R.id.recyclerview);
        calcItemList();
        recyclerview.setPullRefreshEnabled(false);
        recyclerview.setLoadingMoreEnabled(false);
        layoutManager = new GridLayoutManager(getContext(), 3);
        recyclerview.setLayoutManager(layoutManager);
        mAdapter = new GridAdapterView(getContext(), list_items, y);
        recyclerview.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new GridAdapterView.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, GridItem item) {
                getCategoriyes(item.get_index());
                progressDialog.show();
            }
        });



        return rootView;
    }

    private void calcItemList() {
        list_items = new ArrayList<>();

        GridItem item_1 = new GridItem();
        item_1.set_name("کسب و کار");
        item_1.set_color(R.color.grid_1);
        item_1.set_id(1);
        item_1.set_index(0);
        item_1.set_path(R.drawable.grid_icon_1);
        list_items.add(item_1);

        GridItem item_2 = new GridItem();
        item_2.set_name("املاک");
        item_2.set_color(R.color.grid_2);
        item_2.set_id(11);
        item_2.set_index(1);
        item_2.set_path(R.drawable.grid_icon_2);
        list_items.add(item_2);

        GridItem item_3 = new GridItem();
        item_3.set_name("وسائل نقلیه");
        item_3.set_color(R.color.grid_3);
        item_3.set_id(28);
        item_3.set_index(2);
        item_3.set_path(R.drawable.grid_icon_3);
        list_items.add(item_3);

        GridItem item_4 = new GridItem();
        item_4.set_name("دیجیتال");
        item_4.set_color(R.color.grid_4);
        item_4.set_index(3);
        item_4.set_id(37);
        item_4.set_path(R.drawable.grid_icon_4);
        list_items.add(item_4);

        GridItem item_5 = new GridItem();
        item_5.set_name("مربوطه به خانه");
        item_5.set_color(R.color.grid_5);
        item_5.set_id(58);
        item_5.set_index(4);
        item_5.set_path(R.drawable.grid_icon_5);
        list_items.add(item_5);

        GridItem item_6 = new GridItem();
        item_6.set_name("خدمات");
        item_6.set_id(82);
        item_6.set_index(5);
        item_6.set_color(R.color.grid_6);
        item_6.set_path(R.drawable.grid_icon_6);
        list_items.add(item_6);

        GridItem item_7 = new GridItem();
        item_7.set_name("پزشکی");
        item_7.set_id(103);
        item_7.set_index(6);
        item_7.set_color(R.color.grid_7);
        item_7.set_path(R.drawable.grid_icon_7);
        list_items.add(item_7);
    }


    private void getCategoriyes(final int target_id) {

        AppEvents.ChangeToolbarOrder update_category = new AppEvents.ChangeToolbarOrder();
        GlobalBus.getBus().post(update_category);
        lists.clear();

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

                            AppEvents.ChangePagerToID_L2 space_click_event = new AppEvents.ChangePagerToID_L2(target_id,lists);
                            GlobalBus.getBus().post(space_click_event);

                        } else {
                            Toasty.error(getContext(), getString(R.string.connection_error), Toast.LENGTH_LONG).show();
                        }
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
}
