package com.mg;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class ConfirmFinalOrder extends AppCompatActivity {
EditText namdeEdit,phoneEdit,addressEdit,cityEdit;
Button confirmOrder;
String totalAmount = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        totalAmount = getIntent().getStringExtra("Total price");
        setContentView(R.layout.activity_confirm_final_order);
        confirmOrder = (Button) findViewById(R.id.confirm_final_order_btn);
        namdeEdit = (EditText) findViewById(R.id.shippment_name);
        phoneEdit = (EditText) findViewById(R.id.shippment_phone_number);
        cityEdit = (EditText) findViewById(R.id.shippment_city);
        addressEdit = (EditText) findViewById(R.id.shippment_Address);

    }
}