package tmediaa.ir.ahamdian.seach;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.sevenheaven.segmentcontrol.SegmentControl;
import com.squareup.otto.Subscribe;

import es.dmoral.toasty.Toasty;
import th.in.lordgift.widget.EditTextIntegerComma;
import tmediaa.ir.ahamdian.R;
import tmediaa.ir.ahamdian.adapters.BrandAdapter;
import tmediaa.ir.ahamdian.insertorders.categoryselector.CategorySelector;
import tmediaa.ir.ahamdian.model.Brand;
import tmediaa.ir.ahamdian.otto.AppEvents;
import tmediaa.ir.ahamdian.otto.GlobalBus;
import tmediaa.ir.ahamdian.tools.CONST;
import tmediaa.ir.ahamdian.tools.NoDefaultSpinner;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static java.lang.Integer.parseInt;

public class SearchFilterActivity extends AppCompatActivity {

    private SegmentControl mSegmentHorzontal;
    private Switch thumb_switch;
    private TextView switchBtn_txtView;


    private Switch feature_image_switch;
    private Button apply_search_btn, category_selector_btn;
    private NoDefaultSpinner naghliye_brand;
    private RadioGroup amlak_type_group, general_type_group, amlak_room_group;
    private RadioButton amlak_type_shakhsi, amlak_type_bongah, general_type_sale, general_type_request, room_1, room_2, room_3, room_4, room_5;
    private EditTextIntegerComma amlak_vadie_from_txt, amlak_vadie_to_txt, amlak_ejareh_from_txt, amlak_ejareh_to_txt, general_price_from_txt, general_price_to_txt;
    private RelativeLayout naghlie_sal_container, amlak_vadieh_container, amlak_ejareh_container, thumb_selector_container, category_selector_container, amlak_metraj_container, amlak_room_container, amlak_type_container, naghlie_karkard_container, general_type_container, general_price_container;
    private LinearLayout naghlie_brand_container, general_filter_container, amlak_filter_container, naghlie_filter_container;
    private EditText naghliye_sal_from_txt, naghliye_sal_to_txt, amlak_metraj_from_txt, amlak_metraj_to_txt, naghliye_karkard_from_txt, naghliye_karkard_to_txt;


