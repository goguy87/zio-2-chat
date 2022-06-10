package guygo.chat.effects

import zio.{Random, Ref, Task, UIO, RIO, ZIO, ZLayer}
import guygo.chat.effects.ListChatMessages.Filter

trait ChatMessages:
  def create(request: CreateChatMessage): Task[ChatMessage]

  def get(id: ChatMessageId): Task[Option[ChatMessage]]

  def listChatMessages(request: ListChatMessages): Task[ListChatMessagesResponse]

object ChatMessages:

  def create(request: CreateChatMessage): RIO[ChatMessages, ChatMessage] =
    ZIO.serviceWithZIO(_.create(request))

  def get(id: ChatMessageId): RIO[ChatMessages, Option[ChatMessage]] =
    ZIO.serviceWithZIO(_.get(id))

  def listChatMessages(request: ListChatMessages): RIO[ChatMessages, ListChatMessagesResponse] =
    ZIO.serviceWithZIO(_.listChatMessages(request))

case class CreateChatMessage(to: UserId, from: UserId, message: Option[String])

case class ListChatMessages(filter: Filter = Filter.Empty, page: Page = Page.first)

object ListChatMessages:
  sealed trait Filter

  object Filter:
    case class ByUser(id: UserId) extends Filter

    case object Empty extends Filter

case class ListChatMessagesResponse(chatMessages: Seq[ChatMessage], pagingMetadata: PagingMetadata)

case class ChatMessage(id: ChatMessageId, to: UserId, from: UserId, message: Option[String])
