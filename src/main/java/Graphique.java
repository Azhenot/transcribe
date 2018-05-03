import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Created by Tristan on 2/05/2018.
 */
public class Graphique extends JFrame {

    public Graphique() {

        initUI();
    }

    private void initUI() {

        JLabel son = new JLabel("Lien son: ");
        JLabel video = new JLabel("Lien video: ");
        JLabel smoothing = new JLabel("Nombre de lissages: ");
        JLabel cluster = new JLabel("Taille cluster: ");
        JLabel minima = new JLabel("Minimum deviance: ");

        JTextField sonText = new JTextField();
        JTextField videoText = new JTextField();
        JTextField smoothingText = new JTextField();
        JTextField clusterText = new JTextField();
        JTextField minimaText = new JTextField();

        JButton googleSpeech = new JButton("Transcription Son");
        JButton subtitles = new JButton("Sous-titres");
        JButton onlyText = new JButton("Traiter texte");

        createLayout(son, sonText, video, videoText, smoothing, smoothingText, cluster, clusterText, minima, minimaText, googleSpeech, subtitles, onlyText);

        setTitle("Video transcription");
        setSize(300, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void createLayout(JComponent... arg) {

        Container pane = getContentPane();

        GroupLayout gl = new GroupLayout(pane);
        //pane.setLayout(gl);

        gl.setAutoCreateContainerGaps(true);

        JPanel pleft = new JPanel();

        pleft.add(arg[0]);
        pleft.add(arg[2]);
        pleft.add(arg[4]);
        pleft.add(arg[6]);
        pleft.add(arg[8]);

        JPanel pRight = new JPanel();

        pRight.add(arg[1]);
        pRight.add(arg[3]);
        pRight.add(arg[5]);
        pRight.add(arg[7]);
        pRight.add(arg[9]);

        JPanel pBottom = new JPanel();

        pBottom.add(arg[10]);
        pBottom.add(arg[11]);
        pBottom.add(arg[12]);

        pane.add(pleft, BorderLayout.WEST);
        pane.add(pRight,  BorderLayout.EAST);
        pane.add(pBottom, BorderLayout.SOUTH);


    }
}