    private Integer segment_price_send = 0;
    private boolean feature_image_send = false;
    private Integer category_send = 0;
    private Integer general_type_send = 2;
    private Integer naghliye_brand_send;
    private Integer amlak_type_send;
    private Integer amlak_room_send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_filter);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("اعمال فیلتر");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initLayout();

        mSegmentHorzontal = (SegmentControl) findViewById(R.id.segment_control);
        switchBtn_txtView = (TextView) findViewById(R.id.switchBtn_txtView);

        mSegmentHorzontal.setOnSegmentControlClickListener(new SegmentControl.OnSegmentControlClickListener() {
            @Override
            public void onSegmentControlClick(int index) {
                segment_price_send = index;
                Log.d(CONST.APP_LOG,"index: " + index);
            }
        });

        feature_image_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switchBtn_txtView.setText("بله");
                    feature_image_send = true;
                } else {
                    switchBtn_txtView.setText("خیر");
                    feature_image_send = false;
                }
            }
        });

        category_selector_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CategorySelector categorySelector = new CategorySelector();
                categorySelector.show(getSupportFragmentManager(), "categoryselector");
            }
        });

        general_type_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.general_type_sale) {
                    general_type_send = 0;
                } else {
                    general_type_send = 1;
                }
            }
        });


        amlak_type_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.amlak_type_shakhsi:
                        amlak_type_send = 1;
                        break;
                    case R.id.amlak_type_bongah:
                        amlak_type_send = 2;
                        break;
                }
            }
        });

        amlak_room_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.room_1:
                        amlak_room_send = 1;
                        break;
                    case R.id.room_2:
                        amlak_room_send = 2;
                        break;
                    case R.id.room_3:
                        amlak_room_send = 3;
                        break;
                    case R.id.room_4:
                        amlak_room_send = 4;
                        break;
                    case R.id.room_5:
                        amlak_room_send = 5;
                        break;
                }
            }
        });
        apply_search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFilterParams();
            }
        });

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(getString(R.string.fontpath))
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
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


    private void initLayout() {
        feature_image_switch = (Switch) findViewById(R.id.feature_image_switch);
        apply_search_btn = (Button) findViewById(R.id.apply_seach_btn);
        category_selector_btn = (Button) findViewById(R.id.category_selector_btn);
        naghliye_brand = (NoDefaultSpinner) findViewById(R.id.naghliye_brand);
        amlak_type_group = (RadioGroup) findViewById(R.id.amlak_type_group);
        general_type_group = (RadioGroup) findViewById(R.id.general_type_group);
        amlak_room_group = (RadioGroup) findViewById(R.id.amlak_room_group);
        amlak_type_shakhsi = (RadioButton) findViewById(R.id.amlak_type_shakhsi);
        amlak_type_bongah = (RadioButton) findViewById(R.id.amlak_type_bongah);
        general_type_sale = (RadioButton) findViewById(R.id.general_type_sale);
        general_type_request = (RadioButton) findViewById(R.id.general_type_request);
        room_1 = (RadioButton) findViewById(R.id.room_1);
        room_2 = (RadioButton) findViewById(R.id.room_2);
        room_3 = (RadioButton) findViewById(R.id.room_3);
        room_4 = (RadioButton) findViewById(R.id.room_4);
        room_5 = (RadioButton) findViewById(R.id.room_5);
        amlak_vadie_from_txt = (EditTextIntegerComma) findViewById(R.id.amlak_vadie_from_txt);
        amlak_vadie_to_txt = (EditTextIntegerComma) findViewById(R.id.amlak_vadie_to_txt);
        amlak_ejareh_from_txt = (EditTextIntegerComma) findViewById(R.id.amlak_ejareh_from_txt);
        amlak_ejareh_to_txt = (EditTextIntegerComma) findViewById(R.id.amlak_ejareh_to_txt);
        general_price_from_txt = (EditTextIntegerComma) findViewById(R.id.general_price_from_txt);
        general_price_to_txt = (EditTextIntegerComma) findViewById(R.id.general_price_to_txt);
        naghlie_sal_container = (RelativeLayout) findViewById(R.id.naghlie_sal_container);
        amlak_vadieh_container = (RelativeLayout) findViewById(R.id.amlak_vadieh_container);
        amlak_ejareh_container = (RelativeLayout) findViewById(R.id.amlak_ejareh_container);
        thumb_selector_container = (RelativeLayout) findViewById(R.id.thumb_selector_container);
        category_selector_container = (RelativeLayout) findViewById(R.id.category_selector_container);
        amlak_metraj_container = (RelativeLayout) findViewById(R.id.amlak_metraj_container);
        amlak_room_container = (RelativeLayout) findViewById(R.id.amlak_room_container);
        amlak_type_container = (RelativeLayout) findViewById(R.id.amlak_type_container);
        naghlie_karkard_container = (RelativeLayout) findViewById(R.id.naghlie_karkard_container);
        naghlie_brand_container = (LinearLayout) findViewById(R.id.naghlie_brand_container);
        general_type_container = (RelativeLayout) findViewById(R.id.general_type_container);
        general_price_container = (RelativeLayout) findViewById(R.id.general_price_container);
        general_filter_container = (LinearLayout) findViewById(R.id.general_filter_container);
        amlak_filter_container = (LinearLayout) findViewById(R.id.amlak_filter_container);
        naghlie_filter_container = (LinearLayout) findViewById(R.id.naghlie_filter_container);
        naghliye_sal_from_txt = (EditText) findViewById(R.id.naghliye_sal_from_txt);
        naghliye_sal_to_txt = (EditText) findViewById(R.id.naghliye_sal_to_txt);
        amlak_metraj_from_txt = (EditText) findViewById(R.id.amlak_metraj_from_txt);
        amlak_metraj_to_txt = (EditText) findViewById(R.id.amlak_metraj_to_txt);
        naghliye_karkard_from_txt = (EditText) findViewById(R.id.naghliye_karkard_from_txt);
        naghliye_karkard_to_txt = (EditText) findViewById(R.id.naghliye_karkard_to_txt);

        amlak_filter_container.setVisibility(View.GONE);
        naghlie_filter_container.setVisibility(View.GONE);

    }


    @Subscribe
    public void DialogCategoryChose(final AppEvents.DialogCategoryChose events) {
        initsearchform(events.getTargetChosseID());
        category_send = events.getTargetChosseID();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_filter, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.clear_filter) {

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    private void initsearchform(int id) {

        Log.d(CONST.APP_LOG, "id: " + id);
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
                calcAmlak(id);
                break;
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
                calcNaghliye(id);
                break;
            case 104:
            case 105:
            case 106:
            case 107:
            case 108:
            case 109:
            case 110:
            case 111:
                calcPezeshk(id);
                break;
            default:
                calcGeneral(id);
                break;

        }
    }

    private void calcGeneral(int id) {
        amlak_filter_container.setVisibility(View.GONE);
        naghlie_filter_container.setVisibility(View.GONE);

        general_filter_container.setVisibility(View.VISIBLE);
        general_type_container.setVisibility(View.VISIBLE);
        general_price_container.setVisibility(View.VISIBLE);
    }

    private void calcNaghliye(int id) {
        initilizeBrandField();
        amlak_filter_container.setVisibility(View.GONE);
        naghlie_filter_container.setVisibility(View.VISIBLE);
        general_price_container.setVisibility(View.VISIBLE);
        general_type_container.setVisibility(View.VISIBLE);

        switch (id) {
            case 30:
                //yadaki
                naghlie_brand_container.setVisibility(View.GONE);
                naghlie_sal_container.setVisibility(View.GONE);
                naghlie_karkard_container.setVisibility(View.GONE);

                break;
            case 31:
                //motor cycle
                naghlie_brand_container.setVisibility(View.GONE);
                naghlie_sal_container.setVisibility(View.VISIBLE);
                naghlie_karkard_container.setVisibility(View.VISIBLE);
                break;
            case 32:
                //ghayegh
                naghlie_brand_container.setVisibility(View.GONE);
                naghlie_sal_container.setVisibility(View.GONE);
                naghlie_karkard_container.setVisibility(View.GONE);
                break;
            case 33:
                // motafareghe
                naghlie_brand_container.setVisibility(View.GONE);
                naghlie_sal_container.setVisibility(View.GONE);
                naghlie_karkard_container.setVisibility(View.GONE);
                break;
            case 34:
                //savari
                naghlie_brand_container.setVisibility(View.VISIBLE);
                naghlie_sal_container.setVisibility(View.VISIBLE);
                naghlie_karkard_container.setVisibility(View.VISIBLE);
                break;
            case 35:
                // sanghin
                naghlie_brand_container.setVisibility(View.GONE);
                naghlie_sal_container.setVisibility(View.VISIBLE);
                naghlie_karkard_container.setVisibility(View.VISIBLE);
                break;
            case 36:
                // motafareghe
                naghlie_brand_container.setVisibility(View.GONE);
                naghlie_sal_container.setVisibility(View.VISIBLE);
                naghlie_karkard_container.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void initilizeBrandField() {
        naghliye_brand.setPrompt("انتخاب برند");
        BrandAdapter adapter = new BrandAdapter(this, CONST.NAGHLIYE_BRANDS_LIST);
        naghliye_brand.setAdapter(adapter);
        naghliye_brand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Brand item = (Brand) naghliye_brand.getSelectedItem();
                naghliye_brand_send = item.getId();
                Log.d(CONST.APP_LOG, "brnad: " + item.getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void calcAmlak(int id) {
        naghlie_filter_container.setVisibility(View.GONE);
        amlak_filter_container.setVisibility(View.VISIBLE);
        switch (id) {
            // amlak forosh start
            case 15:

                amlak_vadieh_container.setVisibility(View.GONE);
                amlak_ejareh_container.setVisibility(View.GONE);
                general_price_container.setVisibility(View.VISIBLE);
                amlak_room_container.setVisibility(View.VISIBLE);
                general_type_container.setVisibility(View.VISIBLE);
                amlak_metraj_container.setVisibility(View.VISIBLE);
                break;
            case 16:
                amlak_vadieh_container.setVisibility(View.GONE);
                amlak_ejareh_container.setVisibility(View.GONE);
                general_price_container.setVisibility(View.VISIBLE);
                amlak_room_container.setVisibility(View.VISIBLE);
                general_type_container.setVisibility(View.VISIBLE);
                amlak_metraj_container.setVisibility(View.VISIBLE);
                break;
            case 17:
                amlak_vadieh_container.setVisibility(View.GONE);
                amlak_ejareh_container.setVisibility(View.GONE);
                general_price_container.setVisibility(View.VISIBLE);
                amlak_room_container.setVisibility(View.GONE);
                general_type_container.setVisibility(View.VISIBLE);
                amlak_metraj_container.setVisibility(View.VISIBLE);
                break;
            case 18:

                amlak_vadieh_container.setVisibility(View.GONE);
                amlak_ejareh_container.setVisibility(View.GONE);
                general_price_container.setVisibility(View.VISIBLE);
                amlak_room_container.setVisibility(View.VISIBLE);
                general_type_container.setVisibility(View.VISIBLE);
                amlak_metraj_container.setVisibility(View.VISIBLE);

                break;
            // amlak forosh end
            // amlak ejare start
            case 19:

                amlak_vadieh_container.setVisibility(View.VISIBLE);
                amlak_ejareh_container.setVisibility(View.VISIBLE);
                general_price_container.setVisibility(View.GONE);
                amlak_room_container.setVisibility(View.VISIBLE);
                general_type_container.setVisibility(View.VISIBLE);
                amlak_metraj_container.setVisibility(View.VISIBLE);

                break;
            case 20:

                amlak_vadieh_container.setVisibility(View.VISIBLE);
                amlak_ejareh_container.setVisibility(View.VISIBLE);
                general_price_container.setVisibility(View.GONE);
                amlak_room_container.setVisibility(View.VISIBLE);
                general_type_container.setVisibility(View.VISIBLE);
                amlak_metraj_container.setVisibility(View.VISIBLE);

                break;
            case 21:

                amlak_vadieh_container.setVisibility(View.VISIBLE);
                amlak_ejareh_container.setVisibility(View.VISIBLE);
                general_price_container.setVisibility(View.GONE);
                amlak_room_container.setVisibility(View.GONE);
                general_type_container.setVisibility(View.VISIBLE);
                amlak_metraj_container.setVisibility(View.VISIBLE);

                break;
            case 22:

                amlak_vadieh_container.setVisibility(View.VISIBLE);
                amlak_ejareh_container.setVisibility(View.VISIBLE);
                general_price_container.setVisibility(View.GONE);
                amlak_room_container.setVisibility(View.GONE);
                general_type_container.setVisibility(View.VISIBLE);
                amlak_metraj_container.setVisibility(View.VISIBLE);
                amlak_filter_container.setVisibility(View.VISIBLE);
                break;
            case 23:

                amlak_vadieh_container.setVisibility(View.VISIBLE);
                amlak_ejareh_container.setVisibility(View.VISIBLE);
                general_price_container.setVisibility(View.GONE);
                amlak_room_container.setVisibility(View.GONE);
                general_type_container.setVisibility(View.VISIBLE);
                amlak_metraj_container.setVisibility(View.VISIBLE);

                break;
            // amlak ejare end
            // amlak khadamat start
            case 24:

                amlak_vadieh_container.setVisibility(View.GONE);
                amlak_ejareh_container.setVisibility(View.GONE);
                general_price_container.setVisibility(View.GONE);
                amlak_room_container.setVisibility(View.GONE);
                general_type_container.setVisibility(View.GONE);
                amlak_metraj_container.setVisibility(View.GONE);

                break;
            case 25:

                amlak_vadieh_container.setVisibility(View.GONE);
                amlak_ejareh_container.setVisibility(View.GONE);
                general_price_container.setVisibility(View.GONE);
                amlak_room_container.setVisibility(View.GONE);
                general_type_container.setVisibility(View.GONE);
                amlak_metraj_container.setVisibility(View.GONE);

                break;
            case 26:

                amlak_vadieh_container.setVisibility(View.GONE);
                amlak_ejareh_container.setVisibility(View.GONE);
                general_price_container.setVisibility(View.GONE);
                amlak_room_container.setVisibility(View.GONE);
                general_type_container.setVisibility(View.GONE);
                amlak_metraj_container.setVisibility(View.GONE);

                break;
            case 27:

                amlak_vadieh_container.setVisibility(View.GONE);
                amlak_ejareh_container.setVisibility(View.GONE);
                general_price_container.setVisibility(View.GONE);
                amlak_room_container.setVisibility(View.GONE);
                general_type_container.setVisibility(View.GONE);
                amlak_metraj_container.setVisibility(View.GONE);

                break;
        }
    }

    private void calcPezeshk(int id) {
        amlak_filter_container.setVisibility(View.GONE);
        naghlie_filter_container.setVisibility(View.GONE);
        general_filter_container.setVisibility(View.VISIBLE);
        general_type_container.setVisibility(View.GONE);
        general_price_container.setVisibility(View.GONE);
    }


    private void sendFilterParams() {

        boolean can_finish = true;
        Intent resultIntent = new Intent();
        resultIntent.putExtra("segment_price_send", segment_price_send);
        resultIntent.putExtra("feature_image_send", feature_image_send);


        if (general_type_container.isShown()) {

            resultIntent.putExtra("general_type_send", general_type_send);

        }
        if (category_send != 0) {

            resultIntent.putExtra("category_send", category_send);
        }

        if (general_price_container.isShown()) {
            if (!general_price_from_txt.getText().toString().equals("")) {
                resultIntent.putExtra("general_price_from_send", general_price_from_txt.getText().toString());
            }
            if (!general_price_to_txt.getText().toString().equals("")) {
                int general_price_from_value = 0;
                if (!general_price_from_txt.getText().toString().equals("")) {
                    general_price_from_value = Integer.parseInt(general_price_from_txt.getText().toString().replace(",", ""));
                }
                int general_price_to_value = parseInt(general_price_to_txt.getText().toString().replace(",", ""));
                if (general_price_to_value > general_price_from_value) {
                    can_finish = true;
                    resultIntent.putExtra("general_price_to_send", general_price_to_value);
                } else {
                    can_finish = false;
                    Toasty.error(SearchFilterActivity.this, "محدوده قیمت وارد شده دوم از محدوده قیمت اول کوچکتر است.", Toast.LENGTH_LONG).show();
                }
            }
        }

        if (amlak_filter_container.isShown()) {
            if (amlak_metraj_container.isShown()) {

                if (!amlak_metraj_from_txt.getText().toString().trim().equals("")) {
                    resultIntent.putExtra("general_metraj_from_send", amlak_metraj_from_txt.getText().toString().trim());
                }


                if (!amlak_metraj_to_txt.getText().toString().trim().equals("")) {
                    int amlak_metraj_from_value = 0;
                    if (!amlak_metraj_from_txt.getText().toString().equals("")) {
                        amlak_metraj_from_value = parseInt(amlak_metraj_from_txt.getText().toString());
                    }

                    int amlak_metraj_to_value = parseInt(amlak_metraj_to_txt.getText().toString());
                    if (amlak_metraj_to_value > amlak_metraj_from_value) {
                        if (!amlak_metraj_to_txt.getText().toString().trim().equals("")) {
                            can_finish = true;
                            resultIntent.putExtra("general_metraj_to_send", amlak_metraj_to_txt.getText().toString().trim());
                        }
                    } else {
                        can_finish = false;
                        Toasty.error(SearchFilterActivity.this, "متراژ وارد شده دوم از متراژ اول کوچکتر است.", 3000).show();
                    }
                }
            }

            if (amlak_type_container.isShown()) {
                if (amlak_type_send != null) {
                    resultIntent.putExtra("amlak_type_send", amlak_type_send);
                }
            }

            if (amlak_room_container.isShown()) {
                if (amlak_room_send != null) {
                    resultIntent.putExtra("amlak_room_send", amlak_room_send);
                }
            }

            if (amlak_vadieh_container.isShown()) {
                if (!amlak_vadie_from_txt.getText().toString().equals("")) {
                    resultIntent.putExtra("amlak_vadieh_from_value", amlak_vadie_from_txt.getText().toString().replace(",", ""));
                }
                if (!amlak_vadie_to_txt.getText().toString().equals("")) {
                    int amlak_vadieh_from_value = 0;
                    if (!amlak_vadie_from_txt.getText().toString().equals("")) {
                        amlak_vadieh_from_value = parseInt(amlak_vadie_from_txt.getText().toString().replace(",", ""));
                    }

                    int amlak_vadieh_to_value = parseInt(amlak_vadie_to_txt.getText().toString().replace(",", ""));
                    if (!amlak_vadie_to_txt.getText().toString().equals("")) {
                        if (amlak_vadieh_to_value > amlak_vadieh_from_value) {
                            can_finish = true;
                            resultIntent.putExtra("amlak_vadieh_to_value", amlak_vadieh_to_value);
                        } else {
                            can_finish = false;
                            Toasty.error(SearchFilterActivity.this, "محدوده ودیعه وارد شده دوم از محدوده ودیعه اول کوچکتر است.", 3000).show();
                        }
                    }
                }
            }

            if (amlak_ejareh_container.isShown()) {

                if (!amlak_ejareh_from_txt.getText().toString().equals("")) {
                    resultIntent.putExtra("amlak_ejareh_from_send", amlak_ejareh_from_txt.getText().toString().replace(",", ""));
                }
                if (!amlak_ejareh_to_txt.getText().toString().equals("")) {
                    int amlak_ejareh_from_value = 0;
                    if (!amlak_ejareh_from_txt.getText().toString().equals("")) {
                        amlak_ejareh_from_value = parseInt(amlak_ejareh_from_txt.getText().toString().replace(",", ""));
                    }

                    int amlak_ejareh_to_value = parseInt(amlak_ejareh_to_txt.getText().toString().replace(",", ""));

                    if (!amlak_vadie_to_txt.getText().toString().equals("")) {
                        if (amlak_ejareh_to_value > amlak_ejareh_from_value) {
                            can_finish = true;
                            resultIntent.putExtra("amlak_ejareh_to_value", amlak_ejareh_to_value);
                        } else {
                            can_finish = false;
                            Toasty.error(SearchFilterActivity.this, "محدوده اجاره وارد شده دوم از محدوده اجاره اول کوچکتر است.", 3000).show();
                        }
                    }
                }
            }

            if (naghlie_filter_container.isShown()) {
                if (naghlie_brand_container.isShown()) {
                    resultIntent.putExtra("naghliye_brand_send", naghliye_brand_send);
                }

                if (naghlie_sal_container.isShown()) {
                    if (!naghliye_sal_from_txt.getText().toString().trim().equals("")) {
                        resultIntent.putExtra("naghliye_sal_from_send", naghliye_sal_from_txt.getText().toString().trim());
                    }
                    if (!naghliye_sal_to_txt.getText().toString().trim().equals("")) {
                        int naghliye_sal_from_value = 0;
                        if (!naghliye_sal_from_txt.getText().toString().equals("")) {
                            naghliye_sal_from_value = parseInt(naghliye_sal_from_txt.getText().toString());
                        }

                        int naghliye_sal_to_value = parseInt(naghliye_sal_to_txt.getText().toString());
                        if (naghliye_sal_to_value > naghliye_sal_from_value) {
                            if (!naghliye_sal_to_txt.getText().toString().trim().equals("")) {
                                can_finish = true;
                                resultIntent.putExtra("naghliye_sal_to_send", naghliye_sal_to_txt.getText().toString().trim());
                            }
                        } else {
                            can_finish = false;
                            Toasty.error(SearchFilterActivity.this, "مقدار سال وارد شده دوم از مقدار سال اول کوچکتر است.", 3000).show();
                        }
                    }
                }

                if (naghlie_karkard_container.isShown()) {
                    if (!naghliye_karkard_from_txt.getText().toString().trim().equals("")) {
                        resultIntent.putExtra("naghliye_karkard_from_send", naghliye_karkard_from_txt.getText().toString().trim());
                    }
                    if (!naghliye_karkard_to_txt.getText().toString().trim().equals("")) {
                        int naghliye_karkard_from_value = 0;
                        if (!naghliye_karkard_from_txt.getText().toString().equals("")) {
                            naghliye_karkard_from_value = parseInt(naghliye_karkard_from_txt.getText().toString());
                        }

                        int naghliye_karkard_to_value = parseInt(naghliye_karkard_to_txt.getText().toString());
                        if (naghliye_karkard_to_value > naghliye_karkard_from_value) {
                            if (!naghliye_karkard_to_txt.getText().toString().trim().equals("")) {
                                can_finish = true;
                                resultIntent.putExtra("naghliye_karkard_to_send", naghliye_karkard_to_txt.getText().toString().trim());
                            }
                        } else {
                            can_finish = false;
                            Toasty.error(SearchFilterActivity.this, "مقدار کارکرد وارد شده دوم از مقدار کارکرد اول کوچکتر است.", 3000).show();
                        }
                    }
                }
            }

        }

        setResult(RESULT_OK, resultIntent);
        if(can_finish){
            finish();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
