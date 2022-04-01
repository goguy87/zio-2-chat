package guygo.chat.effects

import zio._
import zio.test._
import zio.test.Assertion._

object ChatMessagesTest extends DefaultRunnableSpec {

  def spec = suite("ChatMessages") {

    test("create a chat message") {
      TestEnv.evaluate {
        for
          toUserId <- Random.nextUUID
          fromUserId <- Random.nextUUID
          message = "hello user!"
          chatMessage <- ChatMessages(_.create(
            CreateChatMessage(
              to = toUserId,
              from = fromUserId,
              message = Some(message))))
          result <- ChatMessages(_.get(chatMessage.id))
        yield assert(chatMessage) {
          hasField[ChatMessage, UserId]("from", _.from, equalTo(fromUserId))
            && hasField[ChatMessage, UserId]("to", _.to, equalTo(toUserId))
            && hasField[ChatMessage, Option[String]]("message", _.message, equalTo(Some(message)))
        } && assertTrue(result contains chatMessage)
      }
    }
  }

}
