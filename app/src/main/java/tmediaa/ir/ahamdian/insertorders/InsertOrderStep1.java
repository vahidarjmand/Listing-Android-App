package tmediaa.ir.ahamdian.insertorders;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.http.BasicNameValuePair;
import com.koushikdutta.async.http.NameValuePair;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.koushikdutta.ion.builder.Builders;
import com.nileshp.multiphotopicker.photopicker.activity.PickImageActivity;
import com.squareup.otto.Subscribe;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;
import th.in.lordgift.widget.EditTextIntegerComma;
import tmediaa.ir.ahamdian.R;
import tmediaa.ir.ahamdian.adapters.BrandAdapter;
import tmediaa.ir.ahamdian.adapters.NothingSelectedSpinnerAdapter;
import tmediaa.ir.ahamdian.insertorders.categoryselector.CategorySelector;
import tmediaa.ir.ahamdian.interfaces.CategoryCallback;
import tmediaa.ir.ahamdian.model.Brand;
import tmediaa.ir.ahamdian.otto.AppEvents;
import tmediaa.ir.ahamdian.otto.GlobalBus;
import tmediaa.ir.ahamdian.tools.AppSharedPref;
import tmediaa.ir.ahamdian.tools.CONST;
import tmediaa.ir.ahamdian.tools.ScalingUtilities;

import static android.app.Activity.RESULT_OK;

/**
 * Created by tmediaa on 8/20/2017.
 */

