package br.com.nfrpaiva.cachedemo.view;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.nfrpaiva.cachedemo.dominio.Pessoa;
import br.com.nfrpaiva.cachedemo.dominio.PessoaService;

@RestController
@RequestMapping("pessoa")
public class PessoaController {

    @Autowired
    private PessoaService pessoaService;

    @GetMapping
    public List<Pessoa> getPessoas (){
        return pessoaService.getAll();
    }
    @GetMapping("{id}")
    public Pessoa getByID(@PathVariable Long id){
        return pessoaService.getPessoa(id);
    }
    
}