package guygo.chat.effects

import zio.*
import zio.test.*
import zio.test.Assertion.*
import guygo.chat.effects.ListChatMessages.*

import java.util.UUID

object ChatMessagesTest extends ZIOSpecDefault:

  /**
   * TODO
   * 1. implicit tenant
   */

  def spec = suite("ChatMessages")(
    create,
    get,
    list,
    listWithPaging,
    listWithFilterByUser,
    listWithFilterByUserAdPagination
  ).provideCustomLayer(ChatMessagesLive.layer)

  val randomUserId = Random.nextUUID.map(UserId.from)
  val message = "hello user!"

  def createChatMessage(toUserId: UserId, fromUserId: UserId, message: String) =
    ChatMessages.create(
      CreateChatMessage(
        to = toUserId,
        from = fromUserId,
        message = Some(message)))

  val id = UUID.randomUUID

  val create = test("create a chat message") {
    for
      to <- randomUserId
      from <- randomUserId
      _ <- TestRandom.feedUUIDs(id)
      chatMessage <- createChatMessage(to, from, message)
    yield assertTrue {
      chatMessage == ChatMessage(
        ChatMessageId.from(id),
        to,
        from,
        Some(message))
    }
  }

  val get = test("get a chat message") {
    for
      to <- randomUserId
      from <- randomUserId
      createdChatMessage <- createChatMessage(to, from, message)
      chatMessage <- ChatMessages.get(createdChatMessage.id)
    yield assertTrue(chatMessage contains createdChatMessage)
  }

  val list = test("list all chat messages") {
    for
      to <- randomUserId
      from <- randomUserId
      from2 <- randomUserId
      chat1 <- createChatMessage(to, from, message)
      chat2 <- createChatMessage(to, from, message)
      chat3 <- createChatMessage(to, from2, message)
      result <- ChatMessages.listChatMessages(ListChatMessages())
    yield assert(result.chatMessages) {
      hasSameElements(Seq(chat1, chat2, chat3))
    }
  }

  val listWithPaging = test("list all chat messages with pagination") {
    for
      to <- randomUserId
      from <- randomUserId
      from2 <- randomUserId
      chat1 <- createChatMessage(to, from, message)
      chat2 <- createChatMessage(to, from, message)
      chat3 <- createChatMessage(to, from2, message)
      request = ListChatMessages(
        page = Page.Offset(0, 2))
      firstPage <- ChatMessages.listChatMessages(request)
      secondPage <- ChatMessages.listChatMessages(request.copy(page = Page.Offset(2, 2)))
    yield assert(firstPage) {
      hasField[ListChatMessagesResponse, Seq[ChatMessage]]("chatMessages", _.chatMessages, hasSameElements(Seq(chat1, chat2))) &&
        hasField("pagingMetadata", _.pagingMetadata, equalTo(PagingMetadata.Count(2)))
    } && assert(secondPage) {
      hasField[ListChatMessagesResponse, Seq[ChatMessage]]("chatMessages", _.chatMessages, hasSameElements(Seq(chat3))) &&
        hasField("pagingMetadata", _.pagingMetadata, equalTo(PagingMetadata.Count(1)))
    }
  }

  val listWithFilterByUser = test("list all chat messages from user") {
    for
      to <- randomUserId
      from <- randomUserId
      from2 <- randomUserId
      chat1 <- createChatMessage(to, from, message)
      chat2 <- createChatMessage(to, from, message)
      _ <- createChatMessage(to, from2, message)
      result <- ChatMessages.listChatMessages(ListChatMessages(Filter.ByUser(from)))
    yield assert(result.chatMessages) {
      hasSameElements(Seq(chat1, chat2))
    }
  }

  val listWithFilterByUserAdPagination = test("list all chat messages from user with pagination") {
    for
      to <- randomUserId
      from <- randomUserId
      from2 <- randomUserId
      chat1 <- createChatMessage(to, from, message)
      chat2 <- createChatMessage(to, from, message)
      chat3 <- createChatMessage(to, from, message)
      request = ListChatMessages(
        filter = Filter.ByUser(from),
        page = Page.Offset(0, 2))
      _ <- createChatMessage(to, from2, message)
      firstPage <- ChatMessages.listChatMessages(request)
      secondPage <- ChatMessages.listChatMessages(request.copy(page = Page.Offset(2, 2)))
    yield assert(firstPage.chatMessages) {
      hasSameElements(Seq(chat1, chat2))
    } && assert(secondPage.chatMessages) {
      hasSameElements(Seq(chat3))
    }
  }
