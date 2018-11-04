import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.longrunning.OperationFuture;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.speech.v1.*;
import com.google.cloud.speech.v1p1beta1.*;
import com.google.cloud.speech.v1p1beta1.LongRunningRecognizeMetadata;
import com.google.cloud.speech.v1p1beta1.LongRunningRecognizeResponse;
import com.google.cloud.speech.v1p1beta1.RecognitionAudio;
import com.google.cloud.speech.v1p1beta1.RecognitionConfig;
import com.google.cloud.speech.v1p1beta1.SpeechClient;
import com.google.cloud.speech.v1p1beta1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1p1beta1.SpeechRecognitionResult;
import com.google.cloud.speech.v1p1beta1.SpeechSettings;
import com.google.cloud.speech.v1p1beta1.WordInfo;
import com.google.protobuf.ByteString;
import com.google.protobuf.Duration;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.CFFFont;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.*;
import java.lang.reflect.Array;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by Tristan on 23-04-18.
 */

//hello
public class Text {
    ArrayList<Phrase> phrases = new ArrayList<>();
    String fileName;
    String text = "";
    double wordCount = 0;
    ArrayList<Double> correspondances = new ArrayList<>();
    ArrayList<Integer> localMinimums = new ArrayList<>();
    ArrayList<WordInfo> words = new ArrayList<>();



    public Text(String fileName) {
        this.fileName = fileName;
    }




