import javax.swing.*;
import java.sql.*;
import java.sql.ResultSet;


public class GcashAppClasses {

    private User user;


    final String DB_URL = "jdbc:mysql://sql12.freesqldatabase.com:3306/sql12666768";
    final String USERNAME = "sql12666768";
    final String PASSWORD = "YxDac3ZBu9";
    public Object loginUser(String mobile, String pin){
        User user = null;
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            String sql = "SELECT name,email,mobile,pin FROM users WHERE mobile=? AND pin=?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, mobile);
            preparedStatement.setString(2, pin);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                user = new User();
                String name = resultSet.getString(1);
                System.out.println(name + " Successfully Login");
                return 1;
            }else {
                System.out.println("Login failed");
                return 0;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return user;
    }




    public int checkBalance(int userId) {

        try (Connection conToGetAmountFromBalanceOfUser = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);) {
            PreparedStatement psQueryToGetAmountFromBalanceOfUser = conToGetAmountFromBalanceOfUser.prepareStatement("SELECT amount FROM balance WHERE user_id=?");
            psQueryToGetAmountFromBalanceOfUser.setInt(1, userId);
            ResultSet rsToGetAmountFromBalanceOfUser = psQueryToGetAmountFromBalanceOfUser.executeQuery();
            if (rsToGetAmountFromBalanceOfUser.next()) {
                double amount = rsToGetAmountFromBalanceOfUser.getDouble(1);
                //lblAmount.setText("Your current balance is: Php " + amount);
                System.out.println("User with id number " + userId + " current balance is: Php " + amount);
                return 1;

            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return 0;
    }



//CashIn

    public int cashIn(int userId, double amount) {
        if(!String.valueOf(amount).toString().matches("^[\\+\\-]{0,1}[0-9]+[\\.\\,][0-9]+$")){
//            JOptionPane.showMessageDialog(null,"Please input a valid number for the amount", "Try again", JOptionPane.ERROR_MESSAGE);
            System.out.println("Please input a valid number for the amount");
            return 0;
        }else if(amount<=100.00){
//            JOptionPane.showMessageDialog(null,"Please input a valid number higher than 100 for amount", "Try again", JOptionPane.ERROR_MESSAGE);
            System.out.println("Please input a valid number higher than 100 for amount");
            return 0;
        } else{
            try (Connection con = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);){
                PreparedStatement ps = con.prepareStatement("SELECT name FROM users WHERE id=?");
                ps.setInt(1,userId);
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    String name = rs.getString(1);
                    PreparedStatement ps2 = con.prepareStatement("SELECT amount FROM balance WHERE user_id=?");
                    ps2.setInt(1,userId);
                    ResultSet rs2 = ps2.executeQuery();
                    if(rs2.next()){
                        double balanceAmount = rs2.getDouble(1);
                        try(Connection con2 = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);){
                            PreparedStatement ps3 = con2.prepareStatement("INSERT INTO transaction (amount,name,account_id,date,transfertoid,transferfromid) values (?,?,?,?,?,?)");
                            ps3.setDouble(1, amount);
                            ps3.setString(2, name);
                            ps3.setInt(3, userId);
                            ps3.setString(4, String.valueOf(new Timestamp(System.currentTimeMillis())));
                            ps3.setInt(5, userId);
                            ps3.setInt(6, userId);
                            ps3.executeUpdate();
                            PreparedStatement ps4 = con2.prepareStatement("UPDATE balance SET amount = ? WHERE user_id = ?");
                            ps4.setDouble(1, balanceAmount + amount);
                            ps4.setInt(2, userId);
                            ps4.executeUpdate();
                            try(Connection con3 = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);){
                                PreparedStatement ps5 = con3.prepareStatement("select amount from balance where user_id=?");
                                ps5.setInt(1,userId);
                                ResultSet rs3 = ps2.executeQuery();
                                if(rs3.next()){
                                    double updatedAmount = rs3.getDouble(1);
                                    System.out.println("The previous balance of user with ID number " + userId + " is: Php " + balanceAmount);
                                    System.out.println("The updated balance of user with ID number " + userId + " is: Php " + updatedAmount);
                                    return 1;
                                }
                            }
                        }catch (Exception e2){
                            e2.printStackTrace();
                        }
                    }
                }
            }catch (Exception e1){
                e1.printStackTrace();
            }
        }
        return 0;
    }
