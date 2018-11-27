package com.dl.titan;

import java.io.File;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.janusgraph.core.JanusGraphTransaction;
import org.janusgraph.core.PropertyKey;
import org.janusgraph.core.schema.ConsistencyModifier;
import org.janusgraph.core.schema.JanusGraphIndex;
import org.janusgraph.core.schema.JanusGraphManagement;


/**

g = TinkerFactory.createModern().traversal()

g = TitanFactory.open('conf/titan-berkeleyje-es.properties').traversal()
 
lambda
 map
   constant
   count
   fold
   max
   mean
   min
   sum
   match
   order
   path
   select
   valueMap
 flatmap
   mapKeys
   mapValues
   unfold
   vertex(out,in,both,outE,inE,bothE,outV,inV,bothV,otherV)
 filter
   coin
   cyclicPath
   dedup
   has
   is
   limit
   tail
   range
   sample
   simplePath
   timeLimit
   where
 sideEffect
   aggregate
   store
   group
   groupCount
   inject
   sack
   subgraph
   tree
   union
   profile
 branch
   choose
   coalesce
   local
   repeat
   
barrier
  barrier
  cap

addEdge
addVertex
addProperty
drop

and
or
as
by


bin/gremlin.sh -i ../northwind.groovy
graph = NorthwindFactory.createGraph() 
g = graph.traversal()

g.V().hasLabel('product').groupCount().by('unitPrice').order(local).by(valueDecr).limit(local,1).mapKeys()

g.V().hasLabel("customer").match(
           __.as("c").values("customerId").as("customerId"),
           __.as("c").out("ordered").count().as("orders")
         ).select("customerId", "orders")
         
g.V().hasLabel("customer").or(filter{it.get().value('company')[0]=='A'},filter{it.get().value('company')[0]=='E'}).values('company')

g.V().hasLabel("employee").where(__.not(out("reportsTo"))).repeat(__.in("reportsTo")).emit().tree().by(map{def e = it.get(); e.value('firstName')+" "+e.value('lastName')}).next()
g.V().hasLabel("employee").where(__.not(out("reportsTo"))).repeat(__.as('reportsTo').in("reportsTo").as('employee')).emit().select(last,'reportsTo','employee').by(map{def e = it.get(); e.value('firstName')+" "+e.value('lastName')})


g.V().has("customer", "customerId", "ALFKI").as("customer").
               out("ordered").out("contains").out("is").aggregate("products").
               in("is").in("contains").in("ordered").where(neq("customer")).
               out("ordered").out("contains").out("is").where(without("products")).
               groupCount().order(local).by(valueDecr).mapKeys().limit(5).values("name")


 **/
public class TitanTest {
	public static final String INDEX_NAME = "search";
	
	public static void main(String[] args) {
		JanusGraph g = create("./data");
		query(g);
		g.close();
	}
	
	public static void query(JanusGraph graph){
		GraphTraversalSource g = graph.traversal();
		// want A->C
		Vertex event0 = g.V().has("name", "Root").next();
		GraphTraversal<Vertex,Vertex> ite = g.V(event0).out("travels").out("travels");
		while(ite.hasNext()){
			Vertex v = ite.next();
			System.err.println(""+v.value("name"));
		}
		
//		event1.property("name", "浏览首页2");
//		System.out.println("======" + event1.value("name"));  
	}
	
    public static JanusGraph create(final String directory) {
        JanusGraphFactory.Builder config = JanusGraphFactory.build();
        config.set("storage.backend", "berkeleyje");
        config.set("storage.directory", directory);
        config.set("index." + INDEX_NAME + ".backend", "elasticsearch");
        config.set("index." + INDEX_NAME + ".directory", directory + File.separator + "es");
        config.set("index." + INDEX_NAME + ".elasticsearch.local-mode", true);
        config.set("index." + INDEX_NAME + ".elasticsearch.client-only", false);

        JanusGraph graph = config.open();
        load(graph);
        return graph;
    }
	
	public static void load(JanusGraph g){
		load(g,INDEX_NAME,true);
	}
	
