package tmediaa.ir.ahamdian.myorders;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import tmediaa.ir.ahamdian.R;
import tmediaa.ir.ahamdian.otto.GlobalBus;

public class EditActivity extends AppCompatActivity {
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("ویرایش آگهی");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }



    @Override
    protected void onStart() {
        super.onStart();
        GlobalBus.getBus().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        GlobalBus.getBus().unregister(this);
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
}
