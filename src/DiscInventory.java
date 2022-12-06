
import java.sql.*;
import java.util.Scanner;

public class DiscInventory {
	public static Scanner sc;

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
				1. Print categories
				2. Print products
				3. Add new product
				4. Edit product
				5. Remove product
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

	private static void printProducts() {
		String sql = "SELECT * FROM bok";

		try {
			Connection conn = connect();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

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

	private static void search() {
		String sql = "SELECT * FROM bok WHERE bokForfattare = ? ";

		try {
			Connection conn = connect();
			PreparedStatement pstmt = conn.prepareStatement(sql);

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

	// Metod för insert i bok-tabellen mot databasen
	private static void insert(String titel, String forfattare, int pris) {
		String sql = "INSERT INTO bok(bokTitel, bokForfattare, bokPris) VALUES(?,?,?)";

		try {
			Connection conn = connect();
			PreparedStatement pstmt = conn.prepareStatement(sql);
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
