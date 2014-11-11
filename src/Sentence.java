
public class Sentence {

	public String s_id;
	public String sentence;
	public int xml_start;
	public int xml_end;
	
	public Sentence(String s_id, String sentence, int xml_start, int xml_end){
		this.s_id = s_id;
		this.sentence = sentence;
		this.xml_start = xml_start;
		this.xml_end = xml_end;
	}
	
	public String toString(){
		return "s_id: " + s_id + "\tsentence: " + sentence + "\txml_start: " + xml_start + "\txml_end: " + xml_end;
	}
}
