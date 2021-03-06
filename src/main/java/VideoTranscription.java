import com.itextpdf.text.BadElementException;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by Tristan on 1/05/2018.
 */
public class VideoTranscription {

    public void VideoTranscription(){

    }

    public void videoToWav(String path){

        String newName = path.substring(0, path.length()-4);

        ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-i",path,"-vn", "-acodec", "pcm_s16le", "-ar", "16000", "-ac", "1", newName+".wav");
        pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        pb.redirectError(ProcessBuilder.Redirect.INHERIT);
        try {
            Process p = pb.start();
        } catch (IOException e) {
            e.printStackTrace();

        }
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void googleSpeech(String googleApiKeyLink, String soundLink, String videoLink, int nbSmoothing, int clusterSize) {

        Text text = new Text(".\\src\\main\\resources\\textGoogleTranscription.txt");
        text.transcribeGoogleSpeech(soundLink, googleApiKeyLink);
        text.readFile();
        text.handleTextGoogleSpeech();
        text.setOccToMots();
        text.setPositionMots();
        text.calculateNValueMots();
        text.calculateSigValue();
        text.calculateCorrespondances(clusterSize);
        int i = 0;
        while(i < nbSmoothing){
            text.smoothing();
            ++i;
        }
        text.localMinima(0.0);
        text.writeInFileGoogleSpeech(videoLink);
        text.generateGraph();
    }

    public void fromText(String textFile, int nbSmoothing, int clusterSize) {
        Text text = new Text(textFile);
        text.readFile();
        text.handleTextNormal();
        text.setOccToMots();
        text.setPositionMots();
        text.calculateNValueMots();
        text.calculateSigValue();
        text.calculateCorrespondances(clusterSize);
        int i = 0;
        while(i < nbSmoothing){
            text.smoothing();
            ++i;
        }
        text.localMinima(0.0);
        text.writeInFileNoPdf();
        text.cuePhrases();
        //text.generateGraph();
        text.generateGraphWithCuePhrases();
    }

    public void fromTextTaille(String textFile, int nbSmoothing, int clusterSize) {
        Text text = new Text(textFile);
        text.readFile();
        text.handleTextNormalTaille();
        text.setOccToMots();
        text.setPositionMots();
        text.calculateNValueMots();
        text.calculateSigValue();
        text.calculateCorrespondances(clusterSize);
        int i = 0;
        while(i < nbSmoothing){
            text.smoothing();
            ++i;
        }
        text.localMinima(0.0);
        text.writeInFileNoPdf();
        text.cuePhrases();
        text.generateGraph();
        //text.generateGraphWithCuePhrases();
    }


    public void fromSubtitles(String videoLink, String subtitles, int nbSmoothing, int clusterSize){
        Text text = new Text(subtitles);
        text.readFileSubtitles();
        text.setOccToMots();
        text.setPositionMots();
        text.calculateNValueMots();
        text.calculateSigValue();
        text.calculateCorrespondances(clusterSize);
        int i = 0;
        while(i < nbSmoothing){
            text.smoothing();
            ++i;
        }
        text.localMinima(0.0);
        text.writeInFileImageFromVideo(videoLink);
        text.generateGraph();
    }

    public void compareWordsSig(String textFile, int nbSmoothing, int clusterSize, ArrayList<String> words){
        Text text = new Text(textFile);

        text.readFile();
        text.handleTextNormal();
        text.setOccToMots();
        text.setPositionMots();
        text.calculateNValueMots();
        text.calculateSigValue();
        /*for(Mot mot: text.getBestWordSigValues()){
            System.out.println(mot.getMot() + " " + mot.getSigScore());
        }*/
        ArrayList<ArrayList<Double>> wordDoubles= new ArrayList<>();
        int nWord = 0;
        for(String word: words){
            FileWriter fw = null;
            try {
                fw = new FileWriter("wordSig"+nWord+".txt");
                for (Double corr : text.getWordSigValues(word)){
                    if (!String.valueOf(corr).equals("NaN") && !String.valueOf(corr).equals("-Infinity")) {
                        fw.write(String.valueOf(corr) + "\n");
                    }
                }
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ++nWord;
        }
        text.generateGraphSigWords();
    }

    public void sizePhrases(String s) {
        Text text = new Text(s);
        text.readFile();
        text.handleTextNormal();
        text.setOccToMots();
        text.setPositionMots();
        text.calculateNValueMots();
        text.calculateSigValue();
        text.calculateCorrespondances(15);
        text.taille();
    }
}
