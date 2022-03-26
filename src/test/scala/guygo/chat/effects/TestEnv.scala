package guygo.chat.effects

import zio.{Random, ZEnv, ZIO}

object TestEnv:

  private type TestEnv = Users.Service

  def run[E, A](zio: ZIO[TestEnv with ZEnv, E, A]): ZIO[ZEnv, E, A] =
    zio.provideSomeLayer[ZEnv](UsersLive.live.fresh)
