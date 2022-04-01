package guygo.chat.effects

import zio.{Accessible, Function2ToLayerOps, Random, Ref, Task, UIO, ZIO}
import guygo.chat.effects.ListChatMessages.Filter

object ChatMessages extends Accessible[ChatMessages.Service]:

  trait Service:

    def create(request: CreateChatMessage): Task[ChatMessage]

    def get(id: ChatMessageId): Task[Option[ChatMessage]]

    def listChatMessages(request: ListChatMessages): Task[ListChatMessagesResponse]


case class CreateChatMessage(to: UserId, from: UserId, message: Option[String])

case class ListChatMessages(filter: Filter = Filter.Empty)

object ListChatMessages:
  sealed trait Filter

  object Filter:
    case class From(from: UserId) extends Filter

    case object Empty extends Filter

case class ListChatMessagesResponse(chatMessages: Seq[ChatMessage])

case class ChatMessage(id: ChatMessageId, to: UserId, from: UserId, message: Option[String])

case class ChatMessagesLive(ref: Ref[Map[ChatMessageId, ChatMessage]], random: Random) extends ChatMessages.Service:

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
    random.nextUUID
      .map(ChatMessage(_, request.to, request.from, request.message))


object ChatMessagesLive:

  val live = Ref.make(Map.empty[ChatMessageId, ChatMessage]).toLayer >>> ChatMessagesLive.apply.toLayer


