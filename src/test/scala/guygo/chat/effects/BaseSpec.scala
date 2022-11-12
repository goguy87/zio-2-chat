package guygo.chat.effects

import guygo.chat.effects.BaseSpec._
import zio.Scope
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault}

trait BaseSpec extends ZIOSpecDefault {

  def baseSpec: Spec[TestEnv, Any]

  override def spec = baseSpec.provideLayer(testLayer)
}

object BaseSpec {
  type TestEnv = ChatMessages with Users

  val testLayer = ChatMessagesLive.layer ++ UsersLive.layer
}
