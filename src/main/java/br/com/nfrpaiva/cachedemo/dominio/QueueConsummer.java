package br.com.nfrpaiva.cachedemo.dominio;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import com.hazelcast.core.IQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import br.com.nfrpaiva.cachedemo.mongodb.Item;

@Service
public class QueueConsummer {

    private static Logger logger = LoggerFactory.getLogger(QueueConsummer.class);

    @Autowired
    private HazelcastInstance hazelcastInstance;

    @Autowired
    private MongoTemplate template;

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

    @Scheduled(fixedRate = 1000)    
    @Async
    public void mongodbConsumer() {
        Query query = Query.query(Criteria.where("status").is(null));
        // Item item = template.findAndModify(query, Update.update("status",
        // "consumido"),
        // FindAndModifyOptions.options().returnNew(true), Item.class);
        // if (item != null)
        // logger.info("Mensagem consumida do mongo db: {}", item);
        ILock lock = hazelcastInstance.getLock("mongodb.itemConsumer");
        try {
            if (lock.tryLock(1, TimeUnit.SECONDS)) {
                List<Item> itens = template.find(query.limit(1000), Item.class);
                itens.stream().forEach(item -> {
                    logger.info("Mensagem consumida do mongo db: {}", item);
                    template.updateFirst(Query.query(Criteria.where("_id").is(item.getId())), Update.update("status", "consumido"),Item.class);
                });
            } else {
                logger.warn("Consumo de mensagens ocorrendo em outra instancia");
            }
        } catch (Exception e) {
            logger.error("Correu um erro ao iniciar consumidor de mensagens", e.getMessage());
        } finally {
            if (lock.isLockedByCurrentThread())
                lock.unlock();
        }

    }

    @Scheduled(cron = "0 0/1 * * * ?")
    public void cleanUpMongo() {
        ILock lock = hazelcastInstance.getLock("mongodb.cleanUp");
        try {

            if (lock.tryLock(1, TimeUnit.MILLISECONDS)) {
                Query queryConsumidos = Query.query(Criteria.where("status").is("consumido"));
                long count = template.count(queryConsumidos, "item");
                logger.info("Limpando registros que já foram consumidos do mongo: {}", count);
                template.remove(queryConsumidos, Item.class);
            } else {
                logger.warn(
                        "Não foi possível conseguir o lock necessário para execução dessa tarefa. Outro processo deve estar fazendo isso... ");
            }
        } catch (Exception e) {
            logger.error("Erro ao tentar conseguir lock", e.getMessage());
        } finally {
            if (lock.isLockedByCurrentThread()) {
                lock.unlock();
            }
        }
    }

}