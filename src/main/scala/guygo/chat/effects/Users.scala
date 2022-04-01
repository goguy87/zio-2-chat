package guygo.chat.effects

import zio.{Accessible, Function2ToLayerOps, Random, Ref, Task, ZIO, ZLayer}

object Users extends Accessible[Users.Service]:

  trait Service:
    def create(request: CreateUser): Task[User]

    def get(id: UserId): Task[Option[User]]


case class CreateUser(name: String)

case class User(id: UserId, name: String)

case class UsersLive(ref: Ref[Map[UserId, User]], random: Random) extends Users.Service:

  def create(request: CreateUser): Task[User] =
    for
      id <- random.nextUUID
      user = User(id, request.name)
      _ <- ref.update(_.updated(id, user))
    yield user

  def get(id: UserId): Task[Option[User]] =
    ref.get.map(_.get(id))

object UsersLive:

  val live = Ref.make(Map.empty[UserId, User]).toLayer >>> UsersLive.apply.toLayer
