package edu.cpp.cs580.thunderbird.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GetCppClasses {
		
		Document htmlCpp = null;
		
		public GetCppClasses() throws IOException{	
			htmlCpp = parseClasses();
			if(htmlCpp != null) outputDocument(htmlCpp);
		}
		
		
		public static Document parseClasses() throws IOException{
			Document result = null;
			String cppClassURL = "http://schedule.cpp.edu/";
			Connection.Response cppClass = Jsoup.connect(cppClassURL).method(Connection.Method.GET).execute();
			
			Document cppClassDoc = cppClass.parse();
			
			//Hidden Inputs
	        Element viewState = cppClassDoc.select("input[name=__VIEWSTATE]").first();
	        Element eventTarget = cppClassDoc.select("input[name=__EVENTTARGET]").first();
	        Element eventArgument = cppClassDoc.select("input[name=__EVENTARGUMENT]").first();
	        
	        Element viewStateGenerator = cppClassDoc.select("input[name=__VIEWSTATEGENERATOR]").first();
	        Element viewStateEncrypted = cppClassDoc.select("input[name=__VIEWSTATEENCRYPTED]").first();
	        Element eventValidation = cppClassDoc.select("input[name=__EVENTVALIDATION]").first();
	     
			
			result = Jsoup.connect(cppClassURL + "index.aspx")
								.data("cookieexists", "false")
								.data("__VIEWSTATE", viewState.attr("value"))
								.data("__EVENTTARGET", eventTarget.attr("value"))
								.data("__EVENTARGUMENT", eventArgument.attr("value"))
								.data("__VIEWSTATEGENERATOR", viewStateGenerator.attr("value"))
								.data("__VIEWSTATEENCRYPTED", viewStateEncrypted.attr("value"))
								.data("__EVENTVALIDATION", eventValidation.attr("value"))
								.data("ctl00$ContentPlaceHolder1$TermDDL", "2171") //Term
								.data("ctl00$ContentPlaceHolder1$ClassSubject", "CS") // Class Subject
								.data("ctl00$ContentPlaceHolder1$CatalogNumber", "") // Catalog Number
								.data("ctl00$ContentPlaceHolder1$CatalogNumberCHK", "") // Checkbox --> need to review this again
								.data("ctl00$ContentPlaceHolder1$Description", "") //Title
								.data("ctl00$ContentPlaceHolder1$CourseComponentDDL", "Any Component") // Course Component
								.data("ctl00$ContentPlaceHolder1$CourseAttributeDDL", "Any Attribute") // Course Attribute
								.data("ctl00$ContentPlaceHolder1$CourseCareerDDL", "Any Career") //Course Career
								.data("ctl00$ContentPlaceHolder1$InstModesDDL", "Any Mode") // Instruction Mode
								.data("ctl00$ContentPlaceHolder1$SessionDDL", "Any Session") //Session
								.data("ctl00$ContentPlaceHolder1$ClassDays$0", "checked") //Mon Class
								.data("ctl00$ContentPlaceHolder1$ClassDays$1", "checked") //Tue Class
								.data("ctl00$ContentPlaceHolder1$ClassDays$2", "checked") //Wed Class
								.data("ctl00$ContentPlaceHolder1$ClassDays$3", "checked") //Thur Class
								.data("ctl00$ContentPlaceHolder1$ClassDays$4", "checked") //Fri Class
								.data("ctl00$ContentPlaceHolder1$ClassDays$5", "checked") //Sat Class
								.data("ctl00$ContentPlaceHolder1$ClassDays$6", "checked") //Sun Class
								.data("ctl00$ContentPlaceHolder1$ClassDays$7", "checked") //TBD Class
								.data("ctl00$ContentPlaceHolder1$StartTime", "ANY") // Class strat time
								.data("ctl00$ContentPlaceHolder1$EndTime", "ANY") // Class end time		
								.data("ctl00$ContentPlaceHolder1$Instructor", "") // Instructor
								.data("ctl00$ContentPlaceHolder1$InstructorCHK", "") // Match Instructor
								.data("ctl00$ContentPlaceHolder1$StaffCHK", "") //Staff only
								.data("ctl00$ContentPlaceHolder1$SearchButton", "Search") //Search button
								.cookies(cppClass.cookies())
								.followRedirects(true)
								.post();
		
			//System.out.println("Log -- document: " + document.toString());
			return result;
		}
		
		public static void outputDocument(Document doc) throws IOException{
			//Read HTML and translate to data for usage 
			//CSV file 
			//Get Class Number + Time
			String csvFileName = "src/main/resources/static/data/cppClasses.csv";
			File csvFile = new File(csvFileName);
			
			if(!csvFile.exists()) csvFile.createNewFile();
			
			FileWriter writer = new FileWriter(csvFile);
			
			Elements classes = doc.select("span.ClassTitle"); //<span class="ClassTitle" >
			System.out.println("Log ------- : classes -> " + classes.size());
			Elements tables = doc.select("table");
			System.out.println("Log ------- : tables --> " + tables.size()); // Need to start at classes.size() + 2
			
			int index = 2; // Need to find better way
			for(Element className:classes){
				Element table = tables.get(index);
				Elements rows = table.select(":not(thead) tr");
				
				writer.append(className.text());
				writer.append(",");
				
							for(Element row:rows){

					Elements tds = row.select("td");
					
					if(row == rows.first()) continue;
						
					writer.append(tds.get(0).text());
					writer.append(",");

				}
				
				index++;
				writer.append("\n");
			}
			
			writer.flush();
			writer.close();
			System.out.println("GetCppClasses was completed");
			
		}
	}
	

