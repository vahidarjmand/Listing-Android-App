package tmediaa.ir.ahamdian;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.squareup.otto.Subscribe;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;
import com.zarinpal.ewallets.purchase.OnCallbackVerificationPaymentListener;
import com.zarinpal.ewallets.purchase.PaymentRequest;
import com.zarinpal.ewallets.purchase.ZarinPal;

import cn.pedant.SweetAlert.SweetAlertDialog;
import tmediaa.ir.ahamdian.adapters.InserOrderAdapter;
import tmediaa.ir.ahamdian.otto.AppEvents;
import tmediaa.ir.ahamdian.otto.GlobalBus;

public class InsertOrderActivity extends AppCompatActivity  implements StepperLayout.StepperListener{

    private StepperLayout stepperLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            setContentView(R.layout.activity_insert_order_above);
        }else{
            setContentView(R.layout.activity_insert_order);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.insert_order_title));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        stepperLayout = (StepperLayout) findViewById(R.id.stepperLayout);
        stepperLayout.setTabNavigationEnabled(true);
        stepperLayout.setAdapter(new InserOrderAdapter(getSupportFragmentManager(), this),0);
        stepperLayout.setShowErrorStateEnabled(true);
        stepperLayout.setShowErrorStateOnBackEnabled(true);
        stepperLayout.setListener(this);



        Uri data = getIntent().getData();
        ZarinPal.getPurchase(this).verificationPayment(data, new OnCallbackVerificationPaymentListener() {
            @Override
            public void onCallbackResultVerificationPayment(boolean isPaymentSuccess, String refID, PaymentRequest paymentRequest) {
                if (isPaymentSuccess) {
                    new SweetAlertDialog(InsertOrderActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("پرداخت موفق")
                            .setContentText("آگهی شما با موفقیت ثبت شد.")
                            .setConfirmText("برگشت")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    ActivityCompat.finishAffinity(InsertOrderActivity.this);
                                    Intent i = new Intent(InsertOrderActivity.this,MainActivity.class);
                                    startActivity(i);
                                }
                            })
                            .show();

                } else {
                    new SweetAlertDialog(InsertOrderActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("خطا در پرداخت")
                            .setContentText("اطلاعات پرداخت تایید نشد.")
                            .setConfirmText("تلاش دوباره")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    finish();
                                }
                            })
                            .show();
                }
            }
        });
    }


    @Subscribe
    public void getBack(AppEvents.BackStep events){
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
        if(newStepPosition == 0){
            stepperLayout.showBottomNavgiation();
        }else{

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
    public void closeActivity(final AppEvents.CloseActivity events) {
        /*Intent i = new Intent(InsertOrderActivity.this,MainActivity.class);
        startActivity(i);
        finish();*/

    }

}
