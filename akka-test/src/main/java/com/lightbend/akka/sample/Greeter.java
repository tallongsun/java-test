package com.lightbend.akka.sample;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.lightbend.akka.sample.Printer.Greeting;

//#greeter-messages
public class Greeter extends AbstractActor {
//#greeter-messages
	
  //It is also a common pattern to use a static props method in the class of the Actor that describes how to construct the Actor
  static public Props props(String message, ActorRef printerActor) {
    return Props.create(Greeter.class, () -> new Greeter(message, printerActor));
  }

  //#greeter-messages
  //It is a good practice to put an actor’s associated messages as static classes in the class of the Actor
  static public class WhoToGreet {
    public final String who;

    public WhoToGreet(String who) {
        this.who = who;
    }
  }

  static public class Greet {
    public Greet() {
    }
  }
  //#greeter-messages

  private final String message;
  private final ActorRef printerActor;
  private String greeting = "";

  public Greeter(String message, ActorRef printerActor) {
    this.message = message;
    this.printerActor = printerActor;
  }

  @Override
  public Receive createReceive() {
    return receiveBuilder()
        .match(WhoToGreet.class, wtg -> {
          this.greeting = message + ", " + wtg.who;
        })
        .match(Greet.class, x -> {
          //#greeter-send-message
          printerActor.tell(new Greeting(greeting), getSelf());
          //#greeter-send-message
        })
        .build();
  }
//#greeter-messages
}
//#greeter-messages
