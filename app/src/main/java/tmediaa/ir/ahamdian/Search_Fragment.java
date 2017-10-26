package tmediaa.ir.ahamdian;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tmediaa.ir.ahamdian.tools.CONST;

/**
 * Created by tmediaa on 9/14/2017.
 */

public class Search_Fragment extends Fragment {
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

        Log.d(CONST.APP_LOG,"Search Fragment Instansate");
    }
}