import akka.actor.AbstractLoggingActor
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.japi.pf.ReceiveBuilder
import akka.routing.BroadcastGroup

fun main(args: Array<String>) {

    open class Event
    class Event1 : Event()
    class Event2 : Event()

    class LoggingActor : AbstractLoggingActor() {

        override fun createReceive() =
                ReceiveBuilder()
                        .match(Event1::class.java) { log().info("Received Event 1") }
                        .match(Event2::class.java) { log().info("Received Event 2") }
                        .build()
    }

    val actorSystem = ActorSystem.create("part5")
    val loggerRef1 = actorSystem.actorOf(Props.create(LoggingActor::class.java), "Logger1")
    val loggerRef2 = actorSystem.actorOf(Props.create(LoggingActor::class.java), "Logger2")
    val loggerRef3 = actorSystem.actorOf(Props.create(LoggingActor::class.java), "Logger3")

    val routees = listOf(loggerRef1, loggerRef2, loggerRef3).map { it.path().toString() }
    val broadcastRef = actorSystem.actorOf(BroadcastGroup(routees).props(), "broadcaster")

    broadcastRef.tell(Event1(), ActorRef.noSender())
    broadcastRef.tell(Event2(), ActorRef.noSender())
}