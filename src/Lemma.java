
public class Lemma {

	public String id;
	public String lemma;
	public String main_cat;
	
	public Lemma (String id, String lemma, String main_cat){
		this.id = id;
		this.lemma = lemma;
		this.main_cat = main_cat;
	}
	
	public String toString(){
		return "id: " + id + "\tlemma: " + lemma + "\tmain_cat: " + main_cat;
	}
	
}
