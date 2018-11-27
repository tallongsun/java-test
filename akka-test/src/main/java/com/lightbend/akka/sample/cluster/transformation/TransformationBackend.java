package com.lightbend.akka.sample.cluster.transformation;

import com.lightbend.akka.sample.cluster.transformation.TransformationMessages.TransformationJob;
import com.lightbend.akka.sample.cluster.transformation.TransformationMessages.TransformationResult;

import akka.actor.AbstractActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.ClusterEvent.CurrentClusterState;
import akka.cluster.ClusterEvent.MemberEvent;
import akka.cluster.ClusterEvent.MemberUp;
import akka.cluster.ClusterEvent.UnreachableMember;
import akka.cluster.Member;
import akka.cluster.MemberStatus;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class TransformationBackend extends AbstractActor {
	LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
	Cluster cluster = Cluster.get(getContext().getSystem());

	// subscribe to cluster changes
	@Override
	public void preStart() {
		cluster.subscribe(getSelf(), ClusterEvent.initialStateAsEvents(), MemberEvent.class, UnreachableMember.class);
	}

	// re-subscribe when restart
	@Override
	public void postStop() {
		cluster.unsubscribe(getSelf());
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder().match(MemberUp.class, mUp -> {
			register(mUp.member());
		}).match(CurrentClusterState.class, message -> {
			CurrentClusterState state = (CurrentClusterState) message;  
            for (Member member : state.getMembers()) {  
                if (member.status().equals(MemberStatus.up())) {  
                    register(member);  
                }  
            }  
		}).match(TransformationJob.class, m -> {
			TransformationJob job = (TransformationJob) m;  
			System.out.println(job.getText());  
            getSender().tell(new TransformationResult(job.getText().toUpperCase()), getSelf());  
		}).build();
	}
	
    void register(Member member) {  
        if (member.hasRole("frontend"))  
            getContext().actorSelection(member.address() + "/user/transformationFrontend").tell(TransformationMessages.BACKEND_REGISTRATION, getSelf());  
    }  
}
