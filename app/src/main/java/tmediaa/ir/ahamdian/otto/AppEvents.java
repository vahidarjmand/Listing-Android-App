package tmediaa.ir.ahamdian.otto;

import android.app.Activity;

import java.util.ArrayList;
import java.util.Objects;

import tmediaa.ir.ahamdian.model.CategoryItem;

/**
 * Created by tmediaa on 8/20/2017.
 */

public class AppEvents {
    public static class sendOrderID {

        private int id;

        public sendOrderID(int id) {
            this.id = id;
        }

        public int getId() {
            return this.id;
        }
    }

    public static class BackStep {
        private int order_id;

        public BackStep(int id) {
            order_id = id;
        }

        public int getOrderID() {
            return order_id;
        }
    }

    public static class DialogCategoryChose {
        private String _name;
        private int _id;
        private Objects obj;

        public DialogCategoryChose(String name, int id) {
            _id = id;
            _name = name;
        }

        public int getTargetChosseID() {
            return _id;
        }

        public String getTargetChosseName() {
            return _name;
        }
    }

    public static class UpdateLocation {
        private int city_id;

        public UpdateLocation(int id) {
            city_id = id;
        }

        public int getCityID() {
            return city_id;
        }
    }


    public static class CloseActivity {
        private Activity _activity;

        public CloseActivity(Activity activity) {
            _activity = activity;
        }

        public Activity closeActivity() {
            return _activity;
        }
    }

    public static class SpaceNavClick {
        private int _id;

        public SpaceNavClick(int id) {
            _id = id;
        }

        public int onSpaceClick() {
            return _id;
        }
    }

    public static class ChangePager {
        private int _id;
        private ArrayList<CategoryItem> _list;

        public ChangePager(int id, ArrayList<CategoryItem> list) {
            _list = list;
            _id = id;
        }

        public ArrayList<CategoryItem> getChangeList() {
            return _list;
        }

        public int getID() {
            return _id;
        }
    }

    //for update bottom toolabr to bring front
    public static class ChangeToolbarOrder {

        public ChangeToolbarOrder() {

        }
    }

    //for update bottom toolabr to bring front
    public static class PayOrder {
        private int _id;
        public PayOrder(int id) {
            _id = id;
        }

        public int getOrderID(){
            return  _id;
        }
    }


}
