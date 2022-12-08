
import java.sql.*;
import java.util.Scanner;

public class DiscInventory {
    static Scanner sc = new Scanner(System.in);
    static String URL = "jdbc:sqlite:/home/julia/ITHS/Database/JDBC/Lab3.db";
    static String PRINT_CATEGORIES = "SELECT * FROM Category ORDER BY categoryID";
    static String PRINT_PRODUCTS = "SELECT discCategoryID, discName, discPrice FROM Disc ORDER BY discCategoryID";
    static String DELETE_PRODUCT = "DELETE FROM Disc WHERE discName = ?";
    static String EDIT_PRODUCT = "UPDATE Disc SET discPrice = ? WHERE discName = ?";
    static String ADD_PRODUCT = "INSERT INTO Disc(discName, discCategoryID, discPrice) VALUES(?,?,?)";
    static String PRINT_PRODUCT_IN_CATEGORY = "SELECT categoryName, discName, discPrice FROM Category C INNER JOIN Disc D on C.categoryID = D.discCategoryID WHERE categoryName = ?";
    static String PRINT_AVG_PRICE_PER_CATEGORY = "SELECT categoryName, CASE WHEN avg(discPrice) THEN avg(discPrice) END AS 'avgPrice' FROM Category C INNER JOIN Disc D on C.categoryID = D.discCategoryID GROUP BY categoryName";

    public static void main(String[] args) {
        String choice;
        do {
            printActions();
            choice = sc.nextLine().toLowerCase();
            switchMenu(choice);
        } while (!choice.equals("e"));
    }

    private static void switchMenu(String choice) {
        switch (choice) {
            case "1" -> printCategories();
            case "2" -> avgPrice();
            case "3" -> printProducts();
            case "4" -> addNewProduct();
            case "6" -> deleteProduct();
            case "5" -> editProduct();
            case "7" -> search();
            case "e" -> quitMessage();
            default -> System.out.println("Please choose one of the alternatives below:");
        }
    }

    private static void avgPrice() {
        try (ResultSet rs = getResultSet(PRINT_AVG_PRICE_PER_CATEGORY)) {
            while (rs.next()) {
                System.out.println(
                        rs.getString("categoryName") + " | " +
                                rs.getString("avgPRice"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void printCategories() {
        try (ResultSet rs = getResultSet(PRINT_CATEGORIES)) {
            while (rs.next()) {
                System.out.println(rs.getString("categoryID") + ". " +
                        rs.getString("categoryName"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void printProducts() {
        try (ResultSet rs = getResultSet(PRINT_PRODUCTS)) {
            while (rs.next()) {
                System.out.println(
                        rs.getString("discName") + " | " +
                                rs.getString("discCategoryID") + " | " +
                                rs.getString("discPrice"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static Connection connect() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return connection;
    }

    private static void printActions() {
        System.out.println("""
                                
                Menu
                ====
                1. Print all categories
                2. Print avg price
                3. Print all discs
                4. Add new disc
                5. Remove disc
                6. Edit disc
                7. Search
                e. Exit
                """);
    }

    private static void addNewProduct() {
        System.out.println("Insert name of the disc:");
        String inputDiscName = sc.nextLine();
        System.out.println("Fill in categoryID:");
        printCategories();
        String inputCategoryID = sc.nextLine();
        System.out.println("Fill in price:");
        int inputPrice = Integer.parseInt(sc.nextLine());
        insert(inputDiscName, inputCategoryID, inputPrice);
    }

    private static void deleteProduct() {
        System.out.println("Which disc would you like to remove? (Insert name): ");
        String discName = sc.nextLine();
        delete(discName);
    }

    private static ResultSet getResultSet(String sql) throws SQLException {
        return connect().createStatement().executeQuery(sql);
    }

    private static void search() {
        try (PreparedStatement ps = getPrepStmt(PRINT_PRODUCT_IN_CATEGORY)) {
            System.out.println("Search for a category name");
            ps.setString(1, sc.nextLine());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getString("categoryNAme") + " | " +
                        rs.getString("discName") + " | " +
                        rs.getString("discPrice"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static PreparedStatement getPrepStmt(String sql) throws SQLException {
        return connect().prepareStatement(sql);
    }

    private static void insert(String discName, String categoryID, int price) {
        try (PreparedStatement ps = getPrepStmt(ADD_PRODUCT)) {
            ps.setString(1, discName);
            ps.setString(2, categoryID);
            ps.setInt(3, price);
            ps.executeUpdate();
            System.out.println("You added a new Disc");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void editProduct() {
        System.out.println("Which disc would you like to edit? (Insert name):");
        String discName = sc.nextLine();
        System.out.println("Insert the new price:");
        int discPrice = Integer.parseInt(sc.nextLine());

        edit(discName, discPrice);
    }

    private static void edit(String discName, int discPrice) {
        try (PreparedStatement ps = getPrepStmt(EDIT_PRODUCT)) {

            ps.setInt(1, discPrice);
            ps.setString(2, discName);
            ps.executeUpdate();
            System.out.println("The product is edited");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void delete(String discName) {
        try (PreparedStatement ps = getPrepStmt(DELETE_PRODUCT)) {
            ps.setString(1, discName);
            ps.executeUpdate();
            System.out.println("The disc is deleted");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void quitMessage() {
        System.out.println("Welcome back");
    }

}