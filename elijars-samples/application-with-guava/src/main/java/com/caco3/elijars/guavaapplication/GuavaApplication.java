package com.caco3.elijars.guavaapplication;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

public class GuavaApplication {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ListenableFuture<String> listenableFuture = Futures.immediateFuture(createHelloString());
        System.out.println(listenableFuture.get());
    }

    private static String createHelloString() {
        return "Hello from ListenableFuture, my module = '"
               + GuavaApplication.class.getModule()
               + "', and the ListenableFuture's module = '"
               + ListenableFuture.class.getModule() + "'";
    }
}
