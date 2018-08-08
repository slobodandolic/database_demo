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

public class Database_init implements interfejsi.nagrada, interfejsi.duplikati {

	public Database_init() {
		super();
	}

	Connection connect = null;
	Statement statement = null;
	PreparedStatement preparedStatement = null;
	String nagrada = null;
	Scanner sc = new Scanner(System.in);

	Properties prop = readConfig();
	String dbase = prop.getProperty("database");
	String dbpass = prop.getProperty("dbpassword");
	String dbuser = prop.getProperty("dbuser");

	ArrayList<Integer> ponovljeniKodovi = new ArrayList<>();

	public Properties readConfig() {

		Properties prop = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream("config.properties");

			// properties file
			prop.load(input);

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
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

	public void fillDbase() {



		ArrayList<String> listaKodova = new ArrayList<>();

		ArrayList<Integer> lista = rendomLista();

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

	public String randomstring(int length) {

		final char[] chars = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'Q', 'W', 'E', 'R', 'T', 'Z', 'U', 'I',
				'O', 'P', 'A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L', 'Y', 'X', 'C', 'V', 'B', 'N', 'M' };

		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < length; i++) {
			stringBuilder.append(chars[new Random().nextInt(chars.length)]);
		}
		return stringBuilder.toString();

	}

	public ArrayList<Integer> rendomLista() {

		ArrayList<Integer> lista = new ArrayList<Integer>();
		for (int i = 0; i < 1000; i++) {
			lista.add(i, i);

		}

		Collections.shuffle(lista);

		return lista;
	}

	public void checkAward() {

		try {
			System.out.print("Unesite kod:");

			String kod = sc.next();
			ResultSet resultSet = statement
					.executeQuery("select nagrada from dbkodovi.tabela where kod = '" + kod + "'");
			if (resultSet.next()) {
				System.out.println("Nagrada za uneti kod " + kod + " je: " + resultSet.getString(1) + ".");
			} else {
				System.out.println("Pogresan kod. Pokusajte ponovo.");
			}

			// sc.close();
		} catch (Exception e) {
			System.out.println("Ne radi provera kodova");
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

	public void update() {




		try {

			statement = connect.createStatement();
						
			System.out.print("Unesite id koji zelite da dodate: ");
			int id = sc.nextInt();

			preparedStatement = connect.prepareStatement("update dbkodovi.tabela set kod = ?, nagrada = ? where id = " + id + "");
						
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
			System.out.println("fejluje");
			e.printStackTrace();
		}

	}
	
	public void select () {

		
		try {
						
			System.out.print("Unesite id koji zelite da pogledate: ");
			int id = sc.nextInt();						
			sc.nextLine();

			String sql = "select * from dbkodovi.tabela where id=" + id + "";
			 
			ResultSet result = statement.executeQuery(sql);
			 
			if (result.next()){
			    String kod = result.getString(2);
			    String nagrada = result.getString(3);
			    
				System.out.println("Uneli ste id: " + id + "." + "Kod za navedeni id je: " + kod + " a nagrada je: " + nagrada);
			    
			} else {
				System.out.println("Nepostojeci ID");
			}
			

			
		} catch (Exception e) {
			System.out.println("Nepostojeci kod");
			
		}

		
	}
	
	public void delete () {

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
	
	public void insert () {
		
		
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
	
}
