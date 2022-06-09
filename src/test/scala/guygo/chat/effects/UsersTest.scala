package guygo.chat.effects

import zio._
import zio.test._
import zio.test.Assertion._

object UsersTest extends ZIOSpecDefault {

  def spec = suite("Users") {

    test("create a user") {
      for
        user <- Users.create(CreateUser("guy"))
        result <- Users.get(user.id)
      yield assert(user) {
        hasField[User, String]("name", _.name, equalTo("guy"))
      } && assertTrue(result contains user)
    }
  }.provideCustomLayer(UsersLive.layer)

}
