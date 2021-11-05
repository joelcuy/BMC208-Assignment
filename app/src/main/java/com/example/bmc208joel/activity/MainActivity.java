package com.example.bmc208joel.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.bmc208joel.R;
import com.example.bmc208joel.adapter.CustomBatchListViewAdapter;
import com.example.bmc208joel.objects.BatchClass;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


//Activity to view all batch
public class MainActivity extends AppCompatActivity {
    //View components
    ListView listViewBatchClass;

    //Other variables
    ArrayList<BatchClass> batchClassArrayList; //store list of batches
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ListAdapter listAdapter;
    private ListenerRegistration listenerRegistration; // for live listener


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getCurrentAdminBatch();// set and display current user's batches (based on health centre)

        //select batch to view its details (start BatchDetailsActivity)
        listViewBatchClass = findViewById(R.id.listView_allBatch_batchList);
        listViewBatchClass.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, BatchDetailsActivity.class);
                BatchClass batchClass = batchClassArrayList.get(position);
                intent.putExtra("batchClass", batchClass);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Detach live listener, to stop listening for data changes when activity stops
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        getCurrentAdminBatch(); //start listening for live data changes again
    }

    //TODO fetch batch based on centre name (centre comes from userID)
    //Currently fetching all batch without filtering
    private void getCurrentAdminBatch() {
        listenerRegistration = db.collection("Batches")
                //                .whereEqualTo("centreName", currentUser.getCenter whatever) *set filter here*
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }
                        batchClassArrayList = new ArrayList<>();;
                        for (QueryDocumentSnapshot doc : value) {
                            BatchClass batch = doc.toObject(BatchClass.class);
                            batchClassArrayList.add(batch);
                        }

                        listAdapter = new CustomBatchListViewAdapter(MainActivity.this, batchClassArrayList);
                        listViewBatchClass.setAdapter(listAdapter);
                    }
                });
    }
}