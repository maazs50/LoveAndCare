package com.example.loveandcare.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loveandcare.Items.OrdersDemo;
import com.example.loveandcare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class OrdersAdapter extends ArrayAdapter<OrdersDemo> {
    Context context;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    ArrayList<OrdersDemo> ordersListItems;


    public OrdersAdapter(Context context, ArrayList<OrdersDemo> ordersListItems) {
        super(context, 0, ordersListItems);
        this.context = context;
        this.ordersListItems=ordersListItems;


    }

    @Override
    public View getView(int position,View convertView, ViewGroup parent) {
        View rootView= LayoutInflater.from(context).inflate(R.layout.order_items,null);
        TextView order_id = (TextView)rootView.findViewById(R.id.order_id);
        TextView total = (TextView)rootView.findViewById(R.id.total);
        TextView time = (TextView)rootView.findViewById(R.id.time);
        String order_id1=ordersListItems.get(position).getOrder_id();
        String time1=String.valueOf(ordersListItems.get(position).getTime());
        String price1=ordersListItems.get(position).getPrice();
        order_id.setText(order_id1);
//If I go to orders as soon as I placed the order, I'm getting error.
        try {
    total.setText(time1.substring(0, 20));
}
catch (Exception e){
    Toast.makeText(getContext(), "Reload again", Toast.LENGTH_SHORT).show();
}
        time.setText("₹"+price1);
        return rootView;

    }

}
