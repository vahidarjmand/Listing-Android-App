package tmediaa.ir.ahamdian;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;
import tmediaa.ir.ahamdian.tools.AppSharedPref;
import tmediaa.ir.ahamdian.tools.CONST;

public class LoginActivity extends AppCompatActivity {

    private RelativeLayout gavanin_box;
    private LinearLayout active_code_box;
    private LinearLayout form_container;
    private Context context;
    private ProgressDialog progressDialog;
    private Button btnRegister, btnLogin, active_btn;
    private EditText register_mobile_txt, active_code_txt;
    private String mobile_str, codemmeli_str = "";
    private LinearLayout rootView;
    private String android_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        AppSharedPref.init(context);

        context = this;
        android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("در حال بارگذاری");
        progressDialog.setCancelable(false);
        gavanin_box = (RelativeLayout) findViewById(R.id.gavanin_box);
        rootView = (LinearLayout) findViewById(R.id.rootView);
        active_code_box = (LinearLayout) findViewById(R.id.active_code_box);
        form_container = (LinearLayout) findViewById(R.id.form_container);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        active_btn = (Button) findViewById(R.id.active_btn);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        register_mobile_txt = (EditText) findViewById(R.id.register_mobile_txt);
        active_code_txt = (EditText) findViewById(R.id.active_code);


        active_code_box.setVisibility(View.GONE);
        form_container.setVisibility(View.VISIBLE);


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkForm();

            }
        });
    }

    private void checkForm() {
        if (checkMobile()) {
            loginUser();
        } else {
            register_mobile_txt.setError("لطفا موبایل خود را بطور صحیح وارد کنید.");
        }
    }

    private boolean checkMobile() {
        mobile_str = register_mobile_txt.getText().toString();
        boolean is_mobile_ok = Pattern.matches("09(1[0-9]|3[0-9]|2[1-9])\\d{7}", mobile_str);
        return is_mobile_ok;
    }


    private void loginUser() {
        progressDialog.show();
        Ion.with(context)
                .load(CONST.USER_LOGIN)
                .setBodyParameter("mobile", mobile_str)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        progressDialog.dismiss();
                        if(e == null){
                            JsonParser parser = new JsonParser();
                            JsonObject json_obj = parser.parse(result).getAsJsonObject();
                            boolean is_item = json_obj.get("info").isJsonObject();
                            if(is_item){
                                JsonObject item = json_obj.get("info").getAsJsonObject();
                                int user_id = item.get("id").getAsInt();
                                String username = item.get("username").getAsString();
                                String user_status = item.get("status").getAsString();
                                String active_code = item.get("active_code").getAsString();
                                active_code_box.setVisibility(View.VISIBLE);
                                form_container.setVisibility(View.GONE);

                                try {
                                    byte[] username_bytes = username.getBytes("UTF-8");
                                    AppSharedPref.write("ID", String.valueOf(user_id));
                                    AppSharedPref.write("TOKEN", Base64.encodeToString(username_bytes, Base64.DEFAULT));
                                    AppSharedPref.write("status", user_status);
                                    checkActiveCode(active_code);
                                } catch (UnsupportedEncodingException e1) {
                                    e1.printStackTrace();
                                }
                            }else{
                                Toasty.info(context,"کاربری با این موبایل وجود ندارد لطفا ثبت نان کنید.", Toast.LENGTH_LONG,false).show();
                            }
                        }else{
                            Toasty.error(context,"خطا در برقراری ارتباط با سرور", Toast.LENGTH_LONG,false).show();
                        }
                    }
                });

    }

    private void checkActiveCode(final String code) {
        progressDialog.dismiss();
        active_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!active_code_txt.getText().toString().trim().equals("")) {
                    String enter_code = active_code_txt.getText().toString().trim();
                    if (enter_code.equals(code)) {
                        AppSharedPref.write("online",1);
                        Intent i = new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(i);
                        finish();
                        Toasty.success(context,"با موفقیت وارد نرم افزار شدید.", Toast.LENGTH_LONG,false).show();
                    } else {
                        active_code_txt.setError("کد وارد شده صحیح نمی باشد.");
                    }
                } else {
                    active_code_txt.setError("لطف کد دریافتی را وارد نمایید.");
                }


            }
        });
    }

}
