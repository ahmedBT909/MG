package com.mg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;


public class AdminMaintainProduct extends AppCompatActivity {
    Button apllyChanges;
EditText name , price , des;
 ImageView imageView ;
    String productID="";
    DatabaseReference refM ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_maintain_product);
        apllyChanges = (Button) findViewById(R.id.aplly_chnges);

        productID = getIntent().getStringExtra("pid");
       refM = FirebaseDatabase.getInstance().getReference().child("Products").child(productID);
        name= (EditText) findViewById(R.id.product_name_maintain);
        price= (EditText) findViewById(R.id.product_price_maintain);
        des= (EditText) findViewById(R.id.product_description_maintain);
        imageView = (ImageView) findViewById(R.id.product_image_maintain);
        displaySpicificProductInfo();

        apllyChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applychanges();
            }
        });
    }

    private void applychanges() {
        String pName = name.getText().toString();
        String pPrice = price.getText().toString();
        String pDes = des.getText().toString();
        if(pName.equals("")){
            Toast.makeText(this, "Write Down Product Name", Toast.LENGTH_SHORT).show();
        } else if (pPrice.equals("")) {
            Toast.makeText(this, "Write Down Product Price", Toast.LENGTH_SHORT).show();
        } else if (pDes.equals("")) {
            Toast.makeText(this, "Write Down Product Description", Toast.LENGTH_SHORT).show();
        }else {
            HashMap<String, Object> productMap = new HashMap<>();
            productMap.put("pid", productID);
            productMap.put("description", pDes);
            productMap.put("price", pPrice);
            productMap.put("pname", pName);
            refM.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(AdminMaintainProduct.this, "Changes applied Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AdminMaintainProduct.this, AdminCategoryActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }
    }

    private void displaySpicificProductInfo() {
        refM.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String pName = snapshot.child("pname").getValue().toString();
                    String pPrice = snapshot.child("price").getValue().toString();
                    String pDes = snapshot.child("description").getValue().toString();
                    String pImage = snapshot.child("image").getValue().toString();
                    name.setText(pName);
                    des.setText(pDes);
                    price.setText(pPrice);
                    Picasso.get().load(pImage).into(imageView);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}