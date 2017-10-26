package tmediaa.ir.ahamdian.insertorders;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import java.util.ArrayList;
import java.util.List;

import app.dinus.com.loadingdrawable.LoadingView;
import es.dmoral.toasty.Toasty;
import eu.fiskur.simpleviewpager.ImageURLLoader;
import eu.fiskur.simpleviewpager.SimpleViewPager;
import tmediaa.ir.ahamdian.R;
import tmediaa.ir.ahamdian.otto.GlobalBus;
import tmediaa.ir.ahamdian.tools.CONST;

import static android.R.attr.id;

/**
 * Created by tmediaa on 8/20/2017.
 */

public class InsertOrderStep2 extends Fragment implements BlockingStep {
    private int TAP_THRESHOLD = 2;

    Button final_post_btn, order_edit_btn, order_delete_btn, order_pay_btn;
    LoadingView gallery_loading;
    SimpleViewPager image_pager;
    LinearLayout order_insert_info, order_btn, slider_view, all_field_view, general_field, category_container, general_price_view, general_type_view, amlak_field, amlak_metraj_view, amlak_type_view, amlak_room_view, amlak_ejareh_view,
            amlak_homeshahr_view, amlak_vadie_view, amlak_sanad_view, naghliye_field, naghliye_brand_view, naghliye_sal_view, naghliye_kardkard_view;
    TextView item_title, item_desc, item_category, general_price_view_tv, general_type_view_tv, amlak_metraj_view_tv, amlak_type_view_tv, amlak_room_view_tv,
            amlak_homeshahr_view_tv, amlak_vadie_view_tv, amlak_ejareh_view_tv, amlak_sanad_view_tv, naghliye_brand_view_tv, naghliye_kardkard_view_tv;

