import zio.*
import zio.Console
import guygo.chat.effects._

object Main extends ZIOAppDefault:

  val appLogic = for
    user <- Users(_.create(CreateUserRequest("guy")))
    result <- Users(_.get(user.id))
    _ <- Console.printLine(s"created user: $user, fetched result: $res")
  yield ()

  def run = appLogic.provide(UsersLive.live, Random.live, Console.live)

