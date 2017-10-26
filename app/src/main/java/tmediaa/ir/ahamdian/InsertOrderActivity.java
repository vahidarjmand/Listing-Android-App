package tmediaa.ir.ahamdian;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.squareup.otto.Subscribe;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import tmediaa.ir.ahamdian.adapters.InserOrderAdapter;
import tmediaa.ir.ahamdian.otto.AppEvents;

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

        //GlobalBus.getInstanse().register(this);
    }

    @Override
    protected void onStop() {
       // GlobalBus.getInstanse().unregister(this);
        super.onStop();
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
    public void onAppEvent(AppEvents events){
        //Log.d("APPLOG","Otto Send: " + events.section);
    }

}
