package com.example.loveandcare.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.loveandcare.Adapters.CartAdapter;
import com.example.loveandcare.Adapters.ProductsAdapter;
import com.example.loveandcare.Items.CartItem;
import com.example.loveandcare.Items.ProductItem;
import com.example.loveandcare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CartFragment extends Fragment {

    public FirebaseFirestore firebaseFirestore;
    ArrayList<CartItem> cart;
    CartAdapter adapter;
    ListView cartListView;
    FirebaseAuth mAuth;
    public CartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        firebaseFirestore=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();
        cart = new ArrayList<>();
        adapter = new CartAdapter(getContext(), cart);
        cartListView = (ListView)view.findViewById(R.id.cartListView);
        cartListView.setAdapter(adapter);
        String user_id=mAuth.getUid();
        Query query=  firebaseFirestore.collection("Customer").document("Cart").collection(user_id);
        query.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (!documentSnapshots.isEmpty()) {
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            String id="";
                            String name=doc.getDocument().getString("name");
                            String price=doc.getDocument().getString("price");
                            String unit=doc.getDocument().getString("unit");
                            String category=doc.getDocument().getString("category");
                            String url=doc.getDocument().getString("url");
                            int qty=Integer.parseInt(doc.getDocument().get("quantity").toString());



                            CartItem item = new CartItem(id,name,price,unit,category,url,qty);
                            cart.add(item);
                            adapter.notifyDataSetChanged();

                        }

                    }

                }
                else{
                    Toast.makeText(getContext(), "No data",Toast.LENGTH_SHORT ).show();
                }

            }
        });



        return view;
    }

}
