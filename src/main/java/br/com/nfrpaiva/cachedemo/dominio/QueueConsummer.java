package br.com.nfrpaiva.cachedemo.dominio;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
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

    @Scheduled(fixedRate = 10)
    @Async
    public void mongodbConsumer() {
        Query query = Query.query(Criteria.where("status").is(null));
        Item item = template.findAndModify(query, Update.update("status", "consumido"),
                FindAndModifyOptions.options().returnNew(true), Item.class);
        if (item != null)
            logger.info("Mensagem consumida do mongo db: {}", item);

    }

    @Scheduled(cron = "0/5 * * * * ?")
    public void cleanUpMongo() {
        logger.info("Limpando registros que já foram consumidos do mongo");
        template.remove(Query.query(Criteria.where("status").is("consumido")), Item.class);
    }

}