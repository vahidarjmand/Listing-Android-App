package tmediaa.ir.ahamdian;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.multi.SnackbarOnAnyDeniedMultiplePermissionsListener;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;
import com.luseen.spacenavigation.SpaceOnLongClickListener;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import tmediaa.ir.ahamdian.otto.AppEvents;
import tmediaa.ir.ahamdian.otto.GlobalBus;
import tmediaa.ir.ahamdian.tools.AppSharedPref;
import tmediaa.ir.ahamdian.tools.CONST;
import tmediaa.ir.ahamdian.tools.NoAPIGPSTracker;
import tmediaa.ir.ahamdian.tools.PermissionCheckActivity;
import tmediaa.ir.ahamdian.tools.SampleErrorListener;
import tmediaa.ir.ahamdian.tools.SampleMultiplePermissionListener;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;


public class MainActivity extends PermissionCheckActivity {
    private SpaceNavigationView spaceNavigationView;

    private int mSelectedItem;
    private int current_city_id = 0;

    private ProgressDialog progressDialog;
    private List<String> citiesList = new ArrayList<>();
    private List<Integer> citiesList_number = new ArrayList<>();
    private Context mContext;

    private MultiplePermissionsListener allPermissionsListener;
    private PermissionRequestErrorListener errorListener;
    private CoordinatorLayout rootView;
    private NoAPIGPSTracker mGPS;
    private String target_city_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        rootView = (CoordinatorLayout) findViewById(R.id.rootView);


