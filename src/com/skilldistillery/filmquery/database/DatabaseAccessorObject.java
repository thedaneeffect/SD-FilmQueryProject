package com.skilldistillery.filmquery.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.skilldistillery.filmquery.entities.Actor;
import com.skilldistillery.filmquery.entities.Film;

public class DatabaseAccessorObject implements DatabaseAccessor {

	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private final Connection conn;

	public DatabaseAccessorObject() throws SQLException {
		this.conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sdvid?useSSL=false", "student", "student");
	}

	/**
	 * A utility method used to reduce typing.
	 * 
	 * @param film the film being populated
	 * @param res  the result set used to populate the film
	 * @return the film provided
	 * @throws SQLException
	 */
	private final Film populate(Film film, ResultSet res) throws SQLException {
		film.setId(res.getInt("id"));
		film.setTitle(res.getString("title"));
		film.setDescription(res.getString("description"));
		film.setReleaseYear(res.getDate("release_year"));
		film.setLanguage(res.getString("language"));
		film.setRentalDuration(res.getInt("rental_duration"));
		film.setRentalRate(res.getDouble("rental_rate"));
		film.setLength(res.getInt("length"));
		film.setReplacementCost(res.getDouble("replacement_cost"));
		film.setRating(res.getString("rating"));
		film.setSpecialFeatures(Stream.of(res.getString("special_features").split(",")).collect(Collectors.toSet()));
		film.setActors(this.findActorsByFilmId(film.getId()));
		return film;
	}

	@Override
	public Film findFilmById(int filmId) throws SQLException {
		try (PreparedStatement stmt = this.conn.prepareStatement("SELECT film.*, language.name as 'language' FROM film LEFT JOIN language ON film.language_id = language.id WHERE film.id = ? LIMIT 1")) {
			stmt.setInt(1, filmId);
			ResultSet res = stmt.executeQuery();
			if (res.next()) {
				return populate(new Film(), res);
			}
		}
		return null;
	}

	@Override
	public List<Film> findFilmByKeyword(String keyword) throws SQLException {
		keyword = "%" + keyword + "%";
		
		List<Film> films = new ArrayList<>();
		
		try (PreparedStatement stmt = this.conn
				.prepareStatement("SELECT film.*, language.name as 'language' FROM film LEFT JOIN language ON film.language_id=language.id WHERE film.title LIKE ? OR film.description LIKE ?")) {
			stmt.setString(1, keyword);
			stmt.setString(2, keyword);

			for (ResultSet res = stmt.executeQuery(); res.next();) {
				films.add(populate(new Film(), res));

			}
		}
		return films;
	}

	@Override
	public Actor findActorById(int actorId) throws SQLException {
		try (PreparedStatement stmt = this.conn.prepareStatement("SELECT * FROM actor WHERE id=? LIMIT 1")) {
			stmt.setInt(1, actorId);
			ResultSet res = stmt.executeQuery();
			if (res.next()) {
				return new Actor(res.getInt("id"), res.getString("first_name"), res.getString("last_name"));
			}
		}
		return null;
	}

	@Override
	public List<Actor> findActorsByFilmId(int filmId) throws SQLException {
		try (PreparedStatement stmt = this.conn.prepareStatement("SELECT * FROM film_actor WHERE film_id=?")) {
			stmt.setInt(1, filmId);

			Set<Integer> actorIds = new HashSet<>();

			for (ResultSet res = stmt.executeQuery(); res.next();) {
				actorIds.add(res.getInt("actor_id"));
			}

			List<Actor> actors = new ArrayList<>(actorIds.size());

			for (int id : actorIds) {
				actors.add(this.findActorById(id));
			}

			return actors;
		}
	}

}
