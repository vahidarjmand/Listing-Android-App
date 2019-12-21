package tmediaa.ir.ahamdian;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.squareup.otto.Subscribe;

import tmediaa.ir.ahamdian.myorders.AboutUSActivity;
import tmediaa.ir.ahamdian.myorders.ContactUSActivity;
import tmediaa.ir.ahamdian.myorders.MyOrdersActivity;
import tmediaa.ir.ahamdian.otto.AppEvents;
import tmediaa.ir.ahamdian.otto.GlobalBus;
import tmediaa.ir.ahamdian.tools.AppSharedPref;
import tmediaa.ir.ahamdian.tools.CONST;

public class Profile_Fragment extends Fragment {
    private View rootView;
    private Button my_orders_btn;
    private Button city_selector_btn;

    private Button about_btn;
    private Button contact_btn;

    private ProgressDialog progressDialog;


    public static Profile_Fragment newInstance() {
        Profile_Fragment fragment = new Profile_Fragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rootView = view;

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("در حال بارگذاری");
        my_orders_btn = (Button) rootView.findViewById(R.id.my_orders_btn);
        about_btn = (Button) rootView.findViewById(R.id.about_btn);
        contact_btn = (Button) rootView.findViewById(R.id.contact_btn);
        city_selector_btn = (Button) rootView.findViewById(R.id.city_selector_btn);

        city_selector_btn.setText("انتخاب شهر( " + AppSharedPref.read("CITY_NAME","انتخاب نشده") + " )");
        my_orders_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), MyOrdersActivity.class);
                startActivity(i);
            }
        });
        city_selector_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CONST.showCitySelector(getActivity(),getContext(),progressDialog);
            }
        });

        ((MainActivity) getActivity()).setOnBackClickListener(new MainActivity.OnBackClickListener() {
            @Override
            public boolean onBackClick() {
                return false;
            }
        });

        about_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(getContext(), AboutUSActivity.class);
                startActivity(i);
            }
        });

        contact_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(getContext(), ContactUSActivity.class);
                startActivity(i);
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
}