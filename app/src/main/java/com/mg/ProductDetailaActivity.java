package com.mg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mg.Model.Products;
import com.mg.Pravalent.Prevalent;
import com.mg.databinding.ActivityProductDetailaBinding;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class ProductDetailaActivity extends AppCompatActivity {
Button addToCartButton;

TextView productName , productPrice, productDescripotin;
String productID="" , state = "Normal";

    private ActivityProductDetailaBinding binding;
    private static final String TAG = "AD_DETAILS_TAG";
    private ArrayList<ModelImageSlider> imageSliderArrayList ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        productID = getIntent().getStringExtra("pid");
        addToCartButton = (Button) findViewById(R.id.pd_add_to_cart_button);


        productName =(TextView) findViewById(R.id.product_name_details);
        productPrice =(TextView) findViewById(R.id.product_price_details);
        productDescripotin=(TextView) findViewById(R.id.product_des_details);
        getProductdetails(productID);
        loadAdImage(productID);

    addToCartButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {


                addingToCart();

        }
    });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void addingToCart() {
        String saveCurrentTime , saveCurrentData;
        Calendar calForData= Calendar.getInstance();
        SimpleDateFormat currentData = new SimpleDateFormat("MMM dd ,yyyy");
        saveCurrentData = currentData.format(calForData.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForData.getTime());
       final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        final HashMap<String , Object> cartMap = new HashMap<>();
        cartMap.put("pid",productID);
        cartMap.put("pname",productName.getText().toString());
        cartMap.put("price",productPrice.getText().toString());
        cartMap.put("date",saveCurrentData);
        cartMap.put("time",saveCurrentTime);
 
        cartMap.put("discount","");
        cartListRef.child("User View").child(Prevalent.currentOnlineUser.getPhone())
                .child("Products").child(productID)
                .updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            cartListRef.child("Admin View").child(Prevalent.currentOnlineUser.getPhone())
                                    .child("Products").child(productID)
                                    .updateChildren(cartMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(ProductDetailaActivity.this, "Add to cart List.", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(ProductDetailaActivity.this,HomeActivity.class);
                                                startActivity(intent);
                                            }
                                        }
                                    });
                        }
                    }
                });


    }

    private void getProductdetails(String productID) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Products");
        productRef.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Products products = snapshot.getValue(Products.class);
                    productName.setText(products.getPname());
                    productDescripotin.setText(products.getDescription());
                    productPrice.setText(products.getPrice());


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void loadAdImage(String productID){
        Log.d(TAG, "loadAdImage: ");

        imageSliderArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Products");
        ref.child(productID).child("Images")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        imageSliderArrayList.clear();

                        for(DataSnapshot ds : snapshot.getChildren()){

                            ModelImageSlider modelImageSlider = ds.getValue(ModelImageSlider.class);

                            imageSliderArrayList.add(modelImageSlider);

                        }
                        AdapterImageSlider adapterImageSlider = new AdapterImageSlider(ProductDetailaActivity.this, imageSliderArrayList);
                        binding.productImageDetails.setAdapter(adapterImageSlider);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private  void  checkOrderState(){
        DatabaseReference orderRf;
        orderRf = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getPhone());
        orderRf.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String shiipingState = snapshot.child("state").getValue().toString();

                    if(shiipingState.equals("shipped")){
                        state = "Order Shipped";

                        }else if(shiipingState.equals("not shipped")){
                        state = "Order Placed";

                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}