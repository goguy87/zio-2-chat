package guygo.chat.effects

import zio.{Random, Ref, Task, ZLayer}

case class UsersLive(ref: Ref[Map[UserId, User]]) extends Users:

  def create(request: CreateUser): Task[User] =
    for
      id <- Random.nextUUID.map(UserId.from)
      user = User(id, request.name)
      _ <- ref.update(_.updated(id, user))
    yield user

  def get(id: UserId): Task[Option[User]] =
    ref.get.map(_.get(id))

object UsersLive:

  val layer = ZLayer.fromZIO(Ref.make(Map.empty[UserId, User])) >>>
    ZLayer.fromFunction(UsersLive.apply _)
