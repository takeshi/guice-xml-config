package datastore
import com.mongodb.casbah.MongoConnection
import com.mongodb.Mongo

import javax.annotation.PreDestroy

class MongoConnectionProvider {

  var server = "127.0.0.1"
  var port = 27017
  var name = "test"

  lazy val mongo = new Mongo(server, port)

  lazy val connection = {
    def con = new MongoConnection(mongo);
    con.apply("play").dropDatabase()
    con
  }

  @PreDestroy
  def close() {
    mongo.close()
  }

}