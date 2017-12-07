package tmediaa.ir.ahamdian.itemView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import app.dinus.com.loadingdrawable.LoadingView;
import es.dmoral.toasty.Toasty;
import eu.fiskur.simpleviewpager.ImageURLLoader;
import eu.fiskur.simpleviewpager.SimpleViewPager;
import tmediaa.ir.ahamdian.R;
import tmediaa.ir.ahamdian.myorders.EditActivity;
import tmediaa.ir.ahamdian.otto.GlobalBus;
import tmediaa.ir.ahamdian.tools.AppSharedPref;
import tmediaa.ir.ahamdian.tools.CONST;

public class ShowOrder extends AppCompatActivity {

    private static final int OPEN_EDIT_ORDER = 4578;
    private Context context;
    private ProgressDialog progressDialog;


    private Button upgrade_order_btn, order_edit_btn, order_delete_btn, order_pay_btn;
    private LoadingView gallery_loading;
    private SimpleViewPager image_pager;
    private LinearLayout order_insert_info, order_btn, slider_view, all_field_view, general_field, category_container, general_price_view, general_type_view, amlak_field, amlak_metraj_view, amlak_type_view, amlak_room_view, amlak_ejareh_view,
            amlak_homeshahr_view, amlak_vadie_view, amlak_sanad_view, naghliye_field, naghliye_brand_view, naghliye_sal_view, naghliye_kardkard_view;
    private TextView status_txt, item_title, item_desc, item_category, general_price_view_tv, general_type_view_tv, amlak_metraj_view_tv, amlak_type_view_tv, amlak_room_view_tv,
            amlak_homeshahr_view_tv, amlak_vadie_view_tv, amlak_ejareh_view_tv, amlak_sanad_view_tv, naghliye_brand_view_tv, naghliye_sal_view_tv, naghliye_kardkard_view_tv;

    private View general_price_view_sep,
            general_type_view_sep,
            amlak_metraj_view_sep,
            amlak_type_view_sep,
            amlak_room_view_sep,
            amlak_homeshahr_view_sep,
            amlak_vadie_view_sep,
            amlak_ejareh_view_sep,
            amlak_sanad_view_sep,
            naghliye_brand_view_sep,
            naghliye_sal_view_sep,
            naghliye_kardkard_view_sep;

    private View rootView;
    private JsonObject order;
    private String title;
    private String desc;
    private String email;
    private String tel;
    private String cat_name;
    private String brand_name;
    private JsonArray attachments;
    private String status;

    private List<String> image_urls;
    private int finilize_order_id;


