package tmediaa.ir.ahamdian;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Subscribe;

import tmediaa.ir.ahamdian.all_category.CategoryLevel1Fragment;
import tmediaa.ir.ahamdian.all_category.CategoryLevel2Fragment;
import tmediaa.ir.ahamdian.all_category.CategoryLevel3Fragment;
import tmediaa.ir.ahamdian.all_category.CategoryPagerAdapetr;
import tmediaa.ir.ahamdian.otto.AppEvents;
import tmediaa.ir.ahamdian.otto.GlobalBus;

import static tmediaa.ir.ahamdian.R.id.category_pager;

public class Category_Fragment extends Fragment {

    private ViewPager viewPager;
    CategoryLevel2Fragment level2;
    CategoryLevel3Fragment level3;

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

        viewPager = (ViewPager) view.findViewById(category_pager);
        CategoryPagerAdapetr viewPagerAdapter = new CategoryPagerAdapetr(getChildFragmentManager());

        level2 = new CategoryLevel2Fragment();
        level3 = new CategoryLevel3Fragment();

        viewPagerAdapter.addFragment(new CategoryLevel1Fragment(), "First");
        viewPagerAdapter.addFragment(level2, "Secound");
        viewPagerAdapter.addFragment(level3, "Login");

        viewPager.setAdapter(viewPagerAdapter);

        ((MainActivity) getActivity()).setOnBackClickListener(new MainActivity.OnBackClickListener() {
            @Override
            public boolean onBackClick() {
                boolean can_exit = false;
                if(viewPager.getCurrentItem() == 0){
                    can_exit = false;
                }else{
                    if(viewPager.getCurrentItem() > 0){
                        viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
                        can_exit = true;
                    }
                }
                return can_exit;
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

    }

    @Subscribe
    public void ChangePagerToID_L2(final AppEvents.ChangePagerToID_L2 events) {
        viewPager.setCurrentItem(1);
        level2.fillList(events.getID(), events.getLists());
    }

    @Subscribe
    public void ChangePagerToID_L3(final AppEvents.ChangePagerToID_L3 events) {
        viewPager.setCurrentItem(2);
        level3.fillList(events.getID(), events.getLists());
    }

    private void hideCategoryShown() {
        Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag("categoryshow");
        if (fragment != null)
            getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
    }


}