package guygo.chat.effects

import guygo.chat.effects.ChatMessagesLive.*
import guygo.chat.effects.ListChatMessages.Filter
import guygo.chat.effects.PaginationOps.*
import zio.{Random, Ref, Task, UIO, ZLayer}

case class ChatMessagesLive(ref: Ref[Database])
  extends ChatMessages:

  def create(request: CreateChatMessage): Task[ChatMessage] =
    for
      chatMessage <- toChatMessage(request)
      _ <- ref.update(_.updated(chatMessage.id, chatMessage))
    yield chatMessage

  def get(id: ChatMessageId): Task[Option[ChatMessage]] =
    ref.get.map(_.get(id))

  def listChatMessages(request: ListChatMessages): Task[ListChatMessagesResponse] =
    request.filter match {
      case filter: Filter.ByUser => list(filter, request.page)
      case Filter.Empty => all(request.page)
    }

  private def list(filter: Filter.ByUser, page: Page) =
    ref.get
      .map(_.values.toSeq
        .filter(_.from equals filter.id)
        .paginate(page))
      .map(chatMessages => ListChatMessagesResponse(
        chatMessages = chatMessages,
        pagingMetadata = PagingMetadata.Count(chatMessages.size)))

  private def all(page: Page) = ref.get.map { db =>
    val chatMessages = db.values.toSeq.paginate(page)

    ListChatMessagesResponse(
      chatMessages = chatMessages,
      pagingMetadata = PagingMetadata.Count(chatMessages.size))
  }

  private def toChatMessage(request: CreateChatMessage): UIO[ChatMessage] =
    Random.nextUUID
      .map(ChatMessageId.from)
      .map(ChatMessage(_, request.to, request.from, request.message))

object ChatMessagesLive:

  val layer = ZLayer.fromZIO(Ref.make(Map.empty[ChatMessageId, ChatMessage])) >>>
    ZLayer.fromFunction(ChatMessagesLive.apply _)

  type Database = Map[ChatMessageId, ChatMessage]
