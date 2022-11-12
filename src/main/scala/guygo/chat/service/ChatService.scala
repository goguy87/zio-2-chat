package guygo.chat.service

import guygo.chat.api.v1.chat_service.*
import guygo.chat.api.v1.chat_service.ZioChatService.RChatService
import io.grpc.Status
import zio.ZIO
import zio.Console.printLine

object ChatService extends RChatService[Any] {

  override def sayHello(request: HelloRequest): ZIO[Any, Status, HelloReply] =
    hello(request)
      .mapError(Status.fromThrowable)

  private def hello(request: HelloRequest) =
    for {
      hi <- ZIO.attempt(s"hi from: ${request.name}")
      _ <- printLine(hi)
    } yield HelloReply(hi)

}
