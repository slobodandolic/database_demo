package klasa;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;

public class DatabaseInit
		implements interfejsi.DatabaseCheckAward, interfejsi.DatabaseCheckDuplicates, interfejsi.DatabaseCRUD {

	Connection connect = null;
	Statement statement = null;
	PreparedStatement preparedStatement = null;
	String nagrada = null;
	Scanner sc = new Scanner(System.in);

	Properties prop = readConfig();
	public String dbase = prop.getProperty("database");
	public String dbpass = prop.getProperty("dbpassword");
	public String dbuser = prop.getProperty("dbuser");

	ArrayList<Integer> ponovljeniKodovi = new ArrayList<>();

	public Properties readConfig() {

		Properties prop = new Properties();

		try (InputStream input = new FileInputStream("config.properties"))

		{
			prop.load(input);

		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return prop;
	}

	public void dbConnect() {

		try {
			connect = DriverManager.getConnection(dbase, dbuser, dbpass);
			System.out.println("Connection succesfull");

		} catch (Exception e) {
			System.out.println("Connection fail");
		}
	}

	public void checkAward() {
		
		//proverava nagradu i upisuje 1 kad proveri kod u 4. kolonu


		try {
			System.out.print("Unesite kod:");

			String kod = sc.next();

			String sql = "select proveren from dbkodovi.tabela where kod = '" + kod + "'";
			ResultSet result = statement.executeQuery(sql);

			if (result.next()) {

				if (result.getInt(1) == 0) {
					ResultSet resultSet = statement
							.executeQuery("select nagrada from dbkodovi.tabela where kod = '" + kod + "'");
					if (resultSet.next()) {
						System.out.println("Nagrada za uneti kod " + kod + " je: " + resultSet.getString(1) + ".");

						statement = connect.createStatement();
						preparedStatement = connect
								.prepareStatement("update dbkodovi.tabela set proveren = ? where kod = '" + kod + "'");
						preparedStatement.setInt(1, 1);
						preparedStatement.executeUpdate();

						// ako je kod dobitan, nastavak sa dobitnim kodom u metodi checkUser, unos
						// imena, usernamea etc.

						checkUser(kod);

					}
				} else {
					System.out.println("Kod je vec iskoriscen!");
				}
			} else {
				System.out.println("Pogresan kod. Pokusajte ponovo.");
			}

		} catch (Exception e) {
			System.out.println("Ne radi provera kodova");
		}
	}

	public void fillDbase() {


		ArrayList<String> listaKodova = new ArrayList<>();

		ArrayList<Integer> lista = randomLista();

		for (int i = 0; i < 1000; i++) {

			int x = lista.get(i);

			if (x < 3) {
				nagrada = "Letovanje";
			} else if (x < 53) {
				nagrada = "Prva nagrada";
			} else if (x < 103) {
				nagrada = "Druga nagrada";
			} else if (x < 1000) {
				nagrada = "Zamena";
			}

			String kod = randomstring(8);

			if (!listaKodova.contains(kod)) {
				listaKodova.add(kod);
			} else {
				ponovljeniKodovi.add(i);
				continue;
			}

			try {
				statement = connect.createStatement();

				preparedStatement = connect.prepareStatement("insert into  dbkodovi.tabela values (?, ?, ?)");

				preparedStatement.setInt(1, i);
				preparedStatement.setString(2, kod);
				preparedStatement.setString(3, nagrada);
				preparedStatement.executeUpdate();
			} catch (Exception e) {

			}
		}

	}

	public void update() {

		try {

			statement = connect.createStatement();

			System.out.print("Unesite id koji zelite da dodate: ");
			int id = sc.nextInt();

			preparedStatement = connect
					.prepareStatement("update dbkodovi.tabela set kod = ?, nagrada = ? where id = " + id + "");

			sc.nextLine();

			System.out.print("Unesite kod: ");
			String kod = sc.nextLine();
			preparedStatement.setString(1, kod);

			System.out.print("Unesite nagradu: ");
			String nagrada = sc.nextLine();
			preparedStatement.setString(2, nagrada);

			preparedStatement.executeUpdate();

			System.out.println("Uspesno promenjen unos pod id-em: " + id + " koji je osvojio nagradu: " + nagrada);

		} catch (Exception e) {
			System.out.println("SQL update fail");
			e.printStackTrace();
		}

	}

	public void select() {

		try {

			System.out.print("Unesite id koji zelite da pogledate: ");
			int id = sc.nextInt();
			sc.nextLine();

			String sql = "select * from dbkodovi.tabela where id =" + id + "";

			ResultSet result = statement.executeQuery(sql);

			if (result.next()) {
				String kod = result.getString(2);
				String nagrada = result.getString(3);

				System.out.println(
						"Uneli ste id: " + id + "." + "Kod za navedeni id je: " + kod + " a nagrada je: " + nagrada);

			} else {
				System.out.println("Nepostojeci ID");
			}
		} catch (Exception e) {
			System.out.println("Nepostojeci kod");
		}

	}

	public void delete() {

		try {

			System.out.print("Unesite id koji zelite da obrisete: ");
			int id = sc.nextInt();
			sc.nextLine();

			String sql = "delete from dbkodovi.tabela where id = " + id + "";

			preparedStatement = connect.prepareStatement(sql);
			preparedStatement.executeUpdate();

			System.out.println("Uspesno obrisan unos.");

		} catch (Exception e) {
			System.out.println("Nepostojeci kod");

		}

	}

	public void insert() {

		try {

			statement = connect.createStatement();

			String sql = "insert into  dbkodovi.tabela values (?, ?, ?)";

			preparedStatement = connect.prepareStatement(sql);

			System.out.print("Unesite id koji zelite da dodate: ");
			int id = sc.nextInt();

			preparedStatement.setInt(1, id);
			sc.nextLine();

			System.out.print("Unesite kod: ");
			String kod = sc.nextLine();
			preparedStatement.setString(2, kod);

			System.out.print("Unesite nagradu: ");
			String nagrada = sc.nextLine();
			preparedStatement.setString(3, nagrada);

			preparedStatement.executeUpdate();

			System.out.println("Uspesno dodat unos pod id-em: " + id + " koji je osvojio nagradu: " + nagrada);

		} catch (Exception e) {
			System.out.println("Unos vec postoji.");
		}

	}

	public void checkUser(String kod) {

		// uzima id nagrade iz dbkodovi.tabela
		int idNagrade = 0;

		try {
			String sql = "select * from dbkodovi.tabela where kod = '" + kod + "'";
			ResultSet result = statement.executeQuery(sql);

			if (result.next()) {
				idNagrade = result.getInt(1);
			} else {
				System.out.println("Greska prilikom provere IDa nagrade.");
			}

			System.out.print("Unesite username: ");

			String username = sc.next();

			// proverava username

			try {

				sql = "select * from dbkodovi.korisnici where username = '" + username + "'";

				result = statement.executeQuery(sql);

				if (result.next()) {
					// ako postoji username, pozovi metodu za unos nagrade za taj username

					String user = result.getString(5);
					existingUser(user, idNagrade);

				} else {
					// ako ne postoji username, pozovi metodu za unos novog usernamea

					newUser(idNagrade);
				}

			} catch (Exception e) {
				System.out.println("SQL greska u dbkodovi.korisnici");
			}
		} catch (Exception e) {
			System.out.println("SQL greska u dbkodovi.tabela");
		}
	}

	public String checkEmail() {

		// proverava da li email vec postoji u bazi i vraca string "1" ako postoji

		System.out.print("Unesite email: ");
		String email = sc.next();

		try {
			String sql = "select * from dbkodovi.korisnici where email= '" + email + "'";

			ResultSet result = statement.executeQuery(sql);

			if (result.next()) {
				return "1";
			} else {
				return email;
			}

		} catch (Exception e) {
			System.out.println("SQL greska u dbkodovi.korisnici u checkEmail()");
		}

		return email;
	}

	public void newUser(int idAward) {


		System.out.println("Novi korisnik!");

		DatabaseUser user = new DatabaseUser();
		// username i email moraju da budu jedinstveni
		// poziva metodu za proveru emaila
		// username vec proveren u checkUser metodi

		String email = checkEmail();

		if (email.equals("1")) {
			System.out.println("Postoji email, pokusajte ponovo.");

		} else {

			user.setEmail(email);

			System.out.print("Unesite username:");
			user.setUsername(sc.next());

			System.out.print("Unesite ime:");
			user.setIme(sc.next());

			System.out.print("Unesite prezime:");
			user.setPrezime(sc.next());

			insertUser(user);
		}

		try {

			statement = connect.createStatement();

			String sql = "insert into dbkodovi.tabelakorisnici values (?, ?)";

			preparedStatement = connect.prepareStatement(sql);
			preparedStatement.setString(1, user.getUsername());
			preparedStatement.setInt(2, idAward);

			preparedStatement.executeUpdate();

			System.out.println("Uspesno dodat unos bazu idkorisnika/idnagrade.");

		} catch (Exception e) {
			System.out.println("Greska newUser metoda.");
		}

	}

	public void insertUser(DatabaseUser user) {


		// unos korisnika u bazu korisnika

		try {

			statement = connect.createStatement();

			String sql = "insert into  dbkodovi.korisnici (ime, prezime, email, username) values (?, ?, ?, ?)";

			preparedStatement = connect.prepareStatement(sql);

			preparedStatement.setString(1, user.getIme());

			preparedStatement.setString(2, user.getPrezime());

			preparedStatement.setString(3, user.getEmail());

			preparedStatement.setString(4, user.getUsername());

			preparedStatement.executeUpdate();

			System.out.println("Uspesno dodat unos u bazu korisnika.");

		} catch (Exception e) {
			System.out.println("SQL in insertUser().");
		}

	}

	public void existingUser(String existingUser, int idAward) {



		// dodaje za postojeceg korisnika novu nagradu u bazu

		System.out.println("Cestitamo osvojili ste jos jednu nagradu!");

		String user = existingUser;
		int award = idAward;

		try {

			statement = connect.createStatement();

			String sql = "insert into  dbkodovi.tabelakorisnici values (?, ?)";

			preparedStatement = connect.prepareStatement(sql);

			preparedStatement.setString(1, user);
			preparedStatement.setInt(2, award);

			preparedStatement.executeUpdate();

			System.out.println("Uspesno dodat unos za postojeceg korisnika: " + existingUser + " u bazu idkorisnika/idnagrade");

		} catch (Exception e) {
			System.out.println("SQL in existingUser().");
		}

	}

	public Connection getConnect() {

		return connect;
	}

	public void setConnect(Connection connect) {

		this.connect = connect;
	}

	public String randomstring(int length) {

		final char[] chars = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'Q', 'W', 'E', 'R', 'T', 'Z', 'U', 'I',
				'O', 'P', 'A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L', 'Y', 'X', 'C', 'V', 'B', 'N', 'M' };

		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < length; i++) {
			stringBuilder.append(chars[new Random().nextInt(chars.length)]);
		}
		return stringBuilder.toString();

	}

	public ArrayList<Integer> randomLista() {

		ArrayList<Integer> lista = new ArrayList<Integer>();
		for (int i = 0; i < 1000; i++) {
			lista.add(i, i);

		}

		Collections.shuffle(lista);

		return lista;
	}

	
	public void selectLast() {
		
	//	SELECT * FROM `table_name` WHERE id=(SELECT MAX(id) FROM `table_name`);
		
		try {

			String sql = "select * from dbkodovi.tabela where id = (select max(id) from dbkodovi.tabela)";

			ResultSet result = statement.executeQuery(sql);

			if (result.next()) {
				int kod = result.getInt(1);

				System.out.println("Poslednji popunjen red u tabeli je: " + kod);

			} else {
				System.out.println("Greska selectLast sql");
			}
		} catch (Exception e) {
			System.out.println("Nepostojeci kod");
		}

	}
	@Override
	public void checkDuplicates() {


		try {

			ResultSet resultSetDuplikati = statement
					.executeQuery("select kod from dbkodovi.tabela group by kod having count(*) >=2");

			while (resultSetDuplikati.next()) {
				System.out.println("Sledeci kod ima najmanje jedan duplikat: " + resultSetDuplikati.getString(1));
			}

		} catch (Exception e1) {
			System.out.println("Ne rade duplikati");
		}

	}

}
