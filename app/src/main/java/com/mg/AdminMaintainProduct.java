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
    Button apllyChanges , deletePrdcut;
 EditText name , price , des,quntity;
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
        deletePrdcut = (Button) findViewById(R.id.delete_products);
        quntity = (EditText)findViewById(R.id.product_qunt_maint);
        displaySpicificProductInfo();

        apllyChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applychanges();
            }
        });
        deletePrdcut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteThsproduct();
            }
        });
    }

    private void deleteThsproduct() {
        refM.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent intent = new Intent(AdminMaintainProduct.this, AdminCategoryActivity.class);
                startActivity(intent);
                finish();
                Toast.makeText(AdminMaintainProduct.this, "Product Deleted Successfflly", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void applychanges() {
        String pName = name.getText().toString();
        String pPrice = price.getText().toString();
        String pDes = des.getText().toString();
        String qquntity= quntity.getText().toString();
        if(pName.equals("")){
            Toast.makeText(this, "Write Down Product Name", Toast.LENGTH_SHORT).show();
        } else if (pPrice.equals("")) {
            Toast.makeText(this, "Write Down Product Price", Toast.LENGTH_SHORT).show();
        } else if (pDes.equals("")) {
            Toast.makeText(this, "Write Down Product Description", Toast.LENGTH_SHORT).show();

        } else if (qquntity.equals("")) {
            Toast.makeText(this, "Write Down Product quntity", Toast.LENGTH_SHORT).show();

        } else {
            HashMap<String, Object> productMap = new HashMap<>();
            productMap.put("pid", productID);
            productMap.put("description", pDes);
            productMap.put("price", pPrice);
            productMap.put("pname", pName);
            productMap.put("Quntity",qquntity);
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
                    String qquntity_2 = snapshot.child("Quntity").getValue().toString();
                    name.setText(pName);
                    des.setText(pDes);
                    price.setText(pPrice);
                    quntity.setText(qquntity_2);
                    refM.child("Images").limitToFirst(1);
                    String imageUrl= ""+snapshot.child("imageUrl").getValue();
                    Picasso.get().load(imageUrl).into(imageView);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}