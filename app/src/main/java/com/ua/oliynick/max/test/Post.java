package com.ua.oliynick.max.test;

import com.ua.oliynick.max.adapter.HasKey;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import lombok.Value;

/**
 * Created by max on 02.12.17.
 */
@Value
public class Post implements HasKey {
    private static final AtomicLong keyGenerator = new AtomicLong(10L);

    long postId;
    String username, body;
    Date timestamp;

    public Post(String username, String body, Date timestamp) {
        this.username = username;
        this.body = body;
        this.timestamp = timestamp;
        postId = keyGenerator.incrementAndGet();
    }

    @Override
    public long getViewId() {
        return postId;
    }
}
