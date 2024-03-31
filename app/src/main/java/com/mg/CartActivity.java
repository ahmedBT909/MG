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
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mg.Model.cart;
import com.mg.Pravalent.Prevalent;
import com.mg.ViewHolder.CartViewHolder;

public class CartActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    Button NextProccess;
    TextView txtTotalAmount,txtMsg1;

    private  int total_price =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        recyclerView =findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        NextProccess = (Button) findViewById(R.id.next_proccess_btn);
        txtTotalAmount = (TextView)findViewById(R.id.totalPrice);
        txtMsg1 = (TextView)findViewById(R.id.msg1);

    NextProccess.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(CartActivity.this,ConfirmFinalOrder.class);
            intent.putExtra("Total price",String.valueOf(total_price));
            startActivity(intent);
        finish();
        }
    });
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkOrderState();
        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        FirebaseRecyclerOptions<cart> options =
                new FirebaseRecyclerOptions.Builder<cart>()
                        .setQuery(cartListRef.child("User View")
                                .child(Prevalent.currentOnlineUser.getPhone()).child("Products"),cart.class)
                        .build();
        FirebaseRecyclerAdapter<cart, CartViewHolder> adapter
                =new FirebaseRecyclerAdapter<cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull cart model)
            {
                holder.txtProductQuantity.setText("Quantity = "+model.getQuantity());
                holder.txtProductprice.setText("Price = " + model.getPrice());
                holder.txtProductName.setText(model.getPname());
                int oneTyperProductprice= ((Integer.valueOf(model.getPrice()))* Integer.valueOf(model.getQuantity())) ;
                total_price = total_price+ oneTyperProductprice;
                txtTotalAmount.setText("Total Price EG"+String.valueOf(total_price));
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence options[]  = new CharSequence[]{"Edit" ,"Remove"
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                        builder.setTitle("Cart options:");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(which == 0 ){
                                    Intent intent = new Intent(CartActivity.this,ProductDetailaActivity.class);
                                    intent.putExtra("pid",model.getPid());
                                    startActivity(intent);
                                }if(which == 1 ){
                                    cartListRef.child("User View").
                                            child(Prevalent.currentOnlineUser.getPhone())
                                            .child("Products")
                                            .child(model.getPid())
                                            .removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Toast.makeText(CartActivity.this, "Item Removed Successful", Toast.LENGTH_SHORT).show();


                                                    }
                                                }
                                            });
                                }
                            }
                        });
                        builder.show();
                    }
                });
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout,parent,false);
                CartViewHolder holder = new CartViewHolder(view);
                return  holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
    private  void  checkOrderState(){
        DatabaseReference orderRf;
        orderRf = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getPhone());
        orderRf.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String shiipingState = snapshot.child("state").getValue().toString();
                    String username = snapshot.child("name").getValue().toString();
                    if(shiipingState.equals("shipped")){
                        txtTotalAmount.setText("Dear" + username + "\n order is shipped Successfully");
                        recyclerView.setVisibility(View.GONE);
                        txtMsg1.setVisibility(View.VISIBLE);
                        txtMsg1.setText("Congratulations. you final order has been Shipped\n" +
                                "successfully. soon you will receive your order at\n" +
                                "door step");
                        NextProccess.setVisibility(View.GONE);
                        Toast.makeText(CartActivity.this, "you can pruchase , once your final  first order", Toast.LENGTH_SHORT).show();
                    }else if(shiipingState.equals("not shipped")){
                        txtTotalAmount.setText("Shipped state = Not shipped");
                        recyclerView.setVisibility(View.GONE);
                        txtMsg1.setVisibility(View.VISIBLE);
                        NextProccess.setVisibility(View.GONE);
                        Toast.makeText(CartActivity.this, "you can pruchase , once your final  first order", Toast.LENGTH_SHORT).show();

                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}