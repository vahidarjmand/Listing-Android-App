package tmediaa.ir.ahamdian.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;

import tmediaa.ir.ahamdian.R;
import tmediaa.ir.ahamdian.model.CategoryItem;
import tmediaa.ir.ahamdian.otto.AppEvents;
import tmediaa.ir.ahamdian.otto.GlobalBus;

/**
 * Created by tmediaa on 11/29/2017.
 */

class FillChildTools {
    private Context _context;
    private FlexboxLayout _root;
    private ArrayList<CategoryItem> _list;

    public FillChildTools(Context context, FlexboxLayout root, ArrayList<CategoryItem> list) {
        _context = context;
        _root = root;
        _list = list;

        fill(_root, _list);
    }

    public void updateList(ArrayList<CategoryItem> list) {
        _list = list;
        fill(_root, _list);
    }

    private void fill(FlexboxLayout root, ArrayList<CategoryItem> list) {
        if (root.getChildCount() > 0) {
            root.removeAllViews();
        }

        for (int i = 0; i < list.size(); i++) {
            final CategoryItem item = list.get(i);
            final Button btn = new Button(_context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(5, 5, 5, 5);
            btn.setLayoutParams(params);

            if (item.getChilds().size() > 0) {
                Drawable img = _context.getResources().getDrawable(R.drawable.add_new_item_icon);
                img.setBounds(0, 0, 45, 45);
                btn.setCompoundDrawables(img, null, null, null);
                btn.setCompoundDrawablePadding(5);
            }

            btn.setIncludeFontPadding(true);
            btn.setText(item.getName());
            btn.setTag(R.id.category_list, item.getChilds());
            btn.setTag(R.id._id, item.getId());
            btn.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);
            btn.setBackgroundResource(R.drawable.category_button_bg);
            btn.setTextColor(Color.parseColor("#ffffff"));


            root.addView(btn);

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<CategoryItem> list = (ArrayList<CategoryItem>) btn.getTag(R.id.category_list);
                    AppEvents.ChangePager id_event = new AppEvents.ChangePager((int) btn.getTag(R.id._id), list);
                    GlobalBus.getBus().post(id_event);
                }
            });
        }
    }


}
