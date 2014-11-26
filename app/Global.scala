import models.{OnderdeelFactory, Bootstrap}
import play.api.{Application, GlobalSettings}
import play.libs.Akka

object Global extends GlobalSettings {

  override def onStart(app: Application): Unit = {

    val sys = Akka.system()

    val onderdeelFactory = OnderdeelFactory(sys)

    val bootstrap = sys.actorOf(Bootstrap.props(onderdeelFactory), Bootstrap.name)

    bootstrap ! Bootstrap.Start
  }

}
