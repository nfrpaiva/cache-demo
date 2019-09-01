package br.com.nfrpaiva.cachedemo.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ItemRepository extends MongoRepository<Item, String> {

}