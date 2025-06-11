package com.example;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class HelloServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {

        resp.setContentType("text/plain");
        PrintWriter out = resp.getWriter();

        // Environment variable'lardan değerleri al
        String user = System.getenv("PGUSER");
        String pass = System.getenv("PGPASS");
        String host = System.getenv("PGHOST");
        String db   = System.getenv("PGDB");

        String url = "jdbc:postgresql://" + host + ":5432/" + db;

        try {
            // PostgreSQL sürücüsünü yükle (Tomcat'e eklenmiş olmalı)
            Class.forName("org.postgresql.Driver");

            // Bağlantıyı kur
            Connection conn = DriverManager.getConnection(url, user, pass);

            // Sorgu gönder
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM todo");

            // Sonuçları yazdır
            while (rs.next()) {
                int id = rs.getInt("id");
                String desc = rs.getString("description");
                String details = rs.getString("details");
                boolean done = rs.getBoolean("done");

                out.println("[" + id + "] " + desc + " - " + details + " (done: " + done + ")");
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            out.println("Error connecting to PostgreSQL: " + e.getMessage());
            e.printStackTrace(out);
        }
    }
}
