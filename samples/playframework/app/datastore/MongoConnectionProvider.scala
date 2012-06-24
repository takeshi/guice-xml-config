package datastore
import com.google.inject.Provider
import com.mongodb.casbah.MongoConnection
import com.mongodb.Mongo

import javax.annotation.PreDestroy

class MongoConnectionProvider extends Provider[MongoConnection] {

  var server = "127.0.0.1"
  var port = 27017
  var name = "test"

  lazy val mongo = new Mongo(server, port)

  lazy val connection = new MongoConnection(mongo);

  override def get(): MongoConnection = {
    connection
  }

  @PreDestroy
  def close() {
    mongo.close()
  }
  
}