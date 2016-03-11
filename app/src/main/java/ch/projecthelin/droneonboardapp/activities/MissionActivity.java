package ch.projecthelin.droneonboardapp.activities;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import ch.projecthelin.droneonboardapp.R;
import ch.projecthelin.droneonboardapp.adapters.MissionProductAdapter;
import ch.projecthelin.droneonboardapp.dto.Mission;
import ch.projecthelin.droneonboardapp.dto.MissionProduct;
import ch.projecthelin.droneonboardapp.dto.Product;

import java.util.ArrayList;

public class MissionActivity extends AppCompatActivity {

    private ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        createListView();

    }

    private void createListView() {
        listview = (ListView) findViewById(R.id.list);

        getMissionProducts();
        MissionProductAdapter adapter = new MissionProductAdapter(this, getMissionProducts());
        listview.setAdapter(adapter);
        setOnclickListenerForListView();
    }

    private ArrayList<MissionProduct> getMissionProducts() {
        ArrayList<MissionProduct> list = new ArrayList<>();

        Mission mission = new Mission();

        Product product1 = new Product();
        product1.setName("Coca Cola 0.5L");
        product1.setWeight(500);

        list.add(new MissionProduct(mission, product1, 5));

        Product product2 = new Product();
        product2.setName("Kugelschreiber");
        product2.setWeight(10);
        list.add(new MissionProduct(mission, product2, 5));

        return list;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setOnclickListenerForListView() {
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(getApplicationContext(),
                        "Click ListItem Number " + position, Toast.LENGTH_LONG)
                        .show();
            }
        });

    }


}
