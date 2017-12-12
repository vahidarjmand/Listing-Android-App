package tmediaa.ir.ahamdian.tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import tmediaa.ir.ahamdian.model.Brand;

/**
 * Created by tmediaa on 9/14/2017.
 */

public class CONST {
    public static final String BASE_URL = "http://192.168.115.1/ahmadian/public";
    public static final String STORAGE = "http://192.168.115.1/ahmadian/storage/app/";
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
}


