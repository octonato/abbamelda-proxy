package concurrent

import play.api.libs.concurrent.Akka
import play.api.Play.current
import scala.concurrent.ExecutionContext

object Execution {

  implicit val wsExecutionContext: ExecutionContext =
    Akka.system.dispatchers.lookup("contexts.ws-lookups")
}
