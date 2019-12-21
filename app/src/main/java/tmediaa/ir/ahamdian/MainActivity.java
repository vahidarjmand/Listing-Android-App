package tmediaa.ir.ahamdian;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.farsitel.bazaar.IUpdateCheckService;
import com.squareup.otto.Subscribe;

import cn.pedant.SweetAlert.SweetAlertDialog;
import tmediaa.ir.ahamdian.otto.AppEvents;
import tmediaa.ir.ahamdian.otto.GlobalBus;
import tmediaa.ir.ahamdian.tools.AppSharedPref;
import tmediaa.ir.ahamdian.tools.CONST;
import tmediaa.ir.ahamdian.tools.LocaleUtils;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;


public class MainActivity extends AppCompatActivity {
    //private SpaceNavigationView spaceNavigationView;

    private int mSelectedItem;
    private int current_city_id = 0;

    private ProgressDialog progressDialog;
    private Context mContext;

    private LinearLayout rootView;
    private String target_city_id = "";
    private AHBottomNavigation bottomNavigation;

    IUpdateCheckService service;
    UpdateServiceConnection connection;
    private static final String TAG = "UpdateCheck";

    public MainActivity() {
        LocaleUtils.updateConfig(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initService();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("پلاک");
        setSupportActionBar(toolbar);
        toolbar.setOverScrollMode(View.OVER_SCROLL_NEVER);

        Button add_order_btn = (Button) toolbar.findViewById(R.id.add_order_btn);
        add_order_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppSharedPref.read("online", 0) == 1) {
                    Intent i = new Intent(MainActivity.this, InsertOrderActivity.class);
                    startActivity(i);
                    /*AppEvents.SpaceNavClick space_click_event = new AppEvents.SpaceNavClick(position);
                    GlobalBus.getBus().post(space_click_event);*/
                } else {
                    Intent mainIntent = new Intent(MainActivity.this, RegisterActivity.class);
                    startActivity(mainIntent);
                }
            }
        });
        mContext = this;

        rootView = (LinearLayout) findViewById(R.id.rootView);


        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
        bottomNavigation.setTranslucentNavigationEnabled(true);
        bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#FEFEFE"));
        bottomNavigation.setItemDisableColor(Color.parseColor("#3A000000"));

        AHBottomNavigationItem item1 = new AHBottomNavigationItem(R.string.all_order_label, R.drawable.all_item_icon, R.color.colorPrimary);
        //AHBottomNavigationItem item2 = new AHBottomNavigationItem(R.string.category_tab_label, R.drawable.category_icon, R.color.colorPrimary);
        //AHBottomNavigationItem item3 = new AHBottomNavigationItem(R.string.add_order_label, R.drawable.add_new_item_icon, R.color.colorAccent);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(R.string.search_tab_label, R.drawable.search_icon, R.color.colorPrimary);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(R.string.profile_tab_label, R.drawable.profile_icon, R.color.colorPrimary);

        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            bottomNavigation.setAccentColor(getColor(R.color.primary_dark));
            bottomNavigation.setInactiveColor(getColor(R.color.primary));
        } else {
            bottomNavigation.setAccentColor(getResources().getColor(R.color.primary_dark));
            bottomNavigation.setInactiveColor(getResources().getColor(R.color.primary));
        }


        bottomNavigation.setTranslucentNavigationEnabled(true);

        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.SHOW_WHEN_ACTIVE);

        bottomNavigation.setColored(true);

        bottomNavigation.setCurrentItem(0, true);
        bottomNavigation.enableItemAtPosition(0);
        bottomNavigation.refresh();
        bottomNavigation.restoreBottomNavigation();
        bottomNavigation.setBehaviorTranslationEnabled(true);

        selectFragment(0);

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(final int position, boolean wasSelected) {
                // Do something cool here...

                selectFragment(position);
                AppEvents.SpaceNavClick space_click_event = new AppEvents.SpaceNavClick(position);
                GlobalBus.getBus().post(space_click_event);

                return true;
            }
        });
        bottomNavigation.setOnNavigationPositionListener(new AHBottomNavigation.OnNavigationPositionListener() {
            @Override
            public void onPositionChange(int y) {
                // Manage the new y position
            }
        });


        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;


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
            //showCitySelector();
        }

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(getString(R.string.fontpath))
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
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
            case 1:
                selectedFragment = Search_Fragment.newInstance();
                break;
            case 2:
                selectedFragment = Profile_Fragment.newInstance();
                break;
            case 0:
                selectedFragment = All_Item_Fragment.newInstance();
                break;

        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, selectedFragment);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return true;
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //spaceNavigationView.onSaveInstanceState(outState);
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
        //spaceNavigationView.bringToFront();
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    class UpdateServiceConnection implements ServiceConnection {
        public void onServiceConnected(ComponentName name, IBinder boundService) {
            service = IUpdateCheckService.Stub
                    .asInterface((IBinder) boundService);
            try {
                long vCode = service.getVersionCode("tmediaa.ir.ahamdian");


                PackageInfo pInfo = MainActivity.this.getPackageManager().getPackageInfo(getPackageName(), 0);
                int version = pInfo.versionCode;
                if (version < vCode) {
                    new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("دریافت نسخه جدید")
                            .setContentText("نرم افزار شما به نسخه جدید ارتقاء یافته است جهت دانلود لطفا تایید کنید.")
                            .setConfirmText("تایید")
                            .setCancelText("انصراف")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(final SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setData(Uri.parse("bazaar://details?id=" + "tmediaa.ir.ahamdian"));
                                    intent.setPackage("com.farsitel.bazaar");
                                    startActivity(intent);

                                }
                            })
                            .show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d(TAG, "onServiceConnected(): Connected");
        }

        public void onServiceDisconnected(ComponentName name) {
            service = null;
            Log.d(TAG, "onServiceDisconnected(): Disconnected");
        }
    }

    private void initService() {
        Log.i(TAG, "initService()");
        connection = new UpdateServiceConnection();
        Intent i = new Intent(
                "com.farsitel.bazaar.service.UpdateCheckService.BIND");
        i.setPackage("com.farsitel.bazaar");
        boolean ret = bindService(i, connection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "initService() bound value: " + ret);
    }

    /**
     * This is our function to un-binds this activity from our service.
     */
    private void releaseService() {
        unbindService(connection);
        connection = null;
        Log.d(TAG, "releaseService(): unbound.");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseService();
    }
}