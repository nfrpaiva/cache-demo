package br.com.nfrpaiva.cachedemo;

import java.util.UUID;
import java.util.stream.LongStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.messaging.DefaultMessageListenerContainer;
import org.springframework.data.mongodb.core.messaging.MessageListenerContainer;

import br.com.nfrpaiva.cachedemo.dominio.Pessoa;
import br.com.nfrpaiva.cachedemo.dominio.PessoaRepository;

@SpringBootApplication
@EnableCaching
public class DemoApplication {

	@Autowired
	private PessoaRepository pessoaRepository;
	
	private Logger log = LoggerFactory.getLogger(DemoApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	public CommandLineRunner runner() {
		MDC.put("UMA CHAVE GRANDE", "UM GRANDE VALORSS");
		log.info("XXXXXXXXXXXXXXXXXXXXXXXXX");
		return args -> {
			LongStream.range(1, 10).forEach((i) -> {
				log.info("XXXXXXXXXXXXXXXXXXXXXXXXXXX - Adding pessoa");
				pessoaRepository.save(Pessoa.builder().nome("Um nome " + UUID.randomUUID().toString()).build());
			});

		};
	}

	@Bean
	public MessageListenerContainer messageListenerContainer(MongoTemplate mongoTemplate) {
		return new DefaultMessageListenerContainer(mongoTemplate);

	}

}
