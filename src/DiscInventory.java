
import java.sql.*;
import java.util.Scanner;

public class DiscInventory {
	public static void main(String[] args) {

		String choice;
		do {
			Print.masterStartMenu();
			choice = sc.nextLine().toLowerCase();
			switchMenu(choice, sc, categoryList, products, shoppingCart);
		} while (!choice.equals("e"));

		private static void switchMenu(String choice, Scanner sc, List<Category> categoryList, List<Product> products,
				HashMap<Product, Integer> shoppingCart) {
			switch (choice) {
				case "1" -> Customer.menu(sc, categoryList, products, shoppingCart);
				case "2" -> toAdminMenu(sc, categoryList, products);
				case "3" -> toMasterMenu(sc, categoryList, products, shoppingCart);
				case "e" -> Print.quitMessage();
				default -> System.out.println("Please choose one of the alternatives below:");
			}
		}

		printActions();
		while (!quit) {
			System.out.println("\nVälj (6 för att visa val):");
			int action = scanner.nextInt();
			scanner.nextLine();

			switch (action) {
				case 0 -> {
					System.out.println("\nStänger ner...");
					quit = true;
				}
				case 1 -> selectAll();
				case 2 -> insertBook();

				//insert("Sagan om ringen", "Tolkien, J.R.R", 120);
				case 3 -> update("Bilbo", "Tolkien, J.R.R", 100, 1);
				case 4 ->
					//delete(1);
						deleteBook();
				case 5 -> searchBook();
				case 6 -> printActions();
			}
		}

	}

	private static Scanner scanner = new Scanner(System.in);

	private static Connection connect() {
		// SQLite connection string
		String url = "jdbc:sqlite:C:/Users/julia/DataGripProjects/Database/JDBC/jdbc.db";
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return conn;
	}

	private static void printActions() {
		System.out.println("""
				                
				Products
				========
				1. Print inventory
				2. Add new product
				3. Edit inventory
				4. Remove product
				e. Exit
				""");

		System.out.println("\nVälj:\n");
		System.out.println("0  - Stäng av\n" +
				"1  - Visa alla böcker\n" +
				"2  - Lägga till en ny bok\n" +
				"3  - Uppdatera en bok\n" +
				"4  - Ta bort en bok\n" +
				"5  - Sök efter en författares böckerk\n" +
				"6  - Visa en lista över alla val.");
	}
	// Metod för användarens inmatningar (som en controller)

	private static void insertBook() {
		System.out.println("Skriv in titel på boken: ");
		String inputTitel = scanner.nextLine();
		System.out.println("Skriv in författare på boken: ");
		String inputForfattare = scanner.nextLine();
		System.out.println("Skriv in pris på boken: ");
		int inputPris = scanner.nextInt();
		insert(inputTitel, inputForfattare, inputPris);
		scanner.nextLine();
	}

	private static void deleteBook() {
		System.out.println("Skriv in id:t på boken som ska tas bort: ");
		int inputId = scanner.nextInt();
		delete(inputId);
		scanner.nextLine();
	}

	private static void selectAll() {
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

	private static void searchBook() {
		String sql = "SELECT * FROM bok WHERE bokForfattare = ? ";

		try (
				Connection conn = connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

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

	private static void update(String forfattare, String titel, int pris, int id) {
		String sql = "UPDATE bok SET bokForfattare = ? , "
				+ "bokTitel = ? , "
				+ "bokPris = ? "
				+ "WHERE bokId = ?";

		try (Connection conn = connect();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {

			// set the corresponding param
			pstmt.setString(1, titel);
			pstmt.setString(2, forfattare);
			pstmt.setInt(3, pris);
			pstmt.setInt(4, id);
			// update
			pstmt.executeUpdate();
			System.out.println("Du har uppdaterat vald bok");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	// Delete mot bok-tabellen i databasen

	private static void delete(int id) {
		String sql = "DELETE FROM bok WHERE bokId = ?";

		try (Connection conn = connect();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {

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
