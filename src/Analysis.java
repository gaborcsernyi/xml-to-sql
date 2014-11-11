
public class Analysis {
	public String a_id;
	public String sentence_id;
	public String br_repr;
	public int xml_start;
	public int xml_end;
	
	public Analysis(String a_id, String sentence_id, int xml_start, int xml_end){
		this.a_id = a_id;
		this.sentence_id = sentence_id;
		this.xml_start = xml_start;
		this.xml_end = xml_end;
	}
	
	public Analysis(String a_id, String sentence_id, String br_repr, int xml_start, int xml_end){
		this.a_id = a_id;
		this.sentence_id = sentence_id;
		this.br_repr = br_repr;
		this.xml_start = xml_start;
		this.xml_end = xml_end;
	}
	
	public String toString(){
		return "a_id: " + a_id + "\tsentence_id: " + sentence_id + "\txml_start: " + xml_start + "\txml_end: " + xml_end;
	}
}
