package datastore
import org.junit.BeforeClass
import org.junit.Test
import com.google.inject.Guice
import com.mongodb.casbah.MongoConnection
import com.mongodb.casbah.Imports.MongoDBObject
import com.mongodb.casbah.Imports.wrapDBObj
import com.novus.salat.global.ctx
import com.novus.salat.grater
import javax.inject.Inject
import module.Dependencies
import org.junit.Before

case class User(id: Int, name: String, age: Int)

class DataStoreTest {

  @Before
  def before() {
    val injector = Guice.createInjector(new Dependencies);
    injector.injectMembers(this);
  }

  @Inject
  var connection: MongoConnection = _
  lazy val collection = connection("salat_test")("sample")
  //  val collection = MongoConnection()("salat_test")("sample")

  @Test
  def testDatstore() {
    collection += MongoDBObject("id" -> 1, "name" -> "me", "age" -> 27)
    println(collection.findOne(MongoDBObject("id" -> 1)).get)
  }

  @Test
  def testDS2() {
    val me = User(id = 2, name = "me2", age = 54)
    val g = grater[User]

    collection += g.asDBObject(me)
    val meInDB = collection.findOne(MongoDBObject("id" -> 2)).get
    println(meInDB.getClass)
    // class com.mongodb.BasicDBObject
    println(meInDB)
    // { "_id" : { "$oid" : "4d764830e10d23dc4758c29a"} , "_typeHint" : "User" , "id" : 2 , "name" : "me2" , "age" : 54}
    println(g.asObject(meInDB))
  }
}