package ch.n3utrino.coronadiary

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.annotation.Id
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import redis.clients.jedis.JedisPoolConfig
import java.time.LocalDate


@Configuration
@EnableRedisRepositories
internal class AppConfig {

    @Bean
    fun jedisConnectionFactory(): RedisConnectionFactory? {
        val poolConfig = JedisPoolConfig()
        poolConfig.maxTotal = 10
        poolConfig.maxIdle = 5
        poolConfig.minIdle = 1
        poolConfig.testOnBorrow = true
        poolConfig.testOnReturn = true
        poolConfig.testWhileIdle = true
        return JedisConnectionFactory(poolConfig)
    }


}

@RedisHash("timeline")
data class TimelineDbo(@Id val id: String, val contactDate: LocalDate?, val symptomDate: LocalDate?, val testDate: LocalDate?)

@Repository
interface TimelineRepository : CrudRepository<TimelineDbo?, String?>