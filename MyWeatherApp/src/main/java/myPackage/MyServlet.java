package myPackage;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class MyServlet
 */
public class MyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MyServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
//		API Setup
		String apiKey = "20656159b8ce1d0ca432ea66b37ef319";
		
		String city = request.getParameter("city");
		
//		API Integration
		String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q="+city+"&appid="+apiKey;
		
		URL url = new URL(apiUrl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		
		try {
			
//			Reading data from API response
			InputStream inputStream = connection.getInputStream();
			InputStreamReader reader = new InputStreamReader(inputStream);
			
//			String string
			StringBuilder responseContent = new StringBuilder();
			
//			Input from reader
			Scanner scanner = new Scanner(reader);
			
			while(scanner.hasNext()) {
				responseContent.append(scanner.nextLine());
			}
			scanner.close();
//			System.out.println(responseContent);
			
//	 		Parse the JSON response to extract temperature, date, and humidity
			Gson gson = new Gson();
			JsonObject jsonObject = gson.fromJson(responseContent.toString(),JsonObject.class);
//			System.out.println(jsonObject);
			
//			Date & Time
	        long dateTimestamp = jsonObject.get("dt").getAsLong() * 1000;
	        String date = new Date(dateTimestamp).toString();
//	        System.out.println("date : "+date);
	        
//	      Temperature
	        double temperatureKelvin = jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
	        int temperatureCelsius = (int) (temperatureKelvin - 273.15);
//	        System.out.println("temperatureCelsius: "+temperatureCelsius);
	       
//	      Humidity
	        int humidity = jsonObject.getAsJsonObject("main").get("humidity").getAsInt();
//	        System.out.println("humidity : "+humidity);
	        
//	      Wind Speed
	        double windSpeed = jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();
//	        System.out.println("windSpeed : "+windSpeed);
	        
//	      Weather Condition
	        String weatherCondition = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").getAsString();
//	        System.out.println("weatherCondition : "+ weatherCondition);
			
	        // Set the data as request attributes (for sending to the jsp page)
	        request.setAttribute("date", date);
	        request.setAttribute("city", city);
	        request.setAttribute("temperature", temperatureCelsius);
	        request.setAttribute("weatherCondition", weatherCondition); 
	        request.setAttribute("humidity", humidity);    
	        request.setAttribute("windSpeed", windSpeed);
	        request.setAttribute("weatherData", responseContent.toString());
	        
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		finally {
			connection.disconnect();
		}
		
		// Forward the request to the index.jsp page for rendering
        request.getRequestDispatcher("index.jsp").forward(request, response);
		
	}
 
}
