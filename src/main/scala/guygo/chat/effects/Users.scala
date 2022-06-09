package guygo.chat.effects

import zio.{Random, Ref, Task, RIO, ZIO, ZLayer}

trait Users:
  def create(request: CreateUser): Task[User]

  def get(id: UserId): Task[Option[User]]

object Users:

  def create(request: CreateUser): RIO[Users, User] =
    ZIO.serviceWithZIO[Users](_.create(request))

  def get(id: UserId): RIO[Users, Option[User]] =
    ZIO.serviceWithZIO[Users](_.get(id))


case class CreateUser(name: String)

case class User(id: UserId, name: String)

case class UsersLive(ref: Ref[Map[UserId, User]]) extends Users:

  def create(request: CreateUser): Task[User] =
    for
      id <- Random.nextUUID
      user = User(id, request.name)
      _ <- ref.update(_.updated(id, user))
    yield user

  def get(id: UserId): Task[Option[User]] =
    ref.get.map(_.get(id))

object UsersLive:

  val layer = ZLayer.fromZIO(Ref.make(Map.empty[UserId, User])) >>>
    ZLayer.fromFunction(UsersLive.apply _)
