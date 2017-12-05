package tmediaa.ir.ahamdian;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.http.BasicNameValuePair;
import com.koushikdutta.async.http.NameValuePair;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.koushikdutta.ion.builder.Builders;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import tmediaa.ir.ahamdian.otto.AppEvents;
import tmediaa.ir.ahamdian.otto.GlobalBus;
import tmediaa.ir.ahamdian.seach.SearchFilterActivity;
import tmediaa.ir.ahamdian.tools.CONST;

import static android.app.Activity.RESULT_OK;

/**
 * Created by tmediaa on 9/14/2017.
 */

public class Search_Fragment extends Fragment {

    private ProgressDialog progressDialog;
    private static final int FILTER_ACTIVITY_RESULT_CODE = 101;
    private View rootView;
    private EditText search_txt;
    private ImageButton search_start_btn;
    private Button showFilterDialog;

    private Map<String, List<String>> search_params = new ArrayMap<>();
    private List<NameValuePair> send_paramas = new ArrayList<NameValuePair>();


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
        progressDialog.setCancelable(true);


        search_txt = (EditText) rootView.findViewById(R.id.search_txt);
        search_start_btn = (ImageButton) rootView.findViewById(R.id.search_start_btn);
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILTER_ACTIVITY_RESULT_CODE) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                progressDialog.show();

                if (bundle != null) {
                    for (String key : bundle.keySet()) {
                        Object value = bundle.get(key);
                        Log.d(CONST.APP_LOG, String.format("%s %s (%s)", key, value.toString(), value.getClass().getName()));
                        send_paramas.add(new BasicNameValuePair(key, value.toString()));
                    }
                }
                Builders.Any.B ionBuilder = Ion.with(getContext()).load("POST", CONST.SEARCH_API);
                for (int i = 0; i < send_paramas.size(); i++) {
                    ionBuilder.setBodyParameter(send_paramas.get(i).getName(), send_paramas.get(i).getValue());
                }
                ionBuilder.asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result) {
                        progressDialog.dismiss();

                       // Log.d(CONST.APP_LOG, "result: " + result.getResult());
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