package com.Accio;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@WebServlet("/Search")
public class Search extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        String Keyword = request.getParameter("keyword");
        Connection connection = DBConnection.getConnection();



        try{
//            To Store query of user
            PreparedStatement preparedStatement = connection.prepareStatement("Insert into History values(?,?);");
            preparedStatement.setString(1,Keyword);
            preparedStatement.setString(2,"http://localhost:8080/SearchEngine/Search?keyword="+Keyword);
            preparedStatement.executeUpdate();

//            getting result
            ResultSet resultSet = connection.createStatement().executeQuery("select pageTitle, pageLink, (length(lower(pageText))-length(replace(lower(pageText),'"+Keyword+"','')))/length('"+Keyword+"') as count from pages order by count desc limit 30; ");
            ArrayList<SearchResult> results = new ArrayList<SearchResult>();
            while(resultSet.next()){
                SearchResult searchResult  = new SearchResult();
                searchResult.setTitle(resultSet.getString("pageTitle"));
                searchResult.setLink(resultSet.getString("pageLink"));

                results.add(searchResult);

            }
            for(SearchResult result: results){

                System.out.println(result.getTitle()+"\n"+result.getLink()+"\n");
            }

            request.setAttribute("results", results);
            request.getRequestDispatcher("search.jsp").forward(request,response);
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
        }


        catch (SQLException sqlException){
            sqlException.printStackTrace();
        }
        catch (ServletException servletException)
        {
            servletException.printStackTrace();
        }



    }
}
