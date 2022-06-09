package guygo.chat.effects

import zio.{Random, ZIO}

object TestEnv:

  private type TestEnv = Users & ChatMessages

  def evaluate[E, A](zio: ZIO[ TestEnv, E, A]): ZIO[Any, E, A] =
    zio.provideLayer(UsersLive.layer.fresh ++ ChatMessagesLive.layer.fresh)
