package tmediaa.ir.ahamdian.tools;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;

import com.karumi.dexter.PermissionToken;


public class PermissionCheckActivity extends AppCompatActivity {
    public void showPermissionGranted(String permission) {

    }

    public void showPermissionDenied(String permission, boolean isPermanentlyDenied) {

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void showPermissionRationale(final PermissionToken token) {

    }
}
