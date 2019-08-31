package br.com.nfrpaiva.cachedemo.dominio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("lock")
public class LockController {

    @Autowired
    private HelloService service;

    @GetMapping
    public String sayHello (){

        return service.sayHello();

    }
    
}