    boolean mode;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_order);


        mode = getIntent().getExtras().getBoolean("mode");
        id = getIntent().getExtras().getInt("id");

        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("مدیریت آگهی");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("در حال بارگذاری");


        initViews();

        order_delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeOrder(id);
            }
        });
        switchmode(mode);
        loadorder(id);
    }

    private void switchmode(boolean mode) {
        if (mode) {
            order_insert_info.setVisibility(View.VISIBLE);
            order_btn.setVisibility(View.VISIBLE);


        } else {
            order_insert_info.setVisibility(View.GONE);
            order_btn.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        GlobalBus.getBus().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        GlobalBus.getBus().unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void initViews() {
        order_insert_info = (LinearLayout) findViewById(R.id.order_insert_info);
        order_btn = (LinearLayout) findViewById(R.id.order_btn);
        slider_view = (LinearLayout) findViewById(R.id.slider_view);
        all_field_view = (LinearLayout) findViewById(R.id.all_field_view);
        general_field = (LinearLayout) findViewById(R.id.general_field);
        category_container = (LinearLayout) findViewById(R.id.category_container);
        general_price_view = (LinearLayout) findViewById(R.id.general_price_view);
        general_type_view = (LinearLayout) findViewById(R.id.general_type_view);
        amlak_field = (LinearLayout) findViewById(R.id.amlak_field);
        amlak_metraj_view = (LinearLayout) findViewById(R.id.amlak_metraj_view);
        amlak_type_view = (LinearLayout) findViewById(R.id.amlak_type_view);
        amlak_room_view = (LinearLayout) findViewById(R.id.amlak_room_view);
        amlak_ejareh_view = (LinearLayout) findViewById(R.id.amlak_ejareh_view);
        amlak_homeshahr_view = (LinearLayout) findViewById(R.id.amlak_homeshahr_view);
        amlak_vadie_view = (LinearLayout) findViewById(R.id.amlak_vadie_view);
        amlak_sanad_view = (LinearLayout) findViewById(R.id.amlak_sanad_view);
        naghliye_field = (LinearLayout) findViewById(R.id.naghliye_field);
        naghliye_brand_view = (LinearLayout) findViewById(R.id.naghliye_brand_view);
        naghliye_sal_view = (LinearLayout) findViewById(R.id.naghliye_sal_view);
        naghliye_kardkard_view = (LinearLayout) findViewById(R.id.naghliye_kardkard_view);


        status_txt = (TextView) findViewById(R.id.status_txt);
        item_title = (TextView) findViewById(R.id.item_title);
        item_desc = (TextView) findViewById(R.id.item_desc);
        item_category = (TextView) findViewById(R.id.item_category);
        general_price_view_tv = (TextView) findViewById(R.id.general_price_view_tv);
        general_type_view_tv = (TextView) findViewById(R.id.general_type_view_tv);
        amlak_metraj_view_tv = (TextView) findViewById(R.id.amlak_metraj_view_tv);
        amlak_type_view_tv = (TextView) findViewById(R.id.amlak_type_view_tv);
        amlak_room_view_tv = (TextView) findViewById(R.id.amlak_room_view_tv);
        amlak_homeshahr_view_tv = (TextView) findViewById(R.id.amlak_homeshahr_view_tv);
        amlak_vadie_view_tv = (TextView) findViewById(R.id.amlak_vadie_view_tv);
        amlak_ejareh_view_tv = (TextView) findViewById(R.id.amlak_ejareh_view_tv);
        amlak_sanad_view_tv = (TextView) findViewById(R.id.amlak_sanad_view_tv);
        naghliye_brand_view_tv = (TextView) findViewById(R.id.naghliye_brand_view_tv);
        naghliye_sal_view_tv = (TextView) findViewById(R.id.naghliye_sal_view_tv);
        naghliye_kardkard_view_tv = (TextView) findViewById(R.id.naghliye_kardkard_view_tv);

        upgrade_order_btn = (Button) findViewById(R.id.upgrade_order_btn);
        order_edit_btn = (Button) findViewById(R.id.order_edit_btn);
        order_delete_btn = (Button) findViewById(R.id.order_delete_btn);
        order_pay_btn = (Button) findViewById(R.id.order_pay_btn);
        order_pay_btn.setVisibility(View.GONE);


        general_price_view_sep = (View) findViewById(R.id.general_price_view_sep);
        general_type_view_sep = (View) findViewById(R.id.general_type_view_sep);
        amlak_metraj_view_sep = (View) findViewById(R.id.amlak_metraj_view_sep);
        amlak_type_view_sep = (View) findViewById(R.id.amlak_type_view_sep);
        amlak_room_view_sep = (View) findViewById(R.id.amlak_room_view_sep);
        amlak_homeshahr_view_sep = (View) findViewById(R.id.amlak_homeshahr_view_sep);
        amlak_vadie_view_sep = (View) findViewById(R.id.amlak_vadie_view_sep);
        amlak_ejareh_view_sep = (View) findViewById(R.id.amlak_ejareh_view_sep);
        amlak_sanad_view_sep = (View) findViewById(R.id.amlak_sanad_view_sep);
        naghliye_brand_view_sep = (View) findViewById(R.id.naghliye_brand_view_sep);
        naghliye_sal_view_sep = (View) findViewById(R.id.naghliye_sal_view_sep);
        naghliye_kardkard_view_sep = (View) findViewById(R.id.naghliye_kardkard_view_sep);

        gallery_loading = (LoadingView) findViewById(R.id.gallery_loading);
        image_pager = (SimpleViewPager) findViewById(R.id.image_pager);

        order_pay_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
        //getItemOrder();
    }

    private void loadorder(int id) {
        upgrade_order_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        order_edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i_edit = new Intent(ShowOrder.this, EditActivity.class);
                startActivityForResult(i_edit,OPEN_EDIT_ORDER);
            }
        });

        //removeOrder(events.getId());
        Ion.with(context)
                .load(CONST.GET_ORDER_WITH_STATE)
                .setBodyParameter("order_id", String.valueOf(id))
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {

                        if (e == null) {
                            JsonParser parser = new JsonParser();
                            JsonObject obj = parser.parse(result).getAsJsonObject();
                            if (obj.has("status")) {
                                String status = obj.get("status").getAsString();
                                if (status.equals("found")) {
                                    int cat_id = obj.get("order").getAsJsonObject().get("cat_id").getAsInt();
                                    showOrderView(cat_id, obj);
                                }
                            }
                        } else {
                            Toasty.error(context, "خطا در برقراری ارتباط با سرور", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    private void showOrderView(int cat_id, JsonObject obj) {
        order = obj.get("order").getAsJsonObject();
        title = order.get("title").getAsString();
        desc = order.get("desc").getAsString();
        email = order.get("email").isJsonNull() ? "" : order.get("email").getAsString();
        tel = order.get("title").getAsString();
        cat_name = obj.get("cat_name").getAsString();
        brand_name = obj.get("brand_name").getAsString();
        attachments = obj.get("order").getAsJsonObject().get("attachments").getAsJsonArray();
        status = obj.get("order").getAsJsonObject().get("status").getAsString();


        if(mode){
            showstate(obj.get("states").getAsJsonArray());
        }

        initstatus(status);
        item_title.setText(title);
        item_desc.setText(desc);
        item_category.setText(cat_name);

        showSliderView(attachments);

        switch (cat_id) {
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
                showAmlakForm(cat_id, order);
                disableNaghliyeField();
                break;

            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
                showNaghliyeForm(cat_id, order);
                disableAmlakField();
                break;
            case 104:
            case 105:
            case 106:
            case 107:
            case 108:
            case 109:
            case 110:
            case 111:
                showPezeshkForm(cat_id);
                disableAmlakField();
                disableNaghliyeField();
                break;
            default:
                showGeneralForm(cat_id, order);
                disableAmlakField();
                disableNaghliyeField();
                break;
        }
    }

    private void showstate(JsonArray datas) {
        Log.d(CONST.APP_LOG,"stat: " +datas.size());
    }

    private void initstatus(String status) {
        if(status.equals("enabled")){
            upgrade_order_btn.setVisibility(View.VISIBLE);
            status_txt.setText("آگهی شما منتشر شده و در حال نمایش بصورت عمومی می باشد.");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                status_txt.setTextColor(getColor(R.color.green_700));
            }else{
                status_txt.setTextColor(getResources().getColor(R.color.green_700));
            }
        }
        if(status.equals("disabled")){
            upgrade_order_btn.setVisibility(View.GONE);
            status_txt.setText("آگهی شما در صف انتشار می باشد و بعد از تایید توسط تیم پلاک منتشر خواهد شد.");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                status_txt.setTextColor(getColor(R.color.deep_orange_A700));
            }else{
                status_txt.setTextColor(getResources().getColor(R.color.deep_orange_A700));
            }
        }

    }

    private void showGeneralForm(int id, JsonObject order) {
        general_field.setVisibility(View.VISIBLE);
        showGeneralPrice(order);
        showGeneralType(order);

        general_price_view_sep.setVisibility(View.VISIBLE);
        general_type_view_sep.setVisibility(View.GONE);
    }

    private void showAmlakForm(int id, JsonObject order) {


        switch (id) {
            // amlak forosh start
            case 15:
                amlak_homeshahr_view.setVisibility(View.VISIBLE);
                amlak_sanad_view.setVisibility(View.GONE);
                amlak_vadie_view.setVisibility(View.GONE);
                amlak_ejareh_view.setVisibility(View.GONE);
                general_price_view.setVisibility(View.VISIBLE);
                amlak_room_view.setVisibility(View.VISIBLE);
                general_type_view.setVisibility(View.VISIBLE);
                amlak_metraj_view.setVisibility(View.VISIBLE);

                general_price_view_sep.setVisibility(View.VISIBLE);
                general_type_view_sep.setVisibility(View.VISIBLE);
                amlak_metraj_view_sep.setVisibility(View.VISIBLE);
                amlak_type_view_sep.setVisibility(View.VISIBLE);
                amlak_room_view_sep.setVisibility(View.VISIBLE);
                amlak_homeshahr_view_sep.setVisibility(View.GONE);
                amlak_vadie_view_sep.setVisibility(View.GONE);
                amlak_ejareh_view_sep.setVisibility(View.GONE);
                amlak_sanad_view_sep.setVisibility(View.GONE);
                break;
            case 16:
                amlak_homeshahr_view.setVisibility(View.VISIBLE);
                amlak_sanad_view.setVisibility(View.VISIBLE);
                amlak_vadie_view.setVisibility(View.GONE);
                amlak_ejareh_view.setVisibility(View.GONE);
                general_price_view.setVisibility(View.VISIBLE);
                amlak_room_view.setVisibility(View.VISIBLE);
                general_type_view.setVisibility(View.VISIBLE);
                amlak_metraj_view.setVisibility(View.VISIBLE);

                general_price_view_sep.setVisibility(View.VISIBLE);
                general_type_view_sep.setVisibility(View.VISIBLE);
                amlak_metraj_view_sep.setVisibility(View.VISIBLE);
                amlak_type_view_sep.setVisibility(View.VISIBLE);
                amlak_room_view_sep.setVisibility(View.VISIBLE);
                amlak_homeshahr_view_sep.setVisibility(View.VISIBLE);
                amlak_vadie_view_sep.setVisibility(View.GONE);
                amlak_ejareh_view_sep.setVisibility(View.GONE);
                amlak_sanad_view_sep.setVisibility(View.GONE);
                break;
            case 17:
                amlak_homeshahr_view.setVisibility(View.VISIBLE);
                amlak_sanad_view.setVisibility(View.GONE);
                amlak_vadie_view.setVisibility(View.GONE);
                amlak_ejareh_view.setVisibility(View.GONE);
                general_price_view.setVisibility(View.VISIBLE);
                amlak_room_view.setVisibility(View.GONE);
                general_type_view.setVisibility(View.VISIBLE);
                amlak_metraj_view.setVisibility(View.VISIBLE);

                general_price_view_sep.setVisibility(View.VISIBLE);
                general_type_view_sep.setVisibility(View.VISIBLE);
                amlak_metraj_view_sep.setVisibility(View.VISIBLE);
                amlak_type_view_sep.setVisibility(View.VISIBLE);
                amlak_room_view_sep.setVisibility(View.VISIBLE);
                amlak_homeshahr_view_sep.setVisibility(View.VISIBLE);
                amlak_vadie_view_sep.setVisibility(View.GONE);
                amlak_ejareh_view_sep.setVisibility(View.GONE);
                amlak_sanad_view_sep.setVisibility(View.GONE);
                break;
            case 18:
                amlak_homeshahr_view.setVisibility(View.VISIBLE);
                amlak_sanad_view.setVisibility(View.VISIBLE);
                amlak_vadie_view.setVisibility(View.GONE);
                amlak_ejareh_view.setVisibility(View.GONE);
                general_price_view.setVisibility(View.VISIBLE);
                amlak_room_view.setVisibility(View.VISIBLE);
                general_type_view.setVisibility(View.VISIBLE);
                amlak_metraj_view.setVisibility(View.VISIBLE);

                general_price_view_sep.setVisibility(View.VISIBLE);
                general_type_view_sep.setVisibility(View.VISIBLE);
                amlak_metraj_view_sep.setVisibility(View.VISIBLE);
                amlak_type_view_sep.setVisibility(View.VISIBLE);
                amlak_room_view_sep.setVisibility(View.VISIBLE);
                amlak_homeshahr_view_sep.setVisibility(View.VISIBLE);
                amlak_vadie_view_sep.setVisibility(View.GONE);
                amlak_ejareh_view_sep.setVisibility(View.GONE);
                amlak_sanad_view_sep.setVisibility(View.GONE);
                break;
            // amlak forosh end
            // amlak ejare start
            case 19:
                amlak_sanad_view.setVisibility(View.GONE);
                general_price_view.setVisibility(View.GONE);
                amlak_vadie_view.setVisibility(View.VISIBLE);
                amlak_room_view.setVisibility(View.VISIBLE);
                amlak_ejareh_view.setVisibility(View.VISIBLE);
                general_type_view.setVisibility(View.VISIBLE);
                amlak_metraj_view.setVisibility(View.VISIBLE);
                amlak_homeshahr_view.setVisibility(View.VISIBLE);

                general_price_view_sep.setVisibility(View.VISIBLE);
                general_type_view_sep.setVisibility(View.VISIBLE);
                amlak_metraj_view_sep.setVisibility(View.VISIBLE);
                amlak_type_view_sep.setVisibility(View.VISIBLE);
                amlak_room_view_sep.setVisibility(View.VISIBLE);
                amlak_homeshahr_view_sep.setVisibility(View.VISIBLE);
                amlak_vadie_view_sep.setVisibility(View.VISIBLE);
                amlak_ejareh_view_sep.setVisibility(View.GONE);
                amlak_sanad_view_sep.setVisibility(View.GONE);
                break;
            case 20:
                amlak_sanad_view.setVisibility(View.GONE);
                general_price_view.setVisibility(View.GONE);
                amlak_vadie_view.setVisibility(View.VISIBLE);
                amlak_room_view.setVisibility(View.VISIBLE);
                amlak_ejareh_view.setVisibility(View.VISIBLE);
                general_type_view.setVisibility(View.VISIBLE);
                amlak_metraj_view.setVisibility(View.VISIBLE);
                amlak_homeshahr_view.setVisibility(View.VISIBLE);

                general_price_view_sep.setVisibility(View.VISIBLE);
                general_type_view_sep.setVisibility(View.VISIBLE);
                amlak_metraj_view_sep.setVisibility(View.VISIBLE);
                amlak_type_view_sep.setVisibility(View.VISIBLE);
                amlak_room_view_sep.setVisibility(View.VISIBLE);
                amlak_homeshahr_view_sep.setVisibility(View.VISIBLE);
                amlak_vadie_view_sep.setVisibility(View.VISIBLE);
                amlak_ejareh_view_sep.setVisibility(View.GONE);
                amlak_sanad_view_sep.setVisibility(View.GONE);
                break;
            case 21:
                amlak_sanad_view.setVisibility(View.GONE);
                general_price_view.setVisibility(View.GONE);
                amlak_vadie_view.setVisibility(View.VISIBLE);
                amlak_ejareh_view.setVisibility(View.VISIBLE);
                amlak_room_view.setVisibility(View.GONE);
                general_type_view.setVisibility(View.VISIBLE);
                amlak_metraj_view.setVisibility(View.VISIBLE);
                amlak_homeshahr_view.setVisibility(View.VISIBLE);


                general_price_view_sep.setVisibility(View.GONE);
                general_type_view_sep.setVisibility(View.VISIBLE);
                amlak_metraj_view_sep.setVisibility(View.VISIBLE);
                amlak_type_view_sep.setVisibility(View.VISIBLE);
                amlak_room_view_sep.setVisibility(View.GONE);
                amlak_homeshahr_view_sep.setVisibility(View.VISIBLE);
                amlak_vadie_view_sep.setVisibility(View.VISIBLE);
                amlak_ejareh_view_sep.setVisibility(View.GONE);
                amlak_sanad_view_sep.setVisibility(View.GONE);
                break;
            case 22:
                amlak_sanad_view.setVisibility(View.GONE);
                general_price_view.setVisibility(View.GONE);
                amlak_vadie_view.setVisibility(View.VISIBLE);
                amlak_ejareh_view.setVisibility(View.VISIBLE);
                amlak_room_view.setVisibility(View.GONE);
                general_price_view.setVisibility(View.GONE);
                amlak_vadie_view.setVisibility(View.VISIBLE);
                amlak_room_view.setVisibility(View.VISIBLE);
                general_type_view.setVisibility(View.VISIBLE);
                amlak_metraj_view.setVisibility(View.VISIBLE);
                amlak_homeshahr_view.setVisibility(View.VISIBLE);

                general_price_view_sep.setVisibility(View.GONE);
                general_type_view_sep.setVisibility(View.VISIBLE);
                amlak_metraj_view_sep.setVisibility(View.VISIBLE);
                amlak_type_view_sep.setVisibility(View.VISIBLE);
                amlak_room_view_sep.setVisibility(View.VISIBLE);
                amlak_homeshahr_view_sep.setVisibility(View.VISIBLE);
                amlak_vadie_view_sep.setVisibility(View.VISIBLE);
                amlak_ejareh_view_sep.setVisibility(View.GONE);
                amlak_sanad_view_sep.setVisibility(View.GONE);
                break;
            case 23:
                amlak_sanad_view.setVisibility(View.GONE);
                general_price_view.setVisibility(View.GONE);
                amlak_vadie_view.setVisibility(View.VISIBLE);
                amlak_ejareh_view.setVisibility(View.VISIBLE);
                amlak_room_view.setVisibility(View.GONE);
                general_type_view.setVisibility(View.VISIBLE);
                amlak_metraj_view.setVisibility(View.VISIBLE);
                amlak_homeshahr_view.setVisibility(View.GONE);

                general_price_view_sep.setVisibility(View.GONE);
                general_type_view_sep.setVisibility(View.VISIBLE);
                amlak_metraj_view_sep.setVisibility(View.VISIBLE);
                amlak_type_view_sep.setVisibility(View.VISIBLE);
                amlak_room_view_sep.setVisibility(View.GONE);
                amlak_homeshahr_view_sep.setVisibility(View.GONE);
                amlak_vadie_view_sep.setVisibility(View.VISIBLE);
                amlak_ejareh_view_sep.setVisibility(View.GONE);
                amlak_sanad_view_sep.setVisibility(View.GONE);

                break;
            // amlak ejare end
            // amlak khadamat start
            case 24:
                general_type_view.setVisibility(View.GONE);
                amlak_metraj_view.setVisibility(View.GONE);
                amlak_sanad_view.setVisibility(View.GONE);
                general_price_view.setVisibility(View.GONE);
                amlak_vadie_view.setVisibility(View.GONE);
                amlak_homeshahr_view.setVisibility(View.GONE);
                amlak_ejareh_view.setVisibility(View.GONE);
                amlak_room_view.setVisibility(View.GONE);

                general_price_view_sep.setVisibility(View.GONE);
                general_type_view_sep.setVisibility(View.GONE);
                amlak_metraj_view_sep.setVisibility(View.GONE);
                amlak_type_view_sep.setVisibility(View.GONE);
                amlak_room_view_sep.setVisibility(View.GONE);
                amlak_homeshahr_view_sep.setVisibility(View.GONE);
                amlak_vadie_view_sep.setVisibility(View.GONE);
                amlak_ejareh_view_sep.setVisibility(View.GONE);
                amlak_sanad_view_sep.setVisibility(View.GONE);

                break;
            case 25:
                general_type_view.setVisibility(View.GONE);
                amlak_metraj_view.setVisibility(View.GONE);
                amlak_sanad_view.setVisibility(View.GONE);
                general_price_view.setVisibility(View.GONE);
                amlak_vadie_view.setVisibility(View.GONE);
                amlak_homeshahr_view.setVisibility(View.GONE);
                amlak_ejareh_view.setVisibility(View.GONE);
                amlak_room_view.setVisibility(View.GONE);

                general_price_view_sep.setVisibility(View.GONE);
                general_type_view_sep.setVisibility(View.GONE);
                amlak_metraj_view_sep.setVisibility(View.GONE);
                amlak_type_view_sep.setVisibility(View.GONE);
                amlak_room_view_sep.setVisibility(View.GONE);
                amlak_homeshahr_view_sep.setVisibility(View.GONE);
                amlak_vadie_view_sep.setVisibility(View.GONE);
                amlak_ejareh_view_sep.setVisibility(View.GONE);
                amlak_sanad_view_sep.setVisibility(View.GONE);
                break;
            case 26:
                general_price_view.setVisibility(View.GONE);
                amlak_metraj_view.setVisibility(View.GONE);
                general_type_view.setVisibility(View.GONE);
                amlak_sanad_view.setVisibility(View.GONE);
                general_price_view.setVisibility(View.GONE);
                amlak_homeshahr_view.setVisibility(View.GONE);
                amlak_vadie_view.setVisibility(View.GONE);
                amlak_ejareh_view.setVisibility(View.GONE);
                amlak_room_view.setVisibility(View.GONE);

                general_price_view_sep.setVisibility(View.GONE);
                general_type_view_sep.setVisibility(View.GONE);
                amlak_metraj_view_sep.setVisibility(View.GONE);
                amlak_type_view_sep.setVisibility(View.GONE);
                amlak_room_view_sep.setVisibility(View.GONE);
                amlak_homeshahr_view_sep.setVisibility(View.GONE);
                amlak_vadie_view_sep.setVisibility(View.GONE);
                amlak_ejareh_view_sep.setVisibility(View.GONE);
                amlak_sanad_view_sep.setVisibility(View.GONE);
                break;
            case 27:
                general_type_view.setVisibility(View.GONE);
                amlak_metraj_view.setVisibility(View.GONE);
                amlak_sanad_view.setVisibility(View.GONE);
                general_price_view.setVisibility(View.GONE);
                amlak_homeshahr_view.setVisibility(View.GONE);
                amlak_vadie_view.setVisibility(View.GONE);
                amlak_ejareh_view.setVisibility(View.GONE);
                amlak_room_view.setVisibility(View.GONE);

                general_price_view_sep.setVisibility(View.GONE);
                general_type_view_sep.setVisibility(View.GONE);
                amlak_metraj_view_sep.setVisibility(View.GONE);
                amlak_type_view_sep.setVisibility(View.GONE);
                amlak_room_view_sep.setVisibility(View.GONE);
                amlak_homeshahr_view_sep.setVisibility(View.GONE);
                amlak_vadie_view_sep.setVisibility(View.GONE);
                amlak_ejareh_view_sep.setVisibility(View.GONE);
                amlak_sanad_view_sep.setVisibility(View.GONE);
                break;
            // amlak khadamat end
        }


        showGeneralPrice(order);
        showGeneralType(order);

        int amlak_metraj_value = order.get("amlak_metrajh").isJsonNull() ? 0 : order.get("amlak_metrajh").getAsInt();
        int amlak_vadie_value = order.get("amlak_vadieh").isJsonNull() ? 0 : order.get("amlak_vadieh").getAsInt();
        int amlak_ejareh_value = order.get("amlak_ejare").isJsonNull() ? 0 : order.get("amlak_ejare").getAsInt();
        int amlak_room_count_value = order.get("amlak_room_count").isJsonNull() ? 0 : order.get("amlak_room_count").getAsInt();

        String amlak_sanad_value = "";
        if (!order.get("amlak_sanad_edari").isJsonNull()) {
            if (order.get("amlak_sanad_edari").getAsInt() == 0) {
                amlak_sanad_value = "ندارد";
            } else {
                amlak_sanad_value = "دارد";
            }
        }

        String amlak_homeshahr_value = "";
        if (!order.get("amlak_homeshahr").isJsonNull()) {
            if (order.get("amlak_homeshahr").getAsInt() == 1) {
                amlak_homeshahr_value = "هست";
            } else {
                amlak_homeshahr_value = "نیست";
            }
        }
        String amlak_type_value = "";
        if (!order.get("amlak_type").isJsonNull()) {
            if (order.get("amlak_type").getAsInt() == 1) {
                amlak_type_value = "شخصی";
            } else {
                amlak_type_value = "بنگاه املاک";
            }
        }

        amlak_metraj_view_tv.setText(CONST.formatPriceInt(amlak_metraj_value) + " متر ");
        amlak_sanad_view_tv.setText(amlak_sanad_value);
        amlak_homeshahr_view_tv.setText(amlak_homeshahr_value);
        amlak_vadie_view_tv.setText(CONST.formatPriceInt(amlak_vadie_value) + " تومان ");
        amlak_ejareh_view_tv.setText(CONST.formatPriceInt(amlak_ejareh_value) + " تومان ");
        amlak_room_view_tv.setText(CONST.formatPriceInt(amlak_room_count_value));
        amlak_type_view_tv.setText(amlak_type_value);
    }

    private void showNaghliyeForm(int id, JsonObject order) {
        switch (id) {
            case 30:
                //yadaki
                naghliye_brand_view.setVisibility(View.GONE);
                naghliye_sal_view.setVisibility(View.GONE);
                naghliye_kardkard_view.setVisibility(View.GONE);
                general_price_view_sep.setVisibility(View.VISIBLE);
                general_type_view_sep.setVisibility(View.GONE);
                naghliye_brand_view_sep.setVisibility(View.GONE);
                naghliye_sal_view_sep.setVisibility(View.GONE);
                naghliye_kardkard_view_sep.setVisibility(View.GONE);
                break;
            case 31:
                //motor cycle
                naghliye_brand_view.setVisibility(View.GONE);
                naghliye_sal_view.setVisibility(View.VISIBLE);
                naghliye_kardkard_view.setVisibility(View.VISIBLE);
                general_price_view_sep.setVisibility(View.VISIBLE);
                general_type_view_sep.setVisibility(View.VISIBLE);
                naghliye_brand_view_sep.setVisibility(View.GONE);
                naghliye_sal_view_sep.setVisibility(View.VISIBLE);
                naghliye_kardkard_view_sep.setVisibility(View.GONE);
                break;
            case 32:
                //ghayegh
                naghliye_brand_view.setVisibility(View.GONE);
                naghliye_sal_view.setVisibility(View.GONE);
                naghliye_kardkard_view.setVisibility(View.GONE);
                general_price_view_sep.setVisibility(View.VISIBLE);
                general_type_view_sep.setVisibility(View.GONE);
                naghliye_brand_view_sep.setVisibility(View.GONE);
                naghliye_sal_view_sep.setVisibility(View.GONE);
                naghliye_kardkard_view_sep.setVisibility(View.GONE);
                break;
            case 33:
                // motafareghe
                naghliye_brand_view.setVisibility(View.GONE);
                naghliye_sal_view.setVisibility(View.GONE);
                naghliye_kardkard_view.setVisibility(View.GONE);
                general_price_view_sep.setVisibility(View.VISIBLE);
                general_type_view_sep.setVisibility(View.GONE);
                naghliye_brand_view_sep.setVisibility(View.GONE);
                naghliye_sal_view_sep.setVisibility(View.GONE);
                naghliye_kardkard_view_sep.setVisibility(View.GONE);
                break;
            case 34:
                //savari
                naghliye_brand_view.setVisibility(View.VISIBLE);
                naghliye_sal_view.setVisibility(View.VISIBLE);
                naghliye_kardkard_view.setVisibility(View.VISIBLE);
                general_price_view_sep.setVisibility(View.VISIBLE);
                general_type_view_sep.setVisibility(View.VISIBLE);
                naghliye_brand_view_sep.setVisibility(View.VISIBLE);
                naghliye_sal_view_sep.setVisibility(View.VISIBLE);
                naghliye_kardkard_view_sep.setVisibility(View.GONE);
                break;
            case 35:
                // sanghin
                naghliye_brand_view.setVisibility(View.GONE);
                naghliye_sal_view.setVisibility(View.VISIBLE);
                naghliye_kardkard_view.setVisibility(View.VISIBLE);
                general_price_view_sep.setVisibility(View.VISIBLE);
                general_type_view_sep.setVisibility(View.VISIBLE);
                naghliye_brand_view_sep.setVisibility(View.GONE);
                naghliye_sal_view_sep.setVisibility(View.VISIBLE);
                naghliye_kardkard_view_sep.setVisibility(View.GONE);
                break;
            case 36:
                // motafareghe
                naghliye_brand_view.setVisibility(View.GONE);
                naghliye_sal_view.setVisibility(View.VISIBLE);
                naghliye_kardkard_view.setVisibility(View.VISIBLE);
                general_price_view_sep.setVisibility(View.VISIBLE);
                general_type_view_sep.setVisibility(View.VISIBLE);
                naghliye_brand_view_sep.setVisibility(View.GONE);
                naghliye_sal_view_sep.setVisibility(View.VISIBLE);
                naghliye_kardkard_view_sep.setVisibility(View.GONE);
                break;
        }

        showGeneralType(order);
        showGeneralPrice(order);

        int naghliye_sal_value = order.get("naghliey_sal").isJsonNull() ? 0 : order.get("naghliey_sal").getAsInt();
        int naghliye_karkard_value = order.get("naghliey_karkard").isJsonNull() ? 0 : order.get("naghliey_karkard").getAsInt();

        naghliye_brand_view_tv.setText(brand_name);
        naghliye_sal_view_tv.setText(String.valueOf(naghliye_sal_value));
        naghliye_kardkard_view_tv.setText(String.valueOf(naghliye_karkard_value));

    }

    private void showPezeshkForm(final int id) {
        order_pay_btn.setVisibility(View.VISIBLE);
        upgrade_order_btn.setVisibility(View.GONE);
        all_field_view.setVisibility(View.GONE);


    }

    private void disableAmlakField() {
        amlak_field.setVisibility(View.GONE);
    }

    private void disableNaghliyeField() {
        naghliye_field.setVisibility(View.GONE);
    }

    private void showSliderView(JsonArray images) {
        slider_view.setVisibility(View.VISIBLE);

        if (image_urls != null) {
            image_urls.clear();
            image_pager.clearIndicator();
        }

        if (images.size() > 0) {
            image_urls = new ArrayList<>();
            for (int i = 0; i < images.size(); i++) {
                image_urls.add(CONST.STORAGE + images.get(i).getAsString());
            }

            gallery_loading.setVisibility(View.GONE);
            image_pager.setVisibility(View.VISIBLE);

            image_pager.setImageUrls(image_urls, new ImageURLLoader() {
                @Override
                public void loadImage(ImageView view, String url) {
                    Glide.with(context).load(url).listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String s, Target<GlideDrawable> target, boolean b) {
                            return true;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable glideDrawable, String s, Target<GlideDrawable> target, boolean b, boolean b1) {

                            return false;
                        }
                    }).into(view);
                }
            });

            int indicatorColor = Color.parseColor("#ffffff");
            int selectedIndicatorColor = Color.parseColor("#c1c1c1");
            image_pager.showIndicator(indicatorColor, selectedIndicatorColor);
        } else {
            slider_view.setVisibility(View.GONE);
        }
    }

    private void showGeneralPrice(JsonObject order) {
        if (order.has("price_type")) {
            if (!order.get("price_type").isJsonNull()) {
                int price_type = order.get("price_type").getAsInt();

                switch (price_type) {
                    case 0:
                        general_price_view_tv.setText(CONST.formatPriceDouble(order.get("price").getAsDouble()) + " تومان ");
                        general_price_view.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        general_price_view_tv.setText("رایگان");
                        general_price_view.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        general_price_view_tv.setText("جهت معاوضه");
                        general_price_view.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        general_price_view_tv.setText("توافقی");
                        general_price_view.setVisibility(View.VISIBLE);
                        break;
                }
            }
        }
    }

    private void showGeneralType(JsonObject order) {
        if (order.has("general_type")) {
            if (!order.get("general_type").isJsonNull()) {
                int general_type = order.get("general_type").getAsInt();
                switch (general_type) {
                    case 0:
                        general_type_view_tv.setText("فروشی");
                        break;
                    case 1:
                        general_type_view_tv.setText("درخواستی");
                        break;
                }
            }
        }
    }


    private void removeOrder(final int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("حذف آگهی");
        builder.setMessage("آیا مطمئن هستید ؟");


        builder.setPositiveButton("بله", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String app_token = AppSharedPref.read("TOKEN", "");
                byte[] data = Base64.decode(app_token, Base64.DEFAULT);
                try {
                    String user_pass = new String(data, "UTF-8");
                    Ion.with(context)
                            .load(CONST.APP_TOKEN)
                            .setBodyParameter("username", user_pass)
                            .setBodyParameter("password", user_pass.split("_")[0])
                            .asString()
                            .setCallback(new FutureCallback<String>() {
                                @Override
                                public void onCompleted(Exception e, String result) {
                                    if (e == null) {
                                        JsonParser parser = new JsonParser();
                                        JsonObject json_obj = parser.parse(result).getAsJsonObject();
                                        if (json_obj.has("token")) {
                                            String token = json_obj.get("token").getAsString();
                                            Ion.with(context)
                                                    .load(CONST.REMOVE_ORDER)
                                                    .setHeader("Authorization", "Bearer " + token)
                                                    .setBodyParameter("order_id", String.valueOf(id))
                                                    .asString()
                                                    .setCallback(new FutureCallback<String>() {
                                                        @Override
                                                        public void onCompleted(Exception e, String result) {
                                                            Toasty.success(context, "آگهی با موفقیت حذف شد.", Toast.LENGTH_LONG).show();

                                                            Intent resultIntent = new Intent();
                                                            resultIntent.putExtra("update", true);
                                                            finish();
                                                        }
                                                    });
                                        }
                                    } else {
                                        Toasty.error(context, "خطا در برفراری ارتباط با سرور", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("نه", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }


}
