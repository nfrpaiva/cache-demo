package br.com.nfrpaiva.cachedemo.dominio;

import java.util.concurrent.TimeUnit;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HelloService {

    @Autowired
    private HazelcastInstance hazelcastInstance;

    private Logger logger = LoggerFactory.getLogger(HelloService.class);

    public String sayHello() {
        ILock lock = hazelcastInstance.getLock("hello");
        try {
            // logger.info("Tentando obter lock");
            if (lock.tryLock(100, TimeUnit.MILLISECONDS)) {
                // logger.info("Lock obtido");
                Thread.sleep(100);
                hazelcastInstance.getQueue("mensagens").put("Hello service disso olá para o mundo");
                return "Olá mundo \n";
            } else {
                throw new Exception("Não consegui obter o lock que você pediu e não fiz nada");
            }

        } catch (Exception e) {
            logger.error("Erro ao obter o lock");
        } finally {
            if (lock.isLockedByCurrentThread())
                lock.unlock();
        }
        return "Não pude dizer olá \n";
    }

}