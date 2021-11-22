package com.skilldistillery.filmquery.app;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import com.skilldistillery.filmquery.database.DatabaseAccessor;
import com.skilldistillery.filmquery.database.DatabaseAccessorObject;
import com.skilldistillery.filmquery.entities.Film;

public class FilmQueryApp {

	DatabaseAccessor db;

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
		System.out.println("Year: " + film.getReleaseYear().toString().substring(0, 5));
		System.out.println("Rating: " + film.getRating());
		System.out.println("Description: " + film.getDescription());
	}

	private void startUserInterface(Scanner in) {
		menu: while (true) {
			showMenuOptions();

			try {
				int option = readInt(in);

				switch (option) {
				case 1: {// Film by ID
					System.out.println("Please enter a whole number ID:");
					int id = readInt(in);

					Film film = db.findFilmById(id);

					if (film != null) {
						printFilm(film);
					} else {
						System.out.println("Film ID not found: " + id);
					}
					break;
				}

				case 2: {// Film by Keyword
					System.out.println("Please enter a keyword:");
					String keyword = readString(in);

					List<Film> films = db.findFilmByKeyword(keyword);

					if (films.size() > 0) {
						for (Film film : films) {
							printFilm(film);
						}
					} else {
						System.out.println("No films found.");
					}

					break;
				}

				case 3: // Quit
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
		System.out.println("\t1) Lookup film by ID");
		System.out.println("\t2) Lookup film by Keyword");
		System.out.println("\t3) Exit");
	}

	/**
	 * Causes the prompt "> " to show, indicating the application is awaiting user
	 * input.
	 * 
	 * @param in the scanner.
	 * @return the user input.
	 */
	private String readString(Scanner in) {
		System.out.print("> ");
		return in.nextLine();
	}

	/**
	 * Causes the prompt "> " to show indicating the application is awaiting user
	 * input.
	 * 
	 * @param in the scanner.
	 * @return the user input.
	 */
	private int readInt(Scanner in) throws NumberFormatException {
		return Integer.parseInt(this.readString(in));
	}

}
