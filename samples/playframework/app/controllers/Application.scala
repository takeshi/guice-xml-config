package controllers

import scala.collection.mutable.LinkedList

import com.redis.RedisClientPool

import akka.actor.actorRef2Scala
import akka.actor.Actor
import akka.actor.Props
import common.LogMsg
import common.Logger
import common.LoggingAction
import javax.inject.Inject
import play.api.Play.current
import play.api.libs.concurrent.Akka
import play.api.libs.iteratee.Enumerator.Pushee
import play.api.libs.iteratee.Enumerator
import play.api.libs.iteratee.Input
import play.api.libs.iteratee.Iteratee
import play.api.mvc.Controller
import play.api.mvc.WebSocket
import service.MongoService
object Sample {
  trait Test {

  }
}
case class Message(m: String, pushee: Enumerator.Pushee[String])
case class Close(pushee: Enumerator.Pushee[String])

class MyActor extends Actor {
  val logger = Logger[MyActor]
  override def receive = {
    case m: String => {
      println("broadcast message " + m + " " + Application.list.size)
      for (e <- Application.list) {
        self.tell(Message(m, e))
      }
    }
    case ms: Message => {
      logger.log(LogMsg.IN, ms.m)
      ms.pushee.push(ms.m);
    }
    case c: Close => c.pushee.close();
    case _ => println("not found")
  }
}

object Application extends Controller {

  @Inject
  var service: MongoService = _

  @Inject
  var clientPool: RedisClientPool = _

  def index = LoggingAction { request =>
    //    clientPool.withClient(client => {
    //      client.subscribe("list-2")(e => print("sub " + e))
    //    })

    Async {
      Akka.future {
        def i = service.invoke
        var value: Option[String] = null
        clientPool.withClient { client =>
          client.subscribe("list-2")(m => println(m))
          //          value = client.lpop("list-1");
        }
        Ok(views.html.index("Your new application is ready. " + i + " " + value));
      }
    }
  }

  var list: LinkedList[Pushee[String]] = new LinkedList[Pushee[String]]

  def socket =
    WebSocket.using[String] { handler =>
      val system = Akka.system;
      val myActor = system.actorOf(Props[MyActor]);
      val in = Iteratee.foreach[String] { m => println(m); myActor ! m }.mapDone { _ =>
        println("Disconnected")
      }
      var out = Enumerator.pushee[String]({ e => list = list :+ e }, {
        print("complete")
      }, (m: String, i: Input[String]) => print(m + " " + i))

      (in, out)
    }
}