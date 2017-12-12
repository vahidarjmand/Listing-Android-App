package tmediaa.ir.ahamdian.itemView;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

/**
 * Created by tmediaa on 12/7/2017.
 */

public class YourXValueFormatter implements IAxisValueFormatter {
    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        float[] xAxis = axis.mEntries;
        //Log.d(CONST.APP_LOG,"label: " + xAxis[0]);
        /*if (value == xAxis[whichLabel]) {
            do your thing...
        }*/
        return "ss";
    }


}