//CashIn


//CashTransfer
public int cashTransfer(String mobileOfReceiver, String mobileOfSender, double amount) {
    if(!mobileOfReceiver.matches("^\\d+$")){
        System.out.println("Mobile of recipient must contain valid numbers only. Thanks.");
        return 0;
    }

    if(mobileOfReceiver.length()!=11){
        System.out.println("Mobile of recipient requires eleven(11) valid set of numbers. Thanks.");
        return 0;
    }


    if(!mobileOfSender.matches("^\\d+$")){
        System.out.println("Mobile of sender must contain valid numbers only. Thanks.");
        return 0;
    }

    if(mobileOfSender.length()!=11){
        System.out.println("Mobile of sender requires eleven(11) valid set of numbers. Thanks.");
        return 0;
    }

    if(mobileOfReceiver.matches(mobileOfSender)){
        System.out.println("Recipient's mobile number can't be the same with Sender's mobile and vice versa");
        return 0;
    }

    if(!String.valueOf(amount).matches("^[\\+\\-]{0,1}[0-9]+[\\.\\,][0-9]+$")){
        System.out.println("Please input a valid number for the cash amount. Thanks.");
        return 0;
    }

    if(amount<=100.00){
        System.out.println("Please input a valid number higher than 100 for amount. Thanks.");
        return 0;
    }

    try(Connection conToGetIdOfSender = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);){
        PreparedStatement psToQueryIdOfSender = conToGetIdOfSender.prepareStatement("select id from users where mobile=?");
        psToQueryIdOfSender.setString(1,mobileOfSender);
        ResultSet rsToGetIdOfSender = psToQueryIdOfSender.executeQuery();
        if(rsToGetIdOfSender.next()){
            int idOfSender = rsToGetIdOfSender.getInt(1);
            try(Connection conToGetBalanceOfSender = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);){
                PreparedStatement psToGetBalanceOfSender = conToGetBalanceOfSender.prepareStatement("select amount from balance where id=?");
                psToGetBalanceOfSender.setInt(1,idOfSender);
                ResultSet rsToGetBalanceOfSender = psToGetBalanceOfSender.executeQuery();
                if(rsToGetBalanceOfSender.next()){
                    double amountOfSender = rsToGetBalanceOfSender.getDouble(1);
                    if(amountOfSender<amount){
                        System.out.println("The sender doesn't have enough balance to transfer that amount");
                        return 0;
                    }else{
                        try(Connection conToGetIdOfRecipient = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);){
                            PreparedStatement psToGetIdOfRecipient = conToGetIdOfRecipient.prepareStatement("select id from users where mobile=?");
                            psToGetIdOfRecipient.setString(1,mobileOfReceiver);
                            ResultSet rsToGetIdOfRecipient = psToGetIdOfRecipient.executeQuery();
                            if(rsToGetIdOfRecipient.next()){
                                int idOfRecipient = rsToGetIdOfRecipient.getInt(1);
                                try(Connection conToGetBalanceOfRecipient = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);){
                                    PreparedStatement psToGetBalanceOfRecipient = conToGetBalanceOfRecipient.prepareStatement("select amount from balance where id=?");
                                    psToGetBalanceOfRecipient.setDouble(1,idOfRecipient);
                                    ResultSet rsToGetBalanceOfRecipient = psToGetBalanceOfRecipient.executeQuery();
                                    if(rsToGetBalanceOfRecipient.next()){
                                        double balanceOfRecipient = rsToGetBalanceOfRecipient.getDouble(1);
                                        try(Connection conToGetNameOfRecipient = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);){
                                            PreparedStatement psToGetNameOfRecipient = conToGetNameOfRecipient.prepareStatement("select name from users where mobile=?");
                                            psToGetNameOfRecipient.setString(1,mobileOfReceiver);
                                            ResultSet rsToGetNameOfRecipient = psToGetNameOfRecipient.executeQuery();
                                            if(rsToGetNameOfRecipient.next()){
                                                String nameOfRecipient = rsToGetNameOfRecipient.getString(1);
                                                try(Connection conToInsertToTransactionTable = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);){
                                                PreparedStatement psToInsertToTransactionTable = conToInsertToTransactionTable.prepareStatement("INSERT INTO transaction(amount,name,account_id,date,transfertoid,transferfromid) values (?,?,?,?,?,?)");
                                                psToInsertToTransactionTable.setDouble(1,amount);
                                                psToInsertToTransactionTable.setString(2, nameOfRecipient);
                                                psToInsertToTransactionTable.setInt(3, idOfRecipient);
                                                psToInsertToTransactionTable.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
                                                psToInsertToTransactionTable.setInt(5, idOfRecipient);
                                                psToInsertToTransactionTable.setInt(6, idOfSender);
                                                psToInsertToTransactionTable.executeUpdate();
                                                try(Connection conToUpdateBalanceOfRecipient = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);){
                                                    PreparedStatement psToUpdateBalanceOfRecipient = conToUpdateBalanceOfRecipient.prepareStatement("UPDATE balance SET amount = ? WHERE user_id = ?");
                                                    psToUpdateBalanceOfRecipient.setDouble(1,balanceOfRecipient + amount);
                                                    psToUpdateBalanceOfRecipient.setInt(2,idOfRecipient);
                                                    psToUpdateBalanceOfRecipient.executeUpdate();
                                                    try(Connection conToUpdateBalanceOfSender = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);){
                                                        PreparedStatement psToUpdateBalanceOfSender = conToUpdateBalanceOfSender.prepareStatement("UPDATE balance SET amount = ? WHERE user_id = ?");
                                                        psToUpdateBalanceOfSender.setDouble(1,amountOfSender-amount);
                                                        psToUpdateBalanceOfSender.setInt(2,idOfSender);
                                                        psToUpdateBalanceOfSender.executeUpdate();
                                                        try(Connection conToPrintLatestTransaction = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);){
                                                            PreparedStatement psQueryToPrintLatestTransaction = conToPrintLatestTransaction.prepareStatement("SELECT MAX(date), name, amount FROM transaction WHERE transferfromid=?");
                                                            psQueryToPrintLatestTransaction.setInt(1,idOfSender);
                                                            ResultSet rsToPrintLatestTransaction = psQueryToPrintLatestTransaction.executeQuery();
                                                            if(rsToPrintLatestTransaction.next()){
                                                                String date = rsToPrintLatestTransaction.getString(1);
                                                                //String name = rsToPrintLatestTransaction.getString(2);
                                                                double transactionAmount = rsToPrintLatestTransaction.getDouble(3);
                                                                System.out.println("Transfer Cash Successful!\nAmount Transferred: Php" + amount + "\nRecipient's Name: " + nameOfRecipient + "\nTransfer Date: " + date + "\nRecipient's Previous Balance: Php" +balanceOfRecipient+ "\nUpdated Balance Of Recipient: Php" + (balanceOfRecipient+amount) + "\nSender's Previous Balance: Php" + amountOfSender + "\nUpdated Balance Of Sender: Php" + (amountOfSender-amount));
                                                                return 1;
                                                            }
                                                        }catch(Exception e1){
                                                            e1.printStackTrace();
                                                        }
                                                    }catch(Exception e1){
                                                        e1.printStackTrace();
                                                    }
                                                }catch(Exception e1){
                                                    e1.printStackTrace();
                                                }
                                        }catch(Exception e1){
                                            e1.printStackTrace();
                                        }
                                            }
                                        }catch(Exception e1){
                                            e1.printStackTrace();
                                        }



                                    }
                                }catch(Exception e1){
                                    e1.printStackTrace();
                                }
                            }

                        }catch(Exception e1){
                            e1.printStackTrace();
                        }
                    }
                }
            }catch (Exception e1){
                e1.printStackTrace();
            }
        }


    }catch (Exception e1){
        e1.printStackTrace();
    }

        return 0;
}

