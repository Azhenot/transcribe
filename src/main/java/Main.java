import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by Tristan on 23-04-18.
 */
public class Main {

    public static void main(String[] args) {

        /*EventQueue.invokeLater(() -> {
            Graphique ex = new Graphique();
            ex.setVisible(true);
        });*/

        VideoTranscription t = new VideoTranscription();
        //t.videoToWav("C:\\Users\\Tristan\\Documents\\GitHub\\transcribe\\videoMIT.mp4");
        //t.googleSpeech("gs://videosmasi/videoMIT.wav", "videoMIT.mp4", 8, 15, 5.0);
        //t.fromText("C:\\Users\\Tristan\\Documents\\GitHub\\transcribe\\StemLem.txt", 50, 15, 5.0);
        //t.fromSubtitles("videoMIT.mp4", "subs with time.txt", 50, 15, 5.0);
        t.fromTextTaille("C:\\Users\\Tristan\\Documents\\GitHub\\transcribe\\src\\main\\resources\\testTest.txt", 25, 15, 5.0);
        //ArrayList<String> words = new ArrayList<>();
        //words.add("the");
        //words.add("fibonacci");
        //words.add("ford");
        //t.compareWordsSig("C:\\Users\\Tristan\\Documents\\GitHub\\transcribe\\src\\main\\resources\\StemLem.txt", 50, 15, 5.0, words);

        //t.sizePhrases("C:\\Users\\Tristan\\Documents\\GitHub\\transcribe\\src\\main\\resources\\testTest.txt");


    }

    /*public class SimpleEx extends JFrame {

        public SimpleEx() {

            initUI();
        }

        private void initUI() {

            setTitle("Simple example");
            setSize(300, 200);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
        }
    }*/

}
