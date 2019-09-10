package br.com.nfrpaiva.cachedemo;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.junit.Test;
import org.springframework.util.StopWatch;

public class ThreadTest {

    @Test
    public void test() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        int taskSize = 100;
        ExecutorService pool = Executors.newFixedThreadPool(50);
        IntStream.range(0, taskSize).forEach(i -> pool.execute(() -> {
            try {
                Thread.sleep(1000);
                System.out.println("Olá " + UUID.randomUUID().toString());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));
        pool.shutdown();
        System.out.println("Aguardando finalização das Threads");
        pool.awaitTermination(1, TimeUnit.DAYS);
        stopWatch.stop();
        System.out.println("Fim " + stopWatch.getTotalTimeSeconds());

    }

}