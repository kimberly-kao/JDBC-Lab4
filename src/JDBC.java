import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JDBC {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/lab4";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static void displaySchedule(String startLocationName, String destinationName, String date, Connection conn) throws SQLException {

        List<List<String>> rows = new ArrayList<>();

        String sql = "SELECT t.StartLocationName, t.DestinationName, tF.Date, tF.ScheduledStartTime," + "" +
                        " tF.ScheduledArrivalTime, tf.DriverName, tF.BusID" +
                    " FROM trip as t, tripoffering as tF "+
                    "WHERE t.TripNumber = tF.TripNumber " +
                    "AND t.StartLocationName = ?" +
                    " AND t.DestinationName = ?" +
                    " AND tF.date = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, startLocationName);
        pstmt.setString(2, destinationName);
        pstmt.setString(3, date);
        ResultSet rs = pstmt.executeQuery();

        List<String> header = Arrays.asList("StartLocation", "DestinationName", "Date", "ScheduledStartTime",
                                "ScheduledArrivalTime", "DriverID", "BusID");
        rows.add(header);

        while (rs.next()) {
            String startName = rs.getString("StartLocationName");
            String endName = rs.getString("DestinationName");
            String date1 = rs.getString("Date");
            String startTime = rs.getString("ScheduledStartTime");
            String arrivalTime = rs.getString("ScheduledArrivalTime");
            String driver = rs.getString("DriverName");
            int bus = rs.getInt("BusID");
            rows.add(Arrays.asList(startName, endName, date1, startTime, arrivalTime, driver, String.valueOf(bus)));
        }

        System.out.println(formatAsTable(rows));
    }

    public static void deleteTripOffering(int tripNumber, String date, String scheduledStartTime, Statement stmt) throws SQLException {
        System.out.println("Deleting from table...");
        String sql = "DELETE from  TripOffering " +
                "WHERE tripNumber=" + tripNumber +
                " AND date= '" + date + "'" +
                " AND scheduledStartTime= '" + scheduledStartTime + "'";
        System.out.println("Sucessfully deleted");
        System.out.println("Rows affected: " + stmt.executeUpdate(sql));

    }

    public static void addTripOffering(int tripNumber, String date, String scheduledStartTime,
                                       String scheduledArrivalTime, String driverName, int busID,
                                       Connection conn) throws SQLException {
        String sql1 = "INSERT INTO TripOffering(tripNumber,date,scheduledStartTime,scheduledArrivalTime,driverName,busID)" +
                "VALUES(?,?,?,?,?,?)";
        PreparedStatement pstmt = conn.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS);

        System.out.println("Adding data to table...");

        pstmt.setInt(1, tripNumber);
        pstmt.setString(2, date);
        pstmt.setString(3, scheduledStartTime);
        pstmt.setString(4, scheduledArrivalTime);
        pstmt.setString(5, driverName);
        pstmt.setInt(6, busID);

        System.out.println("Rows affected: " + pstmt.executeUpdate());
        System.out.println("Data added");
    }

    public static void updateDriver(int tripNumber, String date, String scheduledStartTime, String driverName,
                                      Connection conn) throws SQLException {
        System.out.println("Editing Driver...");
        String sql1 = "UPDATE TripOffering " +
                "SET DriverName = ?" + " WHERE tripNumber = ? AND date = ? AND scheduledStartTime = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS);
        pstmt.setString(1, driverName);
        pstmt.setInt(2, tripNumber);
        pstmt.setString(3, date);
        pstmt.setString(4, scheduledStartTime);
        System.out.println("Rows affected: " + pstmt.executeUpdate());
        System.out.println("Data updated");
    }

    public static void updateBus(int tripNumber, String date, String scheduledStartTime, int busID, Connection conn) throws SQLException {
        System.out.println("Editing BusID...");
        String sql1 = "UPDATE TripOffering " +
                "SET busID = ?" + " WHERE tripNumber = ? AND date = ? AND scheduledStartTime = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS);
        pstmt.setInt(1, busID);
        pstmt.setInt(2, tripNumber);
        pstmt.setString(3, date);
        pstmt.setString(4, scheduledStartTime);
        System.out.println("Rows affected: " + pstmt.executeUpdate());
        System.out.println("Data updated");
    }

    public static void displayTripStopInfo() {

    }

    public static void displaySchedule() {

    }

    public static void addDrive(String driverName, String driverTelephoneNumber, Connection conn) throws SQLException {
        String sql1 = "INSERT INTO driver(driverName,driverTelephoneNumber)" +
                "VALUES(?,?)";
        PreparedStatement pstmt = conn.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS);

        System.out.println("Adding data to table...");

        pstmt.setString(1, driverName);
        pstmt.setString(2, driverTelephoneNumber);

        System.out.println("Rows affected: " + pstmt.executeUpdate());
        System.out.println("Data added");
    }

    public static void addBus(int busID, String model, int year, Connection conn) throws SQLException {
        String sql1 = "INSERT INTO bus(busID,model,year)" +
                "VALUES(?,?,?)";
        PreparedStatement pstmt = conn.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS);

        System.out.println("Adding new bus to table...");

        pstmt.setInt(1, busID);
        pstmt.setString(2, model);
        pstmt.setInt(3, year);

        System.out.println("Rows affected: " + pstmt.executeUpdate());
        System.out.println("Bus added");
    }

    public static void deleteBus(int busID, Statement stmt) throws SQLException {
        System.out.println("Deleting bus from table...");
        String sql = "DELETE from  bus " +
                "WHERE busID=" + busID;
        System.out.println("Sucessfully deleted bus");
        System.out.println("Rows affected: " + stmt.executeUpdate(sql));
    }

    public static String formatAsTable(List<List<String>> rows)
    {
        int[] maxLengths = new int[rows.get(0).size()];
        for (List<String> row : rows)
        {
            for (int i = 0; i < row.size(); i++)
            {
                maxLengths[i] = Math.max(maxLengths[i], row.get(i).length());
            }
        }

        StringBuilder formatBuilder = new StringBuilder();
        for (int maxLength : maxLengths)
        {
            formatBuilder.append("%-").append(maxLength + 2).append("s");
        }
        String format = formatBuilder.toString();

        StringBuilder result = new StringBuilder();
        for (List<String> row : rows)
        {
            result.append(String.format(format, row.toArray(new String[0]))).append("\n");
        }
        return result.toString();
    }

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL,USER,PASSWORD);
            Statement stmt = conn.createStatement();

            displaySchedule("LA", "SF", "1-1", conn);

//            addTripOffering(1, "10-22", "10:00AM", "10:30AM", "Bob",
//                    2, conn);
//
//            updateDriver(1, "10-22", "10:00AM", "Dave", conn);
//
//            updateBus(1, "10-22", "10:00AM", 4, conn);
//
//            deleteTripOffering(1, "10-22", "10:00AM", stmt);
//
//            addBus(2, "Toyota", 2001, conn);
//
//            deleteBus(2, stmt);

            conn.close();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
}