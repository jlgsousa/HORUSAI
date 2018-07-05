package horusai.masterapp.utils;

import android.os.AsyncTask;

import java.util.Date;
import java.util.Properties;
import java.util.Random;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by joaosousa on 21/06/18.
 */

public class mailSender extends javax.mail.Authenticator {
    private static String code = generateCode();
    private static Send send = new Send();

    public static String getCode() {
        return code;
    }

    public static void send(String from, String password, String to) {
        send.execute(new String[]{from, password, to});
    }

//    public static String getLoggedGmail(Context context) {
//        return getLoggedGmailAccount(context);
//    }

    private static String generateCode(int... codeLength) {
        String alphaNumericDictionary = "ABCDEFGHIJKLMNOPQRSTUVXWYZ1234567890";
        int passwordLength = 8;

        if (codeLength != null && codeLength.length == 1) {
            passwordLength = codeLength[0];
        }

        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < passwordLength; i++) {
            stringBuilder.append(alphaNumericDictionary.charAt(random.nextInt(alphaNumericDictionary.length())));
        }
        return stringBuilder.toString();
    }

//    private static String getLoggedGmailAccount(Context context) {
//        AccountManager manager = (AccountManager) context.getSystemService(ACCOUNT_SERVICE);
//        Account[] list = manager.getAccounts();
//
//        for(Account account: list) {
//            if(account.type.equalsIgnoreCase("com.google")) {
//                return account.name;
//            }
//        }
//
//        return null;
//    }

    public static class Send extends AsyncTask<String[], Integer, Void> {
        private Session session = null;

        protected Void doInBackground(String[]... params) {
            String from = params[0][0];
            String password = params[0][1];
            String to = params[0][2];

            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.port", "465");
            props.put("mail.smtp.starttls.enable", "true");

            session = Session.getDefaultInstance(props);

            try{
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(from));
                InternetAddress[] toAddresses = { new InternetAddress(to) };
                message.setRecipients(Message.RecipientType.TO, toAddresses);
                message.setSubject("subject");

                message.setText("Your code is " + code);
                message.setSentDate(new Date());

                // sends the e-mail
                Transport t = session.getTransport("smtp");
                t.connect(from, password);
                t.sendMessage(message, message.getAllRecipients());
                t.close();

            } catch(MessagingException e) {
                e.printStackTrace();
            } catch(Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}