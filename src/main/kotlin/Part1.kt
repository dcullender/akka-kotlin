import akka.actor.AbstractLoggingActor
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.japi.pf.ReceiveBuilder

fun main(args: Array<String>) {

    class HelloKotlinActor : AbstractLoggingActor() {
        override fun createReceive() = ReceiveBuilder().match(String::class.java) { log().info(it) }.build()
    }

    val actorSystem = ActorSystem.create("part1")
    val actorRef = actorSystem.actorOf(Props.create(HelloKotlinActor::class.java))
    actorSystem.log().info("Sending Hello Kotlin")
    actorRef.tell("Hello Kotlin", ActorRef.noSender())
}