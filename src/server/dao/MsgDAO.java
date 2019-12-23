package server.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class MsgDAO {

    /**
     *
     * @param pUsrLoginWho - login user, who write message
     * @param pUsrLoginWhom -login user, whom write message
     * @param pMsg - test message
     * @return - true if all is ok
     * @throws SQLException
     */
    public static boolean saveMsg(String pUsrLoginWho, String pUsrLoginWhom, String pMsg) throws SQLException {
        Connection dbConnection = ConnectionDAO.getDBConnection();
        Statement statement = null;
        Boolean isFindWho;
        Boolean isFindWhom;
        boolean ret;

        isFindWho = UserDAO.findUser(pUsrLoginWho);
        isFindWhom = UserDAO.findUser(pUsrLoginWhom);

        if (isFindWho & isFindWhom) {
            try {
                String insertSQL = "INSERT INTO public.\"CHATANN_MSG\"(\"CHAT_ID\", \"DATE\", \"USER_WHO\", \"USER_WHOM\", \"TEXT\") " +
                        "VALUES (DEFAULT, current_date, '" + pUsrLoginWho.toUpperCase() + "', '" + pUsrLoginWhom.toUpperCase() + "', '" + pMsg + "')";
                statement = dbConnection.createStatement();
                int executeUpdate = statement.executeUpdate(insertSQL);
                if (executeUpdate == 1) {
                    ret = true;
                } else {
                    ret = false;
                }

            } catch (SQLException e) {
                System.out.println(e.getMessage());
                ret = false;
            } finally {
                if (statement != null) {
                    statement.close();
                }
            }
        } else {
            ret = false;
        }

        return ret;
    }
}
