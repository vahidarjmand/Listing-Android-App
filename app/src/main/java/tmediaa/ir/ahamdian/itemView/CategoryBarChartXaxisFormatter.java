package tmediaa.ir.ahamdian.itemView;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

/**
 * Created by tmediaa on 12/8/2017.
 */

public class CategoryBarChartXaxisFormatter implements IAxisValueFormatter {
    private String[] mValues;

    public CategoryBarChartXaxisFormatter(String[] values) {
        this.mValues = values;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        // "value" represents the position of the label on the axis (x or y)
        int val = (int) value;
        String label = "";
        if (val >= 0 && val < mValues.length) {
            label = mValues[val];
        } else {
            label = "";
        }
        return label;
    }
}