	public static void load(JanusGraph graph,String mixedIndexName,boolean uniqueNameCompositeIndex){

        //Create Schema
        JanusGraphManagement mgmt = graph.openManagement();
        
        final PropertyKey name = mgmt.makePropertyKey("name").dataType(String.class).make();
        
        JanusGraphManagement.IndexBuilder nameIndexBuilder = mgmt.buildIndex("name", Vertex.class).addKey(name);
        if (uniqueNameCompositeIndex)
            nameIndexBuilder.unique();
        JanusGraphIndex namei = nameIndexBuilder.buildCompositeIndex();
        mgmt.setConsistency(namei, ConsistencyModifier.LOCK);
        
        if (null != mixedIndexName)
            mgmt.buildIndex("vertices", Vertex.class).addKey(name).buildMixedIndex(mixedIndexName);

        final PropertyKey uid = mgmt.makePropertyKey("uid").dataType(String.class).make();
        final PropertyKey time = mgmt.makePropertyKey("time").dataType(String.class).make();
        mgmt.makeEdgeLabel("travels").signature(uid,time).make();

        mgmt.makeVertexLabel("event").make();

        mgmt.commit();

        
        ///
        JanusGraphTransaction tx = graph.newTransaction();
        // vertices
        Vertex event0 = tx.addVertex(T.label, "event", "name", "Root");
        Vertex event1 = tx.addVertex(T.label, "event", "name", "A");
        Vertex event2 = tx.addVertex(T.label, "event", "name", "B");
        Vertex event3 = tx.addVertex(T.label, "event", "name", "C");
        Vertex event4 = tx.addVertex(T.label, "event", "name", "D");

        // edges
        // A->B
        event0.addEdge("travels", event1, "uid", "u1", "time", "t1");
        event1.addEdge("travels", event2, "uid", "u1", "time", "t1+1");
        
        // A
        event0.addEdge("travels", event1, "uid", "u2", "time", "t2");
        
        // A->B->C->D
        event0.addEdge("travels", event1, "uid", "u3", "time", "t3");
        event1.addEdge("travels", event2, "uid", "u3", "time", "t3+1");
        event2.addEdge("travels", event3, "uid", "u3", "time", "t3+2");
        event3.addEdge("travels", event4, "uid", "u3", "time", "t3+3");

        // commit the transaction to disk
        tx.commit();
        ///
        
        /*
        // vertices
        Vertex u11 = tx.addVertex(T.label, "event", "name", "A","uid","u1","time","11");
        Vertex u12 = tx.addVertex(T.label, "event", "name", "B","uid","u1","time","12");
        Vertex u13 = tx.addVertex(T.label, "event", "name", "A","uid","u1","time","13");

        Vertex u21 = tx.addVertex(T.label, "event", "name", "A","uid","u2","time","21");

        Vertex u31 = tx.addVertex(T.label, "event", "name", "A","uid","u3","time","31");
        Vertex u32 = tx.addVertex(T.label, "event", "name", "B","uid","u3","time","32");
        Vertex u33 = tx.addVertex(T.label, "event", "name", "C","uid","u3","time","33");
        Vertex u34 = tx.addVertex(T.label, "event", "name", "D","uid","u3","time","34");
        Vertex u35 = tx.addVertex(T.label, "event", "name", "A","uid","u3","time","35");
        Vertex u36 = tx.addVertex(T.label, "event", "name", "B","uid","u3","time","36");
        Vertex u37 = tx.addVertex(T.label, "event", "name", "C","uid","u3","time","37");
        
        Vertex u41 = tx.addVertex(T.label, "event", "name", "E","uid","u4","time","41");
        
        Vertex u51 = tx.addVertex(T.label, "event", "name", "A","uid","u5","time","51");
        Vertex u52 = tx.addVertex(T.label, "event", "name", "F","uid","u5","time","52");
        Vertex u53 = tx.addVertex(T.label, "event", "name", "C","uid","u5","time","53");
        
        Vertex u61 = tx.addVertex(T.label, "event", "name", "A","uid","u6","time","61");
        Vertex u62 = tx.addVertex(T.label, "event", "name", "C","uid","u6","time","62");
        
        Vertex u71 = tx.addVertex(T.label, "event", "name", "A","uid","u7","time","71");
        Vertex u72 = tx.addVertex(T.label, "event", "name", "B","uid","u7","time","72");
        Vertex u73 = tx.addVertex(T.label, "event", "name", "C","uid","u7","time","73");
        
        // edges
        // A->B->A
        u11.addEdge("travels", u12);
        u12.addEdge("travels", u13);
        
        // A
        
        // A->B->C->D->A->B->C
        u31.addEdge("travels", u32);
        u32.addEdge("travels", u33);
        u33.addEdge("travels", u34);
        u34.addEdge("travels", u35);
        u35.addEdge("travels", u36);
        u36.addEdge("travels", u37);

        //E
        
        // A->F->C
        u51.addEdge("travels", u52);
        u52.addEdge("travels", u53);
        
        // A->C
        u61.addEdge("travels", u62);
        
        // A->B->C
        u71.addEdge("travels", u72);
        u72.addEdge("travels", u73);
        */
	}
}
