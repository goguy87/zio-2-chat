package guygo.chat.effects

import guygo.chat.effects.ListChatMessages.Filter
import zio.{Random, Ref, Task, UIO, ZLayer}

case class ChatMessagesLive(ref: Ref[Map[ChatMessageId, ChatMessage]]) 
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
      case Filter.From(from) =>
        ref.get
          .map(_.values
            .filter(_.from equals from))
          .map(chatMessages => ListChatMessagesResponse(chatMessages.toSeq))

      case Filter.Empty =>
        ref.get.map { db =>
          ListChatMessagesResponse(db.values.toSeq)
        }
    }

  private def toChatMessage(request: CreateChatMessage): UIO[ChatMessage] =
    Random.nextUUID
      .map(ChatMessageId.from)
      .map(ChatMessage(_, request.to, request.from, request.message))

object ChatMessagesLive:

  val layer = ZLayer.fromZIO(Ref.make(Map.empty[ChatMessageId, ChatMessage])) >>>
    ZLayer.fromFunction(ChatMessagesLive.apply _)
