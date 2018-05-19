# transcribe

## Prerequisites:

1. Have FFMPEG installed at least version 4.0 for extracting pictures of video.
2. Have Python installed at least version 3.6 for pre-processing Text.
3. Maven project to get dependencies.
4. Have an account as google developer and generate API.json key for google speech.

## Methods:

### VideoToWav:
1. String => Path to video File.

### googleSpeech:
1. String => Path to file containing the JSON api key for Google Speech.
2. String => Link to (PUBLIC or PRIVATE) .WAV file contained in a google storage bukket.
3. String => Path to video File.
4. Integer => Number of times to smooth the graph. 8 times being the minimum. 50 times works good to remove any false positive.
5. Integer => Size of cluster of phrases. 15 times being the minimum recommended. 20-25 has tendency to have better results.

### fromText:
1. String => Path to  transcription text File.
2. Integer => Number of times to smooth the graph. 8 times being the minimum. 50 times works good to remove any false positive.
3. Integer => Size of cluster of phrases. 15 times being the minimum recommended. 20-25 has tendency to have better results.

### fromSubtitles:
1. String => Path to  transcription text File.
2. String => Path to video File.
3. Integer => Number of times to smooth the graph. 8 times being the minimum. 50 times works good to remove any false positive.
4. Integer => Size of cluster of phrases. 15 times being the minimum recommended. 20-25 has tendency to have better results.

### compareWordsSig:
1. String => Path to  transcription text File.
2. Integer => Number of times to smooth the graph. 8 times being the minimum. 50 times works good to remove any false positive.
3. Integer => Size of cluster of phrases. 15 times being the minimum recommended. 20-25 has tendency to have better results.
4. ArrayList of String => Words to analyze. 3 being the maximum at the moment.

## Example of usage:

VideoTranscription t = new VideoTranscription();

### Extract WAV sound file from video:

t.videoToWav("PathToVideo.mp4");

### Using Google Speech for sound transcription:

t.googleSpeech("PathToJsonApiFile", "gs://googleBukket/soundOfVideo.wav", "PathToVideo.mp4", 8, 15);

### Using Text file for transcription, no images will be extracted as no timestamps are given

t.fromText("PathToTextFile.txt", 50, 15);

### Using subtitles.srt files

t.fromSubtitles("PathToVideo.mp4", "subsWithTimeStamps.txt", 50, 20);

### Getting words Significance score:

ArrayList<String> words = new ArrayList<>();
words.add("the");
words.add("fibonacci");
words.add("ford");
t.compareWordsSig("PathToTextFile.txt", 50, 15, words);

