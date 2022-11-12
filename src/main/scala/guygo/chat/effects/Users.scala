package guygo.chat.effects

import zio.{Random, Ref, Task, RIO, ZIO, ZLayer}

trait Users {
  def create(request: CreateUser): Task[User]

  def get(id: UserId): Task[Option[User]]
  
}

object Users {

  def create(request: CreateUser): RIO[Users, User] =
    ZIO.serviceWithZIO(_.create(request))

  def get(id: UserId): RIO[Users, Option[User]] =
    ZIO.serviceWithZIO(_.get(id))

}


case class CreateUser(name: String)

case class User(id: UserId, name: String)
