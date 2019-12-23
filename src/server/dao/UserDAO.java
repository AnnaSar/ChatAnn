package server.dao;

import java.sql.*;

public class UserDAO {
    /**
     * Check login and password
     * @param pUsrLogin - unic user name
     * @param pUsrPass - password
     * @param pRegNew - if user not found when reg new
     * @return 1 - user found and pass correct, 0 - user found, but pass is not correct, 2 - create new user, 3 - name is busy, 4 - error
     * @throws SQLException
     */
    public static int checkUser(String pUsrLogin, String pUsrPass, Boolean pRegNew) throws SQLException {
        Connection dbConnection = ConnectionDAO.getDBConnection();
        Statement statement = null;
        int ret = 4;
        String usrLogin = null;
        String selectSQL = "SELECT \"USER_LOGIN\", \"USER_PASS\" from public.\"CHATANN_USERS\" where \"USER_LOGIN\" = '" + pUsrLogin.toUpperCase() + "'";
        try {
            statement = dbConnection.createStatement();
            ResultSet rs = statement.executeQuery(selectSQL);

            while (rs.next()) {
                usrLogin = rs.getString("USER_LOGIN");
                String usrPass = rs.getString("USER_PASS");

                if (usrPass.equals(pUsrPass)) {
                    ret = 1;
                } else {
                    ret = 0;
                }
            }

            if (usrLogin == null) {
                int res = 0;
                if (pRegNew) {
                    res = regUser(pUsrLogin.toUpperCase(), pUsrPass);
                    switch (res) {
                        case 1:
                            ret = 2;
                            break;
                        case 2:
                            ret = 3;
                            break;
                        default:
                            ret = 4;
                            break;

                    }
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
        return ret;
    }

    /**
     * Find user by login
     * @param pUsrLogin
     * @return true - user founded, false - user is not founded
     * @throws SQLException
     */
    public static Boolean findUser(String pUsrLogin) throws SQLException {
        Connection dbConnection = ConnectionDAO.getDBConnection();
        Statement statement = null;
        String usrLogin = null;
        Boolean ret = false;
        String selectSQL = "SELECT \"USER_LOGIN\" from public.\"CHATANN_USERS\" where \"USER_LOGIN\" = '" + pUsrLogin.toUpperCase() + "'";
        try {
            statement = dbConnection.createStatement();
            // выбираем данные с БД
            ResultSet rs = statement.executeQuery(selectSQL);

            // И если что то было получено то цикл while сработает
            while (rs.next()) {
                usrLogin = rs.getString("USER_LOGIN");
               // System.out.println("user login : " + usrLogin);
            }

            if (usrLogin == null) {
                ret = false;
            } else {
                ret = true;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
        return ret;
    }

    /**
     * Register new user
     * @param pUsrLogin
     * @param pUsrPass
     * @return 1- new User created, 0 - error, 2 - name is busy
     * @throws SQLException
     */
    public static int regUser(String pUsrLogin, String pUsrPass) throws SQLException {
        Connection dbConnection = ConnectionDAO.getDBConnection();
        Statement statement = null;
        Boolean isBusy;
        int ret = 0;

        isBusy = findUser(pUsrLogin);

        if (isBusy) {
            ret = 2;
        } else {
            try {
                String insertSQL = "INSERT INTO public.\"CHATANN_USERS\"(\"USER_LOGIN\", \"USER_PASS\") VALUES ('" + pUsrLogin.toUpperCase() + "', '" + pUsrPass + "')";
                statement = dbConnection.createStatement();
                int update = statement.executeUpdate(insertSQL);
                if (update == 1) {
                    ret = 1;
                } else {
                    ret = 0;
                }
            } catch (SQLException e) {
                ret = 0;
                System.out.println(e.getMessage());
            } finally {
                if (statement != null) {
                    statement.close();
                }
            }
        }
        return ret;
    }
}
