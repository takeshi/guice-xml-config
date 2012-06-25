package datastore
import com.redis.RedisClient
import com.redis.RedisClientPool

class RedisClientManager {
  var host = "localhost"
  var port = 6379

  lazy val clientPool = new RedisClientPool(host, port)

}