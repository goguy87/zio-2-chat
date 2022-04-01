package guygo.chat.effects

import zio.{Accessible, Function2ToLayerOps, Random, Ref, Task, UIO}

object ChatMessages extends Accessible[ChatMessages.Service]:

  trait Service:

    def create(request: CreateChatMessage): Task[ChatMessage]

    def get(id: ChatMessageId): Task[Option[ChatMessage]]


case class CreateChatMessage(to: UserId, from: UserId, message: Option[String])

case class ChatMessage(id: ChatMessageId, to: UserId, from: UserId, message: Option[String])

case class ChatMessagesLive(ref: Ref[Map[ChatMessageId, ChatMessage]], random: Random) extends ChatMessages.Service:

  def create(request: CreateChatMessage): Task[ChatMessage] =
    for
      chatMessage <- toChatMessage(request)
      _ <- ref.update(_.updated(chatMessage.id, chatMessage))
    yield chatMessage

  def get(id: ChatMessageId): Task[Option[ChatMessage]] =
    ref.get.map(_.get(id))

  private def toChatMessage(request: CreateChatMessage): UIO[ChatMessage] =
    random.nextUUID
      .map(ChatMessage(_, request.to, request.from, request.message))


object ChatMessagesLive:

  val live = Ref.make(Map.empty[ChatMessageId, ChatMessage]).toLayer >>> ChatMessagesLive.apply.toLayer


