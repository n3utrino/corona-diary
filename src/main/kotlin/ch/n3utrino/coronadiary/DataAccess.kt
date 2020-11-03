package ch.n3utrino.coronadiary

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import java.net.URI
import java.time.LocalDate


@Configuration
@EnableRedisRepositories
internal class RedisConfig {

    @Bean
    fun jedisPool(): JedisPool {
        val redisURI = URI(System.getenv("REDIS_URL") ?: "redis://localhost:6379")
        val poolConfig = JedisPoolConfig()
        poolConfig.maxTotal = 10
        poolConfig.maxIdle = 5
        poolConfig.minIdle = 1
        poolConfig.testOnBorrow = true
        poolConfig.testOnReturn = true
        poolConfig.testWhileIdle = true
        return JedisPool(poolConfig, redisURI)
    }

}

@RedisHash("timeline")
data class TimelineDbo(
    @Id val id: String,
    val contactDate: LocalDate?,
    val symptomDate: LocalDate?,
    val testDate: LocalDate?,
    val userAgent: String,
    val ip: String,
)

@Repository
interface TimelineRepository : CrudRepository<TimelineDbo?, String?>