//CashTransfer

//view transaction

    public int viewTransaction(int idOfRequester, String dateRequested) {

        if(!dateRequested.matches("^\\d{4}-\\d{2}-\\d{2} ([0-1]?\\d|2[0-3])(?::([0-5]?\\d))?(?::([0-5]?\\d))?$")){
            System.out.println("Please input a valid YYYY-MM-DD HH:MM:SS. Thanks.");
            return 0;
        }

        try(Connection conToQueryDateRequested = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);){
            PreparedStatement psToQueryDateRequested = conToQueryDateRequested.prepareStatement("SELECT amount,name,account_id,date,transfertoid,transferfromid FROM transaction where date=? AND transferfromid=?");
            psToQueryDateRequested.setString(1,dateRequested);
            psToQueryDateRequested.setInt(2,idOfRequester);
            ResultSet rsToQueryDateRequested = psToQueryDateRequested.executeQuery();
            if(rsToQueryDateRequested.next()){
                double amount = rsToQueryDateRequested.getDouble(1);
                String name = rsToQueryDateRequested.getString(2);
                int account_id = rsToQueryDateRequested.getInt(3);
                String transactionDate = rsToQueryDateRequested.getString(4);
                int transfertoid = rsToQueryDateRequested.getInt(5);
                int transferfromid = rsToQueryDateRequested.getInt(6);
                System.out.println("Transaction Details\nAmount: Php" + amount +"\nRecipient's Name: " + name + "\nAccount ID of Recipient: " + account_id + "\nTransfer Date: " +transactionDate);
                return 1;
            }
        }catch(Exception e1){
            e1.printStackTrace();
        }
        return 0;
    }

