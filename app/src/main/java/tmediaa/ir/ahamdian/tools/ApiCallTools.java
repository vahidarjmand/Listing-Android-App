package tmediaa.ir.ahamdian.tools;

import android.content.Context;
import android.provider.Settings;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.http.NameValuePair;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.koushikdutta.ion.builder.Builders;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
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

    public static void getTK(Context context, String app_token, final completeEvent callBack) {
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

    public static void getTKWithPassword(Context context, String app_token, String password, final completeEvent callBack) {
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

    public void getOrders(Context context, String url, final int city, final int page, final onOrderLoad orderLoad) {
        this.orderLoad = orderLoad;
        Ion.with(context)
                .load(url)
                .setBodyParameter("city_id", String.valueOf(city))
                .setBodyParameter("page", String.valueOf(page))
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null) {
                        } else {
                            listData.clear();
                            JsonArray items = result.get("items").getAsJsonObject().get("data").getAsJsonArray();
                            total_page = result.get("items").getAsJsonObject().get("last_page").getAsInt();

                            if (page > total_page) {
                                status = true;
                            } else {
                                status = false;
                            }

                            for (int i = 0; i < items.size(); i++) {
                                JsonObject item = items.get(i).getAsJsonObject();
                                OrderItem orderItem = new OrderItem();
                                orderItem.setId(item.get("id").getAsInt());
                                orderItem.setCat_name(item.get("catname").getAsString());
                                orderItem.setTitle(item.get("title").getAsString());
                                orderItem.setDesc(item.get("desc").getAsString());
                                orderItem.setAttachments(item.get("attachments").getAsJsonArray());
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


    public void getOrdersWithID(Context context, String url, final int city, final int page, int id, final onOrderLoad orderLoad) {


        this.orderLoad = orderLoad;
        Ion.with(context)
                .load(url)
                .setBodyParameter("city_id", String.valueOf(city))
                .setBodyParameter("page", String.valueOf(page))
                .setBodyParameter("cat_id", String.valueOf(id))
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null) {

                        } else {
                            listData.clear();
                            JsonArray items = result.get("items").getAsJsonObject().get("data").getAsJsonArray();
                            total_page = result.get("items").getAsJsonObject().get("last_page").getAsInt();

                            if (page > total_page) {
                                status = true;
                            } else {
                                status = false;
                            }

                            for (int i = 0; i < items.size(); i++) {
                                JsonObject item = items.get(i).getAsJsonObject();
                                OrderItem orderItem = new OrderItem();
                                orderItem.setId(item.get("id").getAsInt());
                                orderItem.setCat_name(item.get("catname").getAsString());
                                orderItem.setTitle(item.get("title").getAsString());
                                orderItem.setDesc(item.get("desc").getAsString());
                                orderItem.setAttachments(item.get("attachments").getAsJsonArray());
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


    public void getOrdersByName(Context context, String url, final int city, String str, final int page, final onOrderLoad orderLoad) {
        this.orderLoad = orderLoad;
        Ion.with(context)
                .load(url)
                .setBodyParameter("city_id", String.valueOf(city))
                .setBodyParameter("page", String.valueOf(page))
                .setBodyParameter("search", str)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (e != null) {
                        } else {

                            CONST.writeFile(result);
                            listData.clear();

                            JsonParser parser = new JsonParser();
                            JsonObject root = parser.parse(result).getAsJsonObject();
                            JsonArray items = root.get("items").getAsJsonObject().get("data").getAsJsonArray();
                            total_page = root.get("items").getAsJsonObject().get("last_page").getAsInt();

                            if (page > total_page) {
                                status = true;
                            } else {
                                status = false;
                            }

                            for (int i = 0; i < items.size(); i++) {
                                JsonObject item = items.get(i).getAsJsonObject();
                                OrderItem orderItem = new OrderItem();
                                orderItem.setId(item.get("id").getAsInt());
                                orderItem.setCat_name(item.get("catname").getAsString());
                                orderItem.setTitle(item.get("title").getAsString());
                                orderItem.setDesc(item.get("desc").getAsString());
                                orderItem.setAttachments(item.get("attachments").getAsJsonArray());
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

    public void getOrdersByBundle(final Context context, String url, final int city, final int page, List<NameValuePair> send_paramas, final onOrderLoad orderLoad) {
        this.orderLoad = orderLoad;

        Builders.Any.B ionBuilder = Ion.with(context).load("POST", url);
        for (int i = 0; i < send_paramas.size(); i++) {
            ionBuilder.setBodyParameter(send_paramas.get(i).getName(), send_paramas.get(i).getValue());
        }

        ionBuilder.setBodyParameter("city_id", String.valueOf(city));

        ionBuilder.asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {


                if (e == null) {
                    JsonParser parser = new JsonParser();
                    JsonObject object = parser.parse(result.getResult()).getAsJsonObject();


                    JsonArray items = object.get("items").getAsJsonObject().get("data").getAsJsonArray();
                    total_page = object.get("items").getAsJsonObject().get("last_page").getAsInt();


                    if (page > total_page) {
                        status = true;
                    } else {
                        status = false;
                    }

                    listData.clear();
                    for (int i = 0; i < items.size(); i++) {
                        JsonObject item = items.get(i).getAsJsonObject();
                        OrderItem orderItem = new OrderItem();
                        orderItem.setId(item.get("id").getAsInt());
                        orderItem.setCat_name(item.get("catname").getAsString());
                        orderItem.setTitle(item.get("title").getAsString());
                        orderItem.setDesc(item.get("desc").getAsString());
                        orderItem.setAttachments(item.get("attachments").getAsJsonArray());
                        orderItem.setDate(item.get("updated_at").getAsString());
                        listData.add(orderItem);

                    }
                    if (orderLoad != null) {
                        orderLoad.onOrdersLoad(listData, status);
                    }

                } else {
                    Toasty.error(context, "خطا در برفراری ارتباط با سرور", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    public interface onOrderLoad {
        void onOrdersLoad(ArrayList<OrderItem> items, boolean is_end);
    }
}
