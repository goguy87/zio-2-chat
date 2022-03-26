package guygo.chat.effects

import zio._
import zio.test._
import zio.test.Assertion._

object UsersTest extends DefaultRunnableSpec {

  def spec = suite("Users") {

    test("create a user") {
      TestEnv.run {
        for
          user <- Users(_.create(CreateUserRequest("guy")))
          result <- Users(_.get(user.id))
        yield assert(user)(
          hasField("name", (user: User) => user.name, equalTo("guy")))
          && assertTrue(result contains user)
      }
    }
  }

}
