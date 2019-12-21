package tmediaa.ir.ahamdian;

import android.app.Application;
import android.content.res.Configuration;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import java.util.Locale;

import tmediaa.ir.ahamdian.tools.LocaleUtils;

/**
 * Created by tmediaa on 12/15/2017.
 */
@ReportsCrashes(formUri = "http://mbaas.ir/api/acra/b071f91")
public class Pelak extends Application {
    @Override
    public void onCreate() {
        // The following line triggers the initialization of ACRA
        super.onCreate();
        ACRA.init(this);  LocaleUtils.setLocale(new Locale("fa"));
        LocaleUtils.updateConfig(this, getBaseContext().getResources().getConfiguration());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleUtils.updateConfig(this, newConfig);
    }
}