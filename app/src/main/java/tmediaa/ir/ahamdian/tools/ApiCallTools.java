package tmediaa.ir.ahamdian.tools;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

import tmediaa.ir.ahamdian.model.OrderItem;

/**
 * Created by tmediaa on 9/21/2017.
 */

public class ApiCallTools {
    private static String android_id = "";
    private onOrderLoad orderLoad;
    int total_page = 0;
    private ArrayList<OrderItem> listData = new ArrayList<OrderItem>();
    private boolean status = false;

    public interface completeEvent {
        void onCompleted(Exception e, JsonObject response);
    }

    public static void getTK(Context context,String app_token , final completeEvent callBack) {
        android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        Ion.with(context)
                .load(CONST.APP_TOKEN)
                .setBodyParameter("username", app_token)
                .setBodyParameter("password", android_id)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (callBack != null) {
                            callBack.onCompleted(e, result);
                        }
                    }
                });
    }

    public static void getTKWithPassword(Context context,String app_token,String password , final completeEvent callBack) {
        android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        Ion.with(context)
                .load(CONST.APP_TOKEN)
                .setBodyParameter("username", app_token)
                .setBodyParameter("password", password)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (callBack != null) {
                            callBack.onCompleted(e, result);
                        }
                    }
                });
    }

    public void getOrders(Context context, String url, final int page, final onOrderLoad orderLoad) {
        this.orderLoad = orderLoad;
        Ion.with(context)
                .load(CONST.GET_ORDERS)
                .addQuery("page", String.valueOf(page))
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null) {
                            Log.d(CONST.APP_LOG, "Error : " + e.getMessage());
                        } else {
                            listData.clear();
                            JsonArray items = result.get("items").getAsJsonObject().get("data").getAsJsonArray();
                            total_page = result.get("items").getAsJsonObject().get("last_page").getAsInt();

                            if(page > total_page){
                                status = true;
                            }else{
                                status = false;
                            }

                            for (int i = 0; i < items.size(); i++) {
                                JsonObject item = items.get(i).getAsJsonObject();
                                OrderItem orderItem = new OrderItem();
                                orderItem.setId(item.get("id").getAsInt());
                                orderItem.setTitle(item.get("title").getAsString());
                                orderItem.setDesc(item.get("desc").getAsString());
                                orderItem.setDate(item.get("updated_at").getAsString());
                                listData.add(orderItem);
                            }

                            if (orderLoad != null) {
                                orderLoad.onOrdersLoad(listData, status);
                            }

                        }
                    }
                });
    }


    public interface onOrderLoad {
        void onOrdersLoad(ArrayList<OrderItem> items,boolean is_end);
    }
}
