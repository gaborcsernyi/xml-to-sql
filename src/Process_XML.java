import java.io.*;
import org.jdom.Document;
import org.jdom.Element;
//import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Process_XML {
	public static void main(String[] args) throws Exception {
		
		String filepath="";
		
		try{
			
			FileInputStream fis = new FileInputStream(filepath + args[0]); 
		    BufferedReader xml_input = new BufferedReader (new InputStreamReader(fis,"UTF-8"));
		    
		    String s;
		    String lin = "";
		    
		    s = xml_input.readLine();
		    int scount = 0;
		    int formcount = 0;
		    int lemmacount = 0;
		    int countline = 1;
		    int s_xml_start = 1;  //the number of the line where the analysis of the given sentence starts
		    int s_xml_end = -1;   //the number of the line where the analysis of the given sentence ends
		    int read_a = -1;
		    int a_xml_start = -1;
		    int a_xml_stop = -1;
		    String sentence = "";
		    String sid = "";
		    String analysis = "";
		    Vector<String> as = new Vector<String>();
		    Vector<String> apos = new Vector<String>();
		    
		    ArrayList<Lemma> lemmas_vect = new ArrayList<Lemma>();
		    ArrayList<Form> forms_vect = new ArrayList<Form>();
		    ArrayList<Junction> junct_vect = new ArrayList<Junction>();
		    ArrayList<Analysis> analyses_vect = new ArrayList<Analysis>();
		    ArrayList<Sentence> sentences_vect = new ArrayList<Sentence>();

		    while (true) {
		    	lin = lin + s.trim() + '\n';
		    	if (s.indexOf("text=\"") > 0){
		    		sentence = s.substring(s.indexOf("text=\"") + 6, s.lastIndexOf("\">"));
		    		scount++;
		    		sid = "s_" + String.format("%06d",scount);
		    		//System.out.println("#" + sentence + "#");
		    	}
		    	if (s.indexOf("<a id") == 0){
		    		//System.out.println("start_a: "+countline);
		    		read_a = 1;
		    		a_xml_start = countline;
		    	}
		    	if (read_a == 1){
		    		analysis = analysis + "\t" + s.trim() + "\n";
		    	}		    	
		    	if (s.indexOf("</a>") == 0){
		    		//System.out.println("stop_a: "+countline);
		    		read_a = 1;
		    		a_xml_stop = countline;
		    		read_a = 0;
		    		//System.out.println(analysis);
		    		apos.add(a_xml_start + "," + a_xml_stop);
		    		as.add(analysis);
		    		analysis="";		    		
		    	}		    	
		    	if (s.indexOf("</s>") == 0){
		    		s_xml_end = countline;
		    		//System.out.println(lin);

		    	//HERE COMES THE PROCESSING OF THE ACTUAL SENTENCE (it ends here, after this comes a new sentence)
		    		//System.out.println("### " + s_xml_start + "\t" + s_xml_end + " ###");
		    		System.out.println("[" + scount + "]\t" + sentence);
		    			    		
		    		Sentence current_s = new Sentence(sid, sentence, s_xml_start, s_xml_end);
		    		sentences_vect.add(current_s);		    		   		
		    		
		    		for (int i=0; i<as.size(); ++i){
		    			//XML PROCESSING VIA JDOM (with SAX doc handler)
			    		SAXBuilder builder = new SAXBuilder();
			    		Document document = builder.build(new StringReader(as.get(i)));
						Element root = document.getRootElement(); //-> <a>
						List<?> terminalsElements = root.getChildren("terminals");
						String bracketed = root.getChildText("bracketed");
						//System.out.println(bracketed);
						//System.out.println((scount + 1) + "\t" + (acount + 1) + "\t" +terminalsElements);
						
						// ###   <terminals>   ###
						for (int terminalscount=0; terminalscount<terminalsElements.size(); terminalscount++){
							Element terminalsNode = (Element) terminalsElements.get(terminalscount);
							List <?> tElements = terminalsNode.getChildren("t");
							//System.out.println((scount + 1) + "\t" + (acount + 1) + "\t" + (terminalscount + 1) + "\t" +tElements);
							
							for (int tcount=0; tcount<tElements.size(); tcount++){
								Element tNode = (Element)tElements.get(tcount);
								String lemma = tNode.getAttribute("lem").getValue();
								String form = tNode.getAttribute("word").getValue();
								String morphology = tNode.getAttribute("morph").getValue();
								String path = tNode.getAttribute("path").getValue();
								//System.out.println(form + " [" + lemma + "]");
								String morph_cat = "";
								if (morphology.indexOf("null")==0){
									morph_cat = "null";
								}
								else {
									if (morphology.indexOf("+") == morphology.lastIndexOf("+")){
										//System.out.println(morphology);
										morph_cat = morphology.substring(morphology.indexOf("+"));
										//System.out.println("morph_cat: #" + morph_cat + "#");
									}
									else {
										int startpos = 0;
										int endpos = 0;
										int firstplus = morphology.indexOf("+");
										
										int firstpsp = morphology.lastIndexOf("+ ");
										int dbpos = morphology.lastIndexOf("^DB");
										int cbpos = morphology.lastIndexOf("^CB+");
										int vulgfeat = morphology.lastIndexOf("+Vulg");
										
										if (firstpsp > -1) firstpsp = firstpsp+2;
										if (dbpos > -1) dbpos = dbpos + 3;
										if (cbpos > -1) cbpos = cbpos + 4;
										if (vulgfeat > -1) vulgfeat = vulgfeat + 5;
										
										int[] findstartpos = new int[4];
										findstartpos[0]=firstpsp;
										findstartpos[1]=dbpos;
										findstartpos[2]=cbpos;
										findstartpos[3]=vulgfeat;
										int findposfdc=-1;
										for (int fsp=0; fsp<4; ++fsp){
											if (findstartpos[fsp]>findposfdc)
												findposfdc = findstartpos[fsp];
										}
										
										if (findposfdc>firstplus) startpos= morphology.indexOf("+", findposfdc);
										else startpos = firstplus;
										
										//System.out.println(morphology);
										
										if (morphology.indexOf("+", startpos+1) > 0){
											endpos = morphology.indexOf("+", startpos+1);
											//System.out.println("{END} startpos: " + startpos + "\tendpos: " + endpos);
											morph_cat = morphology.substring(startpos, endpos-1); 
										}
										else {
											//System.out.println("{NOEND} startpos: " + startpos + "-");
											morph_cat = morphology.substring(startpos);
										}
										
										//System.out.println("morph_cat: #" + morph_cat + "#");
									}
								}
								
								// CHECKING FORMS and LEMMAS
								int formexists = 0;
								for (int p=0; p<forms_vect.size(); ++p){
									Form oneForm = forms_vect.get(p);
									// if the form already exists, the new analysis should be linked to it, too
									if (oneForm.form.equals(form) && oneForm.morphology.equals(morphology) && oneForm.path.equals(path)){
										Junction fa = new Junction(oneForm.id, "a_" + String.format("%06d", scount) + "-" + String.format("%02d", i));
										//System.out.println(sid + "-" + i);
										if (junct_vect.contains(fa) == false){
											//System.out.println(oneForm.id + "\t" + sid + "-" + i);
											junct_vect.add(fa);}
										//else {System.out.println("benne1");}
										formexists = 1;
									}
								}
								// in case the form does not exist in the list yet, check whether its lemma is already added
								if (formexists == 0){
									int lemmaexists = 0;
									String mem_lid = "";
									for (int lcnt =0; lcnt<lemmas_vect.size(); ++lcnt){
										Lemma oneLemma = lemmas_vect.get(lcnt);
										if (oneLemma.lemma.equals(lemma) && oneLemma.main_cat.equals(morph_cat)){
											lemmaexists = 1;
											mem_lid = oneLemma.id;
											break;
										}
									}
									// if the lemma is not in the list yet, add it, and link the form to it
									if (lemmaexists == 0){
										lemmacount++;
										Lemma newLemma = new Lemma("l_" + String.format("%07d",lemmacount), lemma, morph_cat);
										lemmas_vect.add(newLemma);
										formcount++;
										Form newForm = new Form("f_" + String.format("%07d",formcount), form, "l_" + String.format("%07d",lemmacount), morphology, path);
										forms_vect.add(newForm);
										Junction newJnct = new Junction("f_" + String.format("%07d",formcount), "a_" + String.format("%06d", scount) + "-" + String.format("%02d", i));
										if (junct_vect.contains(newJnct) == false){
											junct_vect.add(newJnct);}
										//else {System.out.println("benne2a");}
									}
									// if the lemma is already added, link the form to it
									else if (lemmaexists == 1){
										formcount++;
										Form newForm = new Form("f_" + String.format("%07d",formcount), form, mem_lid, morphology, path);
										forms_vect.add(newForm);
										Junction newJnct = new Junction("f_" + String.format("%07d",formcount), "a_" + String.format("%06d", scount) + "-" + String.format("%02d", i));
										if (junct_vect.contains(newJnct) == false){
											junct_vect.add(newJnct);}
										//else {System.out.println("benne2b");}
									}
									
								}
								
								//System.out.println("lemma: " + lemma);
								//System.out.println("kategória: #" + morph_cat + "#");
								//System.out.println("alak: " + form);
								//System.out.println("morfológia: " + morphology);
							}
							
						}
						
						String postmp = apos.get(i);
						int commaplace = postmp.indexOf(",");
						int startan = Integer.parseInt(postmp.substring(0, commaplace));
						int endan = Integer.parseInt(postmp.substring((commaplace)+1));
						Analysis current_a = new Analysis("a_" + String.format("%06d", scount) + "-" + String.format("%02d", i), sid, bracketed, startan, endan);
						analyses_vect.add(current_a);
						
		    		}
		    		
		    		as.removeAllElements();
		    		apos.removeAllElements();
		    		lin="";
		    		s_xml_start = countline + 2;
		    	}
		    	s = xml_input.readLine();
		    	++countline;
		    	if (s == null) break;
		    }
		    
			xml_input.close();
			
			FileOutputStream out1 = new FileOutputStream("E:\\Java_debug2\\SQL_101_lemma.sql");
			FileOutputStream out2 = new FileOutputStream("E:\\Java_debug2\\SQL_102_form.sql");
			FileOutputStream out3 = new FileOutputStream("E:\\Java_debug2\\SQL_103_sentence.sql");
			FileOutputStream out4 = new FileOutputStream("E:\\Java_debug2\\SQL_104_analysis.sql");
			FileOutputStream out5 = new FileOutputStream("E:\\Java_debug2\\SQL_105_junction.sql");
			
		    OutputStreamWriter lemma_stdo = new OutputStreamWriter(out1,"UTF8");
		    OutputStreamWriter form_stdo = new OutputStreamWriter(out2,"UTF8");
		    OutputStreamWriter sentence_stdo = new OutputStreamWriter(out3,"UTF8");
		    OutputStreamWriter analysis_stdo = new OutputStreamWriter(out4,"UTF8");
		    OutputStreamWriter fa_junction_stdo = new OutputStreamWriter(out5,"UTF8");
			

			String sql_script_lemma = "";
			lemma_stdo.write("SET unique_checks=0;\nSET foreign_key_checks=0;\nSET autocommit=0;\nINSERT INTO lemma VALUES");
			for (int t1=0; t1<lemmas_vect.size()-1; ++t1){
				Lemma lemma_ins = lemmas_vect.get(t1);
				lemma_stdo.write("(\"" + lemma_ins.id + "\",\"" + lemma_ins.lemma + "\",\"" + lemma_ins.main_cat + "\"),\n");
			}
			Lemma lemma_ins = lemmas_vect.get(lemmas_vect.size()-1);
			lemma_stdo.write("(\"" + lemma_ins.id + "\",\"" + lemma_ins.lemma + "\",\"" + lemma_ins.main_cat + "\");\nSET unique_checks=1;\nSET foreign_key_checks=1;\nSET autocommit=1;");
			
			String sql_script_form = "";
			form_stdo.write("SET unique_checks=0;\nSET foreign_key_checks=0;\nSET autocommit=0;\nINSERT INTO form VALUES");
			for (int t1=0; t1<forms_vect.size()-1; ++t1){
				Form form_ins = forms_vect.get(t1);
				form_stdo.write("(\"" + form_ins.id + "\",\"" + form_ins.form + "\",\"" + form_ins.l_id + "\",\"" + form_ins.morphology + "\",\"" + form_ins.path + "\"),\n");
			}
			Form form_ins = forms_vect.get(forms_vect.size()-1);
			form_stdo.write("(\"" + form_ins.id + "\",\"" + form_ins.form + "\",\"" + form_ins.l_id + "\",\"" + form_ins.morphology + "\",\"" + form_ins.path + "\");\nSET unique_checks=1;\nSET foreign_key_checks=1;\nSET autocommit=1;");
			
			String sql_script_sentence = "";
			sentence_stdo.write("SET unique_checks=0;\nSET foreign_key_checks=0;\nSET autocommit=0;\nINSERT INTO sentence VALUES");
			for (int t1=0; t1<sentences_vect.size()-1; ++t1){
				Sentence sentence_ins = sentences_vect.get(t1);
				sentence_stdo.write("(\"" + sentence_ins.s_id + "\",\"" + sentence_ins.sentence + "\"," + sentence_ins.xml_start + "," + sentence_ins.xml_end + "),\n");
			}
			Sentence sentence_ins = sentences_vect.get(sentences_vect.size()-1);
			sentence_stdo.write("(\"" + sentence_ins.s_id + "\",\"" + sentence_ins.sentence + "\"," + sentence_ins.xml_start + "," + sentence_ins.xml_end + ");\nSET unique_checks=1;\nSET foreign_key_checks=1;\nSET autocommit=1;");
			
			String sql_script_analysis = "";
			analysis_stdo.write("SET unique_checks=0;\nSET foreign_key_checks=0;\nSET autocommit=0;\nINSERT INTO analysis VALUES");
			for (int t1=0; t1<analyses_vect.size()-1; ++t1){
				Analysis analysis_ins = analyses_vect.get(t1);
				analysis_stdo.write("(\"" + analysis_ins.a_id + "\",\"" + analysis_ins.sentence_id + "\",\"" + analysis_ins.br_repr + "\"," + analysis_ins.xml_start + "," + analysis_ins.xml_end + "),\n");
		    }
			Analysis analysis_ins = analyses_vect.get(analyses_vect.size()-1);
			analysis_stdo.write("(\"" + analysis_ins.a_id + "\",\"" + analysis_ins.sentence_id + "\",\"" + analysis_ins.br_repr + "\"," + analysis_ins.xml_start + "," + analysis_ins.xml_end + ");\nSET unique_checks=1;\nSET foreign_key_checks=1;\nSET autocommit=1;");
			
			String sql_script_fa = "";
			fa_junction_stdo.write("SET unique_checks=0;\nSET foreign_key_checks=0;\nSET autocommit=0;\nINSERT INTO fa_junction VALUES");
			for (int t1=0; t1<junct_vect.size()-1; ++t1){
				Junction junction_ins = junct_vect.get(t1);
				fa_junction_stdo.write("(\"" + junction_ins.fid + "\",\"" + junction_ins.aid + "\"),\n");
			}
			Junction junction_ins = junct_vect.get(junct_vect.size()-1);
			fa_junction_stdo.write("(\"" + junction_ins.fid + "\",\"" + junction_ins.aid + "\");\nSET unique_checks=1;\nSET foreign_key_checks=1;\nSET autocommit=1;");
		    
			lemma_stdo.write(sql_script_lemma);
			form_stdo.write(sql_script_form);
			sentence_stdo.write(sql_script_sentence);
			analysis_stdo.write(sql_script_analysis);
			fa_junction_stdo.write(sql_script_fa);
			
			lemma_stdo.flush();
			form_stdo.flush();
			sentence_stdo.flush();
			analysis_stdo.flush();
			fa_junction_stdo.flush();
			
			lemma_stdo.close();
			form_stdo.close();
			sentence_stdo.close();
			analysis_stdo.close();
			fa_junction_stdo.close();
			
		}
		
	    catch (FileNotFoundException exc){
	      System.err.println("File could not be found");
	      System.exit(1);
	    }
	    
	    catch (IOException exc){
	      System.err.println("I/O error");
	      exc.printStackTrace();
	      System.exit(2);
	    }
	     
	}
}