package com.example.finaldemo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FinalDemoApplicationTests {

    @Test
    void contextLoads() {
        Object object1 = new Object();
        Object object2 = new Object();
        Thread thread1 = new Thread(() -> {
            synchronized (object1) {
                System.out.printf("线程" + Thread.currentThread().getName() + "得到了锁");
                synchronized (object2) {
                    System.out.printf("线程" + Thread.currentThread().getName() + "得到了锁");
                    System.out.printf("线程" + Thread.currentThread().getName() + "释放了锁");
                    try {
                        object1.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.printf("程序结束");
                }

            }

        });

        new Thread(() -> {
            synchronized (object1){
                System.out.printf("线程"+Thread.currentThread().getName()+"得到了锁");
                

            }

        });
    }

}
