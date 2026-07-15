package student;

import java.sql.Connection;
import java.util.List;
import rs.ac.bg.etf.sab.operations.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ASUS D509D
 */
public class ss220011_Impl implements GenresOperations, MoviesOperations, RatingsOperations,
        TagsOperations, UsersOperations, WatchlistsOperations, GeneralOperations {

    private Connection getConn() throws SQLException {
        return DB.getInstance().getConnection();
    }

    //GenresOperations
    @Override
    public Integer addGenre(String name) {
        String query = "INSERT INTO Zanr(Naziv) OUTPUT INSERTED.ZanrId VALUES (?)";
        try (Connection conn = getConn(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ss220011_Impl.class.getName()).log(Level.INFO, "addGenre odbijen: {0}", ex.getMessage());
        }
        return null;
    }

    @Override
    public Integer updateGenre(Integer id, String name) {
        String query = "UPDATE Zanr SET Naziv = ? WHERE ZanrId = ?";
        try (Connection conn = getConn()) {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, name);
            ps.setInt(2, id);

            int rows = ps.executeUpdate();
            return rows > 0 ? id : null;
        } catch (SQLException ex) {
            Logger.getLogger(ss220011_Impl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public Integer removeGenre(Integer genreId) {
        String deleteVeze = "DELETE FROM FilmZanr WHERE ZanrId = ?";
        String deleteZanr = "DELETE FROM Zanr WHERE ZanrId = ?";
        try (Connection conn = getConn()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps1 = conn.prepareStatement(deleteVeze); PreparedStatement ps2 = conn.prepareStatement(deleteZanr)) {
                ps1.setInt(1, genreId);
                ps1.executeUpdate();
                ps2.setInt(1, genreId);

                int rows = ps2.executeUpdate();
                conn.commit();
                return rows > 0 ? genreId : null;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ss220011_Impl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public boolean doesGenreExist(String name) {
        String query = "SELECT 1 FROM Zanr WHERE Naziv = ?";
        try (Connection conn = getConn()) {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery();) {
                return rs.next();
            }
        } catch (SQLException ex) {
            Logger.getLogger(ss220011_Impl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public Integer getGenreId(String name) {
        String query = "SELECT ZanrId FROM Zanr WHERE Naziv = ?";
        try (Connection conn = getConn()) {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("ZanrId");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ss220011_Impl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public List<Integer> getAllGenreIds() {
        List<Integer> list = new ArrayList<>();
        String query = "SELECT ZanrId FROM Zanr";
        try (Connection conn = getConn()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                list.add(rs.getInt("ZanrId"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ss220011_Impl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    @Override
    public Integer addMovie(String title, Integer genreId, String director) {
        String insertFilm = "INSERT INTO Filmovi(Naslov, Reziser) OUTPUT INSERTED.FilmId VALUES (?, ?)";
        String insertVeza = "INSERT INTO FilmZanr (FilmId, ZanrId) VALUES (?, ?)";
        try (Connection conn = getConn()) {
            conn.setAutoCommit(false);
            Integer filmId = null;
            try (PreparedStatement ps = conn.prepareStatement(insertFilm)) {
                ps.setString(1, title);
                ps.setString(2, director);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        filmId = rs.getInt(1);
                    }
                }
            }
            if (filmId != null && genreId != null) {
                try (PreparedStatement ps2 = conn.prepareStatement(insertVeza)) {
                    ps2.setInt(1, filmId);
                    ps2.setInt(2, genreId);
                    ps2.executeUpdate();
                }
            }
            conn.commit();
            return filmId;
        } catch (SQLException ex) {
            Logger.getLogger(ss220011_Impl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public Integer updateMovieTitle(Integer id, String naziv) {
        String query = "UPDATE Filmovi SET Naslov = ? WHERE FilmId = ?";
        try (Connection conn = getConn()) {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, naziv);
            ps.setInt(2, id);

            int rows = ps.executeUpdate();
            return rows > 0 ? id : null;
        } catch (SQLException ex) {
            Logger.getLogger(ss220011_Impl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public Integer addGenreToMovie(Integer idF, Integer idZ) {
        String query = "INSERT INTO FilmZanr (FilmId, ZanrId) VALUES (?, ?)";
        try (Connection conn = getConn(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idF);
            ps.setInt(2, idZ);

            int rows = ps.executeUpdate();
            return rows > 0 ? idF : null;
        } catch (SQLException ex) {
            Logger.getLogger(ss220011_Impl.class.getName()).log(Level.INFO, "addGenreToMovie odbijen: {0}", ex.getMessage());
        }
        return null;
    }

    @Override
    public Integer removeGenreFromMovie(Integer idF, Integer idZ) {
        String query = "DELETE FROM FilmZanr WHERE FilmId = ? AND ZanrId = ?";
        try (Connection conn = getConn(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idF);
            ps.setInt(2, idZ);

            int rows = ps.executeUpdate();
            return rows > 0 ? idF : null;
        } catch (SQLException ex) {
            Logger.getLogger(ss220011_Impl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public Integer updateMovieDirector(Integer idF, String reziser) {
        String query = "UPDATE Filmovi SET Reziser = ? WHERE FilmId = ?";
        try (Connection conn = getConn()) {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, reziser);
            ps.setInt(2, idF);

            int rows = ps.executeUpdate();
            return rows > 0 ? idF : null;
        } catch (SQLException ex) {
            Logger.getLogger(ss220011_Impl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public Integer removeMovie(Integer idF) {
        try (Connection conn = getConn()) {
            conn.setAutoCommit(false);
            String[] q = {
                "DELETE FROM FilmZanr WHERE FilmId = ?",
                "DELETE FROM FilmTag WHERE FilmId = ?",
                "DELETE FROM ListaZaGledanje WHERE FilmId = ?",
                "DELETE FROM Ocena WHERE FilmId = ?"
            };
            for (String sql : q) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, idF);
                    ps.executeUpdate();
                }
            }
            String sqlF = "DELETE FROM Filmovi WHERE FilmId = ?";
            try (PreparedStatement psF = conn.prepareStatement(sqlF)) {
                psF.setInt(1, idF);
                int rows = psF.executeUpdate();
                conn.commit();
                return rows > 0 ? idF : null;
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ss220011_Impl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public List<Integer> getMovieIds(String naslov, String reziser) {
        List<Integer> list = new ArrayList<>();
        String query = "SELECT FilmId FROM Filmovi WHERE Naslov = ? AND Reziser = ?";
        try (Connection conn = getConn()) {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, naslov);
            ps.setString(2, reziser);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getInt("FilmId"));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ss220011_Impl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    @Override
    public List<Integer> getAllMovieIds() {
        List<Integer> list = new ArrayList<>();
        String query = "SELECT FilmId FROM Filmovi";
        try (Connection conn = getConn(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(rs.getInt("FilmId"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ss220011_Impl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    @Override
    public List<Integer> getMovieIdsByGenre(Integer idZ) {
        List<Integer> list = new ArrayList<>();
        String query = "SELECT FilmId FROM FilmZanr WHERE ZanrId = ?";
        try (Connection conn = getConn(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idZ);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getInt("FilmId"));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ss220011_Impl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    @Override
    public List<Integer> getGenreIdsForMovie(Integer idF) {
        List<Integer> list = new ArrayList<>();
        String query = "SELECT ZanrId FROM FilmZanr WHERE FilmId = ?";
        try (Connection conn = getConn(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idF);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getInt("ZanrId"));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ss220011_Impl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    @Override
    public List<Integer> getMovieIdsByDirector(String reziser) {
        List<Integer> list = new ArrayList<>();
        String query = "SELECT FilmId FROM Filmovi WHERE Reziser = ?";
        try (Connection conn = getConn(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, reziser);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getInt("FilmId"));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ss220011_Impl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    @Override
    public String getMovieTrend(Integer idF) {
        String query = "SELECT Status FROM Filmovi WHERE FilmId = ?";
        try (Connection conn = getConn()) {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, idF);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("Status");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ss220011_Impl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public boolean addRating(Integer idK, Integer idF, Integer ocena) {
        String query = "INSERT INTO Ocena (FilmId, KorisnikId, Vrednost, DatumOcene) VALUES (?, ?, ?, GETDATE())";
        try (Connection conn = getConn()) {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, idF);
            ps.setInt(2, idK);
            ps.setInt(3, ocena);
            boolean ok = ps.executeUpdate() > 0;
            if (ok) {
                try (PreparedStatement cs = conn.prepareStatement("EXEC SP_REWARD_USER_ ?, ?")) {
                    cs.setInt(1, idK);
                    cs.setInt(2, idF);
                    cs.execute();
                } catch (SQLException ignore) {

                }
            }
            return ok;
        } catch (SQLException ex) {
            Logger.getLogger(ss220011_Impl.class.getName()).log(Level.INFO, "Unos ocene odbijen: {0}", ex.getMessage());
        }
        return false;
    }

    @Override
    public boolean updateRating(Integer idK, Integer idF, Integer ocena) {
        String query = "UPDATE Ocena SET Vrednost = ?, DatumOcene = GETDATE() WHERE FilmId = ? AND KorisnikId = ?";
        try (Connection conn = getConn()) {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, ocena);
            ps.setInt(2, idF);
            ps.setInt(3, idK);
            boolean ok = ps.executeUpdate() > 0;
            if (ok) {
                try (PreparedStatement cs = conn.prepareStatement("EXEC SP_REWARD_USER_ ?, ?")) {
                    cs.setInt(1, idK);
                    cs.setInt(2, idF);
                    cs.execute();
                } catch (SQLException ignore) {

                }
            }
            return ok;
        } catch (SQLException ex) {
            Logger.getLogger(ss220011_Impl.class.getName()).log(Level.INFO, "Izmena ocene odbijena: {0}", ex.getMessage());
        }
        return false;
    }

    @Override
    public boolean removeRating(Integer idK, Integer idF) {
        String query = "DELETE FROM Ocena WHERE FilmId = ? AND KorisnikId = ?";
        try (Connection conn = getConn()) {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, idF);
            ps.setInt(2, idK);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(ss220011_Impl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public Integer getRating(Integer idK, Integer idF) {
        String query = "SELECT Vrednost FROM Ocena WHERE FilmId = ? AND KorisnikId = ?";
        try (Connection conn = getConn()) {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, idF);
            ps.setInt(2, idK);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("Vrednost");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ss220011_Impl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public List<Integer> getRatedMoviesByUser(Integer idK) {
        List<Integer> list = new ArrayList<>();
        String query = "SELECT FilmId FROM Ocena WHERE KorisnikId = ?";
        try (Connection conn = getConn(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idK);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getInt("FilmId"));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ss220011_Impl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    @Override
    public List<Integer> getUsersWhoRatedMovie(Integer idF) {
        List<Integer> list = new ArrayList<>();
        String query = "SELECT KorisnikId FROM Ocena WHERE FilmId = ?";
        try (Connection conn = getConn(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idF);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getInt("KorisnikId"));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ss220011_Impl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    @Override
    public Integer addTag(Integer idF, String tag) {
        String query = "INSERT INTO FilmTag (FilmId, Tag) VALUES (?, ?)";
        try (Connection conn = getConn(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idF);
            ps.setString(2, tag);
            return ps.executeUpdate() > 0 ? idF : null;
        } catch (SQLException ex) {
            Logger.getLogger(ss220011_Impl.class.getName()).log(Level.INFO, "addTag odbijen: {0}", ex.getMessage());
        }
        return null;
    }

    @Override
    public Integer removeTag(Integer idF, String tag) {
        String query = "DELETE FROM FilmTag WHERE FilmId = ? AND Tag = ?";
        try (Connection conn = getConn(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idF);
            ps.setString(2, tag);
            int rows = ps.executeUpdate();
            return rows > 0 ? idF : null;
        } catch (SQLException ex) {
            Logger.getLogger(ss220011_Impl.class.getName()).log(Level.INFO, "removeTag odbijen: {0}", ex.getMessage());
        }
        return null;
    }

    @Override
    public int removeAllTagsForMovie(Integer idF) {
        String query = "DELETE FROM FilmTag WHERE FilmId = ?";
        try (Connection conn = getConn()) {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, idF);
            return ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(ss220011_Impl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    @Override
    public boolean hasTag(Integer idF, String tag) {
        String query = "SELECT 1 FROM FilmTag WHERE FilmId = ? AND Tag = ?";
        try (Connection conn = getConn()) {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, idF);
            ps.setString(2, tag);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            Logger.getLogger(ss220011_Impl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public List<String> getTagsForMovie(Integer idF) {
        List<String> list = new ArrayList<>();
        String query = "SELECT Tag FROM FilmTag WHERE FilmId = ?";
        try (Connection conn = getConn(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idF);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getString("Tag"));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ss220011_Impl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    @Override
    public List<Integer> getMovieIdsByTag(String tag) {
        List<Integer> list = new ArrayList<>();
        String query = "SELECT DISTINCT FilmId FROM FilmTag WHERE Tag = ?";
        try (Connection conn = getConn(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, tag);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getInt("FilmId"));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ss220011_Impl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    @Override
    public List<String> getAllTags() {
        List<String> list = new ArrayList<>();
        String query = "SELECT DISTINCT Tag FROM FilmTag";
        try (Connection conn = getConn(); PreparedStatement ps = conn.prepareStatement(query)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getString("Tag"));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ss220011_Impl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    @Override
    public Integer addUser(String korIme) {
        String query = "INSERT INTO Korisnici (KorisnickoIme, BrojNagrada) OUTPUT INSERTED.KorisnikId VALUES (?, 0)";
        try (Connection conn = getConn(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, korIme);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ss220011_Impl.class.getName()).log(Level.INFO, "addUser odbijen: {0}", ex.getMessage());
        }
        return null;
    }

    @Override
    public Integer updateUser(Integer idK, String korIme) {
        String query = "UPDATE Korisnici SET KorisnickoIme = ? WHERE KorisnikId = ?";
        try (Connection conn = getConn()) {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, korIme);
            ps.setInt(2, idK);
            int rows = ps.executeUpdate();
            return rows > 0 ? idK : null;
        } catch (SQLException ex) {
            Logger.getLogger(ss220011_Impl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public Integer removeUser(Integer idK) {
        try (Connection conn = getConn()) {
            conn.setAutoCommit(false);
            String[] q = {
                "DELETE FROM ListaZaGledanje WHERE KorisnikId = ?",
                "DELETE FROM Ocena WHERE KorisnikId = ?"
            };
            for (String sql : q) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, idK);
                    ps.executeUpdate();
                }
            }
            String query = "DELETE FROM Korisnici WHERE KorisnikId = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, idK);
                int rows = ps.executeUpdate();
                conn.commit();
                return rows > 0 ? idK : null;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ss220011_Impl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public boolean doesUserExist(String korIme) {
        String query = "SELECT 1 FROM Korisnici WHERE KorisnickoIme = ?";
        try (Connection conn = getConn()) {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, korIme);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            Logger.getLogger(ss220011_Impl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public Integer getUserId(String korIme) {
        String query = "SELECT KorisnikId FROM Korisnici WHERE KorisnickoIme = ?";
        try (Connection conn = getConn()) {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, korIme);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("KorisnikId");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ss220011_Impl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public List<Integer> getAllUserIds() {
        List<Integer> list = new ArrayList<>();
        String query = "SELECT KorisnikId FROM Korisnici";
        try (Connection conn = getConn(); PreparedStatement ps = conn.prepareStatement(query)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getInt("KorisnikId"));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ss220011_Impl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    @Override
    public List<Integer> getRecommendedMoviesFromFavoriteGenres(Integer idK) {
        List<Integer> recommendedMovies = new ArrayList<>();
        String query
                = "WITH OmiljeniZanrovi AS ("
                + "    SELECT fz.ZanrId "
                + "    FROM Ocena o "
                + "    JOIN FilmZanr fz ON o.FilmId = fz.FilmId "
                + "    WHERE o.KorisnikId = ? "
                + "    GROUP BY fz.ZanrId "
                + "    HAVING AVG(CAST(o.Vrednost AS DECIMAL(10,3))) >= 8.0"
                + "), "
                + "StatistikaFilmova AS ("
                + "    SELECT o.FilmId, "
                + "           COUNT(o.Vrednost) as BrojOcena, "
                + "           AVG(CAST(o.Vrednost AS DECIMAL(10,3))) as ProsecnaOcena "
                + "    FROM Ocena o "
                + "    GROUP BY o.FilmId"
                + ") "
                + "SELECT DISTINCT f.FilmId, sf.ProsecnaOcena "
                + "FROM Filmovi f "
                + "JOIN FilmZanr fz ON f.FilmId = fz.FilmId "
                + "JOIN StatistikaFilmova sf ON f.FilmId = sf.FilmId "
                + "WHERE fz.ZanrId IN (SELECT ZanrId FROM OmiljeniZanrovi) "
                + "  AND f.FilmId NOT IN (SELECT FilmId FROM Ocena WHERE KorisnikId = ?) "
                + "  AND f.FilmId NOT IN (SELECT FilmId FROM ListaZaGledanje WHERE KorisnikId = ?) "
                + "  AND ((sf.BrojOcena >= 4 AND sf.ProsecnaOcena >= 7.5) OR (sf.BrojOcena < 4 AND sf.ProsecnaOcena >= 9.0)) "
                + "ORDER BY sf.ProsecnaOcena DESC, f.FilmId ASC";
        try (Connection conn = getConn(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idK);
            ps.setInt(2, idK);
            ps.setInt(3, idK);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    recommendedMovies.add(rs.getInt("FilmId"));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ss220011_Impl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return recommendedMovies;
    }

    @Override
    public Integer getRewards(Integer idK) {
        String query = "SELECT BrojNagrada FROM Korisnici WHERE KorisnikId = ?";
        try (Connection conn = getConn(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idK);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("BrojNagrada");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ss220011_Impl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
        //ili return 0;
    }

    @Override
    public List<String> getThematicSpecializations(Integer idK) {
        List<String> specializations = new ArrayList<>();
        String query
                = "SELECT ft.Tag "
                + "FROM Ocena o "
                + "JOIN FilmTag ft ON o.FilmId = ft.FilmId "
                + "WHERE o.KorisnikId = ? AND o.Vrednost >= 8 "
                + "GROUP BY ft.Tag "
                + "HAVING COUNT(ft.Tag) >= 2 "
                + "ORDER BY ft.Tag ASC";
        try (Connection conn = getConn(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idK);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    specializations.add(rs.getString("Tag"));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ss220011_Impl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return specializations;
    }

    @Override
    public String getUserDescription(Integer idK) {
        String queryFilmovi = "SELECT COUNT(DISTINCT FilmId) as UkupnoFilmova FROM Ocena WHERE KorisnikId = ?";
        String queryTagovi = "SELECT COUNT(DISTINCT ft.Tag) as UkupnoTagova "
                + "FROM Ocena o "
                + "JOIN FilmTag ft ON o.FilmId = ft.FilmId "
                + "WHERE o.KorisnikId = ?";

        int ukupnoFilmova = 0;
        int ukupnoTagova = 0;

        try (Connection conn = getConn()) {
            try (PreparedStatement ps1 = conn.prepareStatement(queryFilmovi)) {
                ps1.setInt(1, idK);
                try (ResultSet rs1 = ps1.executeQuery()) {
                    if (rs1.next()) {
                        ukupnoFilmova = rs1.getInt("UkupnoFilmova");
                    }
                }
            }
            try (PreparedStatement ps2 = conn.prepareStatement(queryTagovi)) {
                ps2.setInt(1, idK);
                try (ResultSet rs2 = ps2.executeQuery()) {
                    if (rs2.next()) {
                        ukupnoTagova = rs2.getInt("UkupnoTagova");
                    }
                }
            }
            if (ukupnoFilmova < 10) {
                return "undefined";
            } else if (ukupnoTagova >= 10 && ukupnoFilmova >= 10) {
                return "curious";
            } else {
                return "focused";
            }
        } catch (SQLException ex) {
            Logger.getLogger(ss220011_Impl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "undefined";
    }

    @Override
    public boolean addMovieToWatchlist(Integer idK, Integer idF) {
        String query = "INSERT INTO ListaZaGledanje (KorisnikId, FilmId) VALUES (?, ?)";
        try (Connection conn = getConn()) {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, idK);
            ps.setInt(2, idF);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(ss220011_Impl.class.getName()).log(Level.INFO, "watchlist odbijen: {0}", ex.getMessage());
        }
        return false;
    }

    @Override
    public boolean removeMovieFromWatchlist(Integer idK, Integer idF) {
        String query = "DELETE FROM ListaZaGledanje WHERE KorisnikId = ? AND FilmId = ?";
        try (Connection conn = getConn()) {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, idK);
            ps.setInt(2, idF);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(ss220011_Impl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public boolean isMovieInWatchlist(Integer idK, Integer idF) {
        String query = "SELECT 1 FROM ListaZaGledanje WHERE KorisnikId = ? AND FilmId = ?";
        try (Connection conn = getConn()) {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, idK);
            ps.setInt(2, idF);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            Logger.getLogger(ss220011_Impl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public List<Integer> getMoviesInWatchlist(Integer idK) {
        List<Integer> list = new ArrayList<>();
        String query = "SELECT FilmId FROM ListaZaGledanje WHERE KorisnikId = ?";
        try (Connection conn = getConn(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idK);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getInt("FilmId"));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ss220011_Impl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    @Override
    public List<Integer> getUsersWithMovieInWatchlist(Integer idF) {
        List<Integer> list = new ArrayList<>();
        String query = "SELECT KorisnikId FROM ListaZaGledanje WHERE FilmId = ?";
        try (Connection conn = getConn(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idF);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getInt("KorisnikId"));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ss220011_Impl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    @Override
    public void eraseAll() {
        String[] tabele = {
            "ListaZaGledanje", "Ocena", "FilmZanr", "FilmTag",
            "Filmovi", "Zanr", "Korisnici"
        };
        try (Connection conn = getConn()) {
            try (Statement stmt = conn.createStatement()) {
                for (String tabela : tabele) {
                    stmt.executeUpdate("ALTER TABLE [" + tabela + "] DISABLE TRIGGER ALL");
                }
                for (String tabela : tabele) {
                    stmt.executeUpdate("DELETE FROM [" + tabela + "]");
                }
                for (String tabela : tabele) {
                    stmt.executeUpdate("ALTER TABLE [" + tabela + "] ENABLE TRIGGER ALL");
                }
                //System.out.println("Baza uspesno ociscena");
            }
        } catch (SQLException ex) {
            Logger.getLogger(ss220011_Impl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
