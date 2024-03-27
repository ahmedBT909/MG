package com.mg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mg.Pravalent.Prevalent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmFinalOrder extends AppCompatActivity {
EditText namdeEdit,phoneEdit,addressEdit,cityEdit;
Button confirmOrder;
String totalAmount = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        totalAmount = getIntent().getStringExtra("Total price");
        Toast.makeText(this, "Total price =EG" + totalAmount, Toast.LENGTH_SHORT).show();
        setContentView(R.layout.activity_confirm_final_order);
        confirmOrder = (Button) findViewById(R.id.confirm_final_order_btn);
        namdeEdit = (EditText) findViewById(R.id.shippment_name);
        phoneEdit = (EditText) findViewById(R.id.shippment_phone_number);
        cityEdit = (EditText) findViewById(R.id.shippment_city);
        addressEdit = (EditText) findViewById(R.id.shippment_Address);
        confirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check();
            }
        });

    }

    private void check() {
        if(TextUtils.isEmpty(namdeEdit.getText().toString())){
            Toast.makeText(this, "Please provide full your name", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(phoneEdit.getText().toString())){
            Toast.makeText(this, "Please provide your phone number", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(addressEdit.getText().toString())){
            Toast.makeText(this, "Please provide your Address", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(cityEdit.getText().toString())){
            Toast.makeText(this, "Please provide your city", Toast.LENGTH_SHORT).show();
        }else {
            confirm_Order();
        }
    }

    private void confirm_Order() {
        String saveCurrentTime , saveCurrentData;
        Calendar calForData= Calendar.getInstance();
        SimpleDateFormat currentData = new SimpleDateFormat("MMM dd ,yyyy");
        saveCurrentData = currentData.format(calForData.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForData.getTime());

        final DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference()
                .child("Orders")
                .child(Prevalent.currentOnlineUser.getPhone());
        HashMap<String , Object> orderMap =new HashMap<>();
        orderMap.put("total Amount",totalAmount);
        orderMap.put("name",namdeEdit.getText().toString());
        orderMap.put("phone ",phoneEdit.getText().toString());
        orderMap.put("address",addressEdit.getText().toString());
        orderMap.put("city",cityEdit.getText().toString());
        orderMap.put("Date",saveCurrentData);
        orderMap.put("Time",saveCurrentTime);
        orderMap.put("state","not shipped");
        orderRef.updateChildren(orderMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
             FirebaseDatabase.getInstance().getReference().child("Cart List")
                     .child("User View")
                     .child(Prevalent.currentOnlineUser.getPhone()).
                     removeValue()
                     .addOnCompleteListener(new OnCompleteListener<Void>() {
                         @Override
                         public void onComplete(@NonNull Task<Void> task) {
                             if (task.isSuccessful()){
                                 Toast.makeText(ConfirmFinalOrder.this, "your final order has benn placed Successfully", Toast.LENGTH_SHORT).show();
                                 Intent intent = new Intent(ConfirmFinalOrder.this,HomeActivity.class);
                                 intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                 startActivity(intent);
                            finish();
                             }

                         }
                     });
            }
            }
        });


    }
}