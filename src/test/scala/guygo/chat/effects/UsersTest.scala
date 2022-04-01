package guygo.chat.effects

import zio._
import zio.test._
import zio.test.Assertion._

object UsersTest extends DefaultRunnableSpec {

  def spec = suite("Users") {

    test("create a user") {
      TestEnv.evaluate {
        for
          user <- Users(_.create(CreateUser("guy")))
          result <- Users(_.get(user.id))
        yield assert(user) {
          hasField[User, String]("name", _.name, equalTo("guy"))
        } && assertTrue(result contains user)
      }
    }
  }

}
