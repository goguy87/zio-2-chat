import zio.*
import zio.Console
import guygo.chat.api.v1.chat_service.HelloRequest
import guygo.chat.api.v1.chat_service.ZioChatService.ChatServiceClient
import guygo.chat.effects.*
import guygo.chat.service.ChatService

object Main extends ZIOAppDefault {

  val myApp = for {
    _ <- ChatServerMain.myAppLogic.fork
    client <- ChatClient.scoped
    _ <- ZIO.scoped {
      client.sayHello(HelloRequest("guyg"))
    }
  } yield ()

  def run = myApp
  
}
