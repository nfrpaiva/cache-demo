package br.com.nfrpaiva.cachedemo.mongodb;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;

import org.assertj.core.api.Assertions;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ItemRepositoryTest {

    @Autowired
    private ItemRepository repository;

    @Autowired
    private MongoTemplate template;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Before
    public void setup() {
        repository.deleteAll();
    }

    @Test
    public void testIfTemplateExists() {
        Assertions.assertThat(template).isNotNull();
    }

    @Test
    public void test() {
        Item item = new Item();
        item.setMensagem("Uma breve descricao");

    }

    @Test
    public void findAndModify() {
        Item item = new Item();
        item.setMensagem("Um item");
        Item result = repository.insert(item);
        System.out.println(result);
        Query query = Query.query(Criteria.where("descricao").is(item.getMensagem()));
        List<Item> find = template.find(query, Item.class);
        Update update = Update.update("status", "atualizado");
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);
        System.out.println(find);
        Item novoItem = template.findAndModify(query, update, options, Item.class);

        System.out.println(novoItem);
    }

    @Test
    public void grid() throws Exception {
        DBObject metaData = new BasicDBObject();
        metaData.put("user", "nilton");
        Assertions.assertThat(gridFsTemplate).isNotNull();
        InputStream logoStream = this.getClass().getClassLoader().getResourceAsStream("logo.svg");
        ObjectId imageID = gridFsTemplate.store(logoStream, "logo.svg", "image/svg", metaData);
        System.out.println(imageID);
        GridFSFile findOne = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(imageID)));
        System.out.println(findOne);
        InputStream inputStream = gridFsTemplate.getResource(findOne).getInputStream();
        Files.copy(inputStream, Paths.get("/tmp/logo.svg"), StandardCopyOption.REPLACE_EXISTING);
    }
}