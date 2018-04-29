
import akka.actor.AbstractLoggingActor
import akka.actor.ActorSystem
import akka.actor.Props
import akka.japi.pf.ReceiveBuilder

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

    val actorSystem = ActorSystem.create("part4")
    val loggerRef1 = actorSystem.actorOf(Props.create(LoggingActor::class.java), "Logger1")
    val loggerRef2 = actorSystem.actorOf(Props.create(LoggingActor::class.java), "Logger2")
    val loggerRef3 = actorSystem.actorOf(Props.create(LoggingActor::class.java), "Logger3")
    val eventStream = actorSystem.eventStream()
    // setup our subscriptions
    eventStream.subscribe(loggerRef1, Event::class.java)
    eventStream.subscribe(loggerRef2, Event1::class.java)
    eventStream.subscribe(loggerRef3, Event2::class.java)
    // publish some messages to the event stream
    eventStream.publish(Event1())
    eventStream.publish(Event2())
}