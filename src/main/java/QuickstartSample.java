import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.longrunning.OperationFuture;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.speech.v1.*;
import com.google.cloud.speech.v1p1beta1.*;
import com.google.cloud.speech.v1p1beta1.LongRunningRecognizeMetadata;
import com.google.cloud.speech.v1p1beta1.RecognitionAudio;
import com.google.cloud.speech.v1p1beta1.RecognitionConfig;
import com.google.cloud.speech.v1p1beta1.RecognizeResponse;
import com.google.cloud.speech.v1p1beta1.SpeechClient;
import com.google.cloud.speech.v1p1beta1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1p1beta1.SpeechRecognitionResult;
import com.google.cloud.speech.v1p1beta1.SpeechSettings;
import com.google.cloud.speech.v1p1beta1.WordInfo;
import com.google.protobuf.ByteString;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tristan on 28/04/2018.
 */

// Imports the Google Cloud client library
import com.google.cloud.speech.v1p1beta1.RecognitionConfig.AudioEncoding;
import com.google.protobuf.ByteString;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


public class QuickstartSample {

    /**
     * Demonstrates using the Speech API to transcribe an audio file.
     */
    public static void main(String... args) throws Exception {

        System.out.println("Working Directory = " +
                System.getProperty("user.dir"));

        CredentialsProvider credentialsProvider = FixedCredentialsProvider.create(ServiceAccountCredentials.fromStream(new FileInputStream("C:/Users/Tristan/Documents/GitHub/Significance-score/google/TravelMasi-06c5ced3936a.json")));

        SpeechSettings settings = SpeechSettings.newBuilder().setCredentialsProvider(credentialsProvider).build();
        //SpeechClient speechClient = SpeechClient.create(settings);

        // Instantiates a client
        try (SpeechClient speechClient = SpeechClient.create(settings)) {

            // The path to the audio file to transcribe
            String fileName = "./src/main/resources/output.wav";
            String uri = "gs://videosmasi/videoMIT.wav";

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
            OperationFuture<com.google.cloud.speech.v1p1beta1.LongRunningRecognizeResponse, LongRunningRecognizeMetadata> response =
                    speechClient.longRunningRecognizeAsync(config, audio);
            while (!response.isDone()) {
                System.out.println("Waiting for response...");
                Thread.sleep(10000);
                System.out.println("Waiting for response...2");
            }

            List<SpeechRecognitionResult> results = response.get().getResultsList();
            String text = "";

            for (SpeechRecognitionResult result : results) {
                // There can be several alternative transcripts for a given chunk of speech. Just use the
                // first (most likely) one here.
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                System.out.printf("Transcription: %s\n", alternative.getTranscript());
                text += result.getAlternativesList().get(0).getTranscript();
                for(WordInfo wordInfo: result.getAlternativesList().get(0).getWordsList()){
                    System.out.println("Word: "+wordInfo.getWord()+" Start: "+wordInfo.getStartTime()+" End: "+wordInfo.getEndTime());
                }
            }

            System.out.println(text);
            FileWriter fw = null;
            try {
                fw = new FileWriter("text.txt");
                fw.write(text);
                fw.close();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