//view transaction


//view user transaction


    public int viewUserTransaction(int idOfRequester) {
        try(Connection conToViewUserTransaction = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);){
            PreparedStatement psToViewUserTransaction = conToViewUserTransaction.prepareStatement("select DATE,ACCOUNT_ID,TRANSFERFROMID,NAME,AMOUNT from transaction where transferfromid=?");
            psToViewUserTransaction.setInt(1,idOfRequester);
            ResultSet rsToViewUserTransaction = psToViewUserTransaction.executeQuery();
            ResultSetMetaData rsmd = rsToViewUserTransaction.getMetaData();
            System.out.print("          DATE                RECIPIENT_ID        SENDER_ID           RECIPIENT                 AMOUNT");
            System.out.println();
            int columnsNumber = rsmd.getColumnCount();
            while (rsToViewUserTransaction.next()){
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print("       ");
                    String columnValue = rsToViewUserTransaction.getString(i);
                    System.out.print(columnValue + "        ");
                }
                System.out.println("");
            }
            return 1;
        }catch(Exception e1){
            e1.printStackTrace();
        }
        return 0;
    }

//view user transaction


//view all transaction

    public int viewAllTransaction(int idOfRequester) {
        try(Connection conToViewUserTransaction = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);){
            PreparedStatement psToViewUserTransaction = conToViewUserTransaction.prepareStatement("select DATE,ACCOUNT_ID,TRANSFERFROMID,NAME,AMOUNT from transaction where transfertoid=? OR transferfromid=?");
            psToViewUserTransaction.setInt(1,idOfRequester);
            psToViewUserTransaction.setInt(2,idOfRequester);
            ResultSet rsToViewUserTransaction = psToViewUserTransaction.executeQuery();
            ResultSetMetaData rsmd = rsToViewUserTransaction.getMetaData();
            System.out.print("          DATE                RECIPIENT_ID    SENDER_ID               RECIPIENT              AMOUNT");
            System.out.println();
            int columnsNumber = rsmd.getColumnCount();
            while (rsToViewUserTransaction.next()){
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print("       ");
                    String columnValue = rsToViewUserTransaction.getString(i);
                    System.out.print(columnValue + "        ");
                }
                System.out.println("");
            }
            return 1;
        }catch(Exception e1){
            e1.printStackTrace();
        }
        return 0;
    }

//view all transaction

}

