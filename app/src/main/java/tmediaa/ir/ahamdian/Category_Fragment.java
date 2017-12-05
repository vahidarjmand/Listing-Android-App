package tmediaa.ir.ahamdian;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import tmediaa.ir.ahamdian.all_category.GridAdapter;
import tmediaa.ir.ahamdian.all_category.ShowCategory;
import tmediaa.ir.ahamdian.model.GridItem;
import tmediaa.ir.ahamdian.otto.AppEvents;
import tmediaa.ir.ahamdian.otto.GlobalBus;

public class Category_Fragment extends Fragment {

    private GridView mGridView;
    private List<GridItem> list_items;

    private FrameLayout rootView;
    public static Category_Fragment newInstance() {
        Category_Fragment fragment = new Category_Fragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.category_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rootView = (FrameLayout) view.findViewById(R.id.frame_box);
        rootView.setVisibility(View.GONE);
        mGridView = (GridView) view.findViewById(R.id.grid_view);
        calcItemList();
        mGridView.setAdapter(new GridAdapter(getContext(),list_items));
        
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ShowCategory nextFrag = new ShowCategory();
                Bundle data = new Bundle();
                data.putInt("selected_category", position);
                nextFrag.setArguments(data);
                rootView.setVisibility(View.VISIBLE);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.rootView, nextFrag,"categoryshow")
                        .addToBackStack(null)
                        .commit();

            }
        });


        ((MainActivity) getActivity()).setOnBackClickListener(new MainActivity.OnBackClickListener() {
            @Override
            public boolean onBackClick() {
                if(rootView.isShown()){
                    rootView.setVisibility(View.GONE);
                    hideCategoryShown();
                    Category_Fragment fragment = Category_Fragment.newInstance();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, fragment);
                    transaction.commit();



                    return false;
                }
                return true;
            }
        });

    }
    private void calcItemList() {
        list_items = new ArrayList<>();

        GridItem item_1 = new GridItem();
        item_1.set_name("کسب و کار");
        item_1.set_color(R.color.grid_1);
        item_1.set_id(1);
        item_1.set_path(R.drawable.grid_icon_1);
        list_items.add(item_1);

        GridItem item_2 = new GridItem();
        item_2.set_name("املاک");
        item_2.set_color(R.color.grid_2);
        item_2.set_id(11);
        item_2.set_path(R.drawable.grid_icon_2);
        list_items.add(item_2);

        GridItem item_3 = new GridItem();
        item_3.set_name("وسائل نقلیه");
        item_3.set_color(R.color.grid_3);
        item_3.set_id(28);
        item_3.set_path(R.drawable.grid_icon_3);
        list_items.add(item_3);

        GridItem item_4 = new GridItem();
        item_4.set_name("دیجیتال");
        item_4.set_color(R.color.grid_4);
        item_4.set_id(37);
        item_4.set_path(R.drawable.grid_icon_4);
        list_items.add(item_4);

        GridItem item_5 = new GridItem();
        item_5.set_name("مربوطه به خانه");
        item_5.set_color(R.color.grid_5);
        item_5.set_id(58);
        item_5.set_path(R.drawable.grid_icon_5);
        list_items.add(item_5);

        GridItem item_6 = new GridItem();
        item_6.set_name("خدمات");
        item_6.set_id(82);
        item_6.set_color(R.color.grid_6);
        item_6.set_path(R.drawable.grid_icon_6);
        list_items.add(item_6);

        GridItem item_7 = new GridItem();
        item_7.set_name("پزشکی");
        item_7.set_id(103);
        item_7.set_color(R.color.grid_7);
        item_7.set_path(R.drawable.grid_icon_7);
        list_items.add(item_7);
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

    @Subscribe
    public void onSpaceClick(final AppEvents.SpaceNavClick events) {
        hideCategoryShown();
    }

    private void hideCategoryShown() {
        Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag("categoryshow");
        if(fragment != null)
            getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
    }


}