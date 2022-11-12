import guygo.chat.api.v1.chat_service.ZioChatService.ChatServiceClient
import scalapb.zio_grpc.ZManagedChannel
import io.grpc.ManagedChannelBuilder
import zio.{Scope, ZIO, ZLayer}

object ChatClient {

  val channel = ZManagedChannel[Any](
    ManagedChannelBuilder
      .forAddress("localhost", 9000)
      .usePlaintext()
  )

  val scoped: ZIO[Scope, Throwable, ChatServiceClient.ZService[Any, Any]] = 
    ChatServiceClient.scoped(channel)
}
