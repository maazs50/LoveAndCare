package com.example.loveandcare.Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.loveandcare.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_medicines:
                    mTextMessage.setText("Medicines");
                    return true;
                case R.id.navigation_grocery:
                    mTextMessage.setText("Groceries");
                    return true;
                case R.id.navigation_care:
                    mTextMessage.setText("Care");
                    return true;
                     case R.id.navigation_cart:
                    mTextMessage.setText("Cart");
                    return true;
                case R.id.navigation_orders:
                    mTextMessage.setText("Orders");
                    return true;


            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        Map<String,Object> map=new HashMap<>();
        map.put("Name", "Demo");
        db.collection("Demo").document().set(map);
    }

}
