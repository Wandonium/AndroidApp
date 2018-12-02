package com.example.wando.daystarlaptops.ui;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.wando.daystarlaptops.R;
import com.example.wando.daystarlaptops.database.entity.Admin;
import com.example.wando.daystarlaptops.database.entity.Item;
import com.example.wando.daystarlaptops.database.entity.Owner;
import com.example.wando.daystarlaptops.viewmodel.LoginViewModel;
import com.example.wando.daystarlaptops.viewmodel.OwnerItemsViewModel;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class OwnerItemsActivity extends AppCompatActivity {

    private Button cmdCheckIn,cmdCheckOut;
    private TextView stdName, stdId;
    private OwnerItemsViewModel ownerItemsViewModel;
    private String owner_id, admin_id, location_id;
    private Owner owner;
    private List<Item> ownerItems;
    private List<String> item_IDs;
    private ListView listView;
    private Disposable disposable;

    private static final String TAG = OwnerItemsActivity.class.getName();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_items);

        admin_id = getIntent().getStringExtra("ADMIN_ID");
        location_id = getIntent().getStringExtra("LOCATION_ID");

        item_IDs = new ArrayList<>();

        // instantiate the Text Views
        stdName = findViewById(R.id.tv_std_name);
        stdId = findViewById(R.id.tv_std_id);

        // instantiate listview
        listView = findViewById(R.id.listContainer);

        // instantiate view model
        ownerItemsViewModel = ViewModelProviders.of(this).get(OwnerItemsViewModel.class);

        // get owner ID from previous activity
        owner_id = getIntent().getStringExtra("OWNER_ID");

        // get owner (student or staff member) from the db using their id
        owner = ownerItemsViewModel.getOwner(owner_id);

        // set owner details on ui
        stdName.setText(owner.getName());
        stdId.setText(owner_id);


        ownerItems = ownerItemsViewModel.getOwnerItems(owner_id);
        for(Item item: ownerItems) {
            item_IDs.add(item.getId());
        }
        OwnerItemsAdapter ownerItemsAdapter = new OwnerItemsAdapter();
        listView.setAdapter(ownerItemsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Item item = ownerItems.get(position);
                Snackbar.make(view, "Owner ID: " + item.getOwner_id() +"\n"+"Item Manufacturer: "+ item.getMake(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
            }
        });

        // instantiate the buttons
        cmdCheckIn=findViewById(R.id.cmdCheckIn);
        cmdCheckOut=findViewById(R.id.cmdCheckOut);

        cmdCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[][] theId_s = new String[ownerItems.size()][3];
                for(int i = 0; i < ownerItems.size(); i++)
                {
                    Item item = ownerItems.get(i);
                    theId_s[i][0] = admin_id;
                    theId_s[i][1] = item.getId();
                    theId_s[i][2] = owner_id;

                }
                Intent replyIntent=new Intent (v.getContext(),CheckInActivity.class);
                if (theId_s.length == 0) {
                    setResult(RESULT_CANCELED, replyIntent);
                } else {
                    Bundle mBundle = new Bundle();
                    mBundle.putSerializable("CHECKIN_TABLE_FOREIGN_KEYS", theId_s);
                    replyIntent.putExtras(mBundle);
                    replyIntent.putExtra("CHECK_ACTIVITY", "check_in");
                    replyIntent.putExtra("LOCATION_ID", location_id);
                    setResult(RESULT_OK, replyIntent);
                }
                v.getContext().startActivity(replyIntent);
            }
        });

        cmdCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent (v.getContext(),CheckInActivity.class);
                intent.putExtra("OWNER_ID", owner_id);
                intent.putExtra("CHECK_ACTIVITY", "check_out");
                intent.putExtra("LOCATION_ID", location_id);
                v.getContext().startActivity(intent);
            }
        });

    }

    class OwnerItemsAdapter extends BaseAdapter {

        private int lastPosition = -1;

        @Override
        public int getCount() {
            return ownerItems.size();
        }

        @Override
        public Item getItem(int position) {
            return ownerItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Item myItem = getItem(position);
            convertView = getLayoutInflater().inflate(R.layout.row_item, null);

            TextView itemType = convertView.findViewById(R.id.tv_item_type);
            TextView serialNo = convertView.findViewById(R.id.tv_item_serial_no);
            ImageView info = convertView.findViewById(R.id.item_info);

            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
            final View result = convertView;
            result.startAnimation(animation);
            lastPosition = position;

            itemType.setText(myItem.getItem());
            serialNo.setText(myItem.getSerial_number());
            info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String date = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.MEDIUM).format(myItem.getReg_datetime());
                    Snackbar.make(v, "Item ID: " +myItem.getId()
                            + "\nItem was registerd on: " + date, Snackbar.LENGTH_LONG)
                            .setAction("No action", null).show();
                }
            });
            info.setTag(position);

            // Return the completed view to render on screen
            return convertView;
        }
    }
}
