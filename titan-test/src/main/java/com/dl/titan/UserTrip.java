package com.dl.titan;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import org.apache.tinkerpop.gremlin.process.traversal.Order;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.Path;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.janusgraph.core.JanusGraphTransaction;
import org.janusgraph.core.schema.JanusGraphManagement;
import org.janusgraph.core.util.JanusGraphCleanup;


//g = TitanFactory.open('conf/titan-berkeleyje-es.properties').traversal();
//g = JanusGraphFactory.open('../titan-1.0.0-hadoop1/conf/titan-berkeleyje-es.properties').traversal();

//g.V().has('name','A').group().by('uid').unfold().map{it.get().value.min()}

//g.V().has('name','A').has('uid','u3').has('time','31').repeat(out('travels')).emit(has('name',within('C','D'))).path().by('name')
//g.V().has('name','A').repeat(out('travels')).until(has('name','C')).path().by('name').map{it.get().objects()}.groupCount()
//g.V().has('name','A').repeat(out('travels')).until(has('name','C')).path().by(map{def e=it.get();e.value('name')+' '+e.value('uid')}).map{it.get().objects()}.dedup()
//g.V().has('name','A').emit(has('name',within('A','C'))).repeat(out('travels')).path().by('name')
public class UserTrip {
	public static final String INDEX_NAME = "search";
	
	private static ExecutorService executor = Executors.newFixedThreadPool(10, new ThreadFactory() {
		@Override
		public Thread newThread(Runnable r) {
			Thread th = new Thread(r);
			th.setName("user-trip-processor");
			return th;
		}
	});
	
	public static Map<String,Integer[]> userTrip = new HashMap<String, Integer[]>();
	
	private static List<MyPath> allPath = new ArrayList<>();
	
	public static class MyPath{
		public List<String> names = new ArrayList<>();
		public List<Integer> spans = new ArrayList<>();
		public int type;
		
		public static List<String> getAllSourceNodes(){
			List<String> result = new ArrayList<>();
			for(MyPath path : allPath){
				if(path.type==1){
					result.addAll(path.names);
				}
			}
			return result;
		}
		
		@Override
		public String toString() {
			return "MyPath [names=" + names + ", spans=" + spans + ", type=" + type + "]";
		}
		
	}
	
	public static void main(String[] args) {
//		reset();
		
		initGraph();
		
		JanusGraph g = create("./data");
		load(g);
		query(g);
		g.close();
		
		printGraph();
	}
	
	public static void reset(){
		JanusGraph g = create("./data");
		g.close();
		JanusGraphCleanup.clear(g);
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
        return graph;
    }
	
	
	public static void load(JanusGraph graph){

        //1.Create Schema
        JanusGraphManagement mgmt = graph.openManagement();
        mgmt.makePropertyKey("name").dataType(String.class).make();
        mgmt.makePropertyKey("uid").dataType(String.class).make();
        mgmt.makePropertyKey("time").dataType(Long.class).make();
        mgmt.makeEdgeLabel("travels").make();
        mgmt.makeVertexLabel("event").make();
        mgmt.commit();
        
        //2.Create Data
        List<Map<String,Object>> allRawData = initAllData();
        for(Map<String,Object> raw : allRawData){
        	createVertexAndEdge(graph, raw);
        }

	}

	
	public static class UserTripProcessor implements Callable<String>{
		public UserTripProcessor(GraphTraversalSource g ,String uid,long time){
			this.g= g;
			this.uid = uid;
			this.time = time;
		}
		private String uid;
		private long time;
		private GraphTraversalSource g;

		@Override
		@SuppressWarnings({"rawtypes","unchecked"})
		public String call() throws Exception {
			for(int i=0;i<allPath.size();i++){
				MyPath mypath = allPath.get(i);
				String source = mypath.names.get(0);
				String target = mypath.names.get(mypath.names.size()-1);
				boolean isOver = (mypath.type==3);
				//calculate path to each target
				//g.V().has('name','A').has('uid','u3').as('from')
				//  .repeat(out('travels').as('to')).until(has('name','B').match(__.as('from').values('time').map{it.get()+3}.as('maxTime'),__.as('to').values('time').as('toTime')).where('toTime',lte('maxTime'))).as('from')
				//  .repeat(out('travels').as('to')).until(has('name','D').match(__.as('from').values('time').map{it.get()+2}.as('maxTime'),__.as('to').values('time').as('toTime')).where('toTime',lte('maxTime'))).path().by('name')
				GraphTraversal subIte = g.V().has("name",source).has("uid",uid).has("time",time).as("from");
				for(int j = 1; j<mypath.names.size();j++){
					int timeSpan = mypath.spans.get(j-1);
					subIte = subIte.repeat(__.out("travels").as("to")).until(
							__.has("name",mypath.names.get(j)).match(__.as("from").values("time").map(it->(long)it.get()+timeSpan).as("maxTime"),
									__.as("to").values("time").as("toTime")).where("toTime",P.lte("maxTime")));
					if(j < mypath.names.size()-1){
						subIte = subIte.as("from");
					}
				}
				subIte = subIte.path();
				if(subIte.hasNext()){
					Path path = (Path)subIte.next();

					System.err.println(path);
					if(isOver){
						System.err.println("over");
						for(String name : mypath.names){
							incrUserCompleteOf(name);
						}
					}else{
						System.err.println("not over");
						for(String name : mypath.names.subList(0, mypath.names.size()-1)){
							incrUserPassOf(name);
						}
						Vertex end = (Vertex)path.objects().get(path.objects().size()-1);
						GraphTraversal hasOtherNextNodeIte = g.V(end).repeat(__.out("travels")).until(__.has("name",P.neq(end.value("name"))));
						if(hasOtherNextNodeIte.hasNext()){
							incrUserLeaveOf(target);
						}else{
							incrUserStayOf(target);
						}
					}
					break;
				}
			}
			return uid;
		}
		
	}
	
