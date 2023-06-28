package it.polito.tdp.yelp.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;


import it.polito.tdp.yelp.db.YelpDao;

public class Model {
	
	
	private List<String> città;
	private List<Business> locali;
	private List<Business> business; 
	private List<Review> reviews;
	private SimpleWeightedGraph<Business, DefaultWeightedEdge> grafo;
	private YelpDao dao;
	private Map<String, Business> idMap;
	

	public Model() {
		dao = new YelpDao();
		idMap = new HashMap<String,Business>();
		dao.getAllBusiness(idMap);
	}

	public List<String> getCittà() {
		if(this.città==null) {
			YelpDao dao = new YelpDao();
			this.città = dao.getAllCitta();
		}
		return this.città;
	}
	
	public void creaGrafo(String citta) {
		
		grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		//aggiungo vertici "filtrati"
			Graphs.addAllVertices(grafo, dao.getVertici(citta, idMap));
			business = dao.getVertici(citta, idMap);
			
			//aggiungo gli archi
			for(Business b1: business) {
				for(Business b2: business) {
					if(!b1.equals(b2)) {
						LatLng posizione1 = new LatLng(b1.getLatitude(), b1.getLongitude());
						LatLng posizione2 = new LatLng(b2.getLatitude(), b2.getLongitude());
						double peso = LatLngTool.distance(posizione1,posizione2, LengthUnit.KILOMETER);
						Graphs.addEdge(this.grafo, b1, b2, peso);
						
					}
				}
			}
			
			System.out.println("Vertici: " +this.grafo.vertexSet().size());
			System.out.println("Archi: " +this.grafo.edgeSet().size());
				
		
		
	}
	
	public String localeDistante(Business b) {
		double max = 0.0 ;
		Business scelto = null;
		List<Business> businessVicini =Graphs.neighborListOf(this.grafo, b);
		
		for(Business b2: businessVicini) {
			DefaultWeightedEdge e = this.grafo.getEdge(b, b2);
			double peso = this.grafo.getEdgeWeight(e);
			if(peso >max) {
				max = peso;
				scelto = b2;
			}
		}
		
		return("Locale: " + scelto.toString() + " Distanza: " + max);
	
	
	}
	
	public int getNVertici(){
		return this.grafo.vertexSet().size();
	}
	
	
	/**
	 * Metodo che restituisce il numero di archi del grafo
	 * @return
	 */
	public int getNArchi(){
		return this.grafo.edgeSet().size();
	}

	public List<Business> getVertici() {
		return new ArrayList<Business>(this.grafo.vertexSet());
		}


	
}
