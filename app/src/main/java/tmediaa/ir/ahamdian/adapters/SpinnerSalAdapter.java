package tmediaa.ir.ahamdian.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import tmediaa.ir.ahamdian.R;

/**
 * Created by tmediaa on 10/6/2017.
 */

public class SpinnerSalAdapter extends BaseAdapter {
    private Context context;
    private List<int[]> mBrands;
    private LayoutInflater inflator;

    public SpinnerSalAdapter(Context context, List<int[]> brands) {
        this.context = context;
        this.mBrands = brands;
        inflator = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mBrands.size();
    }

    @Nullable
    @Override
    public int[] getItem(int position) {
        return mBrands.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = inflator.inflate(R.layout.spinner_branditem, null);
        // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
        TextView label = (TextView) convertView.findViewById(R.id.label);

        return label;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        convertView = inflator.inflate(R.layout.spinner_branditem, null);
        // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
        TextView label = (TextView) convertView.findViewById(R.id.label);
        label.setText("Sal: " + mBrands.get(position));
        return label;
    }
}
