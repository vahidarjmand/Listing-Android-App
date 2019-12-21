package tmediaa.ir.ahamdian.all_category;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import tmediaa.ir.ahamdian.R;
import tmediaa.ir.ahamdian.model.GridItem;

/**
 * Created by tmediaa on 12/13/2017.
 */

public class GridAdapterView extends RecyclerView.Adapter<GridAdapterView.ViewHolder> implements View.OnClickListener {
    private Context mContext;
    private ArrayList<GridItem> datas;
    private int y_size;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public GridAdapterView(Context context, ArrayList<GridItem> _datas, int size) {
        this.mContext = context;
        this.datas = _datas;
        this.y_size = size;
    }


    @Override
    public GridAdapterView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_view, parent, false);
        view.setOnClickListener(this);



        GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) view.getLayoutParams();
        params.height = y_size;
        view.setLayoutParams(params);
        return new GridAdapterView.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        GridItem item = datas.get(position);
        viewHolder.title_tv.setText(item.get_name());
        viewHolder.imageview.setBackgroundResource(item.get_path());
        viewHolder.itemView.setTag(datas.get(position));
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v, (GridItem) v.getTag());
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageview;
        public TextView title_tv;
        public View border;

        public ViewHolder(View view) {
            super(view);
            imageview = (ImageView) view.findViewById(R.id.img);
            title_tv = (TextView) view.findViewById(R.id.title);
            border = (View) view.findViewById(R.id.border);
        }
    }

    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, GridItem data);
    }
}
