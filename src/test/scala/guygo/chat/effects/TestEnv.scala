package guygo.chat.effects

import zio.{Random, ZEnv, ZIO}

object TestEnv:

  private type TestEnv = Users.Service & ChatMessages.Service

  def evaluate[E, A](zio: ZIO[TestEnv with ZEnv, E, A]): ZIO[ZEnv, E, A] =
    zio.provideSome[ZEnv](UsersLive.live.fresh, ChatMessagesLive.live.fresh)
