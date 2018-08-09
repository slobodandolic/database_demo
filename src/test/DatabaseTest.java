package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import klasa.DatabaseMain;

class DatabaseTest {

	DatabaseMain dbtest = new DatabaseMain();
	Connection connect = null;
	Statement statement = null;
	PreparedStatement preparedStatement = null;

	@Test
	void testDbConnect() {

		dbtest.dbConnect();
		assertTrue(dbtest.getConnect() != null);
		System.out.println("testDbConnect Success");

	}

	@Test
	void testFillDbase() {

		int id = 100;

		String sql = "select * from dbkodovi.tabela where id=" + id + "";

		try {
			dbtest.dbConnect();
			statement = dbtest.getConnect().createStatement();

			ResultSet result = statement.executeQuery(sql);
			if (result.next()) {
				String kod = result.getString(2);
				assertEquals(8, kod.length());

			}

			System.out.println("testFillDbase Success");

		} catch (Exception e) {
			System.out.println("testFillDbase Fail");
		}

	}

	@Test
	void testRandomstring() {

		assertEquals(8, dbtest.randomstring(8).length());

		System.out.println("testRandomString Success");
	}

	@Test
	void testRandomLista() {

		ArrayList<Integer> testLista = dbtest.randomLista();

		assertEquals(1000, testLista.size());

		System.out.println("testRandomLista Success");
	}

	@Test
	void testCheckAward() {

		int id = 100;
		String sql = "select * from dbkodovi.tabela where id=" + id + "";

		try {
			dbtest.dbConnect();
			statement = dbtest.getConnect().createStatement();

			ResultSet result = statement.executeQuery(sql);
			if (result.next()) {
				String kod = result.getString(3);
				assertEquals("Zamena", kod);

			}

			System.out.println("testCheckAward Success");

		} catch (Exception e) {
			System.out.println("testCheckAward Fail");
		}

	}

}

/*
 * 
 * ***ZA OVE TREBA MOCK***
 * 
 * @Test void testUpdate() { fail("Not yet implemented"); }
 * 
 * @Test void testDelete() { fail("Not yet implemented"); }
 * 
 * @Test void testInsert() { fail("Not yet implemented"); }
 */
