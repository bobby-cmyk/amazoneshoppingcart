package vttp.batch5.ssf.shoppingcart;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class AppConfig {
    
    // Initialise a logger
    private final Logger logger = Logger.getLogger(AppConfig.class.getName());

    // Get all redis configuration from environment variables

    //SPRING_DATA_REDIS_HOST
    @Value("${spring.data.redis.host}")
    private String redisHost;

    //SPRING_DATA_REDIS_PORT
    @Value("${spring.data.redis.port}")
    private int redisPort;

    //SPRING_DATA_REDIS_DATABASE
    @Value("${spring.data.redis.database}")
    private int redisDatabase;

    //SPRING_DATA_REDIS_USERNAME
    @Value("${spring.data.redis.username}")
    private String redisUsername;

    //SPRING_DATA_REDIS_PASSWORD
    @Value("${spring.data.redis.password}")
    private String redisPassword;

    @Bean("redis-object")
    public RedisTemplate<String, Object> createRedisTemplateObject() {
        
        // Create a database configuration
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisHost, redisPort);

        // Set the database -> Selecting 0 (first database) -> assuming using free tier only allow for one database 
        config.setDatabase(redisDatabase);

        // Set the username and password -> will only be set when connecting to cloud
        if (!redisUsername.trim().equals("")) {
            logger.info("Setting Redis username and password");
            
            config.setUsername(redisUsername);
            config.setPassword(redisPassword);
        }

        // Now that configuring database is completed...
        // Create a connection to the database
        JedisClientConfiguration jedisClient = JedisClientConfiguration.builder().build();

        // Create a factory to connect to Redis
        JedisConnectionFactory jedisFac = new JedisConnectionFactory(config, jedisClient);
        jedisFac.afterPropertiesSet();

        // Create RedisTemplate
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(jedisFac);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        return redisTemplate;
    }
}