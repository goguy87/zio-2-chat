package guygo.chat

import java.util.UUID

package object effects {

  opaque type UserId = UUID
  opaque type ChatMessageId = UUID

  object UserId:
    def from(uuid: UUID): UserId = uuid

  object ChatMessageId:
    def from(uuid: UUID): ChatMessageId = uuid

}
