package tmediaa.ir.ahamdian;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;
import com.luseen.spacenavigation.SpaceOnLongClickListener;

import tmediaa.ir.ahamdian.tools.CONST;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;


public class MainActivity extends AppCompatActivity {
    private SpaceNavigationView spaceNavigationView;

    private int mSelectedItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spaceNavigationView = (SpaceNavigationView) findViewById(R.id.space);
        spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.search_icon));
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.profile_icon));
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.category_icon));
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.all_item_icon));
        spaceNavigationView.setInActiveCentreButtonIconColor(Color.WHITE);

        spaceNavigationView.changeCurrentItem(2);
        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                Intent i = new Intent(MainActivity.this,InsertOrderActivity.class);
                startActivity(i);
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                Log.d(CONST.APP_LOG,"Name: "+ itemName + " -- " + itemIndex);
                selectFragment(itemIndex);
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
                Toast.makeText(MainActivity.this, itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();
            }
        });

        spaceNavigationView.setSpaceOnLongClickListener(new SpaceOnLongClickListener() {
            @Override
            public void onCentreButtonLongClick() {
                Toast.makeText(MainActivity.this, "onCentreButtonLongClick", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(int itemIndex, String itemName) {
                Toast.makeText(MainActivity.this, itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();
            }
        });


        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;


        selectFragment(2);
        if(CONST.IS_HELP){
            new MaterialTapTargetPrompt.Builder(MainActivity.this, 0)
                    .setTarget((width / 2) + 5f, height - 90f)
                    .setPrimaryText("افزودن آگهی شما")
                    .setIcon(R.drawable.add_new_item_icon)
                    .setSecondaryText("با انتخاب این گزینه میتوانید آگهی جدید خود را اضافه کنید.")
                    .setAnimationInterpolator(new FastOutSlowInInterpolator())
                    .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
                        @Override
                        public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state) {
                            if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED) {

                            }
                        }
                    })
                    .show();
        }
    }


    private void selectFragment(int item) {
        Fragment selectedFragment = null;
        switch (item) {
            case 0:
                selectedFragment = Search_Fragment.newInstance();
                break;
            case 1:
                selectedFragment = Profile_Fragment.newInstance();
                break;
            case 2:
                selectedFragment = All_Item_Fragment.newInstance();
                break;
            case 3:
                selectedFragment = Category_Fragment.newInstance();
                break;
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, selectedFragment);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.map_action:
                Toast.makeText(this, "Refresh selected", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        spaceNavigationView.onSaveInstanceState(outState);
    }
}