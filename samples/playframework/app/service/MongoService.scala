package service
import java.util.concurrent.atomic.AtomicInteger

import com.google.inject.Inject
import com.mongodb.casbah.Imports.MongoDBObject
import com.mongodb.casbah.Imports.wrapDBObj
import com.mongodb.casbah.MongoConnection
import com.novus.salat.global.ctx
import com.novus.salat.grater

import common.LogMsg
import common.Logger
import model.Item

object MongoService {
  val counter = new AtomicInteger;
  val inc = new AtomicInteger;

}

class MongoService {
  val logger = Logger[MongoService];

  @Inject
  var connection: MongoConnection = _

  def invoke = {
    val c = MongoService.counter.incrementAndGet();
    val inc = MongoService.inc.incrementAndGet();
    var collection = connection("play")("sample");
    val me = Item(id = inc, name = "me2", age = 54)
    val g = grater[Item]
    collection += g.asDBObject(me)
    MongoService.counter.decrementAndGet();
    logger.log(LogMsg.IN, c.toString() + " " + inc);
    val meInDB = collection.findOne(MongoDBObject("id" -> inc)).get
    logger.log(LogMsg.OUT, c.toString() + " " + inc);
    g.asObject(meInDB)

  }
}