package br.com.nfrpaiva.cachedemo;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HazelCastTest {

    private Logger logger = org.slf4j.LoggerFactory.getLogger(HazelCastTest.class);

    @Autowired
    private HazelcastInstance instance ;

    @Test
    public void someTest () throws Exception{
        ILock lock = instance.getLock("piroca");
        lock.lock();
        lock.unlock();
    }

    @Test
    @Ignore
    public void testLock() throws  Exception {

        IntStream.range(1, 3).forEach((i)->{
            MyLocker locker = new MyLocker("Thread "+i);
            Thread thread = new Thread(locker);
            thread.start();
        });

        while (true){
            Thread.sleep(1000);
        }
    }

    class MyLocker implements Runnable {

        private String name;

        public MyLocker(String name){
            this.name = name;
        }

        @Override
        public void run() {
            while (true ){
                ILock lock = instance.getLock("locker1");
                try{
                    if (lock.tryLock(1, TimeUnit.SECONDS)){
                        logger.info("{} - Obtive um lock e vou ficar parado fazendo nada para segura-lo", name);
                        Thread.sleep(2000);
                    }else {
                        logger.warn("{} - Não foi possível obter um lock", name);
                    }
                }catch (Exception e ){
                    logger.error("{} - Erro ao obter um lock",name, e.getMessage());
                }finally{
                    if (lock.isLockedByCurrentThread()){
                        logger.info("{} - Não quero mais o locker liberando", name);
                        lock.unlock();
                    }
                }
            }

        }

    }

}
