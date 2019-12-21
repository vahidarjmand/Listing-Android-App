package tmediaa.ir.ahamdian.insertorders.categoryselector;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import tmediaa.ir.ahamdian.R;
import tmediaa.ir.ahamdian.adapters.CategoryMenuPagerAdapter;
import tmediaa.ir.ahamdian.interfaces.CategoryCallback;
import tmediaa.ir.ahamdian.model.Brand;
import tmediaa.ir.ahamdian.model.CategoryItem;
import tmediaa.ir.ahamdian.otto.AppEvents;
import tmediaa.ir.ahamdian.otto.GlobalBus;
import tmediaa.ir.ahamdian.tools.CONST;


public class CategorySelector extends DialogFragment implements DialogInterface.OnKeyListener {
    private Context context;
    private LinearLayout progress_container;
    private LinearLayout pager_container;
    private ArrayList<CategoryItem> lists = new ArrayList<>();
    private ViewPager category_pager;
    private View rootView;
    private CategoryMenuPagerAdapter adapter;
    private CategoryCallback mCallback;

    private int selected_id = 0;
    private String selected_name = "";

    public static CategorySelector newInstanse(CategoryCallback callback) {
        CategorySelector f = new CategorySelector();
        f.mCallback = callback;
        return f;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mCallback = (CategoryCallback) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling fragment must implement Callback interface");
        }

        context = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.category_selector_dialog, container);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        progress_container = (LinearLayout) rootView.findViewById(R.id.progress_container);
        pager_container = (LinearLayout) rootView.findViewById(R.id.pager_container);
        pager_container.setVisibility(View.GONE);
        getCategoriyes();
        return rootView;
    }

    private void getCategoriyes() {
        Ion.with(getContext())
                .load(CONST.CATEGORIES_URL)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e == null) {
                            progress_container.setVisibility(View.GONE);
                            pager_container.setVisibility(View.VISIBLE);

                            JsonArray items = result.get("items").getAsJsonArray();
                            JsonArray brands = result.get("brands").getAsJsonArray();
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

                            for (JsonElement obj : brands) {
                                JsonObject item = obj.getAsJsonObject();
                                Brand brand = new Brand();
                                brand.setId(item.get("id").getAsInt());
                                brand.setName(item.get("name").getAsString());
                                CONST.NAGHLIYE_BRANDS_LIST.add(brand);
                            }

                            category_pager = (ViewPager) rootView.findViewById(R.id.category_pager);

                            adapter = new CategoryMenuPagerAdapter(context, lists, new CategoryMenuPagerAdapter.PagerCallback() {
                                @Override
                                public void onChangePager(int page) {
                                    category_pager.setCurrentItem(page + 1);

                                }

                                @Override
                                public void closeMenu(int id, String name) {
                                    dismiss();
                                    selected_id = id;
                                    selected_name = name;
                                }
                            });


                            category_pager.setAdapter(adapter);


                        } else {
                            Toasty.error(getContext(),getString(R.string.connection_error), Toast.LENGTH_LONG).show();
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

    public void onResume() {
        super.onResume();


        Window window = getDialog().getWindow();
        Point size = new Point();

        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);

        int width = size.x;
        int height = size.y;
        window.setLayout((int) (width * 0.75), (int) (height * 0.9));
        window.setGravity(Gravity.CENTER);
        getDialog().setOnKeyListener(this);
    }


    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if ((keyCode == android.view.KeyEvent.KEYCODE_BACK)) {
            //This is the filter
            if (event.getAction() != KeyEvent.ACTION_DOWN) {
                if (category_pager.getCurrentItem() > 0) {
                    category_pager.setCurrentItem(category_pager.getCurrentItem() - 1);

                } else {
                    dismiss();
                }

                if (category_pager.getCurrentItem() == 0) {
                    adapter.loadRoot();
                }
                return true;
            } else {
                //Hide your keyboard here!!!!!!
                return true; // pretend we've processed it
            }
        } else
            return false; // pass on to be processed as normal
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mCallback != null) {

            mCallback.onCategoryChoose(selected_id, selected_name);
        }else{
            AppEvents.DialogCategoryChose id_event = new AppEvents.DialogCategoryChose(selected_name,selected_id);
            GlobalBus.getBus().post(id_event);
        }
    }
}
