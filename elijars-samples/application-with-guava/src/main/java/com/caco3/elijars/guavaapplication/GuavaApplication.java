package com.caco3.elijars.guavaapplication;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

public class GuavaApplication {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ListenableFuture<String> listenableFuture = Futures.immediateFuture("Hello from ListenableFuture");
        System.out.println(listenableFuture.get());
    }
}
