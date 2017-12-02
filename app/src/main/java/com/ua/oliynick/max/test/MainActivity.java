package com.ua.oliynick.max.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.ua.oliynick.max.adapter.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import lombok.experimental.var;
import lombok.val;

public class MainActivity extends AppCompatActivity {

    private final PostsAdapter adapter;

    public MainActivity() {
        adapter = new PostsAdapter();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView rv = findViewById(R.id.rv);

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        val data = new ArrayList<Post>();

        for (var i = 0; i < 20; ++i) {
            data.add(createRandomItem());
        }

        adapter.addOrUpdate(data);

        findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.addOrUpdate(createRandomItem());
            }
        });

        findViewById(R.id.swap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.toggleComparator();

                ((Button) findViewById(R.id.swap)).setText(adapter.isAscending() ? "Ascending" : "Descending");
            }
        });
    }

    private Post createRandomItem() {
        return new Post("Max", "Hello", new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * new Random().nextInt(5)));
    }

}
