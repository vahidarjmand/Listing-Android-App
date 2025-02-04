package tmediaa.ir.ahamdian;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.linchaolong.android.imagepicker.ImagePicker;
import com.linchaolong.android.imagepicker.cropper.CropImage;
import com.linchaolong.android.imagepicker.cropper.CropImageView;
import com.marcoscg.dialogsheet.DialogSheet;
import com.squareup.otto.Subscribe;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;
import com.zarinpal.ewallets.purchase.OnCallbackRequestPaymentListener;
import com.zarinpal.ewallets.purchase.OnCallbackVerificationPaymentListener;
import com.zarinpal.ewallets.purchase.PaymentRequest;
import com.zarinpal.ewallets.purchase.ZarinPal;

import java.io.UnsupportedEncodingException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;
import tmediaa.ir.ahamdian.adapters.InserOrderAdapter;
import tmediaa.ir.ahamdian.otto.AppEvents;
import tmediaa.ir.ahamdian.otto.GlobalBus;
import tmediaa.ir.ahamdian.tools.AppSharedPref;
import tmediaa.ir.ahamdian.tools.CONST;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class InsertOrderActivity extends AppCompatActivity implements StepperLayout.StepperListener {

    private Context context;
    private StepperLayout stepperLayout;
    private ProgressDialog progressDialog;
    private ImagePicker imagePicker = new ImagePicker();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            setContentView(R.layout.activity_insert_order_above);
        } else {
            setContentView(R.layout.activity_insert_order);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.insert_order_title));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        stepperLayout = (StepperLayout) findViewById(R.id.stepperLayout);
        stepperLayout.setTabNavigationEnabled(true);
        stepperLayout.setAdapter(new InserOrderAdapter(getSupportFragmentManager(), this), 0);
        stepperLayout.setShowErrorStateEnabled(true);
        stepperLayout.setShowErrorStateOnBackEnabled(true);
        stepperLayout.setListener(this);


        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(getString(R.string.fontpath))
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }


    @Subscribe
    public void getBack(AppEvents.BackStep events) {
        stepperLayout.setCurrentStepPosition(0);
    }
    /*@Override
    protected void attachBaseContext(Context newBase) {
        //super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.insert_ad_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

       /* if(id == R.id.own_ad_menu){

        }*/
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    @Override
    public void onCompleted(View completeButton) {

    }

    @Override
    public void onError(VerificationError verificationError) {

    }

    @Override
    public void onStepSelected(int newStepPosition) {
        if (newStepPosition == 0) {
            stepperLayout.showBottomNavgiation();
        } else {

            stepperLayout.hideBottomNavgiation();
            stepperLayout.setTabNavigationEnabled(false);

        }

    }

    @Override
    public void onReturn() {

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
    public void onBackPressed() {
        int currentStepPosition = stepperLayout.getCurrentStepPosition();
        if (currentStepPosition > 0) {
            stepperLayout.onBackClicked();
        } else {
            finish();
        }
    }


    @Subscribe
    public void PayOrder(final AppEvents.PayOrder events) {
        progressDialog.setMessage("در حال اتصال به درگاه پرداخت");
        progressDialog.show();
        ZarinPal purchase = ZarinPal.getPurchase(context);
        PaymentRequest paymentRequest = ZarinPal.getPaymentRequest();
        int order_price = Integer.parseInt(AppSharedPref.read("order_price", "1000"));
        paymentRequest.setMerchantID("62f24130-d20a-11e7-b22b-000c295eb8fc");
        paymentRequest.setAmount(order_price);
        paymentRequest.setDescription("پرداخت هزینه ثبت آگهی");
        paymentRequest.setCallbackURL("return://zarinpalpaymentPay");
        purchase.startPayment(paymentRequest, new OnCallbackRequestPaymentListener() {
            @Override
            public void onCallbackResultPaymentRequest(int status, String authority, Uri paymentGatewayUri, Intent intent) {
                if (status == 100) {
                    progressDialog.dismiss();
                    startActivity(intent);
                } else {
                    Toast.makeText(context, "خطا در پرداخت", Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    private void finalizeOrder() {
        progressDialog.show();
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
                                    //progressDialog.show();
                                    Ion.with(context)
                                            .load(CONST.FINALIZE_ORDER)
                                            .setHeader("Authorization", "Bearer " + token)
                                            .setBodyParameter("order_id", AppSharedPref.read("PAY_ID", "0"))
                                            .asString()
                                            .setCallback(new FutureCallback<String>() {
                                                @Override
                                                public void onCompleted(Exception e, String result) {
                                                    progressDialog.dismiss();
                                                    CONST.writeFile(result);
                                                    if (e == null) {
                                                        finishActivity(1);
                                                        JsonParser jsonParser = new JsonParser();
                                                        JsonObject root = (JsonObject) jsonParser.parse(result);
                                                        if (root.has("status")) {
                                                            if (root.get("status").getAsString().equals("ok")) {
                                                                finish();
                                                                Toasty.success(context, "آگهی با موفقیت ثبت شد و بعد از تایید منتشر خواهد شد", Toast.LENGTH_LONG).show();

                                                            } else {
                                                                Toasty.error(context, "خطا در برفراری ارتباط با سرور", Toast.LENGTH_LONG).show();
                                                            }
                                                        }
                                                    } else {
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


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri data = intent.getData();
        ZarinPal.getPurchase(this).verificationPayment(data, new OnCallbackVerificationPaymentListener() {
            @Override
            public void onCallbackResultVerificationPayment(boolean isPaymentSuccess, String refID, PaymentRequest paymentRequest) {
                if (isPaymentSuccess) {
                    finalizeOrder();

                } else {
                    Toasty.error(getApplicationContext(), "پرداخت شما با موفقیت صورت نگرفت.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    public void showAlert() {
        new SweetAlertDialog(InsertOrderActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("پرداخت موفق")
                .setContentText("آگهی شما با موفقیت ثبت شد.")
                .setConfirmText("برگشت")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        ActivityCompat.finishAffinity(InsertOrderActivity.this);
                        Intent i = new Intent(InsertOrderActivity.this, MainActivity.class);
                        startActivity(i);
                    }
                })
                .show();
    }


    @Subscribe
    public void openImagePicker(AppEvents.openImagePicker event) {
        openImagePickerIntent();
    }


    private void openImagePickerIntent() {
        imagePicker.setTitle("تصویر مورد نظر را انتخاب کنید.");
        imagePicker.setCropImage(true);
        startCameraOrGallery();

    }

    private void startCameraOrGallery() {
        final ImagePicker.Callback callback = new ImagePicker.Callback() {
            @Override
            public void onPickImage(Uri imageUri) {

            }

            @Override
            public void onCropImage(Uri imageUri) {
                AppEvents.onPickCrop id_event = new AppEvents.onPickCrop(imageUri);
                GlobalBus.getBus().post(id_event);
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
                        imagePicker.startGallery(InsertOrderActivity.this, callback);

                    }
                })
                .setNegativeButton("دوربین", new DialogSheet.OnNegativeClickListener() {
                    @Override
                    public void onClick(View v) {
                        imagePicker.startCamera(InsertOrderActivity.this, callback);

                    }
                })
                .setButtonsColorRes(R.color.colorPrimary)  // Default color is accent
                .show();


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imagePicker.onActivityResult(this, requestCode, resultCode, data);
        Log.d("APP_LOG", "data: " + data);
    }
}
