package tmediaa.ir.ahamdian.tools;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import tmediaa.ir.ahamdian.R;
import tmediaa.ir.ahamdian.SplashScreen;
import tmediaa.ir.ahamdian.model.Brand;
import tmediaa.ir.ahamdian.otto.AppEvents;
import tmediaa.ir.ahamdian.otto.GlobalBus;

/**
 * Created by tmediaa on 9/14/2017.
 */

public class CONST {
    public static final String BASE_URL = "http://pelakapp.ir";
    public static final String STORAGE = "http://pelakapp.ir/";
    public static final int SPLASH_TIME = 3000;
    public static boolean IS_HELP = false;
    public static String APP_LOG = "APP_LOG";
    public static String GET_ORDERS = BASE_URL + "/api/vr1/getOrders";
    public static String GET_ORDERS_WITH_ID = BASE_URL + "/api/vr1/getOrdersByCat";
    public static String GET_ORDER_EDIT = BASE_URL + "/api/vr1/getOrderedit";
    public static final String APP_TOKEN = BASE_URL + "/api/vr1/accessapi";
    public static final String USER_REGISTER = BASE_URL + "/api/vr1/registerUser";
    public static final String USER_LOGIN = BASE_URL + "/api/vr1/loginUser";
    public static final String ACTIVE_USER = BASE_URL + "/api/vr1/active_user";
    public static final String CATEGORIES_URL = BASE_URL + "/api/vr1/getCategories";
    public static final String GET_ORDER = BASE_URL + "/api/vr1/getOrder";
    public static final String GET_ORDER_WITH_STATE = BASE_URL + "/api/vr1/getOrderStates";
    public static final String ADD_STATE = BASE_URL + "/api/vr1/addState";
    public static final String GET_CITIES = BASE_URL + "/api/vr1/getLocation";
    public static final String ADD_ORDER = BASE_URL + "/api/vr1/add_order";
    public static final String EDIT_ORDER = BASE_URL + "/api/vr1/edit_order";
    public static final String EDIT_ORDER_V2  = BASE_URL + "/api/vr1/edit_order_2";
    public static final String GET_SETS = BASE_URL + "/api/vr1/getSettings";
    public static final String RENEW_ORDER =  BASE_URL + "/api/vr1/renewOrder";
    public static final String UPGRADE_ORDER =  BASE_URL + "/api/vr1/upgradeOrder";


    public static final String REMOVE_ORDER = BASE_URL + "/api/vr1/del_order";
    public static final String GET_LOCATION_ID = BASE_URL + "/api/vr1/getLocationID";
    public static final String FINALIZE_ORDER = BASE_URL + "/api/vr1/finalize_order";
    public static final String SEARCH_API = BASE_URL + "/api/vr1/search";
    public static final String SEARCHSTR = BASE_URL + "/api/vr1/searchByStr";
    public static final String MY_ORDERS = BASE_URL + "/api/vr1/getuserOrders";

    public static ArrayList<Brand> NAGHLIYE_BRANDS_LIST = new ArrayList<>();


    public static void writeFile(String data) {
        final File path =
                Environment.getExternalStoragePublicDirectory
                        (
                                //Environment.DIRECTORY_PICTURES
                                Environment.DIRECTORY_DOCUMENTS + "/pelak/"
                        );

        // Make sure the path directory exists.
        if (!path.exists()) {
            // Make it, if it doesn't exit
            path.mkdirs();
        }

        final File file = new File(path, "output.html");

        // Save your stream, don't forget to flush() it before closing it.

        try {
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(data);

            myOutWriter.close();

            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
    public static  String formatInt(String number) {
        NumberFormat nf = NumberFormat.getInstance(new Locale("fa", "IR"));
        String str = nf.format(Long.valueOf(number));

        return str;
    }
    public static  String formatPriceInt(int number) {

        //String price_str = th.in.lordgift.widget.Utils.insertNumberComma(String.valueOf(number));

        NumberFormat nf = NumberFormat.getInstance(new Locale("fa", "IR"));
        String str = nf.format(Long.valueOf(number));

        return str;
    }
    public static  String formatPriceDouble(double number) {

        //String price_str = th.in.lordgift.widget.Utils.insertNumberComma(String.valueOf(number));

        NumberFormat nf = NumberFormat.getInstance(new Locale("fa", "IR"));
        String str = nf.format(Double.valueOf(number));

        return str;
    }

    public static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return manager.getActiveNetworkInfo();
    }

    public static boolean isConnected(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        return info != null && info.isConnected();
    }





    public static void showCitySelector(final Activity activity, final Context context, final ProgressDialog progressDialog) {
        final List<String> citiesList = new ArrayList<>();
        final List<Integer> citiesList_number = new ArrayList<>();


        progressDialog.show();
        citiesList.clear();
        Ion.with(context)
                .load(CONST.GET_CITIES)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        progressDialog.dismiss();


                        if (e == null) {
                            JsonParser jsonParser = new JsonParser();
                            JsonObject root = jsonParser.parse(result).getAsJsonObject();

                            JsonArray cities = root.get("cities").getAsJsonArray();
                            for (int i = 0; i < cities.size(); i++) {
                                citiesList_number.add(cities.get(i).getAsJsonObject().get("id").getAsInt());
                                citiesList.add(cities.get(i).getAsJsonObject().get("name").getAsString());
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.list_title, citiesList);
                            final AlertDialog.Builder builder = new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_LIGHT);
                            LayoutInflater inflater = activity.getLayoutInflater();
                            View view = inflater.inflate(R.layout.titlebar, null);
                            TextView title_tv = (TextView) view.findViewById(R.id.title_tv);
                            title_tv.setText("شهر محل سکونت خود را از لیست انتخاب کنید.");
                            builder.setCancelable(false);
                            builder.setCustomTitle(view);
                            builder.setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    int selected_id = citiesList_number.get(which);
                                    AppSharedPref.write("CITY_ID", String.valueOf(selected_id));
                                    AppSharedPref.write("CITY_NAME", citiesList.get(which));

                                    AppEvents.UpdateLocation id_event = new AppEvents.UpdateLocation(selected_id);
                                    GlobalBus.getBus().post(id_event);
                                    dialog.dismiss();
                                    Intent mStartActivity = new Intent(activity, SplashScreen.class);
                                    int mPendingIntentId = 123456;
                                    PendingIntent mPendingIntent = PendingIntent.getActivity(activity, mPendingIntentId, mStartActivity,
                                            PendingIntent.FLAG_CANCEL_CURRENT);
                                    AlarmManager mgr = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
                                    mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                                    System.exit(0);
                                }
                            });
                            builder.show();


                        } else {
                            Toasty.error(context, context.getString(R.string.connection_error), Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }

}


