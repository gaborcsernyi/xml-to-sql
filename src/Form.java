//import java.util.Vector;


public class Form {
	
	public String id;
	public String form;
	public String l_id;
	public String morphology;
	public String path;
	
	public Form (String id, String form, String l_id, String morphology, String path){
		this.id = id;
		this.form = form;
		this.l_id = l_id;
		this.morphology = morphology;
		this.path = path;
	}
	
	public String toString(){
		return "id: " + id + "\tform: " + form + "\tl_id: " + l_id + "\tmorphology: " + morphology + "\tpath: " + path;
	}
	
}
