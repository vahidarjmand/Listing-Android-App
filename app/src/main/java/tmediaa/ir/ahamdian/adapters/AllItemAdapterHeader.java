package tmediaa.ir.ahamdian.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import tmediaa.ir.ahamdian.R;
import tmediaa.ir.ahamdian.model.CategoryItem;
import tmediaa.ir.ahamdian.model.OrderItem;

/**
 * Created by tmediaa on 9/20/2017.
 */

public class AllItemAdapterHeader extends RecyclerView.Adapter<AllItemAdapterHeader.ViewHolder> implements View.OnClickListener {
    public ArrayList<CategoryItem> datas = null;
    private Context mContext;

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;


    public AllItemAdapterHeader(Context context, ArrayList<CategoryItem> datas) {

        

        this.mContext = context;
        this.datas = datas;
    }

    //Create a new view, called by LayoutManager
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.all_item_row, viewGroup, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    //The operation that binds the data to the interface
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        /*JsonArray img_list = datas.get(position).getAttachments();

        if (img_list.size() > 0) {

            Log.d(CONST.APP_LOG,"url: " + CONST.STORAGE + img_list.get(0).toString().replaceAll("\"", ""));
            Glide.with(mContext)
                    .load(CONST.STORAGE + img_list.get(0).toString().replaceAll("\"", ""))
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.error_placeholder)
                    .into(viewHolder.order_thumb);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                viewHolder.order_thumb.setImageDrawable(mContext.getDrawable(R.drawable.no_image));
            } else {
                viewHolder.order_thumb.setImageDrawable(mContext.getResources().getDrawable(R.drawable.no_image));
            }
        }


        viewHolder.order_title.setText(datas.get(position).getTitle());
        viewHolder.order_desc.setText("در: " + datas.get(position).getCat_name());

        viewHolder.status.setVisibility(View.GONE);
        if(datas.get(position).isOwn_mode()){
            viewHolder.status.setVisibility(View.VISIBLE);
            if(datas.get(position).getStatus().equals("disabled")){
                viewHolder.status.setText("در صف انتشار");
                viewHolder.status.setTextColor(mContext.getResources().getColor(R.color.white));
                viewHolder.status.setBackgroundColor(mContext.getResources().getColor(R.color.maroon));
            }else if(datas.get(position).getStatus().equals("enabled")){
                viewHolder.status.setText("منتشر شده");
                viewHolder.status.setTextColor(mContext.getResources().getColor(R.color.white));
                viewHolder.status.setBackgroundColor(mContext.getResources().getColor(R.color.green));
            }else if(datas.get(position).getStatus().equals("expired")){
                viewHolder.status.setText("منقضی شده");
                viewHolder.status.setTextColor(mContext.getResources().getColor(R.color.white));
                viewHolder.status.setBackgroundColor(mContext.getResources().getColor(R.color.text_color));
            }
        }

        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            TimeZone tz = TimeZone.getTimeZone("Asia/Tehran");
            Date date = dateformat.parse(datas.get(position).getDate());
            dateformat.setTimeZone(tz);
            String datetime = dateformat.format(date);

            //problem solive with this link
            //https://stackoverflow.com/questions/14687608/best-way-to-format-a-date-relative-to-now-on-android
            //https://stackoverflow.com/questions/22073073/default-locale-for-getrelativetimespanstring

            Date system_time = new Date();
            CharSequence system_time_string = DateFormat.format("yyyy-MM-dd HH:mm:ss", system_time.getTime());
            // viewHolder.order_time.setReferenceTime(date.getTime());
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //viewHolder.order_time.setText(datas.get(position).getDate());
        viewHolder.itemView.setTag(datas.get(position));*/
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v, (OrderItem) v.getTag());
        }
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    //Get the number of data
    @Override
    public int getItemCount() {
        return datas.size();
    }

    private String getDifferen(long diff){
        long seconds = diff/1000;
        long minutes = seconds/60;
        long hours = minutes/60;
        long days = hours/24;
        long weeks = days/7;
        long months = weeks/4;
        long years = months/12;

        if (seconds <= 60) {
            return "همین الان";
        }else {
            if (minutes <= 60) {
                if (minutes == 1) {
                    return "یک دقیقه قبل";
                } else {
                    return minutes + " دقیقه قبل";
                }
            }
            //Hours
            else {
                if (hours <= 24) {
                    if (hours == 1) {
                        return "یک ساعت پیش";
                    } else {
                        return hours + " ساعت قبل";
                    }
                }
                //Days
                else {
                    if (days <= 7) {
                        if (days == 1) {
                            return "دیروز";
                        } else {
                            return days + " روز پیش";
                        }
                    }
                    //Weeks
                    else {
                        if (weeks <= 4.3) {
                            if (weeks == 1) {
                                return "یک هفته پیش";
                            } else {
                                return weeks + " هفته پیش";
                            }
                        }
                        //Months
                        else {
                            if (months <= 12) {
                                if (months == 1) {
                                    return "یک ماه پیش";
                                } else {
                                    return months + " ماه پیش ";
                                }
                            }
                            //Years
                            else {
                                if (years == 1) {
                                    return "یک سال پیش";
                                } else {
                                    return years + " سال پیش ";
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    //define interface
    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, OrderItem data);
    }

    //Custom ViewHolder holds all the interface elements for each Item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView order_title;
        public TextView order_desc;
        public TextView status;
        public TextView order_time;
        public ImageView order_thumb;

        public ViewHolder(View view) {
            super(view);
            order_title = (TextView) view.findViewById(R.id.order_title);
            status = (TextView) view.findViewById(R.id.status);
            order_time = (TextView) view.findViewById(R.id.order_time);
            //order_desc = (TextView) view.findViewById(R.id.order_desc);
            order_thumb = (ImageView) view.findViewById(R.id.order_thumb);
        }
    }
}