    public void readFile(){
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {

            String sCurrentLine;

            while ((sCurrentLine = br.readLine()) != null) {
                if(!sCurrentLine.equals("")){
                    text += sCurrentLine;
                    text += " ";
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readFileSubtitles(){
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {

            String sCurrentLine;
            int seconds = 0;
            String phrase = "";
            while ((sCurrentLine = br.readLine()) != null) {
                if(!sCurrentLine.equals("")){
                    if(sCurrentLine.contains("-->")){
                        String time = sCurrentLine.substring(17,25);
                        seconds = 0;
                        seconds = Integer.parseInt(time.substring(0,1)) * 3600;
                        seconds += Integer.parseInt(time.substring(3,5)) * 60;
                        seconds += Integer.parseInt(time.substring(6,8));
                    }
                    else if(sCurrentLine.matches("[0-9]+")) {
                    }else{
                        char endPhrase = sCurrentLine.charAt(sCurrentLine.length()-1);

                        if(endPhrase == '!' || endPhrase == '?' || endPhrase == '.'){
                            phrase += sCurrentLine;
                            phrases.add(new Phrase(phrase, seconds));
                            phrase = "";
                        }else{
                            phrase += sCurrentLine;
                            phrase += " ";
                        }
                    }
                    text += sCurrentLine;
                    text += " ";
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleTextSubtitles() {
        System.out.println(text);
    }

    public void handleTextGoogleSpeech() {
        text = text.toLowerCase();
        text.replace("/\n"," ");
        text.trim();
        text = text.replace("  "," ");//2 or more space to 1
        String phrase = "";
        String word = "";
        for(int i = 0; i < text.length(); i++)
        {
            char c = text.charAt(i);
            if(c == ' '){
                word = "";
            }
            word += c;
            phrase += c;
            if( c == '.' || c == '?' || c == '!'/*cptMots == 15*/){
                System.out.println("ici1 :" + word);
                WordInfo wordInfo = getWordInfo(word.trim());
                words.remove(wordInfo);
                System.out.println("ici2: " + wordInfo);

                if(wordInfo != null){
                    phrases.add(new Phrase(phrase, wordInfo.getEndTime().getSeconds()));
                    phrase = "";
                }

            }

        }
    }

    public void handleTextNormal() {
        text = text.toLowerCase();
        text.replace("/\n"," ");
        text.trim();
        text = text.replace("  "," ");//2 or more space to 1
        String phrase = "";
        int cptMots = 0;
        for(int i = 0; i < text.length(); i++)
        {
            char c = text.charAt(i);
            phrase += c;
            if(c == ' '){
                ++cptMots;
            }
            if( c == '.' || c == '?' || c == '!'/*cptMots == 15*/){
                //If line is a timestamp, saved and phrase gets reference
                phrases.add(new Phrase(phrase));
                phrase = "";
                cptMots = 0;
            }

        }
    }

    public void handleTextNormalTaille() {
        text = text.toLowerCase();
        text.replace("/\n"," ");
        text.trim();
        text = text.replace("  "," ");//2 or more space to 1
        String phrase = "";
        int cptMots = 0;
        for(int i = 0; i < text.length(); i++)
        {
            char c = text.charAt(i);
            phrase += c;
            if(c == ' '){
                ++cptMots;
            }
            if( cptMots == 15){
                //If line is a timestamp, saved and phrase gets reference
                phrases.add(new Phrase(phrase));
                phrase = "";
                cptMots = 0;
            }

        }
    }



    public WordInfo getWordInfo(String wordToLook){
        for(WordInfo wordInfo: words){
            if(wordInfo.getWord().equals(wordToLook)){
                return wordInfo;
            }
        }
        return null;
    }

    public double getNbMots(){
        if(wordCount == 0){
            double nbMots = 0;
            for (Phrase phrase: phrases) {
                nbMots += phrase.getNbMots();
            }
            wordCount = nbMots;
        }
        return wordCount;

    }

    public double occMot(String motToLook){
        double motOcc = 0;
        for(Phrase phrase: phrases){
            motOcc += phrase.occMot(motToLook);
        }
        return motOcc;
    }

    public void setOccToMots(){
        for(Phrase phrase: phrases){
            for(Mot mot: phrase.getMots()){
                mot.setOccurences(occMot(mot.getMot()));
                mot.calculateN(getNbMots());
            }
        }
    }

    public void setPositionMots(){
        int positionMot = 0;
        int positionPhrase = 0;
        for(Phrase phrase: phrases){
            for(Mot mot: phrase.getMots()){
                mot.setPositionText(positionMot);
                ++positionMot;
            }
            ++positionPhrase;
        }
    }

    public void calculateNValueMots(){
        for(Phrase phrase: phrases){
            for(Mot mot: phrase.getMots()){
                mot.setOccurences(occMot(mot.getMot()));
                mot.calculateN(getNbMots());
            }
        }
    }

    public List<Integer> calculateDistancesMot(Mot motToCalculate){
        ArrayList<Integer> distances = new ArrayList<>();

        for(Phrase phrase: phrases){
            for(Mot mot: phrase.getMots()){
                int distanceValue = motToCalculate.getPositionText() - mot.getPositionText();

                if(motToCalculate.getMot().equals(mot.getMot())){
                    if(distanceValue < 0){
                        distanceValue = distanceValue * (-1);
                    }
                    if(motToCalculate != mot){
                        distances.add(distanceValue);
                    }
                }

            }
        }
        Collections.sort(distances);
        if(distances.size() > 10){
            return distances.subList(0,9);
        }else if(distances.size() == 0){
            return distances;
        }
        else{
            return distances.subList(0,distances.size());
        }
    }

    public void calculateSigValue(){
        for(Phrase phrase: phrases){
            for(Mot mot: phrase.getMots()){
                mot.calculateSigValue(calculateDistancesMot(mot), getNbMots());
            }
        }
        normalizing();
    }

    public void normalizing(){
        Double min = 100.0;
        Double max = 0.0;
        String minMot = "";
        for(Phrase phrase: phrases){
            for(Mot mot: phrase.getMots()){
                if(mot.getSigScore() != 0.0) {
                    if (mot.getSigScore() > max) {
                        max = mot.getSigScore();
                        minMot = mot.getMot();

                    } else if (mot.getSigScore() < min) {
                        min = mot.getSigScore();
                    }
                }
            }
        }
        for(Phrase phrase: phrases){
            for(Mot mot: phrase.getMots()){
                mot.normalize(min, max);
            }
        }
    }

    public void breakPointPoints(int debut, int clusterSize) {
        int cpt = debut;
        ArrayList<Phrase> clusterPhrases1 = new ArrayList<>();
        ArrayList<Phrase> clusterPhrases2 = new ArrayList<>();
        double clusterScore1 = 0;
        double clusterScore2 = 0;
        while(cpt < phrases.size() && cpt < debut+(clusterSize*2)){
            if(cpt < debut+clusterSize){
                clusterPhrases1.add(phrases.get(cpt));
                clusterScore1 += phrases.get(cpt).getScore();
            }else if(cpt >= debut+clusterSize && cpt < debut+(clusterSize*2)){
                clusterPhrases2.add(phrases.get(cpt));
                clusterScore2 += phrases.get(cpt).getScore();
            }
            ++cpt;
        }

        System.out.println("SIZE"+clusterPhrases1.size());
        System.out.println("SIZE2"+clusterPhrases2.size());

        ArrayList<Double> scores  = wordsCluster1In2(clusterPhrases1, clusterPhrases2);
        double Ap = scores.get(0);
        double App = scores.get(1);
        double Bp = scores.get(2);
        double Bpp = scores.get(3);
        clusterScore1 = scores.get(4);
        clusterScore2 = scores.get(5);


        System.out.println("Ap "+Ap);
        System.out.println("Bp "+Bp);
        System.out.println("App "+App);
        System.out.println("Bpp "+Bpp);
        System.out.println("c1 "+clusterScore1);

        System.out.println("c2 "+clusterScore2);

        double correspondance = (((Ap-App)/clusterScore1)+((Bp-Bpp)/clusterScore2))/2;
        if(clusterPhrases2.size() == clusterPhrases1.size()){
            correspondances.add(correspondance);
        }
    }

    public void breakPointPoints2(int debut, int clusterSize) {
        int cpt = debut;
        ArrayList<Phrase> clusterPhrases1 = new ArrayList<>();
        ArrayList<Phrase> clusterPhrases2 = new ArrayList<>();
        double clusterScore1 = 0;
        double clusterScore2 = 0;
        double max1 = 0;
        double max2 = 0;

        while(cpt < phrases.size() && cpt < debut+(clusterSize*2)){
            if(cpt < debut+clusterSize){
                clusterPhrases1.add(phrases.get(cpt));
                clusterScore1 += phrases.get(cpt).getScore();
            }else if(cpt >= debut+clusterSize && cpt < debut+(clusterSize*2)){
                clusterPhrases2.add(phrases.get(cpt));
                clusterScore2 += phrases.get(cpt).getScore();
            }
            ++cpt;
        }

        System.out.println(clusterPhrases2);
        ArrayList<Double> scores  = wordsCluster1In2(clusterPhrases1, clusterPhrases2);
        double Ap = scores.get(0);
        double Bp = scores.get(0);
        double App = scores.get(1);
        double Bpp = scores.get(1);

        System.out.println("Ap "+Ap);
        System.out.println("Bp "+Bp);
        System.out.println("App "+App);
        System.out.println("Bpp "+Bpp);
        System.out.println("c1 "+clusterScore1);

        System.out.println("c2 "+clusterScore2);

        double correspondance = (((Ap-App)/clusterScore1)+((Bp-Bpp)/clusterScore2))/2;
        if(clusterPhrases2.size() == clusterPhrases1.size()){
            correspondances.add(correspondance);
        }
    }

    ArrayList<Long> images = new ArrayList<>();

    public void writeInFileGoogleSpeech(String videoLink){
        FileWriter fw = null;
        try {
            fw = new FileWriter("outText.txt");
            int cpt = 0;
            for (Double corr : correspondances) {
                ++cpt;
                if (!String.valueOf(corr).equals("NaN") && !String.valueOf(corr).equals("-Infinity")) {
                    fw.write(String.valueOf(corr) + "\n");
                }
            }
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fw = new FileWriter("outTextMinimums.txt");
            int compteur = 0;
            int compteurMin = 0;
            int autreCpt = 0;
            ArrayList<Double> newLocalMinimums = new ArrayList<>();
            while (compteur < localMinimums.get(localMinimums.size() - 1) && compteurMin < localMinimums.size()) {
                if (compteur == localMinimums.get(compteurMin)) {
                    newLocalMinimums.add(correspondances.get(localMinimums.get(compteurMin)));
                    images.add(phrases.get(localMinimums.get(compteurMin)).getEndTime());
                    ++compteurMin;
                } else {
                    newLocalMinimums.add(0.0);
                }
                ++compteur;
            }
            for (Double corr : newLocalMinimums) {
                fw.write(String.valueOf(corr) + "\n");
            }
            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }





        for (Long dur : images) {
            ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-ss",""+dur,"-i", videoLink, "-vframes", "1", "-s", "480x300", "-f", "image2", "imagefile"+dur+".jpg", "-y");
            pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);
            try {
                Process p = pb.start();
                try {
                    p.waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
        int compteur = 0;
        int compteurMin = 0;
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream("GoogleSpeechResult.pdf"));
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        document.open();
        while (compteur < phrases.size() && compteur < localMinimums.get(localMinimums.size() - 1) && compteurMin < localMinimums.size()) {
            if (compteur == localMinimums.get(compteurMin)) {
                Paragraph chunk = new Paragraph(phrases.get(compteur).getRead());
                try {
                    document.add(chunk);
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
                if(phrases.get(compteur) != null && phrases.get(compteur).getEndTime() != 0){
                    Image img = null;
                    try {
                        img = Image.getInstance(("imagefile" + phrases.get(compteur).getEndTime() + ".jpg"));
                    } catch (BadElementException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(img != null){
                        try {
                            document.add(img);
                        } catch (DocumentException e) {
                            e.printStackTrace();
                        }
                    }
                }
                ++compteurMin;
            } else {
                Paragraph chunk = new Paragraph(phrases.get(compteur).getRead());
                try {
                    document.add(chunk);
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
            }
            ++compteur;
        }
        document.close();

    }

    public ArrayList<Double> wordsCluster1In2(ArrayList<Phrase> clusterPhrases1, ArrayList<Phrase>  clusterPhrases2){
        double score = 0;
        double score2 = 0;
        double scoreCluster1 = 0;


        for(Phrase phrase: clusterPhrases1){
            for(Mot mot: phrase.getMots()){
                boolean ok = true;
                for(Phrase phrase2: clusterPhrases2){
                    for(Mot mot2: phrase2.getMots()){
                        if(mot.getMot().equals(mot2.getMot()) && ok){
                            score += mot.getSigScore();
                            ok = false;
                        }
                    }
                }
                if(ok){
                    score2 += mot.getSigScore();
                }
                scoreCluster1 += mot.getSigScore();
            }
        }
        ArrayList<Double> toReturn  = new ArrayList<>();
        toReturn.add(score);
        toReturn.add(score2);

        double scoreB = 0;
        double scoreB2 = 0;
        double scoreCluster2 = 0;

        for(Phrase phrase: clusterPhrases2){
            for(Mot mot: phrase.getMots()){
                boolean ok = true;
                for(Phrase phrase2: clusterPhrases1){
                    for(Mot mot2: phrase2.getMots()){
                        if(mot.getMot().equals(mot2.getMot()) && ok){
                            scoreB += mot.getSigScore();
                            ok = false;
                        }
                    }
                }
                if(ok){
                    scoreB2 += mot.getSigScore();
                }
                scoreCluster2 += mot.getSigScore();
            }
        }
        toReturn.add(scoreB);
        toReturn.add(scoreB2);
        toReturn.add(scoreCluster1);
        toReturn.add(scoreCluster2);

        return toReturn;
    }

    public void calculateCorrespondances(int clusterSize){
        int cpt = 0;
        while(cpt < phrases.size()){
            breakPointPoints(cpt, clusterSize);
            ++cpt;
        }
    }

    public void readPhrases(){
        for(Phrase phrase: phrases){
            phrase.read();
        }
    }

    public void smoothing(){
        int cpt = 0;
        ArrayList<Double> correspondances2 = new ArrayList<>();
        correspondances2.add(correspondances.get(0));

        while(cpt < correspondances.size()-2){

            if(correspondances.get(cpt) != Double.NaN && correspondances.get(cpt+1) != Double.NEGATIVE_INFINITY && correspondances.get(cpt+2) != Double.POSITIVE_INFINITY){
                Double A = (correspondances.get(cpt+2) - correspondances.get(cpt))/2;
                A = A + correspondances.get(cpt);
                Double B = (correspondances.get(cpt+1) + A)/2;
                correspondances2.add(cpt+1,B);
            }else{
                correspondances2.add(cpt+1,correspondances.get(cpt+1));
            }
            ++cpt;
        }
        correspondances2.add(correspondances.get(cpt));
        correspondances = correspondances2;

    }

    public void localMinima(Double cohesion) {
        int cpt = 0;
        Double min;
        Double max;
        boolean descending = false;
        ArrayList<Integer> maximums = new ArrayList<>();
        ArrayList<Integer> minimums = new ArrayList<>();
        if(correspondances.get(0) > correspondances.get(1)) {
            descending = true;
            maximums.add(0);
        }
        while(cpt < correspondances.size()-1) {
            if (!descending) {
                if (correspondances.get(cpt + 1) > correspondances.get(cpt)) {
                    max = correspondances.get(cpt);
                } else {
                    maximums.add(cpt);
                    descending = true;
                }
            }else {
                if (correspondances.get(cpt + 1) < correspondances.get(cpt)) {
                    min = correspondances.get(cpt);
                } else {
                    minimums.add(cpt);
                    descending = false;
                }
            }
            ++cpt;
        }

        cpt = 0;
        while(cpt < minimums.size() && cpt < maximums.size()-1){
            Double difference1 = correspondances.get(maximums.get(cpt)) - correspondances.get(minimums.get(cpt));
            Double difference2 = correspondances.get(maximums.get(cpt+1)) - correspondances.get(minimums.get(cpt));
            Double toDiff = (difference1+difference2)/2;
            if(difference1 > toDiff-cohesion && difference2 > toDiff-cohesion){
                localMinimums.add(minimums.get(cpt));
            }else{
                localMinimums.add(minimums.get(cpt));
            }
            ++cpt;
        }

    }

    public Double average(ArrayList<Integer> list){
        double somme = 0;
        for(Integer cpt: list){
            somme += correspondances.get(cpt);
        }
        return somme/correspondances.size();
    }

    public void generateGraph() {
        ProcessBuilder pb = new ProcessBuilder("node", "generateSigGraph.js");
        pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        pb.redirectError(ProcessBuilder.Redirect.INHERIT);
        try {
            Process p = pb.start();
            try {
                p.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public void generateGraphSigWords(){
        ProcessBuilder pb = new ProcessBuilder("node", "generateSigGraphSigWords.js");
        pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        pb.redirectError(ProcessBuilder.Redirect.INHERIT);
        try {
            Process p = pb.start();
            try {
                p.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public void generateGraphWithCuePhrases() {
        ProcessBuilder pb = new ProcessBuilder("node", "generateGraphWithCuePhrases.js");
        pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        pb.redirectError(ProcessBuilder.Redirect.INHERIT);
        try {
            Process p = pb.start();
            try {
                p.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public void transcribeGoogleSpeech(String videoLink, String apiKey){

        CredentialsProvider credentialsProvider = null;
        try {
            credentialsProvider = FixedCredentialsProvider.create(ServiceAccountCredentials.fromStream(new FileInputStream(apiKey)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        SpeechSettings settings = null;
        try {
            settings = SpeechSettings.newBuilder().setCredentialsProvider(credentialsProvider).build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //SpeechClient speechClient = SpeechClient.create(settings);

        // Instantiates a client
        try {
            try (SpeechClient speechClient = SpeechClient.create(settings)) {

                // The path to the audio file to transcribe
                String fileName = "./src/main/resources/output.wav";
                String uri = videoLink;

                // Reads the audio file into memory
                Path path = Paths.get(fileName);
                byte[] data = Files.readAllBytes(path);
                ByteString audioBytes = ByteString.copyFrom(data);

                // Builds the sync recognize request
                RecognitionConfig config = RecognitionConfig.newBuilder()
                        //.setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                        .setSampleRateHertz(16000)
                        .setLanguageCode("en-US")
                        .setEnableWordTimeOffsets(true)
                        .setEnableAutomaticPunctuation(true)
                        .build();
                RecognitionAudio audio = RecognitionAudio.newBuilder()
                        .setUri(uri)
                        //.setContent(audioBytes)
                        .build();

                // Use non-blocking call for getting file transcription
                OperationFuture<LongRunningRecognizeResponse, LongRunningRecognizeMetadata> response =
                        speechClient.longRunningRecognizeAsync(config, audio);
                while (!response.isDone()) {
                    System.out.println("Waiting for response...");
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Waiting for response...2");
                }

                List<SpeechRecognitionResult> results = null;
                try {
                    results = response.get().getResultsList();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                String text2 = "";

                for (SpeechRecognitionResult result : results) {
                    // There can be several alternative transcripts for a given chunk of speech. Just use the
                    // first (most likely) one here.
                    SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                    System.out.printf("Transcription: %s\n", alternative.getTranscript());
                    text2 += result.getAlternativesList().get(0).getTranscript();
                    for(WordInfo wordInfo: result.getAlternativesList().get(0).getWordsList()){
                        words.add(wordInfo);
                    }
                }

                FileWriter fw = null;
                try {
                    fw = new FileWriter(".\\src\\main\\resources\\textGoogleTranscription.txt");
                    fw.write(text2);
                    fw.close();
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeInFileNoPdf() {
        FileWriter fw = null;
        try {
            fw = new FileWriter("outText.txt");
            int cpt = 0;
            for (Double corr : correspondances) {
                ++cpt;
                if (!String.valueOf(corr).equals("NaN") && !String.valueOf(corr).equals("-Infinity")) {
                    fw.write(String.valueOf(corr) + "\n");
                }
            }
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fw = new FileWriter("outTextMinimums.txt");
            int compteur = 0;
            int compteurMin = 0;
            int autreCpt = 0;
            ArrayList<Double> newLocalMinimums = new ArrayList<>();
            while (compteur < localMinimums.get(localMinimums.size() - 1) && compteurMin < localMinimums.size()) {
                if (compteur == localMinimums.get(compteurMin)) {
                    newLocalMinimums.add(correspondances.get(localMinimums.get(compteurMin)));
                    ++compteurMin;
                } else {
                    newLocalMinimums.add(0.0);
                }
                ++compteur;
            }
            for (Double corr : newLocalMinimums) {
                fw.write(String.valueOf(corr) + "\n");
            }
            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void writeInFileImageFromVideo(String videoLink) {
        FileWriter fw = null;
        try {
            fw = new FileWriter("outText.txt");
            int cpt = 0;
            for (Double corr : correspondances) {
                ++cpt;
                if (!String.valueOf(corr).equals("NaN") && !String.valueOf(corr).equals("-Infinity")) {
                    fw.write(String.valueOf(corr) + "\n");
                }
            }
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fw = new FileWriter("outTextMinimums.txt");
            int compteur = 0;
            int compteurMin = 0;
            int autreCpt = 0;
            ArrayList<Double> newLocalMinimums = new ArrayList<>();
            while (compteur < localMinimums.get(localMinimums.size() - 1) && compteurMin < localMinimums.size()) {
                if (compteur == localMinimums.get(compteurMin)) {
                    newLocalMinimums.add(correspondances.get(localMinimums.get(compteurMin)));
                    images.add(phrases.get(localMinimums.get(compteurMin)).getEndTime());
                    ++compteurMin;
                } else {
                    newLocalMinimums.add(0.0);
                }
                ++compteur;
            }
            for (Double corr : newLocalMinimums) {
                fw.write(String.valueOf(corr) + "\n");
            }
            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }





        for (Long dur : images) {

            ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-ss",""+dur,"-i", videoLink, "-vframes", "1", "-s", "480x300", "-f", "image2", "imagefile"+dur+".jpg", "-y");
            pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);
            try {
                Process p = pb.start();
                try {
                    p.waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
        int compteur = 0;
        int compteurMin = 0;
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream("subtitlesResult.pdf"));
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        document.open();
        while (compteur < phrases.size() && compteur < localMinimums.get(localMinimums.size() - 1) && compteurMin < localMinimums.size()) {
            if (compteur == localMinimums.get(compteurMin)) {
                Paragraph chunk = new Paragraph(phrases.get(compteur).getRead());
                try {
                    document.add(chunk);
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
                if(phrases.get(compteur) != null && phrases.get(compteur).getEndTime() != 0){
                    Image img = null;
                    try {
                        img = Image.getInstance(("imagefile" + phrases.get(compteur).getEndTime() + ".jpg"));
                    } catch (BadElementException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(img != null){
                        try {
                            document.add(img);
                        } catch (DocumentException e) {
                            e.printStackTrace();
                        }
                    }
                }
                ++compteurMin;
            } else {
                Paragraph chunk = new Paragraph(phrases.get(compteur).getRead());
                try {
                    document.add(chunk);
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
            }
            ++compteur;
        }
        document.close();
    }

    public ArrayList<Double> getWordSigValues(String word){
        ArrayList<Double> wordValues = new ArrayList<>();
        for(Phrase phrase: phrases){
            Mot wordFromPhrase = phrase.getWord(word);
            if(wordFromPhrase != null){
                wordValues.add(wordFromPhrase.getSigScore());
            }else{
                wordValues.add(0.0);
            }
        }
        return wordValues;
    }

    public ArrayList<Mot> getBestWordSigValues(){
        ArrayList<Mot> wordValues = new ArrayList<>();

        for(Phrase phrase: phrases){
            wordValues.addAll(phrase.getMots());
        }
        wordValues.sort(new comparerMot());
        return wordValues;
    }

    public void taille() {
        ArrayList<Integer> taille = new ArrayList<>();
        int cpt = 0;
        int cpt2 =0;
        int one = 0;
        int two = 0;
        while(cpt < phrases.size() - 30){
            one = 0;
            two = 0;
            cpt2 = cpt;
            while(cpt2 < cpt + 15){
                one += phrases.get(cpt2).getMots().size();
                ++cpt2;
            }
            while(cpt2 >= cpt + 15 && cpt2 < cpt + 30) {
                two += phrases.get(cpt2).getMots().size();
                ++cpt2;
            }
            taille.add((one-two));
            ++cpt;
        }


        FileWriter fw = null;
        try {
            fw = new FileWriter("sizePhrase.txt");
            for (int corr : taille) {
                fw.write(String.valueOf(corr) + "\n");
            }
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ProcessBuilder pb = new ProcessBuilder("node", "sizePhrase.js");
        pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        pb.redirectError(ProcessBuilder.Redirect.INHERIT);
        try {
            Process p = pb.start();
            try {
                p.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    class comparerMot implements Comparator<Mot> {

        @Override
        public int compare(Mot e1, Mot e2) {
            if(e1.getSigScore() < e2.getSigScore()){
                return 1;
            } else {
                return -1;
            }
        }
    }



    public void cuePhrases() {
        ArrayList<String> cuePhrases = new ArrayList<>();
        cuePhrases.add("actually");
        cuePhrases.add("also");
        cuePhrases.add("although");
        cuePhrases.add("and");
        cuePhrases.add("basically");
        cuePhrases.add("because");
        cuePhrases.add("but");
        cuePhrases.add("essentially");
        cuePhrases.add("except");
        cuePhrases.add("finally");
        cuePhrases.add("first");
        cuePhrases.add("firstly");
        cuePhrases.add("further");
        cuePhrases.add("furthermore");
        cuePhrases.add("generally");
        cuePhrases.add("however");
        cuePhrases.add("indeed");
        cuePhrases.add("like");
        cuePhrases.add("look");
        cuePhrases.add("next");
        cuePhrases.add("no");
        cuePhrases.add("now");
        cuePhrases.add("ok");
        cuePhrases.add("or");
        cuePhrases.add("otherwise");
        cuePhrases.add("right");
        cuePhrases.add("say");
        cuePhrases.add("second");
        cuePhrases.add("see");
        cuePhrases.add("similarly");
        cuePhrases.add("since");
        cuePhrases.add("so");
        cuePhrases.add("then");
        cuePhrases.add("therefore");
        cuePhrases.add("well");
        cuePhrases.add("yes");

        int score = 0;
        ArrayList<Integer> scoreCuePhrases = new ArrayList<>();
;        for(Phrase p: phrases){
            score = 0;
            for(Mot m: p.getMots()){
                for(String s: cuePhrases){
                    if(s.equals(m.getMot())){
                        ++score;
                    }
                }
            }
            scoreCuePhrases.add(score);
        }

        ArrayList<Double> scoreCuePhrases2 = new ArrayList<>();
        int cpt = 0;
        int start = 15;
        while(start < scoreCuePhrases.size()){
            Double somme = 0.0;
            cpt = start - 15;
            while(cpt < scoreCuePhrases.size() && cpt < start){
                somme += scoreCuePhrases.get(cpt);
                ++cpt;
            }
            somme = somme / 15;
            scoreCuePhrases2.add(somme);
            ++start;
        }


        FileWriter fw = null;
        try {
            fw = new FileWriter("scoreCuePhrase.txt");
            for (Double scoreCuePhrase : scoreCuePhrases2) {
                fw.write(String.valueOf(scoreCuePhrase) + "\n");
            }
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
