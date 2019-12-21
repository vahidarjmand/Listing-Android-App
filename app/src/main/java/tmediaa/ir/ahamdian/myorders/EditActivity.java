package tmediaa.ir.ahamdian.myorders;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.http.BasicNameValuePair;
import com.koushikdutta.async.http.NameValuePair;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;
import com.linchaolong.android.imagepicker.ImagePicker;
import com.linchaolong.android.imagepicker.cropper.CropImage;
import com.linchaolong.android.imagepicker.cropper.CropImageView;
import com.marcoscg.dialogsheet.DialogSheet;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;
import th.in.lordgift.widget.EditTextIntegerComma;
import tmediaa.ir.ahamdian.R;
import tmediaa.ir.ahamdian.adapters.BrandAdapter;
import tmediaa.ir.ahamdian.adapters.NothingSelectedSpinnerAdapter;
import tmediaa.ir.ahamdian.model.Brand;
import tmediaa.ir.ahamdian.otto.GlobalBus;
import tmediaa.ir.ahamdian.tools.AppSharedPref;
import tmediaa.ir.ahamdian.tools.CONST;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class EditActivity extends AppCompatActivity {
    private Context context = this;

    private View rootView;
    private Button category_selector_btn;

    private int TAP_THRESHOLD = 2;

    private LinearLayout category_selector, form_container, general_container, general_price_con, general_type_con, general_select_image_con, general_email_con, general_tel_con, general_title_con, general_desc_con, amlak_container, amlak_metraj_con, amlak_type_con, amlak_otagh_con, amlak_homeshahr_con, amlak_vadie_con, amlak_ejareh_con, amlak_sanad_con, naghliye_container, naghliye_brand_con, naghliye_kardkard_con, naghliye_sal_con, general_price_form_con;
    private Spinner naghliye_brand, naghliye_sal;

    private EditTextIntegerComma general_price, amlak_ejareh, amlak_vadie;
    private EditText general_email, general_tel, general_title, general_desc, amlak_metraj, amlak_otagh, naghliye_kardkard;

    private AlertDialog.Builder prict_alert;
    private Button general_price_type_btn, general_type_btn, general_select_image;
    private RadioGroup amlak_type_group, amlak_homeshahr, amlak_sanad_group;

    private int form_catehory_id, form_price_type, form_price;


    // For Image Picker
    private static final int READ_STORAGE_CODE = 1001;
    private static final int WRITE_STORAGE_CODE = 1002;
    private ArrayList<String> pathList = new ArrayList<>();
    private ArrayList<String> web_pathList = new ArrayList<>();
    private ArrayList<String> final_path = new ArrayList<>();
    private View viewItemSelected;
    private ImageView btnDelete;
    private HorizontalScrollView horizontalScrollView;
    private LinearLayout root;
    private LinearLayout layoutListItemSelect;
    private BitmapFactory.Options options;
    private FrameLayout.LayoutParams imageView_lp;


    private Integer selected_cat_value = 0;
    private Integer price_type_send;
    private String price_send;
    private Integer general_type_send;
    private String general_email_send;
    private String general_tel_send;
    private String general_title_send;
    private String general_desc_send;
    private Integer amlak_metraj_send;
    private Integer amlak_type_send;
    private Integer amlak_otagh_send;
    private Integer amlak_homeshahr_send;
    private Double amlak_vadieh_send;
    private Integer amlak_ejareh_send;
    private Integer amlak_sanad_send;
    private Integer naghliye_brand_send;
    private Integer naghliye_karkard_send;
    private Integer naghliye_sal_send;
    private List<NameValuePair> send_paramas = new ArrayList<NameValuePair>();
    private boolean allow_next = false;
    private boolean is_edit = false;
    private int edit_order_id = 0;
    private ProgressDialog progressDialog;
    private int select_id;
    RadioButton amlak_type_shakhsi, amlak_type_moshaver, amlak_homeshahr_no, amlak_homeshahr_yes, amlak_sanad_yes, amlak_sanad_no;
    private boolean can_send = false;
    private ImagePicker imagePicker = new ImagePicker();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("ویرایش آگهی");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        getLayouts();
        category_selector_btn = (Button) findViewById(R.id.category_selector_btn);


        //for image picker
        options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        imageView_lp = new FrameLayout.LayoutParams(280, 280);
        imageView_lp.setMargins(10, 15, 10, 15);

        select_id = getIntent().getExtras().getInt("id");
        edit_order_id = select_id;
        loadOrderInfo(select_id);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(getString(R.string.fontpath))
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

    private void getLayouts() {
        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);
        category_selector = (LinearLayout) findViewById(R.id.category_selector);
        general_price_type_btn = (Button) findViewById(R.id.general_price_type_btn);
        general_type_btn = (Button) findViewById(R.id.general_type_btn);
        general_select_image = (Button) findViewById(R.id.general_select_image);
        amlak_type_group = (RadioGroup) findViewById(R.id.amlak_type_group);
        amlak_homeshahr = (RadioGroup) findViewById(R.id.amlak_homeshahr);
        amlak_sanad_group = (RadioGroup) findViewById(R.id.amlak_sanad_group);
        form_container = (LinearLayout) findViewById(R.id.form_container);
        general_container = (LinearLayout) findViewById(R.id.general_container);
        general_price = (EditTextIntegerComma) findViewById(R.id.general_price);
        general_tel = (EditText) findViewById(R.id.general_tel);
        general_title = (EditText) findViewById(R.id.general_title);
        general_desc = (EditText) findViewById(R.id.general_desc);
        general_email = (EditText) findViewById(R.id.general_email);
        general_price_con = (LinearLayout) findViewById(R.id.general_price_con);
        general_price_form_con = (LinearLayout) findViewById(R.id.general_price_form_con);
        general_type_con = (LinearLayout) findViewById(R.id.general_type_con);
        general_select_image_con = (LinearLayout) findViewById(R.id.general_select_image_con);
        general_email_con = (LinearLayout) findViewById(R.id.general_email_con);
        general_tel_con = (LinearLayout) findViewById(R.id.general_tel_con);
        general_title_con = (LinearLayout) findViewById(R.id.general_title_con);
        general_desc_con = (LinearLayout) findViewById(R.id.general_desc_con);
        amlak_container = (LinearLayout) findViewById(R.id.amlak_container);
        amlak_metraj_con = (LinearLayout) findViewById(R.id.amlak_metraj_con);
        amlak_metraj = (EditText) findViewById(R.id.amlak_metraj);
        amlak_otagh = (EditText) findViewById(R.id.amlak_otagh);
        amlak_type_con = (LinearLayout) findViewById(R.id.amlak_type_con);
        amlak_otagh_con = (LinearLayout) findViewById(R.id.amlak_otagh_con);
        amlak_homeshahr_con = (LinearLayout) findViewById(R.id.amlak_homeshahr_con);
        amlak_vadie_con = (LinearLayout) findViewById(R.id.amlak_vadie_con);
        amlak_vadie = (EditTextIntegerComma) findViewById(R.id.amlak_vadie);
        amlak_ejareh_con = (LinearLayout) findViewById(R.id.amlak_ejareh_con);
        amlak_ejareh = (EditTextIntegerComma) findViewById(R.id.amlak_ejareh);
        amlak_sanad_con = (LinearLayout) findViewById(R.id.amlak_sanad_con);
        naghliye_container = (LinearLayout) findViewById(R.id.naghliye_container);
        naghliye_brand_con = (LinearLayout) findViewById(R.id.naghliye_brand_con);
        naghliye_brand = (Spinner) findViewById(R.id.naghliye_brand);
        naghliye_sal = (Spinner) findViewById(R.id.naghliye_sal);
        naghliye_kardkard = (EditText) findViewById(R.id.naghliye_kardkard);
        naghliye_kardkard_con = (LinearLayout) findViewById(R.id.naghliye_kardkard_con);
        naghliye_sal_con = (LinearLayout) findViewById(R.id.naghliye_sal_con);
        layoutListItemSelect = (LinearLayout) findViewById(R.id.layoutListItemSelect);

        amlak_type_shakhsi = (RadioButton) findViewById(R.id.amlak_type_shakhsi);
        amlak_type_moshaver = (RadioButton) findViewById(R.id.amlak_type_moshaver);
        amlak_homeshahr_no = (RadioButton) findViewById(R.id.amlak_homeshahr_no);
        amlak_homeshahr_yes = (RadioButton) findViewById(R.id.amlak_homeshahr_yes);
        amlak_sanad_yes = (RadioButton) findViewById(R.id.amlak_sanad_yes);
        amlak_sanad_no = (RadioButton) findViewById(R.id.amlak_sanad_no);

        progressDialog = new ProgressDialog(context);
        horizontalScrollView.setVisibility(View.GONE);
        form_container.setVisibility(View.GONE);

    }

    private void showProgress(String msg) {
        progressDialog.setMessage(msg);
        progressDialog.show();
        progressDialog.setCancelable(false);
    }

    private void showTargetForm(int id) {
        switch (id) {
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
                showAmlakForm(id);
                disableNaghliyeField();
                break;
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
                showNaghliyeForm(id);
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
                showPezeshkForm(id);
                disableAmlakField();
                disableNaghliyeField();
                break;
            default:
                showGeneralForm(id);
                disableAmlakField();
                disableNaghliyeField();
                break;

        }
    }

    private void showAmlakForm(int id) {

        disableNaghliyeField();
        form_container.setVisibility(View.VISIBLE);
        general_container.setVisibility(View.VISIBLE);
        amlak_container.setVisibility(View.VISIBLE);
        naghliye_container.setVisibility(View.GONE);
        naghliye_container.setVisibility(View.GONE);
        amlak_sanad_con.setVisibility(View.GONE);
        initilizeFields();
        switch (id) {
            // amlak forosh start
            case 15:
                amlak_homeshahr_con.setVisibility(View.VISIBLE);
                amlak_sanad_con.setVisibility(View.GONE);
                amlak_vadie_con.setVisibility(View.GONE);
                amlak_ejareh_con.setVisibility(View.GONE);
                general_price_con.setVisibility(View.VISIBLE);
                amlak_otagh_con.setVisibility(View.VISIBLE);
                general_type_con.setVisibility(View.VISIBLE);
                amlak_metraj_con.setVisibility(View.VISIBLE);
                break;
            case 16:
                amlak_homeshahr_con.setVisibility(View.VISIBLE);
                amlak_sanad_con.setVisibility(View.VISIBLE);
                amlak_vadie_con.setVisibility(View.GONE);
                amlak_ejareh_con.setVisibility(View.GONE);
                general_price_con.setVisibility(View.VISIBLE);
                amlak_otagh_con.setVisibility(View.VISIBLE);
                general_type_con.setVisibility(View.VISIBLE);
                amlak_metraj_con.setVisibility(View.VISIBLE);
                break;
            case 17:
                amlak_homeshahr_con.setVisibility(View.VISIBLE);
                amlak_sanad_con.setVisibility(View.GONE);
                amlak_vadie_con.setVisibility(View.GONE);
                amlak_ejareh_con.setVisibility(View.GONE);
                general_price_con.setVisibility(View.VISIBLE);
                amlak_otagh_con.setVisibility(View.GONE);
                general_type_con.setVisibility(View.VISIBLE);
                amlak_metraj_con.setVisibility(View.VISIBLE);
                break;
            case 18:
                amlak_homeshahr_con.setVisibility(View.VISIBLE);
                amlak_sanad_con.setVisibility(View.VISIBLE);
                amlak_vadie_con.setVisibility(View.GONE);
                amlak_ejareh_con.setVisibility(View.GONE);
                general_price_con.setVisibility(View.VISIBLE);
                amlak_otagh_con.setVisibility(View.VISIBLE);
                general_type_con.setVisibility(View.VISIBLE);
                amlak_metraj_con.setVisibility(View.VISIBLE);
                break;
            // amlak forosh end
            // amlak ejare start
            case 19:
                amlak_sanad_con.setVisibility(View.GONE);
                general_price_con.setVisibility(View.GONE);
                amlak_vadie_con.setVisibility(View.VISIBLE);
                amlak_otagh_con.setVisibility(View.VISIBLE);
                amlak_ejareh_con.setVisibility(View.VISIBLE);
                general_type_con.setVisibility(View.VISIBLE);
                amlak_metraj_con.setVisibility(View.VISIBLE);
                amlak_homeshahr_con.setVisibility(View.VISIBLE);
                break;
            case 20:
                amlak_sanad_con.setVisibility(View.GONE);
                general_price_con.setVisibility(View.GONE);
                amlak_vadie_con.setVisibility(View.VISIBLE);
                amlak_otagh_con.setVisibility(View.VISIBLE);
                amlak_ejareh_con.setVisibility(View.VISIBLE);
                general_type_con.setVisibility(View.VISIBLE);
                amlak_metraj_con.setVisibility(View.VISIBLE);
                amlak_homeshahr_con.setVisibility(View.VISIBLE);
                break;
            case 21:
                amlak_sanad_con.setVisibility(View.GONE);
                general_price_con.setVisibility(View.GONE);
                amlak_vadie_con.setVisibility(View.VISIBLE);
                amlak_ejareh_con.setVisibility(View.VISIBLE);
                amlak_otagh_con.setVisibility(View.GONE);
                general_type_con.setVisibility(View.VISIBLE);
                amlak_metraj_con.setVisibility(View.VISIBLE);
                amlak_homeshahr_con.setVisibility(View.VISIBLE);
                break;
            case 22:
                amlak_sanad_con.setVisibility(View.GONE);
                general_price_con.setVisibility(View.GONE);
                amlak_vadie_con.setVisibility(View.VISIBLE);
                amlak_ejareh_con.setVisibility(View.VISIBLE);
                amlak_otagh_con.setVisibility(View.GONE);
                general_price_con.setVisibility(View.GONE);
                amlak_vadie_con.setVisibility(View.VISIBLE);
                amlak_otagh_con.setVisibility(View.VISIBLE);
                general_type_con.setVisibility(View.VISIBLE);
                amlak_metraj_con.setVisibility(View.VISIBLE);
                amlak_homeshahr_con.setVisibility(View.VISIBLE);
                break;
            case 23:
                amlak_sanad_con.setVisibility(View.GONE);
                general_price_con.setVisibility(View.GONE);
                amlak_vadie_con.setVisibility(View.VISIBLE);
                amlak_ejareh_con.setVisibility(View.VISIBLE);
                amlak_otagh_con.setVisibility(View.GONE);
                general_type_con.setVisibility(View.VISIBLE);
                amlak_metraj_con.setVisibility(View.VISIBLE);
                amlak_homeshahr_con.setVisibility(View.GONE);

                break;
            // amlak ejare end
            // amlak khadamat start
            case 24:
                general_type_con.setVisibility(View.GONE);
                amlak_metraj_con.setVisibility(View.GONE);
                amlak_sanad_con.setVisibility(View.GONE);
                general_price_con.setVisibility(View.GONE);
                amlak_vadie_con.setVisibility(View.GONE);
                amlak_homeshahr_con.setVisibility(View.GONE);
                amlak_ejareh_con.setVisibility(View.GONE);
                amlak_otagh_con.setVisibility(View.GONE);
                general_type_con.setVisibility(View.GONE);
                break;
            case 25:
                general_type_con.setVisibility(View.GONE);
                amlak_metraj_con.setVisibility(View.GONE);
                amlak_sanad_con.setVisibility(View.GONE);
                general_price_con.setVisibility(View.GONE);
                amlak_vadie_con.setVisibility(View.GONE);
                amlak_homeshahr_con.setVisibility(View.GONE);
                amlak_ejareh_con.setVisibility(View.GONE);
                amlak_otagh_con.setVisibility(View.GONE);
                general_type_con.setVisibility(View.GONE);
                break;
            case 26:
                general_price_con.setVisibility(View.GONE);
                amlak_metraj_con.setVisibility(View.GONE);
                general_type_con.setVisibility(View.GONE);
                amlak_sanad_con.setVisibility(View.GONE);
                general_price_con.setVisibility(View.GONE);
                amlak_homeshahr_con.setVisibility(View.GONE);
                amlak_vadie_con.setVisibility(View.GONE);
                amlak_ejareh_con.setVisibility(View.GONE);
                amlak_otagh_con.setVisibility(View.GONE);
                general_type_con.setVisibility(View.GONE);
                break;
            case 27:
                general_type_con.setVisibility(View.GONE);
                amlak_metraj_con.setVisibility(View.GONE);
                amlak_sanad_con.setVisibility(View.GONE);
                general_price_con.setVisibility(View.GONE);
                amlak_homeshahr_con.setVisibility(View.GONE);
                amlak_vadie_con.setVisibility(View.GONE);
                amlak_ejareh_con.setVisibility(View.GONE);
                amlak_otagh_con.setVisibility(View.GONE);
                general_type_con.setVisibility(View.GONE);
                break;
            // amlak khadamat end
        }
    }

    private void disableAmlakField() {
        amlak_container.setVisibility(View.GONE);
    }

    private void disableNaghliyeField() {
        naghliye_container.setVisibility(View.GONE);
    }

    private void showNaghliyeForm(int id) {
        initilizeBrandField();
        initilizeSalField();
        disableAmlakField();
        initilizeFields();
        form_container.setVisibility(View.VISIBLE);
        naghliye_container.setVisibility(View.VISIBLE);
        general_price_con.setVisibility(View.VISIBLE);
        general_type_con.setVisibility(View.VISIBLE);
        switch (id) {
            case 30:
                //yadaki
                naghliye_brand_con.setVisibility(View.GONE);
                naghliye_sal_con.setVisibility(View.GONE);
                naghliye_kardkard_con.setVisibility(View.GONE);
                break;
            case 31:
                //motor cycle
                naghliye_brand_con.setVisibility(View.GONE);
                naghliye_sal_con.setVisibility(View.VISIBLE);
                naghliye_kardkard_con.setVisibility(View.VISIBLE);
                break;
            case 32:
                //ghayegh
                naghliye_brand_con.setVisibility(View.GONE);
                naghliye_sal_con.setVisibility(View.GONE);
                naghliye_kardkard_con.setVisibility(View.GONE);
                break;
            case 33:
                // motafareghe
                naghliye_brand_con.setVisibility(View.GONE);
                naghliye_sal_con.setVisibility(View.GONE);
                naghliye_kardkard_con.setVisibility(View.GONE);
                break;
            case 34:
                //savari
                naghliye_brand_con.setVisibility(View.VISIBLE);
                naghliye_sal_con.setVisibility(View.VISIBLE);
                naghliye_kardkard_con.setVisibility(View.VISIBLE);
                break;
            case 35:
                // sanghin
                naghliye_brand_con.setVisibility(View.GONE);
                naghliye_sal_con.setVisibility(View.VISIBLE);
                naghliye_kardkard_con.setVisibility(View.VISIBLE);
                break;
            case 36:
                // motafareghe
                naghliye_brand_con.setVisibility(View.GONE);
                naghliye_sal_con.setVisibility(View.VISIBLE);
                naghliye_kardkard_con.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void showGeneralForm(int id) {
        initilizeFields();
        form_container.setVisibility(View.VISIBLE);
        general_container.setVisibility(View.VISIBLE);
        amlak_container.setVisibility(View.GONE);
        naghliye_container.setVisibility(View.GONE);
        general_type_con.setVisibility(View.VISIBLE);
        general_price_con.setVisibility(View.VISIBLE);
    }

    private void checkForm() {

        if (!category_selector_btn.getText().equals("انتخاب")) {
            switch (selected_cat_value) {
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
                    if (checkAmlak(selected_cat_value)) {
                        addGeneralField();
                        addAmlakField();
                        sendMainform();
                    }
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                    //naghliye
                    if (checkNaghliye(selected_cat_value)) {
                        addGeneralField();
                        addNaghliyeField();
                        sendMainform();
                    }
                    break;
                case 104:
                case 105:
                case 106:
                case 107:
                case 108:
                case 109:
                case 110:
                case 111:
                    //pezeshki
                    if (checkGenralNoPrice()) {
                        sendPezeshkForm();
                        sendMainform();
                    }
                    break;
                default:
                    if (checkGeneralField()) {
                        addGeneralField();
                        sendMainform();

                    }
                    break;
            }
        } else {
            showErrorCon(category_selector);
            Toasty.error(context, "لطفا دسته بندی مورد نظر را انتخاب کنید", Toast.LENGTH_LONG, true).show();
        }
    }

    private boolean checkKarkardField() {
        if (naghliye_kardkard.getText().toString().trim().equals("")) {
            showErrorCon(naghliye_kardkard_con);
            Toasty.error(context, "لطفا تعداد کارکرد خودرو را وارد کنید.", Toast.LENGTH_LONG, true).show();
            return false;
        } else {
            naghliye_karkard_send = Integer.valueOf(naghliye_kardkard.getText().toString().trim());
            return true;
        }
    }

    private boolean checkSaleField() {
        if (naghliye_sal_send == null) {
            showErrorCon(naghliye_sal_con);
            Toasty.error(context, "لطفا سال تولید خودرو را وارد کنید.", Toast.LENGTH_LONG, true).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean checkBrandField() {
        if (naghliye_brand_send == null) {
            showErrorCon(naghliye_brand_con);
            Toasty.error(context, "لطفا برند خودرو را انتخاب کنید.", Toast.LENGTH_LONG, true).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean checkAmlakEjare() {
        if (amlak_ejareh.getText().toString().trim().equals("")) {
            showErrorCon(amlak_ejareh_con);
            Toasty.error(context, "لطفا ودیعه مورد نظر را وارد کنید.", Toast.LENGTH_LONG, true).show();
            return false;
        } else {
            amlak_ejareh_send = Integer.parseInt(amlak_ejareh.getValue());
            return true;
        }
    }

    private boolean checkAmlakVadieh() {
        if (amlak_vadie.getText().toString().trim().equals("")) {
            showErrorCon(amlak_vadie_con);
            Toasty.error(context, "لطفا اجاره مورد نظر را وارد کنید.", Toast.LENGTH_LONG, true).show();
            return false;
        } else {
            amlak_vadieh_send = Double.parseDouble(amlak_vadie.getValue());
            return true;
        }
    }

    private boolean checkSanadField() {
        int radioButtonID = amlak_sanad_group.getCheckedRadioButtonId();
        if (radioButtonID == -1) {
            showErrorCon(amlak_sanad_con);
            Toasty.error(context, "لطفا وضعیت سند اداری ملک را انتخاب کنید. ", Toast.LENGTH_LONG, true).show();
            return false;
        } else {
            RadioButton selected_radio = (RadioButton) findViewById(radioButtonID);

            if (selected_radio.getText().toString().equals("دارد")) {
                amlak_sanad_send = 1;
            } else {
                amlak_sanad_send = 2;
            }
            return true;
        }
    }

    private boolean checkHomeShahrField() {
        int radioButtonID = amlak_homeshahr.getCheckedRadioButtonId();
        if (radioButtonID == -1) {
            showErrorCon(amlak_homeshahr_con);
            Toasty.error(context, "لطفا وضعیت مکانی ملک را انتخاب کنید. ", Toast.LENGTH_LONG, true).show();
            return false;
        } else {
            RadioButton selected_radio = (RadioButton) findViewById(radioButtonID);

            if (selected_radio.getText().toString().equals("هست")) {
                amlak_homeshahr_send = 1;
            } else {
                amlak_homeshahr_send = 2;
            }
            return true;
        }
    }

    private boolean checOtaghField() {

        if (amlak_otagh.getText().toString().trim().length() < 1) {
            showErrorCon(amlak_otagh_con);
            Toasty.error(context, "لطفا تعداد اتاق را وارد کنید. ", Toast.LENGTH_LONG, true).show();
            return false;
        } else {
            amlak_otagh_send = Integer.parseInt(amlak_otagh.getText().toString().trim());
            return true;
        }

    }

    private boolean checkAmalkType() {
        int radioButtonID = amlak_type_group.getCheckedRadioButtonId();
        if (radioButtonID == -1) {
            showErrorCon(amlak_type_con);
            Toasty.error(context, "لطفا نوع اگهی دهنده را وارد کنید. ", Toast.LENGTH_LONG, true).show();
            return false;
        } else {
            RadioButton selected_radio = (RadioButton) findViewById(radioButtonID);

            if (selected_radio.getText().toString().equals("شخصی")) {
                amlak_type_send = 1;
            } else {
                amlak_type_send = 2;
            }
            return true;
        }
    }

    private boolean checkAmlakMetraj() {
        if (amlak_metraj.getText().toString().length() < 1) {
            showErrorCon(amlak_metraj_con);
            Toasty.error(context, "لطفا متراژ مورد نظر را وارد کنید", Toast.LENGTH_LONG, true).show();
            return false;
        } else {
            amlak_metraj_send = Integer.parseInt(amlak_metraj.getText().toString());
            return true;
        }
    }

    private boolean checkDescTitle() {
        if (general_desc.getText().toString().trim().length() < 1) {
            showErrorCon(general_desc_con);
            Toasty.error(context, "لطفا توضیحات آگهی را وارد نمایید.", Toast.LENGTH_LONG, true).show();
            return false;
        } else {
            general_desc_send = general_desc.getText().toString().trim();
            return true;
        }
    }

    private boolean checkTitleField() {
        if (general_title.getText().toString().length() < 1) {
            showErrorCon(general_title_con);
            Toasty.error(context, "لطفا عنوان آگهی را وارد نمایید.", Toast.LENGTH_LONG, true).show();
            return false;
        } else {
            general_title_send = general_title.getText().toString().trim();
            return true;
        }
    }

    private boolean checkTelField() {
        String mobile_str = general_tel.getText().toString();
        boolean is_mobile_ok = Pattern.matches("09(0(\\d)|1(\\d)|2(\\d)|3(\\d)|(9(\\d)))\\d{7}$", mobile_str);
        if (is_mobile_ok) {
            general_tel_send = mobile_str;
            return true;
        } else {
            showErrorCon(general_tel_con);
            Toasty.error(context, "لطفا موبایل خود رو بصورت صحیح وارد کنید.", Toast.LENGTH_LONG, true).show();
            return false;
        }
    }

    private boolean checkEmail() {
        if (general_email.getText().toString().length() > 0) {

            if (android.util.Patterns.EMAIL_ADDRESS.matcher(general_email.getText().toString().trim()).matches()) {
                general_email_send = general_email.getText().toString().trim();
                return true;
            } else {
                showErrorCon(general_email_con);
                Toasty.error(context, "لطفا ایمیل خود را بصورت صحیح وارد کنید.", Toast.LENGTH_LONG, true).show();
                return false;
            }
        } else {
            return true;
        }
    }

    private boolean checkImagePicker() {
        return true;
    }

    private boolean checkOrderType() {
        if (general_type_send == null) {
            showErrorCon(general_type_con);
            Toasty.error(context, "لطفا نوع آگهی را انتخاب کنید.", Toast.LENGTH_LONG, true).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean checkPriceField() {
        if (price_type_send != null) {
            if (price_type_send == 0) {
                if (general_price.getText().toString().trim().equals("")) {
                    showErrorCon(general_price_con);
                    Toasty.error(context, "لطفا قیمت مورد نظر را وارد کنید.", Toast.LENGTH_LONG, true).show();
                    return false;
                } else {
                    price_send = general_price.getValue().toString();
                    return true;
                }
            } else {
                price_send = general_price.getValue().toString();
                return true;
            }
        } else {
            showErrorCon(general_price_con);
            Toasty.error(context, "لطفا نوع قیمت را انتخاب کنید.", Toast.LENGTH_LONG, true).show();
            return false;
        }
    }

    private void showErrorCon(final LinearLayout view) {
        int colorFrom = getResources().getColor(R.color.white);
        int colorTo = getResources().getColor(R.color.error_container_color);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(500); // milliseconds
        colorAnimation.setRepeatCount(1);
        colorAnimation.setRepeatMode(ValueAnimator.REVERSE);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                view.setBackgroundColor((int) animator.getAnimatedValue());
            }

        });
        colorAnimation.start();
    }

    private boolean checkGenralNoPriceAndType() {
        if (checkImagePicker()) {
            if (checkEmail()) {
                if (checkTelField()) {
                    if (checkTitleField()) {
                        if (checkDescTitle()) {
                            allow_next = true;
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean checkGenralNoPrice() {
        if (checkImagePicker()) {
            if (checkEmail()) {
                if (checkTelField()) {
                    if (checkTitleField()) {
                        if (checkDescTitle()) {
                            allow_next = true;
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean checkGeneralField() {
        if (checkPriceField()) {
            if (checkOrderType()) {
                if (checkImagePicker()) {
                    if (checkEmail()) {
                        if (checkTelField()) {
                            if (checkTitleField()) {
                                if (checkDescTitle()) {
                                    addGeneralField();
                                    allow_next = true;
                                    return true;
                                } else {
                                    return false;
                                }
                            } else {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean checkNaghliye(int id) {
        if (id == 34) {
            if (checkBrandField()) {
                if (checkKarkardField()) {
                    if (checkSaleField()) {
                        if (checkGeneralField()) {
                            allow_next = true;
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else if (id == 31 || id == 35 || id == 36) {
            if (checkSaleField()) {
                if (checkKarkardField()) {
                    if (checkGeneralField()) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else if (id == 30 || id == 32 || id == 33) {
            if (checkGeneralField()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean checkAmlak(int id) {

        if (id == 15 || id == 18) {
            if (checkAmlakMetraj()) {
                if (checkAmalkType()) {
                    if (checOtaghField()) {
                        if (checkHomeShahrField()) {
                            if (checkGeneralField()) {
                                allow_next = true;
                                return true;
                            } else {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else if (id == 16) {
            if (checkAmlakMetraj()) {
                if (checkAmalkType()) {
                    if (checOtaghField()) {
                        if (checkHomeShahrField()) {
                            if (checkSanadField()) {
                                if (checkGeneralField()) {
                                    return true;
                                } else {
                                    return false;
                                }
                            } else {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else if (id == 17) {
            if (checkAmlakMetraj()) {
                if (checkAmalkType()) {
                    if (checkHomeShahrField()) {
                        if (checkGeneralField()) {
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }

        } else if (id == 19) {
            if (checkAmlakMetraj()) {
                if (checkAmalkType()) {
                    if (checOtaghField()) {
                        if (checkHomeShahrField()) {
                            if (checkAmlakVadieh()) {
                                if (checkAmlakEjare()) {
                                    if (checkGenralNoPrice()) {
                                        return true;
                                    } else {
                                        return false;
                                    }
                                } else {
                                    return false;
                                }
                            } else {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }else if (id == 20) {
            if (checkAmlakMetraj()) {
                if (checkAmalkType()) {
                    if (checOtaghField()) {
                        if (checkHomeShahrField()) {
                            if (checkAmlakVadieh()) {
                                if (checkAmlakEjare()) {
                                    if (checkGenralNoPrice()) {
                                        return true;
                                    } else {
                                        return false;
                                    }
                                } else {
                                    return false;
                                }
                            } else {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else if (id == 21 || id == 22 || id == 23) {

            if (checkAmlakMetraj()) {
                if (checkAmalkType()) {
                    if (checkHomeShahrField()) {
                        if (checkAmlakVadieh()) {
                            if (checkAmlakEjare()) {
                                if (checkGenralNoPrice()) {
                                    return true;
                                } else {
                                    return false;
                                }
                            } else {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else if (id == 24 || id == 25 || id == 26 || id == 27) {
            if (checkAmalkType()) {
                if (checkGenralNoPriceAndType()) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private void initilizeFields() {
        initilizePriceField();
        initilizeTypeField();
        initilizeImagePickerField();
    }

    private void initilizeImagePickerField() {
        general_select_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePickerIntent();
            }
        });
    }

    private void initilizePriceField() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        general_price_form_con.setVisibility(View.GONE);
        TextView title = new TextView(context);
        title.setText("نوع قیمت");
        title.setPadding(20, 20, 20, 20);
        title.setGravity(Gravity.RIGHT);
        title.setTextSize(18);
        alert.setCustomTitle(title);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.select_dialog_item);
        arrayAdapter.add("مقطوع");
        arrayAdapter.add("رایگان");
        arrayAdapter.add("جهت معاوضه");
        arrayAdapter.add("توافقی");

        alert.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                price_type_send = which;
                general_price_type_btn.setText(arrayAdapter.getItem(which).toString());
                if (which != 0) {
                    general_price_form_con.setVisibility(View.GONE);
                } else {
                    general_price_form_con.setVisibility(View.VISIBLE);
                }
            }
        });


        final AlertDialog alertd = alert.create();

        general_price_type_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertd.show();
            }
        });
    }

    private void initilizeTypeField() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(context);

        TextView title = new TextView(context);
        title.setText("نوع آگهی");
        title.setPadding(20, 20, 20, 20);
        title.setGravity(Gravity.RIGHT);
        title.setTextSize(18);
        alert.setCustomTitle(title);


        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.select_dialog_item);
        arrayAdapter.add("ارائه");
        arrayAdapter.add("درخواستی");

        alert.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                general_type_btn.setText(arrayAdapter.getItem(which).toString());
                general_type_send = which;
            }
        });


        final AlertDialog alertd = alert.create();

        general_type_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertd.show();
            }
        });
    }

    private void initilizeBrandField() {


        naghliye_sal.setPrompt("انتخاب برند");
        BrandAdapter adapter = new BrandAdapter(context, CONST.NAGHLIYE_BRANDS_LIST);
        naghliye_brand.setAdapter(adapter);
        naghliye_brand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Brand item = (Brand) naghliye_brand.getSelectedItem();
                naghliye_brand_send = item.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void initilizeSalField() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.naghliye_sal, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        naghliye_sal.setPrompt("انتخاب کنید");

        naghliye_sal.setAdapter(
                new NothingSelectedSpinnerAdapter(
                        adapter,
                        R.layout.spinner_row_nothing_selected,
                        // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                        context));

        naghliye_sal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                naghliye_sal_send = Integer.parseInt(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void showPezeshkForm(int id) {
        initilizeFields();
        form_container.setVisibility(View.VISIBLE);
        naghliye_container.setVisibility(View.GONE);
        amlak_container.setVisibility(View.GONE);
        general_price_con.setVisibility(View.GONE);
        general_type_con.setVisibility(View.GONE);
    }

    private void openImagePickerIntent() {
        imagePicker.setTitle("تصویر مورد نظر را انتخاب کنید.");
        imagePicker.setCropImage(true);

        final ImagePicker.Callback callback = new ImagePicker.Callback() {
            @Override
            public void onPickImage(Uri imageUri) {

            }

            @Override
            public void onCropImage(Uri imageUri) {
                horizontalScrollView.setVisibility(View.VISIBLE);
                viewItemSelected = getLayoutInflater().inflate(R.layout.piclist_item_selected, layoutListItemSelect, false);
                viewItemSelected.setTag(imageUri.getPath());
                btnDelete = (ImageView) viewItemSelected.findViewById(R.id.btnDelete);
                pathList.add(imageUri.getPath());
                calcbtnDelete();
                viewItemSelected.setId(0);
                ImageView imageItem = (ImageView) viewItemSelected.findViewById(R.id.imageItem);
                imageItem.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageItem.setLayoutParams(imageView_lp);
                imageItem.setImageURI(imageUri);
                layoutListItemSelect.addView(viewItemSelected);
            }

            @Override
            public void cropConfig(CropImage.ActivityBuilder builder) {
                builder
                        .setMultiTouchEnabled(true)
                        .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
                        .setOutputCompressQuality(60)
                        .setFixAspectRatio(true)
                        .setAspectRatio(16, 9);
            }

        };

        new DialogSheet(this)
                .setTitle("انتخاب تصویر")
                .setMessage("لطفا توجه داشته باشید در صورتی که تصویر شما شرایط لازم جهت انتشار را نداشته باشد حذف خواهد شد.")
                .setCancelable(true)
                .setPositiveButton("گالری", new DialogSheet.OnPositiveClickListener() {
                    @Override
                    public void onClick(View v) {
                        imagePicker.startGallery(EditActivity.this, callback);

                    }
                })
                .setNegativeButton("دوربین", new DialogSheet.OnNegativeClickListener() {
                    @Override
                    public void onClick(View v) {
                        imagePicker.startCamera(EditActivity.this, callback);

                    }
                })
                .setButtonsColorRes(R.color.colorPrimary)  // Default color is accent
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imagePicker.onActivityResult(this, requestCode, resultCode, data);
    }

    private void addGeneralField() {
        String user_id = AppSharedPref.read("ID", "0");
        Date curDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String DateToStr = format.format(curDate);



        send_paramas.add(new BasicNameValuePair("cat_id", String.valueOf(selected_cat_value)));
        send_paramas.add(new BasicNameValuePair("city_id", AppSharedPref.read("CITY_ID", "")));
        send_paramas.add(new BasicNameValuePair("title", general_title_send));
        send_paramas.add(new BasicNameValuePair("desc", general_desc_send));
        send_paramas.add(new BasicNameValuePair("email", general_email_send));
        send_paramas.add(new BasicNameValuePair("tel", general_tel_send));
        send_paramas.add(new BasicNameValuePair("title", general_title_send));
        send_paramas.add(new BasicNameValuePair("price", price_send));
        send_paramas.add(new BasicNameValuePair("selected_client", user_id));
        send_paramas.add(new BasicNameValuePair("status", "disabled"));

        if (price_type_send != null) {
            send_paramas.add(new BasicNameValuePair("price_type", String.valueOf(price_type_send)));
        }
        if (general_type_send != null) {
            send_paramas.add(new BasicNameValuePair("general_type", String.valueOf(general_type_send)));
        }

    }

    private void addAmlakField() {
        if (amlak_homeshahr_send != null) {
            send_paramas.add(new BasicNameValuePair("amlak_homeshahr", String.valueOf(amlak_homeshahr_send)));
        }
        if (amlak_type_send != null) {
            send_paramas.add(new BasicNameValuePair("amlak_type", String.valueOf(amlak_type_send)));
        }
        if (amlak_otagh_send != null) {
            send_paramas.add(new BasicNameValuePair("amlak_room_count", String.valueOf(amlak_otagh_send)));

        }
        if (amlak_metraj_send != null) {
            send_paramas.add(new BasicNameValuePair("amlak_metrajh", String.valueOf(amlak_metraj_send)));
        }
        if (amlak_ejareh_send != null) {
            send_paramas.add(new BasicNameValuePair("amlak_ejare", String.valueOf(amlak_ejareh_send)));
        }
        if (amlak_vadieh_send != null) {
            send_paramas.add(new BasicNameValuePair("amlak_vadieh", String.valueOf(amlak_vadieh_send)));
        }
        if (amlak_sanad_send != null) {
            send_paramas.add(new BasicNameValuePair("amlak_sanad_edari", String.valueOf(amlak_sanad_send)));
        }
    }

    private void addNaghliyeField() {
        if (naghliye_brand_send != null) {
            send_paramas.add(new BasicNameValuePair("naghliey_brand", String.valueOf(naghliye_brand_send)));
        }
        if (naghliye_sal_send != null) {
            send_paramas.add(new BasicNameValuePair("naghliey_sal", String.valueOf(naghliye_sal_send)));
        }
        if (naghliye_karkard_send != null) {
            send_paramas.add(new BasicNameValuePair("naghliey_karkard", String.valueOf(naghliye_karkard_send)));
        }
    }

    private void sendPezeshkForm() {
        addGeneralField();
    }


    private void sendMainform() {
        final_path.clear();
        int total = layoutListItemSelect.getChildCount() - web_pathList.size();
        int _temp = 0;

        uploaddata();
        /*if(checkhavelocalimage()){
            for (int i = 0; i < layoutListItemSelect.getChildCount(); i++) {
                View item = layoutListItemSelect.getChildAt(i);
                String tag = (String) item.getTag();
                if (!tag.substring(0, 6).equals("images")) {
                    _temp++;
                    DecodeFileAsync task = new DecodeFileAsync(tag, total);
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, _temp);
                }
            }
        }else{

        }*/


    }

    private boolean checkhavelocalimage() {
        boolean condition;
        if(web_pathList.size() > 0){
            if(layoutListItemSelect.getChildCount() == web_pathList.size()){
                condition = false;
            }else{
                condition = true;
            }
        }else{
            if(layoutListItemSelect.getChildCount() > 0){
                condition = true;
            }else{
                condition = false;
            }
        }
        return condition;
    }



    private void uploaddata() {
        String app_token = AppSharedPref.read("TOKEN", "");
        byte[] data = Base64.decode(app_token, Base64.DEFAULT);

        progressDialog.dismiss();
        progressDialog.setMessage("در حال ارسال آگهی");
        progressDialog.show();
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
                                    Builders.Any.B ionBuilder;

                                    ionBuilder = Ion.with(context).load("POST", CONST.EDIT_ORDER_V2);
                                    ionBuilder.setMultipartParameter("order_id", String.valueOf(edit_order_id));

                                    for (int i = 0; i < send_paramas.size(); i++) {
                                        ionBuilder.setMultipartParameter(send_paramas.get(i).getName(), send_paramas.get(i).getValue());
                                    }




                                    ionBuilder.setHeader("Authorization", "Bearer " + token);
                                    for (int j = 0; j < pathList.size(); j++) {
                                        File file = new File(pathList.get(j));
                                        String name = "img_" + j;
                                        ionBuilder.setMultipartFile(name, "image//*", file);

                                    }

                                    for (int  m = 0; m < web_pathList.size(); m++) {
                                        String name = web_pathList.get(m);
                                        ionBuilder.setMultipartParameter("keepimg[]",name);

                                    }

                                    ionBuilder.asString().withResponse().setCallback(new FutureCallback<com.koushikdutta.ion.Response<String>>() {
                                        @Override
                                        public void onCompleted(Exception e, com.koushikdutta.ion.Response<String> result) {
                                            progressDialog.dismiss();
                                            if (e == null) {
                                                JsonParser parser = new JsonParser();
                                                JsonObject json_obj = parser.parse(result.getResult()).getAsJsonObject();



                                                if (json_obj.has("status")) {
                                                    if (json_obj.get("status").getAsString().equals("ok")) {
                                                        JsonObject order = json_obj.get("order").getAsJsonObject();

                                                        finish();

                                                    } else {
                                                        Toasty.error(context, "خطا در ویرایش آگهی لطفا، دوباره تلاش کنید", Toast.LENGTH_LONG).show();
                                                    }
                                                } else {
                                                    Toasty.error(context, "خطا در ویرایش آگهی لطفا، دوباره تلاش کنید", Toast.LENGTH_LONG).show();
                                                }
                                            }else{
                                                Toasty.error(context, "خطا در برفراری ارتباط با سرور", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });

                                }
                            } else {
                                progressDialog.dismiss();
                                Toasty.error(context, "خطا در برفراری ارتباط با سرور", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    private void loadOrderInfo(int select_id) {
        progressDialog.show();
        Ion.with(context)
                .load(CONST.GET_ORDER_EDIT)
                .setBodyParameter("order_id", String.valueOf(select_id))
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        progressDialog.dismiss();
                        if (e == null) {
                            JsonParser parser = new JsonParser();
                            JsonObject obj = parser.parse(result).getAsJsonObject();
                            if (obj.has("status")) {
                                String status = obj.get("status").getAsString();
                                if (status.equals("found")) {
                                    fillFields(obj);

                                }
                            }
                        } else {
                            finish();
                            Toasty.error(context, "خطا در برقراری ارتباط با سرور", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void fillFields(JsonObject obj) {
        JsonObject order = obj.get("order").getAsJsonObject();
        showTargetForm(order.get("cat_id").getAsInt());
        selected_cat_value = order.get("cat_id").getAsInt();
        String cat_name = obj.get("cat_name").getAsString();
        category_selector_btn.setText(cat_name);
        category_selector_btn.setEnabled(false);
        category_selector.setEnabled(false);

        int general_type_value = order.get("general_type").isJsonNull() ? 0 : order.get("general_type").getAsInt();
        int price_type_value = order.get("price_type").isJsonNull() ? 0 : order.get("price_type").getAsInt();
        int price_value = order.get("price").isJsonNull() ? 0 : order.get("price").getAsInt();
        String tel_value = order.get("tel").isJsonNull() ? "" : order.get("tel").getAsString();
        String title_value = order.get("title").isJsonNull() ? "" : order.get("title").getAsString();
        String desc_value = order.get("desc").isJsonNull() ? "" : order.get("desc").getAsString();
        String email_value = order.get("email").isJsonNull() ? "" : order.get("email").getAsString();

        int amlak_metraj_value = order.get("amlak_metrajh").isJsonNull() ? 0 : order.get("amlak_metrajh").getAsInt();
        int amlak_type_value = order.get("amlak_type").isJsonNull() ? 0 : order.get("amlak_type").getAsInt();
        int amlak_room_count_value = order.get("amlak_room_count").isJsonNull() ? 0 : order.get("amlak_room_count").getAsInt();
        int amlak_homeshahr_value = order.get("amlak_homeshahr").isJsonNull() ? 0 : order.get("amlak_homeshahr").getAsInt();
        int amlak_vadieh_value = order.get("amlak_vadieh").isJsonNull() ? 0 : order.get("amlak_vadieh").getAsInt();
        int amlak_ejare_value = order.get("amlak_ejare").isJsonNull() ? 0 : order.get("amlak_ejare").getAsInt();
        int amlak_sanad_edari_value = order.get("amlak_sanad_edari").isJsonNull() ? 0 : order.get("amlak_sanad_edari").getAsInt();
        final int naghliey_brand_value = order.get("naghliey_brand").isJsonNull() ? 0 : order.get("naghliey_brand").getAsInt();
        final int naghliey_sal_value = order.get("naghliey_sal").isJsonNull() ? 0 : order.get("naghliey_sal").getAsInt();
        int naghliey_karkard_value = order.get("naghliey_karkard").isJsonNull() ? 0 : order.get("naghliey_karkard").getAsInt();


        price_send = String.valueOf(price_value);
        price_type_send = price_type_value;
        general_type_send = general_type_value;
        general_email_send = email_value;
        general_tel_send = tel_value;
        general_title_send = title_value;
        general_desc_send = desc_value;
        amlak_metraj_send = amlak_metraj_value;
        amlak_type_send = amlak_type_value;
        amlak_otagh_send = amlak_room_count_value;
        amlak_homeshahr_send = amlak_homeshahr_value;
        amlak_vadieh_send = Double.valueOf(amlak_vadieh_value);
        amlak_ejareh_send = amlak_ejare_value;
        amlak_sanad_send = amlak_sanad_edari_value;
        naghliye_brand_send = naghliey_brand_value;
        naghliye_karkard_send = naghliey_karkard_value;
        naghliye_sal_send = naghliey_sal_value;

        amlak_metraj.setText(String.valueOf(amlak_metraj_value));
        amlak_otagh.setText(String.valueOf(amlak_room_count_value));
        amlak_vadie.setText(String.valueOf(amlak_vadieh_value));
        amlak_ejareh.setText(String.valueOf(amlak_ejare_value));
        naghliye_kardkard.setText(String.valueOf(naghliey_karkard_value));

        general_price.setText(String.valueOf(price_value));
        general_price.setText(String.valueOf(price_value));
        general_email.setText(String.valueOf(email_value));
        general_tel.setText(String.valueOf(tel_value));
        general_title.setText(String.valueOf(title_value));
        general_desc.setText(String.valueOf(desc_value));


        if (amlak_type_value == 1) {
            amlak_type_shakhsi.setChecked(true);
        } else if (amlak_type_value == 2) {
            amlak_type_moshaver.setChecked(true);
        }
        if (amlak_homeshahr_value == 1) {
            amlak_homeshahr_yes.setChecked(true);
        } else if (amlak_homeshahr_value == 2) {
            amlak_homeshahr_no.setChecked(true);
        }
        if (amlak_sanad_edari_value == 1) {
            amlak_sanad_yes.setChecked(true);
        } else if (amlak_sanad_edari_value == 2) {
            amlak_sanad_no.setChecked(true);
        }

        JsonArray brands = obj.get("brands").getAsJsonArray();
        for (JsonElement b : brands) {
            JsonObject item = b.getAsJsonObject();
            Brand brand = new Brand();
            brand.setId(item.get("id").getAsInt());
            brand.setName(item.get("name").getAsString());
            CONST.NAGHLIYE_BRANDS_LIST.add(brand);
        }
        initilizeBrandField();
        naghliye_brand.setSelection(naghliey_brand_value);

        naghliye_sal.setSelection(10);

        naghliye_sal.post(new Runnable() {
            @Override
            public void run() {
                if (naghliey_sal_value < naghliye_sal.getAdapter().getCount()) {
                    naghliye_sal.setSelection(naghliey_sal_value);
                }
            }
        });

        if (price_type_value == 0) {
            general_price_type_btn.setText("مقطوع");
        } else if (price_type_value == 1) {
            general_price_type_btn.setText("رایگان");
        } else if (price_type_value == 2) {
            general_price_type_btn.setText("جهت معاوضه");
        } else if (price_type_value == 3) {
            general_price_type_btn.setText("توافقی");
        }

        if (general_type_value == 0) {
            general_type_btn.setText("ارائه");
        } else if (general_type_value == 1) {
            general_type_btn.setText("درخواستی");
        }

        fillAttachments(order.get("attachments").getAsJsonArray());
    }

    private void fillAttachments(JsonArray attachments) {
        //http://192.168.115.1/ahmadian/storage/app/
        //http://192.168.115.1/ahmadian/storage/app/users/user1/img_##.jpg

        GradientDrawable border = new GradientDrawable();
        border.setColor(0xFFFFFFFF); //white background
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            border.setStroke(6, getColor(R.color.primary)); //black border with full opacity
        } else {
            border.setStroke(3, getResources().getColor(R.color.primary)); //black border with full opacity
        }

        if (attachments.size() > 0) {
            horizontalScrollView.setVisibility(View.VISIBLE);
            for (int i = 0; i < attachments.size(); i++) {
                viewItemSelected = getLayoutInflater().inflate(R.layout.piclist_item_selected, layoutListItemSelect, false);
                btnDelete = (ImageView) viewItemSelected.findViewById(R.id.btnDelete);
                calcbtnDelete();
                viewItemSelected.setId(i);

                String name = attachments.get(i).getAsString().replace("\"", "");
                viewItemSelected.setTag(name);
                ImageView imageItem = (ImageView) viewItemSelected.findViewById(R.id.imageItem);
                web_pathList.add(name);
                Glide.with(context)
                        .load(CONST.STORAGE + attachments.get(i).getAsString().replace("\"", ""))
                        .skipMemoryCache(true)
                        .into(imageItem);
                imageItem.setScaleType(ImageView.ScaleType.FIT_CENTER);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    imageItem.setBackgroundDrawable(border);
                } else {
                    imageItem.setBackground(border);
                }
                imageItem.setLayoutParams(imageView_lp);

                layoutListItemSelect.addView(viewItemSelected);
            }
        }
    }

    private void calcbtnDelete() {
        if (btnDelete != null) {
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ViewGroup parent = (ViewGroup) v.getParent().getParent().getParent();
                    View child = (View) v.getParent().getParent();
                    String tag = (String) child.getTag();
                    parent.removeView(child);

                    if (tag.substring(0,6).equals("images")) {
                        web_pathList.remove(tag);
                    } else {
                        pathList.remove(tag);
                    }



                    if (parent.getChildCount() < 1) {
                        horizontalScrollView.setVisibility(View.GONE);
                    }

                    /*for (int i = 0;i< parent.getChildCount();i++){

                        if(tag.substring(0,3).equals("web")){
                            View child1 = ((ViewGroup) v.getParent().getParent());
                            String w_tag = (String) child1.getTag();
                            //int index = Integer.parseInt(w_tag.substring(w_tag.length() - 1));
                            Log.d(CONST.APP_LOG,w_tag.substring(w_tag.length() - 1));
                            //
                            //parent.removeView(child1);
                        }else{
                            //View child2 = ((ViewGroup) v.getParent().getParent());
                            //pathList.remove(child2.getTag());
                            //parent.removeView(child2);
                        }
                    }*/
                    /*String selected_item = (String) ((View) v.getParent().getParent()).getTag();
                    layoutListItemSelect.removeView((View) v.getParent().getParent());
                    pathList.remove(selected_item);

                    if (pathList.size() == 0) {
                        horizontalScrollView.setVisibility(View.GONE);
                    }*/
                }
            });
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
        getMenuInflater().inflate(R.menu.activity_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save_edit) {
            checkForm();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
