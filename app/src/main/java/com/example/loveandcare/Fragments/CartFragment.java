package com.example.loveandcare.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loveandcare.Adapters.CartAdapter;
import com.example.loveandcare.Items.CartItem;
import com.example.loveandcare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CartFragment extends Fragment {

    public FirebaseFirestore firebaseFirestore;
    ArrayList<CartItem> cart;
    CartAdapter adapter;
    ListView cartListView;
    FirebaseAuth mAuth;
    ImageView emptyCart;
    FloatingActionButton checkoutbtn;

    public CartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        cart = new ArrayList<>();
        adapter = new CartAdapter(getContext(), cart);
        cartListView = (ListView) view.findViewById(R.id.cartListView);
        cartListView.setAdapter(adapter);
        emptyCart = (ImageView) view.findViewById(R.id.image);
        checkoutbtn = view.findViewById(R.id.checkout);
        final Vibrator vibe = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);

        checkoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Have an alert box and inflate the layout
                if (cart.size()!=0){

                    final View alertLayout = LayoutInflater.from(getActivity()).inflate(R.layout.order_summary, null);
                int totalPrice = grandTotal(cart);

                final android.support.v7.app.AlertDialog.Builder alert = new android.support.v7.app.AlertDialog.Builder(getActivity());
                alert.setTitle("Enter details");
                alert.setView(alertLayout);
                alert.setCancelable(true);
                TextView txt_total = (TextView) alertLayout.findViewById(R.id.order_total);
                txt_total.setText("Total price - ₹" + totalPrice);
                Button order = (Button) alertLayout.findViewById(R.id.place_order);
                final EditText name = (EditText) alertLayout.findViewById(R.id.name);
                final EditText phone = (EditText) alertLayout.findViewById(R.id.phoneNumber);
                final EditText address = (EditText) alertLayout.findViewById(R.id.address);


                final android.support.v7.app.AlertDialog dialog = alert.create();
                dialog.show();

                order.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        final String nametxt = name.getText().toString();
                        final String phonetxt = phone.getText().toString();
                        final String addresstxt = address.getText().toString();
                        final int totalPrice = grandTotal(cart);

                        if (!TextUtils.isEmpty(nametxt) && !TextUtils.isEmpty(phonetxt) && !TextUtils.isEmpty(addresstxt)) {
                            vibe.vibrate(100);
                            final ProgressDialog progresRing = ProgressDialog.show(getActivity(), "Order", "Placing order...", true);
                            progresRing.setCancelable(false);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        final String randonName = UUID.randomUUID().toString().substring(0, 8);

                                        final String user_id = mAuth.getUid();

                                        final DocumentReference documentReference = firebaseFirestore.collection("Customer").document("Orders").collection(user_id).document();
                                        final Map<String, Object> data = new HashMap<>();
                                        //Get the cart product details
                                        Map<String, Integer> productDetails = getProducts(cart);

                                        data.put("order_id", randonName);
                                        data.put("user_id", user_id);
                                        data.put("customer_name", nametxt);
                                        data.put("phone", phonetxt);
                                        data.put("address", addresstxt);
                                        data.put("products", productDetails);
                                        data.put("total_price", String.valueOf(totalPrice));
                                        data.put("time", FieldValue.serverTimestamp());
                                        documentReference.set(data)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            firebaseFirestore.collection("Orders").document().set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    //Deleting all the documents in a collection after the order is placed
                                                                    Query deleteCart=firebaseFirestore.collection("Customer").
                                                                            document("Cart").collection(user_id);
                                                                    deleteCart.get().addOnSuccessListener(getActivity(), new OnSuccessListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onSuccess(QuerySnapshot documentSnapshots) {
                                                                            //Getting the size of documents
                                                                            int size=documentSnapshots.size();
                                                                            for (int i=0;i<size;i++) {
                                                                                //Getting the docx Id
                                                                                String documentId = documentSnapshots.getDocuments().get(i).getId();
                                                                                //Full path to be deleted
                                                                                firebaseFirestore.collection("Customer").
                                                                                        document("Cart").collection(user_id).document(documentId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        //After deleting items take back to the orders fragment
                                                                                        Fragment fragment = new OrdersFragment();
                                                                                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                                                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                                                                        fragmentTransaction.replace(R.id.container, fragment);
                                                                                        fragmentTransaction.addToBackStack(null);
                                                                                        fragmentTransaction.commit();
                                                                                    }
                                                                                });                                                                            }

                                                                        }
                                                                    });
//                                                                    firebaseFirestore.collection("Customer").
//                                                                            document("Cart").collection(user_id).document("Medicine-y").delete();
                                                                    progresRing.dismiss();
                                                                    dialog.dismiss();
                                                                    Toast.makeText(getActivity(), "Placed order!\norder id-" + randonName, Toast.LENGTH_SHORT).show();

                                                                }
                                                            });
                                                        }
                                                    }
                                                });
                                    } catch (Exception e) {

                                    }

                                }
                            }).start();

                        } else {
                            Toast.makeText(getActivity(), "Enter all the fields", Toast.LENGTH_SHORT).show();
                        }


                    }
                });

        }else{
            Toast.makeText(getActivity(), "Cart is Empty\nUnable to checkout", Toast.LENGTH_SHORT).show();
        }

            }
        });
        String user_id = mAuth.getUid();
        Query query = firebaseFirestore.collection("Customer").document("Cart").collection(user_id);
        query.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (!documentSnapshots.isEmpty()) {
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            String id = doc.getDocument().getString("id") + "";
                            String name = doc.getDocument().getString("name");
                            String price = doc.getDocument().getString("price");
                            String unit = doc.getDocument().getString("unit");
                            String category = doc.getDocument().getString("category");
                            String url = doc.getDocument().getString("url");
                            int qty = Integer.parseInt(doc.getDocument().get("quantity").toString());


                            CartItem item = new CartItem(id, name, price, unit, category, url, qty);
                            cart.add(item);
                            adapter.notifyDataSetChanged();



                        }

                    }

                } else {
                    emptyCart.setVisibility(View.VISIBLE);
                }

            }
        });


        return view;
    }

    private int grandTotal(List<CartItem> items) {

        int totalPrice = 0;
        for (int i = 0; i < items.size(); i++) {

            int price = Integer.parseInt(items.get(i).getProdPrice());
            int quantity = items.get(i).getQuantity();
            totalPrice += (price * quantity);

        }

        return totalPrice;
    }

    //Get the products
    private Map<String, Integer> getProducts(List<CartItem> cartList) {
        Map<String, Integer> data = new HashMap<>();
        for (int i = 0; i < cartList.size(); i++) {
            data.put(cartList.get(i).getProdName(), cartList.get(i).getQuantity());
        }
        return data;
    }

}
