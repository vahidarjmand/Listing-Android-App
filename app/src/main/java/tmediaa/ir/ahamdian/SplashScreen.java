package tmediaa.ir.ahamdian;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import tmediaa.ir.ahamdian.tools.AppSharedPref;
import tmediaa.ir.ahamdian.tools.CONST;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        AppSharedPref.init(this);
        if (AppSharedPref.check("allow_splash") && AppSharedPref.read("allow_splash", "").equals("true")) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                    Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                }
            }, CONST.SPLASH_TIME);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                    Intent mainIntent = new Intent(SplashScreen.this, RegisterActivity.class);
                    startActivity(mainIntent);
                    finish();
                }
            }, CONST.SPLASH_TIME);
        }
    }
}
