package br.com.nfrpaiva.cachedemo.config;

import java.util.ArrayList;
import java.util.List;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.ManagementCenterConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MaxSizeConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.cache.HazelcastCacheManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HazelCastConfiguration {

    private Logger logger = LoggerFactory.getLogger(HazelCastConfiguration.class);
    private List<MapConfig> mapConfigList;

    @Bean
    public Config hazelCastConfig() {
        Config config = new Config();
        
        config
        .setInstanceName("hz_instance_name")
        .getGroupConfig().setName("sva-cot");
        getMapConfigList().forEach(cfg -> config.addMapConfig(cfg));
        
        JoinConfig join = config.getNetworkConfig().getJoin();
        join.getMulticastConfig().setEnabled(false);
        join.getTcpIpConfig().addMember("localhost,192.168.0.10,192.168.0.4").setEnabled(true);

        ManagementCenterConfig mcConfig = new ManagementCenterConfig();
        mcConfig.setEnabled(true).setUrl("http://localhost:8080/hazelcast-mancenter");
        config.setManagementCenterConfig(mcConfig);

        return config;
    }

    //@Bean
    public ClientConfig clientConfig() {
        ClientConfig clientConfig = new ClientConfig();
        //clientConfig.getGroupConfig().setName("hazelcast-demo-group");
        ClientNetworkConfig networkConfig = clientConfig.getNetworkConfig();
        networkConfig.addAddress("192.168.0.10:5701")
                .setSmartRouting(true)
                //.addOutboundPortDefinition("34700-34703")
                //.setRedoOperation(true)
                .setConnectionAttemptPeriod(5000)
                .setConnectionTimeout(5000)
                .setConnectionAttemptLimit(50);

        return clientConfig;

    }

    //@Bean
    public HazelcastInstance hazelcastInstance(ClientConfig clientConfig) {
        HazelcastInstance hazelcastInstance = HazelcastClient.newHazelcastClient(clientConfig);
        final Config config = hazelcastInstance.getConfig();
        
        mapConfigList = getMapConfigList();
        mapConfigList.forEach(cfg -> {
            try {
                config.addMapConfig(cfg);
                logger.info("Configuração {} adicionada com sucesos", cfg.getName());
            }catch(Exception e){
                logger.error("Não foi possível adicionar a configuração." + 
                            "Esse erro  é comum quando já existe uma configuração " +
                            "diferente da informada na aplicação no servidor", e);
            }
        });

        hazelcastInstance.getMap("pessoa").clear();
        return hazelcastInstance;
    }

    private List<MapConfig> getMapConfigList (){
        List<MapConfig> maps =  new ArrayList<>();
        maps.add(pessoaMapConfig());
        maps.add(testMapMapConfig());
        return maps;
    }

    private MapConfig pessoaMapConfig() {
        return new MapConfig()
                .setName("pessoa")
                .setMaxSizeConfig(new MaxSizeConfig(100, MaxSizeConfig.MaxSizePolicy.PER_NODE))
                .setEvictionPolicy(EvictionPolicy.LRU)
                //.setMaxIdleSeconds(5)
                .setBackupCount(0)
                .setTimeToLiveSeconds(120);
    }
    private MapConfig testMapMapConfig() {
        return new MapConfig()
                .setName("test-map")
                .setMaxSizeConfig(new MaxSizeConfig(5_000_000, MaxSizeConfig.MaxSizePolicy.PER_NODE))
                .setEvictionPolicy(EvictionPolicy.LRU)
                .setBackupCount(0)
                //.setTimeToLiveSeconds(5);
                ;
    }

    @Bean
    public CacheManager cacheManager(HazelcastInstance hazelcastInstance) {
        return new HazelcastCacheManager(hazelcastInstance);
    }

}