public class InsertOrderStep1 extends Fragment implements BlockingStep, CategoryCallback {
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.insert_order_step_1, container, false);


        getLayouts();
        category_selector_btn = (Button) rootView.findViewById(R.id.category_selector_btn);
        category_selector_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CategorySelector categorySelector = new CategorySelector();
                categorySelector.show(getChildFragmentManager(), "categoryselector");
            }
        });


        //for image picker
        options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        imageView_lp = new FrameLayout.LayoutParams(280, 280);
        imageView_lp.setMargins(10, 0, 10, 0);

        return rootView;
    }

    private void getLayouts() {
        horizontalScrollView = (HorizontalScrollView) rootView.findViewById(R.id.horizontalScrollView);
        category_selector = (LinearLayout) rootView.findViewById(R.id.category_selector);
        general_price_type_btn = (Button) rootView.findViewById(R.id.general_price_type_btn);
        general_type_btn = (Button) rootView.findViewById(R.id.general_type_btn);
        general_select_image = (Button) rootView.findViewById(R.id.general_select_image);
        amlak_type_group = (RadioGroup) rootView.findViewById(R.id.amlak_type_group);
        amlak_homeshahr = (RadioGroup) rootView.findViewById(R.id.amlak_homeshahr);
        amlak_sanad_group = (RadioGroup) rootView.findViewById(R.id.amlak_sanad_group);
        form_container = (LinearLayout) rootView.findViewById(R.id.form_container);
        general_container = (LinearLayout) rootView.findViewById(R.id.general_container);
        general_price = (EditTextIntegerComma) rootView.findViewById(R.id.general_price);
        general_tel = (EditText) rootView.findViewById(R.id.general_tel);
        general_title = (EditText) rootView.findViewById(R.id.general_title);
        general_desc = (EditText) rootView.findViewById(R.id.general_desc);
        general_email = (EditText) rootView.findViewById(R.id.general_email);
        general_price_con = (LinearLayout) rootView.findViewById(R.id.general_price_con);
        general_price_form_con = (LinearLayout) rootView.findViewById(R.id.general_price_form_con);
        general_type_con = (LinearLayout) rootView.findViewById(R.id.general_type_con);
        general_select_image_con = (LinearLayout) rootView.findViewById(R.id.general_select_image_con);
        general_email_con = (LinearLayout) rootView.findViewById(R.id.general_email_con);
        general_tel_con = (LinearLayout) rootView.findViewById(R.id.general_tel_con);
        general_title_con = (LinearLayout) rootView.findViewById(R.id.general_title_con);
        general_desc_con = (LinearLayout) rootView.findViewById(R.id.general_desc_con);
        amlak_container = (LinearLayout) rootView.findViewById(R.id.amlak_container);
        amlak_metraj_con = (LinearLayout) rootView.findViewById(R.id.amlak_metraj_con);
        amlak_metraj = (EditText) rootView.findViewById(R.id.amlak_metraj);
        amlak_otagh = (EditText) rootView.findViewById(R.id.amlak_otagh);
        amlak_type_con = (LinearLayout) rootView.findViewById(R.id.amlak_type_con);
        amlak_otagh_con = (LinearLayout) rootView.findViewById(R.id.amlak_otagh_con);
        amlak_homeshahr_con = (LinearLayout) rootView.findViewById(R.id.amlak_homeshahr_con);
        amlak_vadie_con = (LinearLayout) rootView.findViewById(R.id.amlak_vadie_con);
        amlak_vadie = (EditTextIntegerComma) rootView.findViewById(R.id.amlak_vadie);
        amlak_ejareh_con = (LinearLayout) rootView.findViewById(R.id.amlak_ejareh_con);
        amlak_ejareh = (EditTextIntegerComma) rootView.findViewById(R.id.amlak_ejareh);
        amlak_sanad_con = (LinearLayout) rootView.findViewById(R.id.amlak_sanad_con);
        naghliye_container = (LinearLayout) rootView.findViewById(R.id.naghliye_container);
        naghliye_brand_con = (LinearLayout) rootView.findViewById(R.id.naghliye_brand_con);
        naghliye_brand = (Spinner) rootView.findViewById(R.id.naghliye_brand);
        naghliye_sal = (Spinner) rootView.findViewById(R.id.naghliye_sal);
        naghliye_kardkard = (EditText) rootView.findViewById(R.id.naghliye_kardkard);
        naghliye_kardkard_con = (LinearLayout) rootView.findViewById(R.id.naghliye_kardkard_con);
        naghliye_sal_con = (LinearLayout) rootView.findViewById(R.id.naghliye_sal_con);
        layoutListItemSelect = (LinearLayout) rootView.findViewById(R.id.layoutListItemSelect);

        progressDialog = new ProgressDialog(getContext());

    }

    private void showProgress(String msg) {
        progressDialog.setMessage(msg);
        progressDialog.show();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        horizontalScrollView.setVisibility(View.GONE);
        form_container.setVisibility(View.GONE);


    }


    @Override
    public void onSelected() {

    }

    @Override
    public void onError(@NonNull VerificationError error) {
        //handle error inside of the fragment, e.g. show error on EditText
    }


    @Override
    public void onCategoryChoose(int id, String value) {
        nullAllParams();
        selected_cat_value = id;
        if (value.length() > 0) {
            category_selector_btn.setText(value);
            showTargetForm(id);
        }
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

    private void checkForm(StepperLayout.OnNextClickedCallback callback) {
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
                        sendMainform(callback);
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
                        sendMainform(callback);
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
                        sendMainform(callback);
                    }
                    break;
                default:
                    if (checkGeneralField()) {
                        addGeneralField();
                        sendMainform(callback);

                    }
                    break;
            }
        } else {
            showErrorCon(category_selector);
            Toasty.error(getContext(), "لطفا دسته بندی مورد نظر را انتخاب کنید", Toast.LENGTH_LONG, true).show();
        }
    }


    private boolean checkKarkardField() {
        if (naghliye_kardkard.getText().toString().trim().equals("")) {
            showErrorCon(naghliye_kardkard_con);
            Toasty.error(getContext(), "لطفا تعداد کارکرد خودرو را وارد کنید.", Toast.LENGTH_LONG, true).show();
            return false;
        } else {
            naghliye_karkard_send = Integer.valueOf(naghliye_kardkard.getText().toString().trim());
            return true;
        }
    }

    private boolean checkSaleField() {
        if (naghliye_sal_send == null) {
            showErrorCon(naghliye_sal_con);
            Toasty.error(getContext(), "لطفا سال تولید خودرو را وارد کنید.", Toast.LENGTH_LONG, true).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean checkBrandField() {
        if (naghliye_brand_send == null) {
            showErrorCon(naghliye_brand_con);
            Toasty.error(getContext(), "لطفا برند خودرو را انتخاب کنید.", Toast.LENGTH_LONG, true).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean checkAmlakEjare() {
        if (amlak_ejareh.getText().toString().trim().equals("")) {
            showErrorCon(amlak_ejareh_con);
            Toasty.error(getContext(), "لطفا ودیعه مورد نظر را وارد کنید.", Toast.LENGTH_LONG, true).show();
            return false;
        } else {
            amlak_ejareh_send = Integer.parseInt(amlak_ejareh.getValue());
            return true;
        }
    }

    private boolean checkAmlakVadieh() {
        if (amlak_vadie.getText().toString().trim().equals("")) {
            showErrorCon(amlak_vadie_con);
            Toasty.error(getContext(), "لطفا اجاره مورد نظر را وارد کنید.", Toast.LENGTH_LONG, true).show();
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
            Toasty.error(getContext(), "لطفا وضعیت سند اداری ملک را انتخاب کنید. ", Toast.LENGTH_LONG, true).show();
            return false;
        } else {
            RadioButton selected_radio = (RadioButton) rootView.findViewById(radioButtonID);

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
            Toasty.error(getContext(), "لطفا وضعیت مکانی ملک را انتخاب کنید. ", Toast.LENGTH_LONG, true).show();
            return false;
        } else {
            RadioButton selected_radio = (RadioButton) rootView.findViewById(radioButtonID);

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
            Toasty.error(getContext(), "لطفا تعداد اتاق را وارد کنید. ", Toast.LENGTH_LONG, true).show();
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
            Toasty.error(getContext(), "لطفا نوع اگهی دهنده را وارد کنید. ", Toast.LENGTH_LONG, true).show();
            return false;
        } else {
            RadioButton selected_radio = (RadioButton) rootView.findViewById(radioButtonID);

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
            Toasty.error(getContext(), "لطفا متراژ مورد نظر را وارد کنید", Toast.LENGTH_LONG, true).show();
            return false;
        } else {
            amlak_metraj_send = Integer.parseInt(amlak_metraj.getText().toString());
            return true;
        }
    }

    private boolean checkDescTitle() {
        if (general_desc.getText().toString().trim().length() < 1) {
            showErrorCon(general_desc_con);
            Toasty.error(getContext(), "لطفا توضیحات آگهی را وارد نمایید.", Toast.LENGTH_LONG, true).show();
            return false;
        } else {
            general_desc_send = general_desc.getText().toString().trim();
            return true;
        }
    }

    private boolean checkTitleField() {
        if (general_title.getText().toString().length() < 1) {
            showErrorCon(general_title_con);
            Toasty.error(getContext(), "لطفا عنوان آگهی را وارد نمایید.", Toast.LENGTH_LONG, true).show();
            return false;
        } else {
            general_title_send = general_title.getText().toString().trim();
            return true;
        }
    }

    private boolean checkTelField() {
        String mobile_str = general_tel.getText().toString();
        boolean is_mobile_ok = Pattern.matches("09(1[0-9]|3[0-9]|2[1-9])\\d{7}", mobile_str);
        if (is_mobile_ok) {
            general_tel_send = mobile_str;
            return true;
        } else {
            showErrorCon(general_tel_con);
            Toasty.error(getContext(), "لطفا موبایل خود رو بصورت صحیح وارد کنید.", Toast.LENGTH_LONG, true).show();
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
                Toasty.error(getContext(), "لطفا ایمیل خود را بصورت صحیح وارد کنید.", Toast.LENGTH_LONG, true).show();
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
            Toasty.error(getContext(), "لطفا نوع آگهی را انتخاب کنید.", Toast.LENGTH_LONG, true).show();
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
                    Toasty.error(getContext(), "لطفا قیمت مورد نظر را وارد کنید.", Toast.LENGTH_LONG, true).show();
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
            Toasty.error(getContext(), "لطفا نوع قیمت را انتخاب کنید.", Toast.LENGTH_LONG, true).show();
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
        } else if (id == 20 || id == 21 || id == 22 || id == 23) {
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

    @Override
    @UiThread
    public void onCompleteClicked(final StepperLayout.OnCompleteClickedCallback callback) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                callback.complete();
            }
        }, 2000L);
    }

    @Override
    @UiThread
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {
        Toast.makeText(this.getContext(), "برگشت", Toast.LENGTH_LONG).show();
        callback.goToPrevStep();
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
        final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        general_price_form_con.setVisibility(View.GONE);
        TextView title = new TextView(getContext());
        title.setText("نوع قیمت");
        title.setPadding(20, 20, 20, 20);
        title.setGravity(Gravity.RIGHT);
        title.setTextSize(18);
        alert.setCustomTitle(title);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.select_dialog_item);
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
        final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

        TextView title = new TextView(getContext());
        title.setText("نوع آگهی");
        title.setPadding(20, 20, 20, 20);
        title.setGravity(Gravity.RIGHT);
        title.setTextSize(18);
        alert.setCustomTitle(title);


        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.select_dialog_item);
        arrayAdapter.add("فروشی");
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
        BrandAdapter adapter = new BrandAdapter(getContext(), CONST.NAGHLIYE_BRANDS_LIST);
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
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.naghliye_sal, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        naghliye_sal.setPrompt("انتخاب کنید");

        naghliye_sal.setAdapter(
                new NothingSelectedSpinnerAdapter(
                        adapter,
                        R.layout.spinner_row_nothing_selected,
                        // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                        getContext()));

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

    private void showPezeshkForm(int id) {
        initilizeFields();
        form_container.setVisibility(View.VISIBLE);
        naghliye_container.setVisibility(View.GONE);
        amlak_container.setVisibility(View.GONE);
        general_price_con.setVisibility(View.GONE);
        general_type_con.setVisibility(View.GONE);
    }

    private void disableAmlakField() {
        amlak_container.setVisibility(View.GONE);
    }

    private void disableNaghliyeField() {
        naghliye_container.setVisibility(View.GONE);
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

    private void nullAllParams() {
        category_selector_btn.setText("انتخاب کنید");
        general_price_type_btn.setText("انتخاب کنید");
        general_type_btn.setText("انتخاب کنید");

        send_paramas.clear();
        selected_cat_value = null;
        price_type_send = null;
        price_send = null;
        general_type_send = null;
        general_email_send = null;
        general_tel_send = null;
        general_title_send = null;
        general_desc_send = null;
        amlak_metraj_send = null;
        amlak_type_send = null;
        amlak_otagh_send = null;
        amlak_homeshahr_send = null;
        amlak_vadieh_send = null;
        amlak_ejareh_send = null;
        amlak_sanad_send = null;
        naghliye_brand_send = null;
        naghliye_karkard_send = null;
        naghliye_sal_send = null;
    }


    private void openImagePickerIntent() {
        if (isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Intent mIntent = new Intent(getContext(), PickImageActivity.class);
            mIntent.putExtra(PickImageActivity.KEY_LIMIT_MAX_IMAGE, (8 - final_path.size()));
            mIntent.putExtra(PickImageActivity.KEY_LIMIT_MIN_IMAGE, 1);
            startActivityForResult(mIntent, PickImageActivity.PICKER_REQUEST_CODE);
        } else {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, READ_STORAGE_CODE);
        }
    }


    private boolean isPermissionGranted(String permission) {
        //Getting the permission status
        int result = ContextCompat.checkSelfPermission(getContext(), permission);

        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        //If permission is not granted returning false
        return false;
    }

    private void requestPermission(String permission, int code) {

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, code);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Checking the request code of our request
        if (requestCode == READ_STORAGE_CODE) {
            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePickerIntent();
            } else {
                getActivity().finish();
            }
        } else if (requestCode == WRITE_STORAGE_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                getActivity().finish();
            }
        }
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
        send_paramas.add(new BasicNameValuePair("expire_date", DateToStr));
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


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (resultCode == -1 && requestCode == PickImageActivity.PICKER_REQUEST_CODE) {
            this.pathList = intent.getExtras().getStringArrayList(PickImageActivity.KEY_DATA_RESULT);
            if (this.pathList != null && !this.pathList.isEmpty()) {
                horizontalScrollView.setVisibility(View.VISIBLE);
                for (int i = 0; i < pathList.size(); i++) {
                    viewItemSelected = getActivity().getLayoutInflater().inflate(R.layout.piclist_item_selected, layoutListItemSelect, false);
                    viewItemSelected.setTag(pathList.get(i));
                    btnDelete = (ImageView) viewItemSelected.findViewById(R.id.btnDelete);
                    calcbtnDelete();
                    Bitmap bmImg = BitmapFactory.decodeFile(pathList.get(i), options);
                    viewItemSelected.setId(i);
                    ImageView imageItem = (ImageView) viewItemSelected.findViewById(R.id.imageItem);
                    imageItem.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    imageItem.setLayoutParams(imageView_lp);
                    imageItem.setImageBitmap(bmImg);
                    layoutListItemSelect.addView(viewItemSelected);
                }
            }
        }
    }

    private void calcbtnDelete() {
        if (btnDelete != null) {
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String selected_item = (String) ((View) v.getParent().getParent()).getTag();
                    layoutListItemSelect.removeView((View) v.getParent().getParent());
                    pathList.remove(selected_item);

                    if (pathList.size() == 0) {
                        horizontalScrollView.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    private void sendMainform(final StepperLayout.OnNextClickedCallback callback) {

        callback.getStepperLayout().showProgress("در حال ارسال اطلاعات");
        String app_token = AppSharedPref.read("TOKEN", "");
        byte[] data = Base64.decode(app_token, Base64.DEFAULT);



        try {
            String user_pass = new String(data, "UTF-8");

            Log.d(CONST.APP_LOG,"user_pass: " + user_pass);

            Ion.with(getContext())
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



                                    for (int i = 0; i < pathList.size(); i++) {
                                        try {
                                            String path = new DecodeFileAsync().execute(pathList.get(i)).get();
                                            final_path.add(path);
                                        } catch (InterruptedException err) {
                                            err.printStackTrace();
                                        } catch (ExecutionException err) {
                                            err.printStackTrace();
                                        }
                                    }

                                    if (is_edit) {

                                        Log.d(CONST.APP_LOG,"edit order");

                                        ionBuilder = Ion.with(getContext()).load("POST", CONST.EDIT_ORDER);
                                        ionBuilder.setMultipartParameter("order_id", String.valueOf(edit_order_id));

                                        for (int i = 0; i < send_paramas.size(); i++) {
                                            ionBuilder.setMultipartParameter(send_paramas.get(i).getName(), send_paramas.get(i).getValue());
                                        }

                                        ionBuilder.setHeader("Authorization", "Bearer " + token);

                                        for (int j = 0; j < final_path.size(); j++) {
                                            File file = new File(final_path.get(j));
                                            String name = "img_" + j;
                                            ionBuilder.setMultipartFile(name, "image/*", file);
                                        }

                                        ionBuilder.asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
                                            @Override
                                            public void onCompleted(Exception e, Response<String> result) {
                                                callback.getStepperLayout().hideProgress();
                                                callback.goToNextStep();

                                                if (e == null) {

                                                    JsonParser parser = new JsonParser();
                                                    JsonObject json_obj = parser.parse(result.getResult()).getAsJsonObject();

                                                    if (json_obj.has("status")) {
                                                        if (json_obj.get("status").getAsString().equals("ok")) {
                                                            JsonObject order = json_obj.get("order").getAsJsonObject();
                                                            allow_next = false;

                                                            AppEvents.sendOrderID id_event = new AppEvents.sendOrderID(order.get("id").getAsInt());
                                                            GlobalBus.getBus().post(id_event);

                                                        } else {
                                                            Toasty.error(getContext(), "خطا در ارسال آگهی لطفا، دوباره تلاش کنید", Toast.LENGTH_LONG).show();
                                                        }
                                                    } else {
                                                        Toasty.error(getContext(), "خطا در ارسال آگهی لطفا، دوباره تلاش کنید", Toast.LENGTH_LONG).show();
                                                    }
                                                } else {
                                                    Toasty.error(getContext(), "خطا در برفراری ارتباط با سرور", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });

                                    } else {

                                        Log.d(CONST.APP_LOG,"add order");
                                        ionBuilder = Ion.with(getContext()).load("POST", CONST.ADD_ORDER);
                                        for (int i = 0; i < send_paramas.size(); i++) {
                                            ionBuilder.setMultipartParameter(send_paramas.get(i).getName(), send_paramas.get(i).getValue());
                                        }

                                        ionBuilder.setHeader("Authorization", "Bearer " + token);
                                        for (int j = 0; j < final_path.size(); j++) {
                                            File file = new File(final_path.get(j));
                                            String name = "img_" + j;
                                            ionBuilder.setMultipartFile(name, "image/*", file);
                                        }

                                        ionBuilder.asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
                                            @Override
                                            public void onCompleted(Exception e, Response<String> result) {

                                                callback.getStepperLayout().hideProgress();
                                                callback.goToNextStep();

                                                if (e == null) {

                                                    JsonParser parser = new JsonParser();
                                                    JsonObject json_obj = parser.parse(result.getResult()).getAsJsonObject();

                                                    if (json_obj.has("status")) {
                                                        if (json_obj.get("status").getAsString().equals("ok")) {
                                                            JsonObject order = json_obj.get("order").getAsJsonObject();
                                                            allow_next = false;

                                                            AppEvents.sendOrderID id_event = new AppEvents.sendOrderID(order.get("id").getAsInt());
                                                            GlobalBus.getBus().post(id_event);

                                                        } else {
                                                            Toasty.error(getContext(), "خطا در ارسال آگهی لطفا، دوباره تلاش کنید", Toast.LENGTH_LONG).show();
                                                        }
                                                    } else {
                                                        Toasty.error(getContext(), "خطا در ارسال آگهی لطفا، دوباره تلاش کنید", Toast.LENGTH_LONG).show();
                                                    }
                                                } else {
                                                    Toasty.error(getContext(), "خطا در برفراری ارتباط با سرور", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    }


                                }
                            } else {
                                Toasty.error(getContext(), "خطا در برفراری ارتباط با سرور", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


    }

    private void clearForm() {
        nullAllParams();
        final_path.clear();
        pathList.clear();
        horizontalScrollView.setVisibility(View.GONE);
        form_container.setVisibility(View.GONE);
        form_container.setVisibility(View.GONE);
        amlak_container.setVisibility(View.GONE);
        naghliye_container.setVisibility(View.GONE);
        general_title.setText("");
        general_email.setText("");
        general_desc.setText("");
        general_tel.setText("");
        general_price.setText("");
        amlak_ejareh.setText("");
        amlak_vadie.setText("");
        amlak_otagh.setText("");
        amlak_homeshahr.clearCheck();
        amlak_sanad_group.clearCheck();
        amlak_metraj.setText("");
        naghliye_kardkard.setText("");
    }


    private class DecodeFileAsync extends AsyncTask<String, Void, String> {
        private static final int DESIREDWIDTH = 800;
        private static final int DESIREDHEIGHT = 800;
        private String import_url;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress("در حال ارسال تصاویر");
        }

        @Override
        protected String doInBackground(String... params) {
            import_url = params[0];
            String strMyImagePath = null;
            Bitmap scaledBitmap = null;

            try {
                // Part 1: Decode image
                Bitmap unscaledBitmap = ScalingUtilities.decodeFile(import_url, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.FIT);

                scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.FIT);

                // Store to tmp file

                String extr = Environment.getExternalStorageDirectory().toString();
                File mFolder = new File(extr + "/PELAK_TMMFOLDER");
                if (!mFolder.exists()) {
                    mFolder.mkdir();
                }

                String s = (Math.round(Math.random() * 100)) + "_tmp.jpg";

                File f = new File(mFolder.getAbsolutePath(), s);

                strMyImagePath = f.getAbsolutePath();
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(f);
                    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos);
                    fos.flush();
                    fos.close();
                } catch (FileNotFoundException e) {

                    e.printStackTrace();
                } catch (Exception e) {

                    e.printStackTrace();
                }

                scaledBitmap.recycle();
            } catch (Throwable e) {
            }

            if (strMyImagePath == null) {
                return import_url;
            }
            return strMyImagePath;
        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);
            progressDialog.dismiss();
            final_path.add(s);
        }

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        GlobalBus.getBus().register(this);
    }

    @Override
    public void onDetach() {
        GlobalBus.getBus().unregister(this);
        super.onDetach();
    }

    @Override
    public VerificationError verifyStep() {
        //return null if the user can go to the next step, create a new VerificationError instance otherwise
        return null;
    }

    @Override
    @UiThread
    public void onNextClicked(final StepperLayout.OnNextClickedCallback callback) {

        if (selected_cat_value != null) {
            checkForm(callback);
        } else {
            showErrorCon(category_selector);
            Toasty.error(getContext(), "لطفا دسته بندی مورد نظر را انتخاب کنید", Toast.LENGTH_LONG, true).show();
        }
    }


    @Subscribe
    public void getOrderID(AppEvents.BackStep events) {
        edit_order_id = events.getOrderID();
        is_edit = true;
        final_path.clear();
        editUI();


        calcbtnDelete();
    }

    private void editUI() {
        category_selector.setEnabled(false);
        category_selector_btn.setEnabled(false);
    }
}
