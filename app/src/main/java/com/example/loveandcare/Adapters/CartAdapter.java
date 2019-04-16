package com.example.loveandcare.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.loveandcare.Items.CartItem;
import com.example.loveandcare.Items.ProductItem;
import com.example.loveandcare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.*;
public class CartAdapter extends ArrayAdapter<CartItem> {
    Context context;
    ArrayList<CartItem> cartItems;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String user_id;


    public CartAdapter(Context context, ArrayList<CartItem> cartItems){
        super(context, 0, cartItems);
        this.context=context;
        this.cartItems = cartItems;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        db= FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();
        user_id=mAuth.getUid();

        View rootView = LayoutInflater.from(context).inflate(R.layout.cart_items,null);
        //Initiate
        ImageView prodImage = (ImageView)rootView.findViewById(R.id.prodImage);
        final TextView prodName = (TextView)rootView.findViewById(R.id.prodName);
        TextView prodPrice = (TextView)rootView.findViewById(R.id.prodPricePerUnit);
        final TextView prodUnit = (TextView)rootView.findViewById(R.id.prodUnit);
        final Button prodRemove=(Button) rootView.findViewById(R.id.removeProd);
        final Button incrementbtn=(Button) rootView.findViewById(R.id.incrementbtn);
        final Button decrementbtn=(Button) rootView.findViewById(R.id.decrementbtn);
       final TextView quantityTextView = (TextView)rootView.findViewById(R.id.quantity_text_view);
       final TextView prodTotal=(TextView)rootView.findViewById(R.id.textView2);

//Loading image
        String url=cartItems.get(position).getProdImage();
        Glide
                .with(parent)
                .load(url)
                .placeholder(R.drawable.ic_launcher_foreground)
                .centerCrop()
                .into(prodImage);
//To get the Grand total of the cart

        prodName.setText(cartItems.get(position).getProdName());
        prodPrice.setText("₹"+cartItems.get(position).getProdPrice());
        prodUnit.setText("Unit: "+cartItems.get(position).getUnit());
        int quantity=cartItems.get(position).getQuantity();
        quantityTextView.setText(String.valueOf(quantity));
        int price=Integer.parseInt(cartItems.get(position).getProdPrice());
        int total=(int)quantity*price;

        prodTotal.setText("Total : ₹"+total);

        //Action when user clicks on button
        prodRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=cartItems.get(position).getProdName();
                String category=cartItems.get(position).getProdCategory();
                db.document("/Customer/Cart/"+user_id+"/"+category+"-"+name).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getContext(),"Item deleted!",Toast.LENGTH_LONG).show();
                  }
                });


            }
        });
        /*
       These buttons are in the cart for products
        */
        incrementbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//Increments quantity by one
                int quantity=cartItems.get(position).getQuantity();
                String name=cartItems.get(position).getProdName();
                String cat=cartItems.get(position).getProdCategory();
                int price=Integer.parseInt(cartItems.get(position).getProdPrice());
                int total;

                if (quantity == 20) {
                    Toast.makeText(getContext(),"You have reached the maximum quantity" , Toast.LENGTH_SHORT).show();

                return;
                }else {
                    quantity = quantity + 1;
                    total=(int)quantity*price;
                    cartItems.get(position).setQuantity(quantity);
                    Map<String,Object> data=new HashMap<>();
                    data.put("quantity",quantity);
                    db.collection("Customer").document("Cart").collection(user_id).document(cat+"-"+name)
                            .update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getContext(),"Done" ,Toast.LENGTH_SHORT ).show();
                        }
                    });
                }
                quantityTextView.setText(String.valueOf(quantity));
                prodTotal.setText("Total : ₹"+total);
            }

        });
        decrementbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity=cartItems.get(position).getQuantity();
                String name=cartItems.get(position).getProdName();
                String cat=cartItems.get(position).getProdCategory();
                int price=Integer.parseInt(cartItems.get(position).getProdPrice());
                int total;

                if (quantity == 1) {
                    Toast.makeText(getContext(),"You cannot order the quantity as zero" , Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    quantity = quantity - 1;
                    total=(int)quantity*price;
                    //This was the solution for cart button issues as it was not changing
                    cartItems.get(position).setQuantity(quantity);
                    Map<String,Object> data=new HashMap<>();
                    data.put("quantity",quantity);
                    db.collection("Customer").document("Cart").collection(user_id).document(cat+"-"+name)
                            .update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                //Everytime the quantity is stored and updated in the docx
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getContext(),"Done" ,Toast.LENGTH_SHORT ).show();
                        }
                    });

                }

                quantityTextView.setText(String.valueOf(quantity));
                prodTotal.setText("Total : ₹"+total);
            }
        });

        return rootView;
    }


}
