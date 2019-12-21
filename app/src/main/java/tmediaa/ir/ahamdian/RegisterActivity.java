package tmediaa.ir.ahamdian;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.droidbyme.dialoglib.AnimUtils;
import com.droidbyme.dialoglib.DroidDialog;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;
import tmediaa.ir.ahamdian.tools.ApiCallTools;
import tmediaa.ir.ahamdian.tools.AppSharedPref;
import tmediaa.ir.ahamdian.tools.CONST;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class RegisterActivity extends AppCompatActivity {
    private CheckBox gavanin_checkbox;
    private RelativeLayout gavanin_box;
    private LinearLayout active_code_box;
    private LinearLayout form_container;
    private LinearLayout rootView;
    private Context context;
    private ProgressDialog progressDialog;

    private Button btnRegister, btnLogin, active_btn;

    private EditText register_mobile_txt, active_code_txt;

    private String mobile_str, codemmeli_str = "";

    private String android_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        context = this;


        AppSharedPref.init(context);

        if (AppSharedPref.check("online") && (AppSharedPref.read("online", 0) == 1)) {
            Intent i = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(i);
        }

        android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("در حال بارگذاری");
        progressDialog.setCancelable(false);
        gavanin_box = (RelativeLayout) findViewById(R.id.gavanin_box);
        rootView = (LinearLayout) findViewById(R.id.rootView);
        active_code_box = (LinearLayout) findViewById(R.id.active_code_box);
        form_container = (LinearLayout) findViewById(R.id.form_container);
        gavanin_checkbox = (CheckBox) findViewById(R.id.gavanin_checkbox);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        active_btn = (Button) findViewById(R.id.active_btn);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        register_mobile_txt = (EditText) findViewById(R.id.register_mobile_txt);
        active_code_txt = (EditText) findViewById(R.id.active_code);


        active_code_box.setVisibility(View.GONE);
        form_container.setVisibility(View.VISIBLE);

        gavanin_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadConditions();
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkForm();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        permisionCheck();


        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(getString(R.string.fontpath))
                .setFontAttrId(R.attr.fontPath)
                .build()
        );


    }

    private void permisionCheck() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                //Toast.makeText(RegisterActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                DroidDialog dialog = new DroidDialog.Builder(context)
                        .cancelable(true, true)
                        .title("مجوزهای لازم جهت کارکرد صحیح برنامه")
                        .content("این نرم افزار جهت استفاده در تلفن همراه شما نیاز به دسترسی های لازم جهت کارکرد صحیح خود می باشد، لطفا مجوزهای لازم جهت کارکرد صحیح نرم افزار را تایید کنید.")
                        .positiveButton("تایید", new DroidDialog.onPositiveListener() {
                            @Override
                            public void onPositive(Dialog droidDialog) {
                                droidDialog.dismiss();
                            }
                        })
                        .negativeButton("بی خیال", new DroidDialog.onNegativeListener() {
                            @Override
                            public void onNegative(Dialog droidDialog) {

                                droidDialog.dismiss();
                            }

                        })
                        .typeface("GanjNamehSans-Regular.ttf")
                        .animation(AnimUtils.AnimFadeInOut)
                        .divider(true, ContextCompat.getColor(context, R.color.orange))
                        .color(
                                ContextCompat.getColor(context, R.color.colorAccent),
                                ContextCompat.getColor(context, R.color.colorAccent),
                                ContextCompat.getColor(context, R.color.indigo))
                        .show();
            }


        };

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setRationaleTitle(R.string.rationale_title)
                .setRationaleMessage(R.string.rationale_message)
                .setDeniedTitle("رد درخواست صدور مجوز")
                .setDeniedMessage(
                        "در صورت عدم صدور مجوز نمی توانید از همه امکانات نرم افزار استفاده کنید.")
                .setGotoSettingButtonText("مراجعه به بخش مجوزهای نرم افزار")
                .setPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.INTERNET,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                )
                .check();
    }




    private void loadConditions() {
        progressDialog.setMessage("در حال بارگذاری");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Ion.with(context)
                .load(CONST.GET_SETS)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        progressDialog.dismiss();
                        if(e == null){
                            JsonParser parser = new JsonParser();
                            JsonObject root = parser.parse(result).getAsJsonObject();
                            String ghavanin = root.get("settings").getAsJsonObject().get("ghavanin").getAsString();
                            DroidDialog dialog = new DroidDialog.Builder(context)
                                    .cancelable(true, true)
                                    .title("قوانین و مقررات")
                                    .content(ghavanin)
                                    .positiveButton("موافقم", new DroidDialog.onPositiveListener() {
                                        @Override
                                        public void onPositive(Dialog droidDialog) {
                                            gavanin_checkbox.setChecked(true);
                                            droidDialog.dismiss();
                                        }
                                    })
                                    .negativeButton("موافق نیستم", new DroidDialog.onNegativeListener() {
                                        @Override
                                        public void onNegative(Dialog droidDialog) {
                                            gavanin_checkbox.setChecked(false);
                                            droidDialog.dismiss();
                                        }

                                    })
                                    .typeface("GanjNamehSans-Regular.ttf")
                                    .animation(AnimUtils.AnimFadeInOut)
                                    .divider(true, ContextCompat.getColor(context, R.color.orange))
                                    .color(
                                            ContextCompat.getColor(context, R.color.colorAccent),
                                            ContextCompat.getColor(context, R.color.colorAccent),
                                            ContextCompat.getColor(context, R.color.indigo))
                                    .show();


                            /**/
                        }else{
                            Toasty.error(context,getString(R.string.connection_error),Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }


    private void checkForm() {
        boolean is_mobile_ok = false;
        if (checkMobile()) {
            is_mobile_ok = true;
            if (gavanin_checkbox.isChecked()) {
                registerUser();
            } else {
                loadConditions();
            }
        } else {
            register_mobile_txt.setError("لطفا موبایل خود را بطور صحیح وارد کنید.");
        }
    }

    private boolean checkMobile() {
        mobile_str = register_mobile_txt.getText().toString();
        boolean is_mobile_ok = Pattern.matches("09(0(\\d)|1(\\d)|2(\\d)|3(\\d)|(9(\\d)))\\d{7}$", mobile_str);
        return is_mobile_ok;
    }


    private void registerUser() {

        progressDialog.show();
        Ion.with(context)
                .load(CONST.USER_REGISTER)
                .setBodyParameter("device_id", android_id)
                .setBodyParameter("mobile", mobile_str)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        progressDialog.dismiss();
                        if (e != null) {
                            Toast.makeText(context, "خطا در اتصال به سرور", Toast.LENGTH_LONG).show();
                        } else {
                            JsonParser parser = new JsonParser();
                            JsonObject json_obj = parser.parse(result).getAsJsonObject();

                            String status = json_obj.get("status").getAsString();

                            if (e == null) {
                                if (status.equals("error")) {
                                    Toasty.error(context,"شما قبلا ثبت نام کرده اید لطفا وارد شوید",Toast.LENGTH_LONG,false).show();
                                } else {
                                    active_code_box.setVisibility(View.VISIBLE);
                                    form_container.setVisibility(View.GONE);

                                    if (json_obj.has("user")) {
                                        int user_id = json_obj.get("user").getAsJsonObject().get("id").getAsInt();
                                        String username = json_obj.get("user").getAsJsonObject().get("username").getAsString();

                                        String active_code = json_obj.get("user").getAsJsonObject().get("active_code").getAsString();
                                        String user_status = json_obj.get("user").getAsJsonObject().get("status").getAsString();
                                        try {
                                            byte[] username_bytes = username.getBytes("UTF-8");
                                            AppSharedPref.write("ID", String.valueOf(user_id));
                                            AppSharedPref.write("TOKEN", Base64.encodeToString(username_bytes, Base64.DEFAULT));
                                            AppSharedPref.write("status", user_status);
                                            checkActiveCode(active_code);

                                        } catch (UnsupportedEncodingException e1) {
                                            e1.printStackTrace();
                                        }


                                    }
                                }
                            }

                        }
                    }

                });
    }

    private void checkActiveCode(final String code) {
        active_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                if (!active_code_txt.getText().toString().trim().equals("")) {
                    String enter_code = active_code_txt.getText().toString().trim();
                    if (enter_code.equals(code)) {
                        String app_token = AppSharedPref.read("TOKEN", "");
                        byte[] data = Base64.decode(app_token, Base64.DEFAULT);
                        try {
                            String username = new String(data, "UTF-8");
                            progressDialog.show();
                            ApiCallTools.getTK(context, username, new ApiCallTools.completeEvent() {
                                @Override
                                public void onCompleted(Exception e, JsonObject response) {
                                    if(e == null){
                                        String token = response.get("token").getAsString();
                                        String autoriz_tk = "Bearer " + token;

                                        Ion.with(context)
                                                .load(CONST.ACTIVE_USER)
                                                .setHeader("Authorization", autoriz_tk)
                                                .setBodyParameter("id",AppSharedPref.read("ID",""))
                                                .asString()
                                                .setCallback(new FutureCallback<String>() {
                                                    @Override
                                                    public void onCompleted(Exception e, String result) {
                                                        progressDialog.dismiss();
                                                        if(e == null){
                                                            AppSharedPref.write("online",1);
                                                            progressDialog.show();
                                                            Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                                                            startActivity(i);
                                                            finish();
                                                        }else{
                                                            Toast.makeText(context,"خطا در برقراری ارتباط با سرور",Toast.LENGTH_SHORT).show();

                                                        }
                                                    }
                                                });
                                    }else{
                                        Toast.makeText(context,"خطا در برقراری ارتباط با سرور",Toast.LENGTH_SHORT).show();
                                        progressDialog.show();
                                    }
                                }
                            });

                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    } else {
                        active_code_txt.setError("کد وارد شده صحیح نمی باشد.");
                    }
                } else {
                    active_code_txt.setError("لطف کد دریافتی را وارد نمایید.");
                }


            }
        });


    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}