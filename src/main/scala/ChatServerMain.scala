import guygo.chat.service.ChatService
import scalapb.zio_grpc.{ServerMain, ServiceList}

object ChatServerMain extends ServerMain {

  override def services: ServiceList[Any] =
    ServiceList.add(ChatService)

}
