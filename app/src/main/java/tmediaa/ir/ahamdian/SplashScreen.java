package tmediaa.ir.ahamdian;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import tmediaa.ir.ahamdian.tools.AppSharedPref;
import tmediaa.ir.ahamdian.tools.CONST;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        AppSharedPref.init(this);


        Ion.with(this)
                .load(CONST.GET_SETS)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if(e == null){
                            JsonParser parser = new JsonParser();
                            JsonObject root = parser.parse(result).getAsJsonObject();
                            if(!root.isJsonNull()){
                                JsonObject sets;
                                if(!root.get("settings").isJsonNull()){;
                                    sets = root.get("settings").getAsJsonObject();
                                    if(!sets.get("order_price").isJsonNull()){
                                        String order_price = sets.get("order_price").getAsString();
                                        AppSharedPref.write("order_price",order_price);
                                    }
                                    if(!sets.get("renew_price").isJsonNull()){
                                        String renew_price = sets.get("renew_price").getAsString();
                                        AppSharedPref.write("renew_price",renew_price);
                                    }
                                    if(!sets.get("upgrade_price").isJsonNull()){
                                        String upgrade_price = sets.get("upgrade_price").getAsString();
                                        AppSharedPref.write("upgrade_price",upgrade_price);
                                    }
                                }

                            }
                        }
                    }
                });

        /*if(AppSharedPref.read("online", 0) == 1){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                }
            }, CONST.SPLASH_TIME);
        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent mainIntent = new Intent(SplashScreen.this, RegisterActivity.class);
                    startActivity(mainIntent);
                    finish();
                }
            }, CONST.SPLASH_TIME);
        }*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        }, CONST.SPLASH_TIME);

    }
}
