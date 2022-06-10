package guygo.chat.effects

import zio.*
import zio.test.*
import zio.test.Assertion.*

import java.util.UUID

object UsersTest extends ZIOSpecDefault {

  def spec = suite("Users")(
    create,
    get
  ).provideCustomLayer(UsersLive.layer)

  val id = UUID.randomUUID
  val userId = UserId.from(id)
  val name = "guy"
  val createUser = Users.create(CreateUser(name))

  val create = test("create a user") {
    for
      _ <- TestRandom.feedUUIDs(id)
      user <- createUser
    yield  assertTrue(user == User(userId, name))
  }

  val get = test("get a user") {
    for
      createdUser <- createUser
      user <- Users.get(createdUser.id)
    yield  assertTrue(user contains createdUser)
  }

}