    private View rootView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.insert_order_step_2, container, false);

        initViews();
        //sendData = (Button) v.findViewById(R.id.sendData);
        /*sendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlobalBus.getInstanse().post(new AppEvents("salam"));
            }
        });*/
        //initialize your UI

        return rootView;
    }

    private void initViews() {
        order_insert_info = (LinearLayout) rootView.findViewById(R.id.order_insert_info);
        order_btn = (LinearLayout) rootView.findViewById(R.id.order_btn);
        slider_view = (LinearLayout) rootView.findViewById(R.id.slider_view);
        all_field_view = (LinearLayout) rootView.findViewById(R.id.all_field_view);
        general_field = (LinearLayout) rootView.findViewById(R.id.general_field);
        category_container = (LinearLayout) rootView.findViewById(R.id.category_container);
        general_price_view = (LinearLayout) rootView.findViewById(R.id.general_price_view);
        general_type_view = (LinearLayout) rootView.findViewById(R.id.general_type_view);
        amlak_field = (LinearLayout) rootView.findViewById(R.id.amlak_field);
        amlak_metraj_view = (LinearLayout) rootView.findViewById(R.id.amlak_metraj_view);
        amlak_type_view = (LinearLayout) rootView.findViewById(R.id.amlak_type_view);
        amlak_room_view = (LinearLayout) rootView.findViewById(R.id.amlak_room_view);
        amlak_ejareh_view = (LinearLayout) rootView.findViewById(R.id.amlak_ejareh_view);
        amlak_homeshahr_view = (LinearLayout) rootView.findViewById(R.id.amlak_homeshahr_view);
        amlak_vadie_view = (LinearLayout) rootView.findViewById(R.id.amlak_vadie_view);
        amlak_sanad_view = (LinearLayout) rootView.findViewById(R.id.amlak_sanad_view);
        naghliye_field = (LinearLayout) rootView.findViewById(R.id.naghliye_field);
        naghliye_brand_view = (LinearLayout) rootView.findViewById(R.id.naghliye_brand_view);
        naghliye_sal_view = (LinearLayout) rootView.findViewById(R.id.naghliye_sal_view);
        naghliye_kardkard_view = (LinearLayout) rootView.findViewById(R.id.naghliye_kardkard_view);


        item_title = (TextView) rootView.findViewById(R.id.item_title);
        item_desc = (TextView) rootView.findViewById(R.id.item_desc);
        item_category = (TextView) rootView.findViewById(R.id.item_category);
        general_price_view_tv = (TextView) rootView.findViewById(R.id.general_price_view_tv);
        general_type_view_tv = (TextView) rootView.findViewById(R.id.general_type_view_tv);
        amlak_metraj_view_tv = (TextView) rootView.findViewById(R.id.amlak_metraj_view_tv);
        amlak_type_view_tv = (TextView) rootView.findViewById(R.id.amlak_type_view_tv);
        amlak_room_view_tv = (TextView) rootView.findViewById(R.id.amlak_room_view_tv);
        amlak_homeshahr_view_tv = (TextView) rootView.findViewById(R.id.amlak_homeshahr_view_tv);
        amlak_vadie_view_tv = (TextView) rootView.findViewById(R.id.amlak_vadie_view_tv);
        amlak_ejareh_view_tv = (TextView) rootView.findViewById(R.id.amlak_ejareh_view_tv);
        amlak_sanad_view_tv = (TextView) rootView.findViewById(R.id.amlak_sanad_view_tv);
        naghliye_brand_view_tv = (TextView) rootView.findViewById(R.id.naghliye_brand_view_tv);
        naghliye_kardkard_view_tv = (TextView) rootView.findViewById(R.id.naghliye_kardkard_view_tv);

        final_post_btn = (Button) rootView.findViewById(R.id.final_post_btn);
        order_edit_btn = (Button) rootView.findViewById(R.id.order_edit_btn);
        order_delete_btn = (Button) rootView.findViewById(R.id.order_delete_btn);
        order_pay_btn = (Button) rootView.findViewById(R.id.order_pay_btn);
        order_pay_btn.setVisibility(View.GONE);

        gallery_loading = (LoadingView) rootView.findViewById(R.id.gallery_loading);
        image_pager = (SimpleViewPager) rootView.findViewById(R.id.image_pager);

        getItemOrder();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        GlobalBus.getBus().register(this);
    }

    @Override
    public void onDetach() {
        GlobalBus.getBus().unregister(this);
        super.onDetach();
    }

    //Subscribe
    //public void getId(AppEvents.sendOrderID events) {
    public void getItemOrder() {

        Ion.with(getContext())
                .load(CONST.GET_ORDER)
                //.setBodyParameter("order_id", String.valueOf(events.getId()))
                .setBodyParameter("order_id", "8")
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (e == null) {
                            JsonParser parser = new JsonParser();
                            JsonObject obj = parser.parse(result).getAsJsonObject();

                            if (obj.has("status")) {
                                String status = obj.get("status").getAsString();


                                if (status.equals("found")) {
                                    int cat_id = obj.get("order").getAsJsonObject().get("cat_id").getAsInt();
                                    showOrderView(cat_id, obj);
                                }
                            }
                        } else {
                            Toasty.error(getContext(), "خطا در برقراری ارتباط با سرور", Toast.LENGTH_LONG).show();
                        }


                    }
                });
    }

    private void showOrderView(int cat_id, JsonObject obj) {
        JsonObject order = obj.get("order").getAsJsonObject();
        String title = order.get("title").getAsString();
        String desc = order.get("desc").getAsString();
        String email = order.get("email").isJsonNull() ? "" : order.get("email").getAsString();
        String tel = order.get("title").getAsString();
        String cat_name = obj.get("cat_name").getAsString();
        JsonArray attachments = obj.get("attachments").getAsJsonArray();

        item_title.setText(title);
        item_desc.setText(desc);
        item_category.setText(cat_name);
        showSliderView(attachments);

        switch (cat_id) {
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
                showAmlakForm(id, order);
                disableNaghliyeField();
                break;

            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
                showNaghliyeForm(id, order);
                disableAmlakField();
                break;
            case 104:
            case 105:
            case 106:
            case 107:
            case 108:
            case 109:
            case 110:
            case 111:
                showPezeshkForm(id);
                disableAmlakField();
                disableNaghliyeField();
                break;
            default:
                showGeneralForm(id, order);
                disableAmlakField();
                disableNaghliyeField();
                break;


        }


    }

    private void showGeneralForm(int id, JsonObject order) {
        general_field.setVisibility(View.VISIBLE);
        showGeneralPrice(order);
        showGeneralType(order);
    }

    private void showAmlakForm(int id, JsonObject order) {

    }

    private void showNaghliyeForm(int id, JsonObject order) {

    }

    private void showPezeshkForm(int id) {
        order_pay_btn.setVisibility(View.VISIBLE);
        final_post_btn.setVisibility(View.GONE);
        all_field_view.setVisibility(View.GONE);
    }

    private void disableAmlakField() {
        amlak_field.setVisibility(View.GONE);
    }

    private void disableNaghliyeField() {
        naghliye_field.setVisibility(View.GONE);
    }

    private void showGeneralPrice(JsonObject order){
        int price_type = order.get("price_type").getAsInt();

        switch (price_type) {
            case 0:
                general_price_view_tv.setText(CONST.formatPriceInt(order.get("price").getAsInt()) + " تومان ");
                general_price_view.setVisibility(View.VISIBLE);
                break;
            case 1:
                general_price_view_tv.setText("رایگان");
                general_price_view.setVisibility(View.GONE);
                break;
            case 2:
                general_price_view_tv.setText("جهت معاوضه");
                general_price_view.setVisibility(View.GONE);
                break;
            case 3:
                general_price_view_tv.setText("توافقی");
                general_price_view.setVisibility(View.GONE);
                break;
        }

    }
    private void showGeneralType(JsonObject order){
        int general_type = order.get("general_type").getAsInt();
        switch (general_type){
            case 0:
                general_type_view_tv.setText("فروشی");
                break;
            case 1:
                general_type_view_tv.setText("درخواستی");
                break;
        }

    }
    private void showSliderView(JsonArray images) {
        if (images.size() > 0) {
            List<String> image_urls = new ArrayList<>();
            for (int i = 0; i < images.size(); i++) {
                image_urls.add(CONST.STORAGE + images.get(i).getAsString());
            }
            gallery_loading.setVisibility(View.GONE);
            image_pager.setVisibility(View.VISIBLE);

            image_pager.setImageUrls(image_urls, new ImageURLLoader() {
                @Override
                public void loadImage(ImageView view, String url) {
                    Glide.with(getContext()).load(url).listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String s, Target<GlideDrawable> target, boolean b) {
                            return true;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable glideDrawable, String s, Target<GlideDrawable> target, boolean b, boolean b1) {

                            return false;
                        }
                    }).into(view);


                }
            });


            int indicatorColor = Color.parseColor("#ffffff");
            int selectedIndicatorColor = Color.parseColor("#c1c1c1");
            image_pager.showIndicator(indicatorColor, selectedIndicatorColor);
        } else {
            slider_view.setVisibility(View.GONE);
        }
    }


    @Override
    public VerificationError verifyStep() {
        //return null if the user can go to the next step, create a new VerificationError instance otherwise
        return null;
    }

    @Override
    public void onSelected() {
        //update UI when selected


    }

    @Override
    public void onError(@NonNull VerificationError error) {
        //handle error inside of the fragment, e.g. show error on EditText
    }


    @Override
    public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {

    }

    @Override
    public void onCompleteClicked(final StepperLayout.OnCompleteClickedCallback callback) {


    }

    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {
        Log.d(CONST.APP_LOG, "go back");
    }


}