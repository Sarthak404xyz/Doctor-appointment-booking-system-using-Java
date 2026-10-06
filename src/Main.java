import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;

public class Main {

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(
                new InetSocketAddress(8080), 0);

        server.createContext("/", Main::home);

        server.createContext("/style.css",
                exchange -> sendFile(exchange, "web/style.css", "text/css"));

        server.createContext("/script.js",
                exchange -> sendFile(exchange, "web/script.js", "application/javascript"));

        server.createContext("/doctors", Main::doctors);
        server.createContext("/book", Main::book);
        server.createContext("/appointments", Main::appointments);
        server.createContext("/cancel", Main::cancel);

        server.start();

        System.out.println("Doctor Appointment System started.");
        System.out.println("Open: http://localhost:8080");
    }

    static void home(HttpExchange exchange) throws IOException {
        sendFile(exchange, "web/index.html", "text/html");
    }

    static void doctors(HttpExchange exchange) throws IOException {
        try {
            Connection con = Database.getConnection();
            Statement st = con.createStatement();

            ResultSet rs = st.executeQuery(
                    "SELECT * FROM doctors");

            StringBuilder json = new StringBuilder("[");

            while (rs.next()) {
                if (json.length() > 1) json.append(",");

                json.append("{")
                    .append("\"id\":").append(rs.getInt("id")).append(",")
                    .append("\"name\":\"").append(rs.getString("name")).append("\",")
                    .append("\"specialization\":\"")
                    .append(rs.getString("specialization")).append("\",")
                    .append("\"time\":\"")
                    .append(rs.getString("available_time")).append("\"")
                    .append("}");
            }

            json.append("]");

            con.close();
            send(exchange, 200, "application/json", json.toString());

        } catch (Exception e) {
            send(exchange, 500, "text/plain", e.getMessage());
        }
    }

    static void book(HttpExchange exchange) throws IOException {
        try {
            String body = new String(
                    exchange.getRequestBody().readAllBytes(),
                    StandardCharsets.UTF_8);

            Map<String, String> data = parse(body);

            int doctorId = Integer.parseInt(data.get("doctorId"));

            Connection con = Database.getConnection();

            String check =
                    "SELECT * FROM appointments " +
                    "WHERE doctor_id=? AND appointment_date=? " +
                    "AND appointment_time=? AND status='Booked'";

            PreparedStatement checkStmt = con.prepareStatement(check);
            checkStmt.setInt(1, doctorId);
            checkStmt.setString(2, data.get("date"));
            checkStmt.setString(3, data.get("time"));

            ResultSet result = checkStmt.executeQuery();

            if (result.next()) {
                con.close();

                send(exchange, 400, "application/json",
                        "{\"error\":\"This time slot is already booked.\"}");

                return;
            }

            String sql =
                    "INSERT INTO appointments " +
                    "(patient_name,email,phone,doctor_id,appointment_date," +
                    "appointment_time,status) VALUES (?,?,?,?,?,?,?)";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, data.get("name"));
            ps.setString(2, data.get("email"));
            ps.setString(3, data.get("phone"));
            ps.setInt(4, doctorId);
            ps.setString(5, data.get("date"));
            ps.setString(6, data.get("time"));
            ps.setString(7, "Booked");

            ps.executeUpdate();

            con.close();

            send(exchange, 200, "application/json",
                    "{\"success\":true}");

        } catch (Exception e) {
            send(exchange, 500, "application/json",
                    "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    static void appointments(HttpExchange exchange) throws IOException {
        try {
            Connection con = Database.getConnection();

            String sql =
                    "SELECT a.id, a.patient_name, a.email, a.phone, " +
                    "d.name AS doctor, d.specialization, " +
                    "a.appointment_date, a.appointment_time, a.status " +
                    "FROM appointments a " +
                    "JOIN doctors d ON a.doctor_id=d.id " +
                    "ORDER BY a.appointment_date, a.appointment_time";

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            StringBuilder json = new StringBuilder("[");

            while (rs.next()) {
                if (json.length() > 1) json.append(",");

                json.append("{")
                    .append("\"id\":").append(rs.getInt("id")).append(",")
                    .append("\"patient\":\"")
                    .append(rs.getString("patient_name")).append("\",")
                    .append("\"doctor\":\"")
                    .append(rs.getString("doctor")).append("\",")
                    .append("\"specialization\":\"")
                    .append(rs.getString("specialization")).append("\",")
                    .append("\"date\":\"")
                    .append(rs.getString("appointment_date")).append("\",")
                    .append("\"time\":\"")
                    .append(rs.getString("appointment_time")).append("\",")
                    .append("\"status\":\"")
                    .append(rs.getString("status")).append("\"")
                    .append("}");
            }

            json.append("]");

            con.close();
            send(exchange, 200, "application/json", json.toString());

        } catch (Exception e) {
            send(exchange, 500, "text/plain", e.getMessage());
        }
    }

    static void cancel(HttpExchange exchange) throws IOException {
        try {
            String body = new String(
                    exchange.getRequestBody().readAllBytes(),
                    StandardCharsets.UTF_8);

            Map<String, String> data = parse(body);

            Connection con = Database.getConnection();

            PreparedStatement ps = con.prepareStatement(
                    "UPDATE appointments SET status='Cancelled' WHERE id=?");

            ps.setInt(1, Integer.parseInt(data.get("id")));
            ps.executeUpdate();

            con.close();

            send(exchange, 200, "application/json",
                    "{\"success\":true}");

        } catch (Exception e) {
            send(exchange, 500, "text/plain", e.getMessage());
        }
    }

    static Map<String, String> parse(String body) {
        Map<String, String> map = new HashMap<>();

        for (String item : body.split("&")) {
            String[] parts = item.split("=", 2);

            String key = URLDecoder.decode(
                    parts[0], StandardCharsets.UTF_8);

            String value = parts.length > 1
                    ? URLDecoder.decode(parts[1], StandardCharsets.UTF_8)
                    : "";

            map.put(key, value);
        }

        return map;
    }

    static void sendFile(HttpExchange exchange,
                         String path,
                         String type) throws IOException {

        File file = new File(path);
        byte[] data = java.nio.file.Files.readAllBytes(file.toPath());

        exchange.getResponseHeaders().set(
                "Content-Type", type);

        exchange.sendResponseHeaders(200, data.length);

        OutputStream out = exchange.getResponseBody();
        out.write(data);
        out.close();
    }

    static void send(HttpExchange exchange,
                     int code,
                     String type,
                     String response) throws IOException {

        byte[] data = response.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type", type);

        exchange.sendResponseHeaders(code, data.length);

        OutputStream out = exchange.getResponseBody();
        out.write(data);
        out.close();
    }
}