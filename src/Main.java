
import java.sql.*;
import java.util.Scanner;

public class Main {
    static Scanner sc = new Scanner(System.in);
    static String URL = "jdbc:sqlite:src/Lab3.db";
    static String PRINT_CATEGORIES = """
            SELECT * FROM Category
            ORDER BY categoryID""";
    static String PRINT_PRODUCTS = """
            SELECT discID, categoryName, discName, discPrice
            FROM Disc D
            INNER JOIN Category C on C.categoryID = D.discCategoryID
            ORDER BY categoryID""";
    static String DELETE_PRODUCT = """
            DELETE FROM Disc
            WHERE discName = ?""";
    private static final String DELETE_CATEGORY = """
            DELETE FROM Category
            WHERE categoryName = 'Mini Disc'""";
    static String EDIT_PRODUCT = """
            UPDATE Disc SET discPrice = ?
            WHERE discName = ?""";
    static String ADD_PRODUCT = """
            INSERT INTO Disc(discName, discCategoryID, discPrice)
            VALUES(?,?,?)""";

    static String ADD_CATEGORY = """
            INSERT INTO Category (categoryName)
            VALUES ('Mini Disc')""";
    static String PRINT_PRODUCT_IN_CATEGORY = """
            SELECT categoryName, discName, discPrice
            FROM Category C
            INNER JOIN Disc D on C.categoryID = D.discCategoryID
            WHERE categoryName = ?""";
    static String PRINT_AVG_PRICE_PER_CATEGORY = """
            SELECT categoryName,
            CASE
            WHEN avg(discPrice) THEN avg(discPrice)
            END AS 'avgPrice'
            FROM Category C
            INNER JOIN Disc D on C.categoryID = D.discCategoryID
            GROUP BY categoryName""";

    public static void main(String[] args) {
        String choice;
        do {
            printMenu();
            choice = getInput().toLowerCase();
            switchMenu(choice);
        } while (!choice.equals("e"));
    }

    private static String getInput() {
        return sc.nextLine();
    }

    private static void printMenu() {
        System.out.println("""
                                
                Menu
                ====
                1. Add new category (Mini Disc)
                2. Remove category (Mini Disc)
                3. Search disc in category
                4. Print all categories
                5. Print avg prices
                6. Print all discs
                7. Add new disc
                8. Remove disc
                9. Edit disc
                e. Exit
                """);
    }

    private static void switchMenu(String choice) {
        switch (choice) {
            case "1" -> addCategory();
            case "2" -> deleteCategory();
            case "3" -> search();
            case "4" -> printCategories();
            case "5" -> avgPrice();
            case "6" -> printProducts();
            case "7" -> addNewProduct();
            case "8" -> deleteProduct();
            case "9" -> editProduct();
            case "e" -> quitMessage();
            default -> System.out.println("Please choose one of the alternatives below:");
        }
    }

    private static void addCategory() {
        try (PreparedStatement ps = getPrepStmt(ADD_CATEGORY)) {
            ps.executeUpdate();
            System.out.println("You added a new Category");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void deleteCategory() {
        try (PreparedStatement ps = getPrepStmt(DELETE_CATEGORY)) {
            ps.executeUpdate();
            System.out.println("The category is deleted");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
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
                        rs.getString("discID") + " | " +
                                rs.getString("discName") + " | " +
                                rs.getString("categoryName") + " | " +
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

    private static void addNewProduct() {
        System.out.println("Insert name of the disc:");
        String inputDiscName = getInput();
        System.out.println("Fill in categoryID:");
        printCategories();
        String inputCategoryID = getInput();
        System.out.println("Fill in price:");
        int inputPrice = Integer.parseInt(getInput());
        insertProduct(inputDiscName, inputCategoryID, inputPrice);
    }

    private static void deleteProduct() {
        System.out.println("Which disc would you like to remove? (Insert name): ");
        String discName = getInput();
        delete(discName);
    }

    private static ResultSet getResultSet(String sql) throws SQLException {
        return connect().createStatement().executeQuery(sql);
    }

    private static void search() {
        try (PreparedStatement ps = getPrepStmt(PRINT_PRODUCT_IN_CATEGORY)) {
            System.out.println("Search for a category name");
            ps.setString(1, getInput());
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

    private static void insertProduct(String discName, String categoryID, int price) {
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
        String discName = getInput();
        System.out.println("Insert the new price:");
        int discPrice = Integer.parseInt(getInput());

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
