package tmediaa.ir.ahamdian.adapters;

import android.content.Context;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.stepstone.stepper.Step;
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter;
import com.stepstone.stepper.viewmodel.StepViewModel;

import tmediaa.ir.ahamdian.insertorders.InsertOrderStep1;
import tmediaa.ir.ahamdian.insertorders.InsertOrderStep2;

/**
 * Created by tmediaa on 8/20/2017.
 */

public class InserOrderAdapter extends AbstractFragmentStepAdapter {
    public InserOrderAdapter(FragmentManager fm, Context context) {
        super(fm, context);
    }

    @Override
    public Step createStep(int position) {

        Fragment step = null;
        switch (position) {
            case 0:
                step = new InsertOrderStep1();
                break;
            case 1:
                step = new InsertOrderStep2();
                break;
            case 2:
                break;

        }
        return (Step) step;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @NonNull
    @Override
    public StepViewModel getViewModel(@IntRange(from = 0) int position) {
        //Override this method to set Step title for the Tabs, not necessary for other stepper types
        StepViewModel.Builder builder = new StepViewModel.Builder(context)
                .setTitle("tab tab 1");
        switch (position) {
            case 0:
                builder
                        .setBackButtonLabel("انصراف")
                        .setBackButtonVisible(false)
                        .setEndButtonLabel("ثبت آگهی")
                        .setTitle("مشخصات آگهی");
                break;
            case 1:
                builder.setEndButtonLabel("مدیریت آگهی")
                        .setBackButtonLabel("ویرایش آگهی")
                        .setTitle("مدیریت آگهی")
                        .setBackButtonVisible(false)
                        .setEndButtonVisible(false);
                break;

        }
        return builder.create();


    }
}
