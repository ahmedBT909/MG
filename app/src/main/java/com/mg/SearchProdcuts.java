package com.mg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class SearchProdcuts extends AppCompatActivity {
    Button searchBTN;
    EditText inputText;
    RecyclerView searchList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_prodcuts);
        inputText= (EditText) findViewById(R.id.search_product);
        searchBTN = (Button) findViewById(R.id.searchBtn);
        searchList = findViewById(R.id.searchList);
        searchList.setLayoutManager(new LinearLayoutManager(SearchProdcuts.this));
    }
}