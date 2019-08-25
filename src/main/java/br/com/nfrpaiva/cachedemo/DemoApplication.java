package br.com.nfrpaiva.cachedemo;

import java.util.UUID;
import java.util.stream.LongStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

import br.com.nfrpaiva.cachedemo.dominio.Pessoa;
import br.com.nfrpaiva.cachedemo.dominio.PessoaRepository;

@SpringBootApplication
@EnableCaching
public class DemoApplication {

	@Autowired
	private PessoaRepository pessoaRepository;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	public CommandLineRunner runner (){
		return args -> {
			LongStream.range(1, 10).forEach((i)->{
				pessoaRepository.save(Pessoa.builder().nome("Um nome " + UUID.randomUUID().toString()).build());
			});
		};
	}

}

