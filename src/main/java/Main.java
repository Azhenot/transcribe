import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

/**
 * Created by Tristan on 23-04-18.
 */
public class Main {

    public static void main(String[] args) {


        Text text = new Text("C:\\Users\\Tristan\\Documents\\GitHub\\transcribe\\src\\main\\resources\\textFull.txt");
        text.transcribeGoogleSpeech();
        text.readFile();
        text.setOccToMots();
        text.setPositionMots();
        text.calculateNValueMots();
        text.calculateSigValue();
        text.calculateCorrespondances(15);
        text.smoothing();
        text.smoothing();
        text.smoothing();
        text.smoothing();
        text.smoothing();
        text.smoothing();
        text.smoothing();
        text.smoothing();
        text.smoothing();
        text.smoothing();
        text.smoothing();
        text.smoothing();
        text.smoothing();


        text.localMinima(5.0);
        try {
            text.writeInFile();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BadElementException e) {
            e.printStackTrace();
        }
        text.generateGraph();
        //text.readPhrases();
        //text.handleVideo2();


    }

}
