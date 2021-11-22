package com.skilldistillery.filmquery.app;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import com.skilldistillery.filmquery.database.DatabaseAccessor;
import com.skilldistillery.filmquery.database.DatabaseAccessorObject;
import com.skilldistillery.filmquery.entities.Film;

public class FilmQueryApp {

	private final DatabaseAccessor db;

	public static void main(String[] args) throws SQLException {
		FilmQueryApp app = new FilmQueryApp();
		app.launch();
	}

	public FilmQueryApp() throws SQLException {
		this.db = new DatabaseAccessorObject();
	}

	private void launch() {
		try (Scanner in = new Scanner(System.in)) {
			startUserInterface(in);
		}
	}

	private static void printFilm(Film film) {
		System.out.println("Title: " + film.getTitle());
		System.out.println("  Year: " + film.getReleaseYear().toString().substring(0, 4));
		System.out.println("  Rating: " + film.getRating());
		System.out.println("  Description: " + film.getDescription());
	}

	private void promptFilmByID(Scanner in) throws SQLException {
		int id = this.readInt("ID", in);
		Film film = db.findFilmById(id);

		if (film != null) {
			printFilm(film);
		} else {
			System.out.println("Film ID not found: " + id);
		}
	}

	private void promptFilmByKeyword(Scanner in) throws SQLException {
		String keyword = this.readString("Keyword", in);
		List<Film> films = db.findFilmByKeyword(keyword);

		if (!films.isEmpty()) {
			for (Film film : films) {
				printFilm(film);
			}
		} else {
			System.out.println("No films found.");
		}
	}

	private void startUserInterface(Scanner in) {
		menu: while (true) {
			showMenuOptions();

			try {
				int option = readInt("Option", in);
				switch (option) {
				case 1:
					this.promptFilmByID(in);
					break;
				case 2:
					this.promptFilmByKeyword(in);
					break;
				case 3:
					break menu;
				default:
					System.out.println("Invalid option.");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		System.out.println("Bye!");
	}

	private void showMenuOptions() {
		System.out.println("Please select an option:");
		System.out.println("    1) Lookup film by ID");
		System.out.println("    2) Lookup film by Keyword");
		System.out.println("    3) Exit");
	}

	private String readString(String prompt, Scanner in) {
		System.out.print(prompt + ": ");
		return in.nextLine();
	}

	private int readInt(String prompt, Scanner in) throws NumberFormatException {
		return Integer.parseInt(this.readString(prompt, in));
	}

}
