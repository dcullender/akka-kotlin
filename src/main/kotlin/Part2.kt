import akka.actor.AbstractLoggingActor
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.japi.pf.ReceiveBuilder

fun main(args: Array<String>) {

    class ChildActor : AbstractLoggingActor() {

        override fun createReceive() = ReceiveBuilder().match(String::class.java) { log().info(it) }.build()
    }

    class ParentActor : AbstractLoggingActor() {
        override fun preStart() {
            super.preStart()
            context.actorOf(Props.create(ChildActor::class.java), "child1")
            context.actorOf(Props.create(ChildActor::class.java), "child2")
            context.actorOf(Props.create(ChildActor::class.java), "child3")
        }

        override fun createReceive() = ReceiveBuilder()
                .match(String::class.java) { context.children.forEach { child -> child.tell(it, self()) } }
                .build()
    }

    val actorSystem = ActorSystem.create("part2")
    val actorRef = actorSystem.actorOf(Props.create(ParentActor::class.java), "parent")
    actorSystem.log().info("Sending Hello Kotlin")
    actorRef.tell("Hello Kotlin", ActorRef.noSender())

    // send message by actor selection
    actorSystem.actorSelection("akka://part2/user/parent/child1").tell("Hello Kotlin", ActorRef.noSender())
    actorSystem.actorSelection("akka://part2/user/parent/*").tell("Hello Kotlin", ActorRef.noSender())
}