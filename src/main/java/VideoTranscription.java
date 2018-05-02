import com.itextpdf.text.BadElementException;

import java.io.IOException;
import java.net.URISyntaxException;
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

    public void googleSpeech(String soundLink, String videoLink, int nbSmoothing, int clusterSize, Double minima) {

        Text text = new Text(".\\src\\main\\resources\\textGoogleTranscription.txt");
        text.transcribeGoogleSpeech(soundLink);
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
        text.localMinima(minima);
        text.writeInFileGoogleSpeech(videoLink);
        text.generateGraph();
    }

    public void fromText(String textFile, int nbSmoothing, int clusterSize, Double minima) {
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
        text.localMinima(minima);
        text.writeInFileNoPdf();
        text.generateGraph();
    }

    public void fromSubtitles(String videoLink, String subtitles, int nbSmoothing, int clusterSize, Double minima){
        Text text = new Text(subtitles);
        text.readFile();
        text.handleTextSubtitles();
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
        text.localMinima(minima);
        text.writeInFileImageFromVideo(videoLink);
        text.generateGraph();
    }
}
