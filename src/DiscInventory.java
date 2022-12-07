
import java.awt.*;
import java.sql.*;
import java.util.Scanner;

public class DiscInventory {
	static Scanner sc;

	static String PRINT_CATEGORIES = "SELECT * FROM Category ORDER BY categoryID";
	static String PRINT_PRODUCTS = "SELECT * FROM Disc ORDER BY categoryID";

	String sql = "DELETE FROM bok WHERE bokId = ?";
	String sql1 = "UPDATE bok SET bokForfattare = ? , "
			+ "bokTitel = ? , "
			+ "bokPris = ? "
			+ "WHERE bokId = ?";
	String sql2 = "INSERT INTO bok(bokTitel, bokForfattare, bokPris) VALUES(?,?,?)";
	String sql3 = "SELECT * FROM bok WHERE bokForfattare = ? ";
	String sql4 = "SELECT * FROM bok";


	public static void main(String[] args) {
		sc = new Scanner(System.in);

		String choice;
		do {
			printActions();
			choice = sc.nextLine().toLowerCase();
			switchMenu(choice, sc);
		} while (!choice.equals("e"));
	}

	private static void switchMenu(String choice, Scanner sc) {
		switch (choice) {
			case "1" -> printCategories();
			case "2" -> printProducts();
			case "3" -> addNewProduct();
			case "4" -> editProduct("Bilbo", "Tolkien, J.R.R", 100, 1);
			case "5" -> deleteProduct();
			case "6" -> search();
			case "e" -> quitMessage();
			default -> System.out.println("Please choose one of the alternatives below:");
		}
	}

	private static void printCategories() {

		try (ResultSet rs = getResultSet(PRINT_CATEGORIES)) {
			while (rs.next()) {
				System.out.println(rs.getString("categoryName"));
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	private static void printProducts() {
		try (ResultSet rs = getResultSet(PRINT_PRODUCTS)) {
			while (rs.next()) {
				System.out.println(
						rs.getInt("bokId") +
								rs.getString("bokTitel") +
								rs.getString("bokForfattare") +
								rs.getString("bokPris"));
			}

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	private static Connection connect() {
		// SQLite connection string
		String url = "jdbc:sqlite:C:/Users/julia/DataGripProjects/Database/JDBC/jdbc.db";
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(url);
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
				2. Print all discs
				3. Add new disc
				4. Edit disc
				5. Remove disc
				6. Search
				e. Exit
				""");
	}

	// Metod för användarens inmatningar (som en controller)
	private static void addNewProduct() {
		System.out.println("Skriv in titel på boken: ");
		String inputTitel = sc.nextLine();
		System.out.println("Skriv in författare på boken: ");
		String inputForfattare = sc.nextLine();
		System.out.println("Skriv in pris på boken: ");
		int inputPris = sc.nextInt();
		insert(inputTitel, inputForfattare, inputPris);
		sc.nextLine();
	}

	private static void deleteProduct() {
		System.out.println("Skriv in id:t på boken som ska tas bort: ");
		int inputId = sc.nextInt();
		delete(inputId);
		sc.nextLine();
	}

	private static void printBok() {
		String sql = "SELECT * FROM bok";

		try (ResultSet rs = getResultSet(sql)) {

			// loop through the result set
			while (rs.next()) {
				System.out.println(
						rs.getInt("bokId") +
								rs.getString("bokTitel") +
								rs.getString("bokForfattare") +
								rs.getString("bokPris"));
			}

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	private static ResultSet getResultSet(String sql) throws SQLException {
		return connect().createStatement().executeQuery(sql);
	}

	private static void search() {
		String sql = "SELECT * FROM bok WHERE bokForfattare = ? ";

		try (PreparedStatement pstmt = getPstmt(sql)) {

			String inputForfattare = "Astrid Lindgren";

			// set the value
			pstmt.setString(1, inputForfattare);
			//
			ResultSet rs = pstmt.executeQuery();

			// loop through the result set
			while (rs.next()) {
				System.out.println(rs.getInt("bokId") + "\t" +
						rs.getString("bokTitel") + "\t" +
						rs.getString("bokForfattare") + "\t" +
						rs.getString("bokPris"));
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	private static PreparedStatement getPstmt(String sql) throws SQLException {
		return connect().prepareStatement(sql);
	}

	// Metod för insert i bok-tabellen mot databasen
	private static void insert(String titel, String forfattare, int pris) {
		String sql = "INSERT INTO bok(bokTitel, bokForfattare, bokPris) VALUES(?,?,?)";

		try (PreparedStatement pstmt = connect().prepareStatement(sql)) {
			pstmt.setString(1, titel);
			pstmt.setString(2, forfattare);
			pstmt.setInt(3, pris);
			pstmt.executeUpdate();
			System.out.println("Du har lagt till en ny bok");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	// Update mot bok-tabellen i databasen
	private static void editProduct(String forfattare, String titel, int pris, int id) {
		String sql = "UPDATE bok SET bokForfattare = ? , "
				+ "bokTitel = ? , "
				+ "bokPris = ? "
				+ "WHERE bokId = ?";

		try {
			Connection conn = connect();
			PreparedStatement prepStatement = conn.prepareStatement(sql);

			// set the corresponding param
			prepStatement.setString(1, titel);
			prepStatement.setString(2, forfattare);
			prepStatement.setInt(3, pris);
			prepStatement.setInt(4, id);
			// update
			prepStatement.executeUpdate();
			System.out.println("Du har uppdaterat vald bok");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	// Delete mot bok-tabellen i databasen
	private static void quitMessage() {
		System.out.println("Welcome back");
	}

	private static void delete(int id) {
		String sql = "DELETE FROM bok WHERE bokId = ?";

		try {
			Connection conn = connect();
			PreparedStatement pstmt = conn.prepareStatement(sql);

			// set the corresponding param
			pstmt.setInt(1, id);
			// execute the delete statement
			pstmt.executeUpdate();
			System.out.println("Du har tagit bort boken");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

}
