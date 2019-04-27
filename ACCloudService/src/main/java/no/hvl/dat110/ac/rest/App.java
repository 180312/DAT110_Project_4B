package no.hvl.dat110.ac.rest;

import static spark.Spark.after;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.put;
import static spark.Spark.post;
import static spark.Spark.delete;

import com.google.gson.Gson;

/**
 * Hello world!
 *
 */
public class App {
	
	static AccessLog accesslog = null;
	static AccessCode accesscode = null;
	
	public static void main(String[] args) {

		if (args.length > 0) {
			port(Integer.parseInt(args[0]));
		} else {
			port(getHerokuAssignedPort());
		}
		
		

		// objects for data stored in the service
		
		accesslog = new AccessLog();
		accesscode  = new AccessCode();
		
		after((req, res) -> {
  		  res.type("application/json");
  		});
		
		// for basic testing purposes
		get("/accessdevice/hello", (req, res) -> {
			
		 	Gson gson = new Gson();
		 	
		 	return gson.toJson("IoT Access Control Device");
		});
		
		// TODO: implement the routes required for the access control service
		
		post("/accessdevice/log", (req, res) -> {
			
			Gson gson = new Gson();
			
			AccessMessage newMessage = gson.fromJson(req.body(), AccessMessage.class);
			
			int idAdded = accesslog.add(newMessage.getMessage());
			
			return gson.toJson(accesslog.get(idAdded));
		});
		
		get("/accessdevice/log", (req, res) -> {
			
			return accesslog.toJson();
		});
		
		get("/accessdevice/log/:id", (req, res) -> {
			
			Gson gson = new Gson();
			
			int id = Integer.parseInt(req.params(":id"));
			
			return gson.toJson(accesslog.get(id)); 
		});
		
		//hvorfor returnerer man alltid det samme man sender? Vet ikke klienten det samme da?
		//Er det bare slik man gjør, siden klienten sin kopi regnes som gammel?
		
		// Skal man her bruke immutable eller mutable objekter her?
		put("/accessdevice/code", (req, res) -> {
			
			Gson gson = new Gson();
					
			AccessCode newCode = gson.fromJson(req.body(), AccessCode.class);
			
			accesscode = newCode;
			
			return gson.toJson(accesscode);
			
		});
		
		get("/accessdevice/code", (req,res) -> {
			
			Gson gson = new Gson();
			
			return gson.toJson(accesscode);
		});
		
		delete("/accessdevice/log", (req,res) -> {
			
			accesslog.clear();
			
			return accesslog.toJson();
		});
		
    }
	
    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 8080; //return default port if heroku-port isn't set (i.e. on localhost)
    }
    
}
