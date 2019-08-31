package br.com.nfrpaiva.cachedemo.dominio;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class QueueConsummer {

    private static Logger logger = LoggerFactory.getLogger(QueueConsummer.class);

    @Autowired
    private HazelcastInstance hazelcastInstance;

    @Scheduled(fixedDelay = 1)
    public void consumer() {

        try {
            // logger.info("Procurando mensagens");
            IQueue<String> queue = hazelcastInstance.getQueue("mensagens");
            // queue.add("Oi mensagem fixa no codigo");
            String item = queue.take();
            // String item = queue.poll(2, TimeUnit.SECONDS);
            logger.info("Mensagem encontrada na fila: {}", item);
        } catch (InterruptedException e) {
            logger.error("Parece que depois de muito esperar deu um erro", e.getMessage());
            e.printStackTrace();
        }

    }

}