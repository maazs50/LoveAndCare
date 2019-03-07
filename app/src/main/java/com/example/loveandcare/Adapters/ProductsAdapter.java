package com.example.loveandcare.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.loveandcare.Items.ProductItem;
import com.example.loveandcare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProductsAdapter extends ArrayAdapter<ProductItem> {

    Context context;
    ArrayList<ProductItem> productItems;
    FirebaseFirestore db;
    FirebaseAuth mAuth;

    public ProductsAdapter(Context context, ArrayList<ProductItem> productItems){
        super(context, 0, productItems);
        this.context = context;
        this.productItems = productItems;
    }


    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        db= FirebaseFirestore.getInstance();


        View rootView = LayoutInflater.from(context).inflate(R.layout.products_items,null);
        ImageView prodImage = (ImageView)rootView.findViewById(R.id.prodImage);
        final TextView prodName = (TextView)rootView.findViewById(R.id.prodName);
        TextView prodPrice = (TextView)rootView.findViewById(R.id.prodPricePerUnit);
        final TextView prodUnit = (TextView)rootView.findViewById(R.id.prodUnit);
        final Button prodAdd=(Button) rootView.findViewById(R.id.addToCart);

        String url=productItems.get(position).getProdImage();
        Glide
                .with(parent)
                .load(url)
                .placeholder(R.drawable.ic_launcher_foreground)
                .centerCrop()
                .into(prodImage);


        prodName.setText(productItems.get(position).getProdName());
        prodPrice.setText("Price ₹"+productItems.get(position).getProdPrice());
        prodUnit.setText("Units: "+productItems.get(position).getUnit());
        //Action when user clicks on button
        prodAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth=FirebaseAuth.getInstance();
                String user_id=mAuth.getUid();
                final String name=productItems.get(position).getProdName();
                String cat=productItems.get(position).getProdCategory();
                String price=productItems.get(position).getProdPrice();
                String unit=productItems.get(position).getUnit();
                String url=productItems.get(position).getProdImage();
                Map<String,Object> data=new HashMap<>();
                data.put("name",name );
                data.put("category", cat);
                data.put("price",price );
                data.put("unit", unit);
                data.put("url", url);
                data.put("quantity",1);
                db.collection("Customer").document("Cart").collection(user_id).document(cat+"-"+name).set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getContext(),name+" added!" ,Toast.LENGTH_SHORT ).show();
                    }
                });

            }
        });

        return rootView;
    }


}
