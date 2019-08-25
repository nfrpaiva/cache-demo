package br.com.nfrpaiva.cachedemo.dominio;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@CacheConfig(cacheNames = {"pessoa"})
@Service
public class PessoaService {

    private static Logger logger = LoggerFactory.getLogger(PessoaService.class);

    @Autowired
    private PessoaRepository pessoaRepository;

    @Cacheable()
    public List<Pessoa> getAll() {
        logger.info("Não encontrei pessoa no cache vou buscar no banco");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return pessoaRepository.findAll();
    }

    public Pessoa save (Pessoa pessoa){
        return pessoaRepository.saveAndFlush(pessoa);
    }

    @Cacheable(key="#id")
    public Pessoa getPessoa(Long id){
        logger.info("Não encontrei pessoa no cache vou buscar no banco");
        Pessoa pessoa = pessoaRepository.findById(id).orElseThrow(()-> new RuntimeException("Pessoa não encontrada"));
        return pessoa;
    }

    @CacheEvict(value = "pessoa", allEntries = true)
    public void cleanCache() {
        logger.info("Limpando o Cache");
    }
}
