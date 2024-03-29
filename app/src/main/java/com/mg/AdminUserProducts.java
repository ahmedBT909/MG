package com.mg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mg.Model.cart;
import com.mg.ViewHolder.CartViewHolder;

public class AdminUserProducts extends AppCompatActivity {
    RecyclerView productsList;
    RecyclerView.LayoutManager layoutManager;
    DatabaseReference cartListRef;
    private  String userId = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_products);
        userId = getIntent().getStringExtra("uid");
        productsList = findViewById(R.id.ProductsList);
        productsList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        productsList.setLayoutManager(layoutManager);
        cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List")
                .child("Admin View").child(userId).child("Products");

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<cart>options =
                new FirebaseRecyclerOptions.Builder<cart>().
                        setQuery(cartListRef,cart.class).build();
        FirebaseRecyclerAdapter<cart, CartViewHolder> adapter =
                new FirebaseRecyclerAdapter<cart, CartViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull cart model)
                    {
                        holder.txtProductQuantity.setText("Quantity = "+model.getQuantity());
                        holder.txtProductprice.setText("Price = " + model.getPrice());
                        holder.txtProductName.setText(model.getPname());

                    }

                    @NonNull
                    @Override
                    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout,parent,false);
                        CartViewHolder holder = new CartViewHolder(view);
                        return  holder;
                    }
                };
productsList.setAdapter(adapter);
adapter.startListening();
    }
}