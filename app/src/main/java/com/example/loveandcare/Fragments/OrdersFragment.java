package com.example.loveandcare.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.loveandcare.Adapters.OrdersAdapter;
import com.example.loveandcare.Items.OrdersDemo;
import com.example.loveandcare.Activities.OrderDetails;
import com.example.loveandcare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.*;


/**
 * A simple {@link Fragment} subclass.
 */
public class OrdersFragment extends Fragment {

    public FirebaseFirestore firebaseFirestore;
    ArrayList<OrdersDemo> orderProducts;
    OrdersAdapter adapter;
    ListView ordersListView;
    FirebaseAuth mAuth;
    ImageView noOrders;
    int count=0;

    public OrdersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        orderProducts = new ArrayList<>();
        adapter = new OrdersAdapter(getContext(), orderProducts);
        ordersListView = (ListView) view.findViewById(R.id.ordersListView);
        ordersListView.setAdapter(adapter);

        String user_id = mAuth.getUid();
        noOrders = (ImageView) view.findViewById(R.id.image);
        Query query = firebaseFirestore.collection("Customer").document("Orders").collection(user_id).orderBy("time", Query.Direction.DESCENDING);
        query.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (!documentSnapshots.isEmpty()) {
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            String order_id = doc.getDocument().getString("order_id");
                            Date time = (Date) doc.getDocument().get("time");
                            String total = doc.getDocument().getString("total_price");


                            OrdersDemo item = new OrdersDemo(order_id, total, time);
                            adapter.add(item);


                            adapter.notifyDataSetChanged();

                        }
                    }
                    count++;

                } else {
                    noOrders.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), "No data", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ordersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                OrdersDemo ordersDemo=adapter.getItem(position);
                String order_id=ordersDemo.getOrder_id();
                Intent orderDetails=new Intent(getActivity(), OrderDetails.class);
                orderDetails.putExtra("order_id",order_id);
                startActivity(orderDetails);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

}
