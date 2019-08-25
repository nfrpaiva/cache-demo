package br.com.nfrpaiva.cachedemo;

import java.util.Set;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StopWatch;

import br.com.nfrpaiva.cachedemo.dominio.Pessoa;
import br.com.nfrpaiva.cachedemo.dominio.PessoaService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PessoaServiceTest {

    private static Logger logger = LoggerFactory.getLogger(PessoaServiceTest.class);

    @Autowired
    private PessoaService pessoaService;

    @Autowired
    private HazelcastInstance hazelcastInstance;

    @Test
    @Commit
    public void testCachedPessoa() {

        StopWatch sw = new StopWatch();

        sw.start();
        pessoaService.cleanCache();
        pessoaService.getAll();
        sw.stop();
        logger.info("Buscando todas as pessoas com um tempo de {} segundos", sw.getTotalTimeSeconds());
        sw = new StopWatch();
        sw.start();
        pessoaService.getAll();
        sw.stop();
        logger.info("Buscando todas as pessoas mas agora com cache com um tempo de {} segundos", sw.getTotalTimeSeconds());

    }

    @Test
    public void testSalvarPessoas() {
        pessoaService.cleanCache();
        LongStream.range(1, 100_000).forEach((id) ->
                {
                    Pessoa p = Pessoa
                            .builder()
                            .nome(UUID.randomUUID().toString())
                            .id(id)
                            .build();
                    pessoaService.save(p);
                }
        );
    }

    @Test
    public void testObterPessoaDoCache() {
        pessoaService.cleanCache();
        Pessoa nova = Pessoa.builder().nome("Nilton").id(-99L).build();
        pessoaService.save(nova);
        Pessoa p = pessoaService.getPessoa(nova.getId());
        Assertions.assertThat(p).isNotNull();
    }

    @Test
    public void testfastputs () throws Exception{
        IMap<Object, Object> map = hazelcastInstance.getMap("test-map");
        map.clear();
        StopWatch sw = new StopWatch();
        sw.start();
        int range = 5000_000;
        IntStream.range(0, range).forEach((i)-> {
            if (i % 1000 == 0){
                logger.info("Inserido: {}", i);
            }
            map.put(i, "Valor de " + i);
        }
        );
        sw.stop();
        logger.info("Inserido: {} em {} segundos", range, sw.getTotalTimeSeconds());

    }
    @Test
    public void setTest() throws Exception {
        
        // Get the Distributed Set from Cluster.
        Set<String> set = hazelcastInstance.getSet("my-distributed-set");
        // Add items to the set with duplicates
        set.add("item1");
        set.add("item1");
        set.add("item2");
        set.add("item2");
        set.add("item2");
        set.add("item3");
        // Get the items. Note that there are no duplicates.
        for (String item: set) {
            System.out.println(item);
        }
    }
}
