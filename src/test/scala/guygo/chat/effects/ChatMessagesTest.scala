package guygo.chat.effects

import zio._
import zio.test._
import zio.test.Assertion._
import guygo.chat.effects.ListChatMessages._

object ChatMessagesTest extends ZIOSpecDefault {

  def createChatMessage(toUserId: UserId, fromUserId: UserId, message: String) =
    ChatMessages.create(
      CreateChatMessage(
        to = toUserId,
        from = fromUserId,
        message = Some(message)))

  def spec = suite("ChatMessages") {

    test("create a chat message") {
      for
        to <- Random.nextUUID
        from <- Random.nextUUID
        message = "hello user!"
        chatMessage <- createChatMessage(to, from, message)
        result <- ChatMessages.get(chatMessage.id)
      yield assert(chatMessage) {
        hasField[ChatMessage, UserId]("from", _.from, equalTo(from))
          && hasField[ChatMessage, UserId]("to", _.to, equalTo(to))
          && hasField[ChatMessage, Option[String]]("message", _.message, equalTo(Some(message)))
      } && assertTrue(result contains chatMessage)
    }

    test("list all chat messages from user") {
      for
        to <- Random.nextUUID
        from <- Random.nextUUID
        from2 <- Random.nextUUID
        message = "hello user!"
        chat1 <- createChatMessage(to, from, message)
        chat2 <- createChatMessage(to, from, message)
        chat3 <- createChatMessage(to, from2, message)
        result <- ChatMessages.listChatMessages(ListChatMessages(Filter.From(from)))
      yield assertTrue(result.chatMessages.size == 2
        && !result.chatMessages.exists(_.id equals chat3.id))
    }

    /**
     * TODO
     * 1. list messages pagination
     * 2. implicit tenant
     */
  }.provideCustomLayer(ChatMessagesLive.layer)

}
