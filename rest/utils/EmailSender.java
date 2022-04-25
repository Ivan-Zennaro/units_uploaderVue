package org.example.rest.utils;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.servlet.http.HttpServletRequest;
import java.io.FileReader;
import java.io.IOException;

public class EmailSender {


    public static void sendEmail(String nameUploader, String emailRecipient, String nameFile, HttpServletRequest req, String idFile) throws IOException, ParseException {

        Email from = new Email("ivan.zennaro@gmail.com");
        String subject = "There is a new document for you !";
        Email to = new Email(emailRecipient);

        String textEmail = "New docuemnt for you, go to check it \n" +
                nameUploader + " has just uploaded a document for you \n" +
                "Document name: " + nameFile.replace("_"," ") + "\n \n \n "+
                "Link to Homepage: " + req.getRequestURL().toString()
                                    .replaceAll("/api/upl/uploadfile","" ) + "\n \n \n " +
                "Link to File: "  + req.getRequestURL().toString()
                                    .replaceAll("/upl/uploadfile","/gen/linkfile/" + idFile);

        Content content = new Content("text/plain", textEmail);
        Mail mail = new Mail(from, subject, to, content);

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader("./WEB-INF/keys.json"));
        String apiSendGrid = ((JSONObject) obj).get("api").toString();

        SendGrid sg = new SendGrid(apiSendGrid);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            com.sendgrid.Response response = sg.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        } catch (IOException ex) {
            System.out.println("errore " +ex);
        }

    }
}
