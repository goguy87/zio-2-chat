import zio.*
import zio.Console
import guygo.chat.effects._

object Main extends ZIOAppDefault:

  val appLogic = for
    user <- Users.create(CreateUser("guy"))
    result <- Users.get(user.id)
    _ <- Console.printLine(s"created user: $user, fetched result: $result")
  yield ()

  def run = appLogic.provide(UsersLive.layer, Random.live)

