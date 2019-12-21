package tmediaa.ir.ahamdian.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import tmediaa.ir.ahamdian.R;
import tmediaa.ir.ahamdian.model.Brand;

/**
 * Created by tmediaa on 10/6/2017.
 */

public class BrandAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Brand> mBrands;
    private LayoutInflater inflator;

    public BrandAdapter(Context context,ArrayList<Brand> brands) {
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
    public Brand getItem(int position) {
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
        label.setTextColor(Color.BLACK);
        label.setText(mBrands.get(position).getName());
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return super.getDropDownView(position, convertView, parent);
    }
}
