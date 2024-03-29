package com.mg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mg.Model.AdminOrders;

public class AdminNewOrders extends AppCompatActivity {

    RecyclerView orderList;
    private DatabaseReference orderRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_orders);
        orderRef = FirebaseDatabase.getInstance().getReference().child("Orders");
        orderList =findViewById(R.id.order_list);
        orderList.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<AdminOrders> options =
                new FirebaseRecyclerOptions.Builder<AdminOrders>()
                        .setQuery(orderRef,AdminOrders.class)
                        .build();
        FirebaseRecyclerAdapter<AdminOrders,AdminOrderViewHolder>  adapter =
                new FirebaseRecyclerAdapter<AdminOrders, AdminOrderViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AdminOrderViewHolder holder,final int position, @NonNull AdminOrders model) {
                        holder.username.setText("Name :" + model.getName());
                        holder.userPhoneNumber.setText("Phone:" + model.getPhone());
                        holder.userTotalPrice.setText("Total =EG:" + model.getTotalAmount());
                        holder.userDateTime.setText("Order at :" + model.getDate() +" "+model.getTime());
                        holder.userShippedAdress.setText("Shipped Address :" + model.getAddress()+", "+model.getCity());
                        holder.showOrderBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                          String uId = getRef(position).getKey();
                                Intent intent = new Intent(AdminNewOrders.this,AdminUserProducts.class);
                                intent.putExtra("uid",uId);
                                startActivity(intent);
                            }
                        });
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence option[] = new CharSequence[]{
                                  "Yes",
                                  "No"
                                };
                                AlertDialog.Builder builder = new AlertDialog.Builder(AdminNewOrders.this);
                                builder.setTitle("Have you shipped this order products ?");
                                builder.setItems(option, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(which == 0 ){
                                            String uId = getRef(position).getKey();
                                            RemoverOrder(uId);
                                        }else {
                                            finish();
                                        }

                                    }
                                });
                                builder.show();
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public AdminOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.order_layout,parent,false);
                        return  new AdminOrderViewHolder(view);
                    }
                };
        orderList.setAdapter(adapter);
        adapter.startListening();


    }

    private void RemoverOrder(String uId) {
        orderRef.child(uId).removeValue();
    }

    public  static  class   AdminOrderViewHolder  extends RecyclerView.ViewHolder
    {
        public TextView username,userPhoneNumber,userDateTime,userTotalPrice,userShippedAdress;
        public Button showOrderBtn;

        public AdminOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            username =itemView.findViewById(R.id.order_user_name);
            userPhoneNumber = itemView.findViewById(R.id.order_phone_number);
            userDateTime = itemView.findViewById(R.id.order_date_time);
            userTotalPrice = itemView.findViewById(R.id.order_total_price);
            userShippedAdress = itemView.findViewById(R.id.order_Address_city);
            showOrderBtn = itemView.findViewById(R.id.show_all_Products);
        }
    }

}