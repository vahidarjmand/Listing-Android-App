package tmediaa.ir.ahamdian.myorders;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.santalu.emptyview.EmptyView;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import tmediaa.ir.ahamdian.LoginActivity;
import tmediaa.ir.ahamdian.R;
import tmediaa.ir.ahamdian.adapters.AllItemAdapter;
import tmediaa.ir.ahamdian.itemView.ShowOrder;
import tmediaa.ir.ahamdian.model.OrderItem;
import tmediaa.ir.ahamdian.tools.AppSharedPref;
import tmediaa.ir.ahamdian.tools.CONST;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MyOrdersActivity extends AppCompatActivity {

    private static final int OPEN_ORDER_CODE = 547;
    private EmptyView error_view;
    private Context context;
    private ProgressDialog progressDialog;
    private XRecyclerView mRecyclerView;
    private Drawable dividerDrawable;
    private ArrayList<OrderItem> listData = new ArrayList<>();
    private AllItemAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("آگهی های من");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("در حال بارگذاری");
        progressDialog.setCancelable(false);


        mRecyclerView = (XRecyclerView) findViewById(R.id.orders_recyclerview);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        //GridLayoutManager layoutManager = new GridLayoutManager(context,2);

        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dividerDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.item_row_drawable, null);
        } else {
            dividerDrawable = getResources().getDrawable(R.drawable.item_row_drawable);
        }

        mRecyclerView.addItemDecoration(mRecyclerView.new DividerItemDecoration(dividerDrawable));

        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallBeat);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallBeat);

        mAdapter = new AllItemAdapter(context, listData);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setPullRefreshEnabled(false);

        mAdapter.setOnItemClickListener(new AllItemAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, OrderItem data) {
                Intent i =new Intent(MyOrdersActivity.this, ShowOrder.class);
                i.putExtra("id",data.getId());
                i.putExtra("mode",true);
                startActivityForResult(i, OPEN_ORDER_CODE);

            }
        });

        error_view = (EmptyView) findViewById(R.id.error_view);
        error_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(error_view.getState() == EmptyView.State.ERROR){
                    Intent i = new Intent(context, LoginActivity.class);
                    startActivity(i);
                }


            }
        });
        if (AppSharedPref.read("online", 0) == 1) {
            loadOrders();
        } else {
            error_view.showError("جهت مشاهده این قسمت باید وارد نرم افزار شوید.");

        }

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(getString(R.string.fontpath))
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

    private void loadOrders() {

        String app_token = AppSharedPref.read("TOKEN", "");
        byte[] data = Base64.decode(app_token, Base64.DEFAULT);
        progressDialog.show();

        try {
            String user_pass = new String(data, "UTF-8");
            Ion.with(context)
                    .load(CONST.APP_TOKEN)
                    .setBodyParameter("username", user_pass)
                    .setBodyParameter("password", user_pass.split("_")[0])
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            if (e == null) {
                                final JsonParser parser = new JsonParser();
                                JsonObject json_obj = parser.parse(result).getAsJsonObject();
                                if (json_obj.has("token")) {
                                    String token = json_obj.get("token").getAsString();

                                    String user_id = AppSharedPref.read("ID", "0");
                                    Ion.with(context)
                                            .load(CONST.MY_ORDERS)
                                            .setHeader("Authorization", "Bearer " + token)
                                            .setBodyParameter("target_user", user_id)
                                            .asString()
                                            .setCallback(new FutureCallback<String>() {
                                                @Override
                                                public void onCompleted(Exception e, String result) {
                                                    progressDialog.dismiss();
                                                    if (e == null) {
                                                        JsonParser parser1 = new JsonParser();
                                                        JsonObject root = parser1.parse(result).getAsJsonObject();
                                                        JsonArray items = root.get("items").getAsJsonObject().get("data").getAsJsonArray();

                                                        listData.clear();

                                                        if (items.size() > 0) {
                                                            for (int i = 0; i < items.size(); i++) {
                                                                JsonObject item = items.get(i).getAsJsonObject();
                                                                OrderItem orderItem = new OrderItem();
                                                                orderItem.setId(item.get("id").getAsInt());
                                                                orderItem.setCat_name(item.get("catname").getAsString());
                                                                orderItem.setTitle(item.get("title").getAsString());
                                                                orderItem.setDesc(item.get("desc").getAsString());
                                                                orderItem.setOwn_mode(true);
                                                                orderItem.setStatus(item.get("status").getAsString());
                                                                orderItem.setAttachments(item.get("attachments").getAsJsonArray());
                                                                orderItem.setDate(item.get("updated_at").getAsString());
                                                                listData.add(orderItem);
                                                            }
                                                            error_view.showContent();
                                                            mAdapter.notifyDataSetChanged();

                                                        } else {
                                                            error_view.showEmpty();
                                                        }

                                                    } else {
                                                        Toasty.error(context, "خطا در برفراری ارتباط با سرور", Toast.LENGTH_LONG).show();
                                                    }

                                                }
                                            });
                                }
                            } else {
                                progressDialog.dismiss();
                                Toasty.error(context, "خطا در برفراری ارتباط با سرور", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == OPEN_ORDER_CODE) {
            loadOrders();
            if (resultCode == RESULT_OK) {
                /*Log.d(CONST.APP_LOG,"requestCode 3");
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    Log.d(CONST.APP_LOG,"found bndle ");
                    if(bundle.getBoolean("update")){
                        loadOrders();
                    }
                }
*/
            }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