        spaceNavigationView = (SpaceNavigationView) findViewById(R.id.space);
        spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.search_icon));
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.profile_icon));
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.category_icon));
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.all_item_icon));
        spaceNavigationView.setInActiveCentreButtonIconColor(Color.WHITE);


        spaceNavigationView.changeCurrentItem(2);
        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {

                if (AppSharedPref.read("online", 0) == 1) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent i = new Intent(MainActivity.this, InsertOrderActivity.class);
                            startActivity(i);

                            AppEvents.SpaceNavClick space_click_event = new AppEvents.SpaceNavClick(spaceNavigationView.getId());
                            GlobalBus.getBus().post(space_click_event);
                        }
                    }, CONST.SPLASH_TIME);
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent mainIntent = new Intent(MainActivity.this, RegisterActivity.class);
                            startActivity(mainIntent);
                            finish();
                        }
                    }, CONST.SPLASH_TIME);
                }
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                selectFragment(itemIndex);

                AppEvents.SpaceNavClick space_click_event = new AppEvents.SpaceNavClick(spaceNavigationView.getId());
                GlobalBus.getBus().post(space_click_event);
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
                Toast.makeText(MainActivity.this, itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();
            }
        });

        spaceNavigationView.setSpaceOnLongClickListener(new SpaceOnLongClickListener() {
            @Override
            public void onCentreButtonLongClick() {
                Toast.makeText(MainActivity.this, "onCentreButtonLongClick", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(int itemIndex, String itemName) {
                Toast.makeText(MainActivity.this, itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();
            }
        });


        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;


        selectFragment(2);
        if (CONST.IS_HELP) {
            new MaterialTapTargetPrompt.Builder(MainActivity.this, 0)
                    .setTarget((width / 2) + 5f, height - 90f)
                    .setPrimaryText("افزودن آگهی شما")
                    .setIcon(R.drawable.add_new_item_icon)
                    .setSecondaryText("با انتخاب این گزینه میتوانید آگهی جدید خود را اضافه کنید.")
                    .setAnimationInterpolator(new FastOutSlowInInterpolator())
                    .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
                        @Override
                        public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state) {
                            if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED) {

                            }
                        }
                    })
                    .show();
        }


        forceRTLIfSupported();

        target_city_id = AppSharedPref.read("CITY_ID", "");
        if (target_city_id.equals("")) {
            showCitySelector();
        }

        //getUserLocation();

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void forceRTLIfSupported() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
    }

    private void selectFragment(int item) {
        Fragment selectedFragment = null;
        switch (item) {
            case 0:
                selectedFragment = Search_Fragment.newInstance();
                break;
            case 1:
                selectedFragment = Profile_Fragment.newInstance();
                break;
            case 2:
                selectedFragment = All_Item_Fragment.newInstance();
                break;
            case 3:
                selectedFragment = Category_Fragment.newInstance();
                break;
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, selectedFragment);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.map_action:
                showCitySelector();
                break;
        }
        return true;
    }

    private void showCitySelector() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("در حال بارگذاری");
        progressDialog.show();

        citiesList.clear();
        Ion.with(mContext)
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

                            showCitySelectorDialog();
                        } else {
                            Toasty.error(mContext, getString(R.string.connection_error), Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }

    private void showCitySelectorDialog() {


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, citiesList);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
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
                AppEvents.UpdateLocation id_event = new AppEvents.UpdateLocation(selected_id);
                GlobalBus.getBus().post(id_event);


                dialog.dismiss();

                Intent mStartActivity = new Intent(MainActivity.this, SplashScreen.class);
                int mPendingIntentId = 123456;
                PendingIntent mPendingIntent = PendingIntent.getActivity(MainActivity.this, mPendingIntentId, mStartActivity,
                        PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager mgr = (AlarmManager) MainActivity.this.getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                System.exit(0);
            }
        });


        builder.show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        spaceNavigationView.onSaveInstanceState(outState);
    }


    private void getUserLocation() {
        MultiplePermissionsListener feedbackViewMultiplePermissionListener =
                new SampleMultiplePermissionListener(this);
        errorListener = new SampleErrorListener();
        allPermissionsListener =
                new CompositeMultiplePermissionsListener(feedbackViewMultiplePermissionListener,
                        SnackbarOnAnyDeniedMultiplePermissionsListener.Builder.with(rootView,
                                R.string.all_permissions_denied_feedback)
                                .withOpenSettingsButton("permission_rationale_settings_button_text")
                                .build());

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                )
                .withListener(allPermissionsListener)
                .withErrorListener(errorListener)
                .check();
    }

    private OnBackClickListener onBackClickListener;

    public interface OnBackClickListener {
        boolean onBackClick();
    }

    public void setOnBackClickListener(OnBackClickListener onBackClickListener) {
        this.onBackClickListener = onBackClickListener;
    }

    @Override
    public void onBackPressed() {
        if (onBackClickListener != null && onBackClickListener.onBackClick()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        GlobalBus.getBus().register(this);
        super.onStart();

    }

    @Override
    protected void onStop() {
        GlobalBus.getBus().unregister(this);
        super.onStop();

    }

    @Subscribe
    public void ChangeToolbarOrder(final AppEvents.ChangeToolbarOrder events) {
        spaceNavigationView.bringToFront();
    }

    /*
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void showPermissionRationale(final PermissionToken token) {
        new AlertDialog.Builder(this).setTitle(R.string.permission_rationale_title)
                .setMessage(R.string.permission_rationale_message)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        token.continuePermissionRequest();
                    }
                })
                .setPositiveButton(ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        token.continuePermissionRequest();
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        token.cancelPermissionRequest();
                    }
                })
                .show();
    }

    public void showPermissionDenied(String permission, boolean isPermanentlyDenied) {
        Toasty.error(mContext,"خطا در دسترسی به سخت افزار",Toast.LENGTH_LONG).show();
    }

    public void showPermissionGranted(String permission) {
        mGPS = new NoAPIGPSTracker(mContext);

        new SetUserLocation().execute(mGPS.getLatitude(),mGPS.getLongitude());
    }

    private class SetUserLocation extends AsyncTask<Double, Void, Void>{
        @Override
        protected Void doInBackground(Double... params) {

            Double lat = params[0];
            Double lon = params[1];
            getID(lat,lon);
            return null;
        }

        private void getID(Double lat, Double lon) {
            Locale locale = new Locale("fa_IR");
            try {
                Geocoder geocoder = new Geocoder(mContext, locale);
                List<Address> addresses = geocoder.getFromLocation(lat,lon, 1);
                if(addresses.get(0).getLocality() != null){
                    String city_name = addresses.get(0).getLocality();
                    Ion.with(mContext)
                            .load(CONST.GET_LOCATION_ID)
                            .setBodyParameter("city_name",city_name)
                            .asString()
                            .setCallback(new FutureCallback<String>() {
                                @Override
                                public void onCompleted(Exception e, String result) {
                                    if(e == null){
                                        JsonParser parser = new JsonParser();
                                        JsonObject root = parser.parse(result).getAsJsonObject();
                                        if (!root.get("city_id").isJsonNull()){
                                            int _id = root.get("city_id").getAsJsonObject().get("id").getAsInt();
                                            AppSharedPref.write("CITY_ID", String.valueOf(_id));
                                            AppEvents.UpdateLocation id_event = new AppEvents.UpdateLocation(_id);
                                            GlobalBus.getBus().post(id_event);
                                        }
                                    }
                                }
                            });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }*/

}