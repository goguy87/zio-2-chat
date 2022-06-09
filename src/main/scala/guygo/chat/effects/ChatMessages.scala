package guygo.chat.effects

import zio.{Random, Ref, Task, UIO, RIO, ZIO, ZLayer}
import guygo.chat.effects.ListChatMessages.Filter

trait ChatMessages:
  def create(request: CreateChatMessage): Task[ChatMessage]

  def get(id: ChatMessageId): Task[Option[ChatMessage]]

  def listChatMessages(request: ListChatMessages): Task[ListChatMessagesResponse]

object ChatMessages:

  def create(request: CreateChatMessage): RIO[ChatMessages, ChatMessage] =
    ZIO.serviceWithZIO[ChatMessages](_.create(request))

  def get(id: ChatMessageId): RIO[ChatMessages, Option[ChatMessage]] =
    ZIO.serviceWithZIO[ChatMessages](_.get(id))

  def listChatMessages(request: ListChatMessages): RIO[ChatMessages, ListChatMessagesResponse] =
    ZIO.serviceWithZIO[ChatMessages](_.listChatMessages(request))

case class CreateChatMessage(to: UserId, from: UserId, message: Option[String])

case class ListChatMessages(filter: Filter = Filter.Empty)

object ListChatMessages:
  sealed trait Filter

  object Filter:
    case class From(from: UserId) extends Filter

    case object Empty extends Filter

case class ListChatMessagesResponse(chatMessages: Seq[ChatMessage])

case class ChatMessage(id: ChatMessageId, to: UserId, from: UserId, message: Option[String])

case class ChatMessagesLive(ref: Ref[Map[ChatMessageId, ChatMessage]]) extends ChatMessages:

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
      case Filter.Empty => ZIO.succeed(ListChatMessagesResponse(Seq.empty))
    }

  private def toChatMessage(request: CreateChatMessage): UIO[ChatMessage] =
    Random.nextUUID
      .map(ChatMessage(_, request.to, request.from, request.message))


object ChatMessagesLive:

  val layer = ZLayer.fromZIO(Ref.make(Map.empty[ChatMessageId, ChatMessage])) >>>
    ZLayer.fromFunction(ChatMessagesLive.apply _)


