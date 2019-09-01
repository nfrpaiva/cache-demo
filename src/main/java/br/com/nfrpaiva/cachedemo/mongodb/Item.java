package br.com.nfrpaiva.cachedemo.mongodb;

import org.springframework.data.annotation.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class Item {

    public Item(String mensagem) {
        this.mensagem = mensagem;
    }

    @Id
    private String id;

    private String mensagem;

    private String status;

}