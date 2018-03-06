import akka.actor.AbstractLoggingActor
import akka.actor.ActorSystem
import akka.actor.Props
import akka.japi.pf.ReceiveBuilder
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit

fun main(args: Array<String>) {

    class ScheduledActor : AbstractLoggingActor() {

        override fun preStart() {
            super.preStart()
            schedule("Hello Kotlin")
        }

        override fun createReceive() = ReceiveBuilder().match(String::class.java, this::onMessage).build()

        private fun onMessage(message: String) {
            log().info(message)
            schedule("Hello Again")
        }

        private fun schedule(message: String) =
                context.system.scheduler().scheduleOnce(
                Duration.create(1, TimeUnit.SECONDS),
                self, // target actor ref
                message, // message to put in the targets mailbox when triggered.
                context.system.dispatcher(),
                self // sender actor ref
        )
    }

    val actorSystem = ActorSystem.create("part3")
    actorSystem.actorOf(Props.create(ScheduledActor::class.java), "scheduled")
}