	/*
	 */
	@SuppressWarnings({"rawtypes","unchecked"})
	public static void query(JanusGraph graph){
		GraphTraversalSource g = graph.traversal();
		
		//get first A of each user
		//g.V().has('name','A').group().by('uid').by('time').unfold().match(__.as('ut').map{it.get().key}.as('u'),__.as('ut').map{it.get().value.min()}.as('t')).select('u','t')
		GraphTraversal<Vertex, Map<String, Object>> ite = g.V().has("name",P.within(MyPath.getAllSourceNodes())).has("time",P.gte(1)).has("time",P.lte(99)).group().by("uid").by("time").unfold().match(
				__.as("ut").map(it -> ((Entry)it.get()).getKey()).as("u"),
				__.as("ut").map(it -> {Object v = ((Entry)it.get()).getValue();return Collections.min((Collection<Long>) v);}).as("t")).select("u","t");
		List<Future<String>> futures = new ArrayList<>();
		while(ite.hasNext()){
			Map<String,Object> elem = ite.next();
			System.err.println(elem);
			String uid = (String)elem.get("u");
			long time = (Long)elem.get("t");
			
			futures.add(executor.submit(new UserTripProcessor(g, uid, time)));

		}
		for(Future<String> f : futures){
			try {
				System.err.println(f.get());
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		executor.shutdown();
		
	}
	
	
	private static void incrUserCompleteOf(String key){
		incrUserTripOf(key,0);
	}
	private static void incrUserPassOf(String key){
		incrUserTripOf(key,1);
	}
	private static void incrUserStayOf(String key){
		incrUserTripOf(key,2);
	}
	private static void incrUserLeaveOf(String key){
		incrUserTripOf(key,3);
	}
	
	private static void incrUserTripOf(String key,int index){
		Integer[] value = userTrip.get(key);
		if(value == null){
			value = new Integer[]{0,0,0,0};
			userTrip.put(key, value);
		}
		if(value[index] == null){
			value[index] = 0;
		}
		value[index]++;
//		value[value.length-1]++;
	}

	public static void printGraph(){
		for(Entry<String,Integer[]> entry : userTrip.entrySet()){
			System.err.println(entry.getKey()+","+Arrays.toString(entry.getValue()));
		}
	}
	
	private static String[] events = {"A","B","C","D","E","F","G","H"};
	private static Random r = new Random();
	public static List<Map<String,Object>> initAllData2(){
		List<Map<String,Object>> allRawData = new ArrayList<>(); 
		for(int i=0;i<100;i++){
			int maxJ = r.nextInt(20);
			for(int j=1;j<maxJ;j=j+2){
				allRawData.add(initData(events[r.nextInt(events.length)],"u"+i,j));
			}
		}
		return allRawData;
	}
	
	/**
	 */
	private static  List<Map<String,Object>> initAllData(){
        List<Map<String,Object>> allRawData = new ArrayList<>(); 
        //u1
        allRawData.add(initData("A","u1",11));
        allRawData.add(initData("B","u1",13));
        allRawData.add(initData("D","u1",15));
        allRawData.add(initData("E","u1",17));
        allRawData.add(initData("F","u1",19));
        //u2
        allRawData.add(initData("A","u2",21));
        allRawData.add(initData("C","u2",23));
        allRawData.add(initData("D","u2",25));
        allRawData.add(initData("E","u2",27));
        allRawData.add(initData("F","u2",29));
        //u3
        allRawData.add(initData("A","u3",31));
        allRawData.add(initData("B","u3",33));
        allRawData.add(initData("D","u3",35));
        allRawData.add(initData("E","u3",37));
        allRawData.add(initData("G","u3",39));
        //u4
        allRawData.add(initData("A","u4",41));
        allRawData.add(initData("C","u4",43));
        allRawData.add(initData("D","u4",45));
        allRawData.add(initData("E","u4",47));
        //u5
        allRawData.add(initData("A","u5",51));
        allRawData.add(initData("B","u5",53));
        allRawData.add(initData("D","u5",55));
        //u6
        allRawData.add(initData("A","u6",61));
        allRawData.add(initData("C","u6",63));
        allRawData.add(initData("D","u6",65));
        //u7
        allRawData.add(initData("A","u7",71));
        allRawData.add(initData("B","u7",73));
        //u8
        allRawData.add(initData("A","u8",81));
        allRawData.add(initData("A","u8",83));
        //u9
        allRawData.add(initData("A","u9",91));
        allRawData.add(initData("B","u9",93));
        allRawData.add(initData("D","u9",95));
        allRawData.add(initData("H","u9",97));
        
        //u10
        allRawData.add(initData("C","u10",91));
        allRawData.add(initData("A","u10",93));
        allRawData.add(initData("B","u10",95));
        allRawData.add(initData("B","u10",97));
        //u11
        allRawData.add(initData("A","u11",91));
        allRawData.add(initData("B","u11",93));
        allRawData.add(initData("C","u11",95));
        allRawData.add(initData("D","u11",97));
        //u12
        allRawData.add(initData("A","u12",91));
        allRawData.add(initData("B","u12",92));
        allRawData.add(initData("C","u12",93));
        allRawData.add(initData("D","u12",94));
        //u13
        allRawData.add(initData("A","u13",91));
        allRawData.add(initData("G","u13",93));
        return allRawData;
	}
	
	
	private static void initGraph(){
		JanusGraph graph = create("./graph");
        
		//initGraph0(graph);
        
        GraphTraversalSource gts = graph.traversal();
        GraphTraversal<Vertex, Path> gt = gts.V().has("type",1).emit().repeat(__.outE("travels").inV()).path();
        while(gt.hasNext()){
        	Path path = gt.next();
        	MyPath mypath = new MyPath();
        	for(int i =0;i<path.objects().size();i++){
        		if(i%2==0){
        			mypath.names.add(((Vertex)path.objects().get(i)).value("name"));
        		}else{
        			mypath.spans.add(((Edge)path.objects().get(i)).value("span"));
        		}
        		if(i == path.objects().size()-1){
        			mypath.type = ((Vertex)path.objects().get(i)).value("type");
        		}
        	}
        	
        	allPath.add(mypath);
        }
        graph.close();
        
        Collections.reverse(allPath);
        for(MyPath p : allPath){
        	System.err.println(p);
        }

	}

	public static void initGraph0(JanusGraph graph) {
		//1.Create Schema
        JanusGraphManagement mgmt = graph.openManagement();
        mgmt.makePropertyKey("name").dataType(String.class).make();
        mgmt.makePropertyKey("type").dataType(Integer.class).make();
        mgmt.makePropertyKey("span").dataType(Integer.class).make();
        mgmt.makeEdgeLabel("travels").make();
        mgmt.makeVertexLabel("event").make();
        mgmt.commit();
        
        //2.Create Data
        JanusGraphTransaction tx = graph.newTransaction();
        // vertices
        Vertex a = tx.addVertex(T.label, "event", "name", "A","type",1);
        Vertex b = tx.addVertex(T.label, "event", "name", "B","type",2);
        Vertex c = tx.addVertex(T.label, "event", "name", "C","type",2);
        Vertex d = tx.addVertex(T.label, "event", "name", "D","type",2);
        Vertex e = tx.addVertex(T.label, "event", "name", "E","type",2);
        Vertex f = tx.addVertex(T.label, "event", "name", "F","type",3);
        Vertex g = tx.addVertex(T.label, "event", "name", "G","type",3);

        // edges
        a.addEdge("travels", b, "span", 3);
        a.addEdge("travels", c, "span", 3);
        b.addEdge("travels", d, "span", 3);
        c.addEdge("travels", d, "span", 3);
        d.addEdge("travels", e, "span", 3);
        e.addEdge("travels", f, "span", 3);
        e.addEdge("travels", g, "span", 3);

        tx.commit();
	}
	
	private static Map<String,Object>  initData(String name,String uid,long time) {
		Map<String,Object> rawData = new LinkedHashMap<>();
        rawData.put("name", name);
        rawData.put("uid", uid);
        rawData.put("time", time);
        return rawData;
	}
	private static void createVertexAndEdge(JanusGraph graph, Map<String, Object> raw) {
        GraphTraversalSource g = graph.traversal();
        
        GraphTraversal<Vertex, Vertex> gt = g.V().has("uid", raw.get("uid")).order().by("time",Order.decr);
        
		if(gt.hasNext()){
			Vertex lastV = gt.next();
			
			Vertex v = graph.addVertex(T.label, "event", "name", raw.get("name"), "uid", raw.get("uid"), "time", raw.get("time"));
			System.err.println("vertex:"+raw.get("name")+","+raw.get("uid")+","+raw.get("time")+","+v);
			
			lastV.addEdge("travels", v);
			System.err.println("edge:"+raw.get("name")+","+raw.get("uid")+","+raw.get("time"));
			
		}else{
			Vertex v = graph.addVertex(T.label, "event", "name", raw.get("name"), "uid", raw.get("uid"), "time", raw.get("time"));
			System.err.println("vertex:"+raw.get("name")+","+raw.get("uid")+","+raw.get("time")+","+v);
		}
		
		graph.tx().commit();
	}

}
