package petkov.ui;

import com.mortennobel.imagescaling.ResampleFilters;
import com.mortennobel.imagescaling.ResampleOp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import petkov.cnn.ConvolutionalNeuralNetwork;
import petkov.data.LabeledImage;
import petkov.nn.NeuralNetwork;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.Executors;
/*
* CReated by sam_Petkov 22.05.2021
*/
public class UI {

    private final static Logger LOGGER = LoggerFactory.getLogger(UI.class);

    private static final int FRAME_WIDTH = 1200;
    private static final int FRAME_HEIGHT = 628;
    private final NeuralNetwork neuralNetwork = new NeuralNetwork();
    private final ConvolutionalNeuralNetwork convolutionalNeuralNetwork = new ConvolutionalNeuralNetwork();
    private final java.awt.Color Gcolor = new java.awt.Color(102, 102, 102);
    
    private DrawArea drawArea;
    private JFrame mainFrame;
    private JPanel mainPanel;
    private JPanel drawAndDigitPredictionPanel;
    private SpinnerNumberModel modelTrainSize;
    private JSpinner trainField;
    private int TRAIN_SIZE = 30000;
    private final Font sansSerifBold = new Font("Courier New", Font.BOLD, 18);
    private int TEST_SIZE = 10000;
    private SpinnerNumberModel modelTestSize;
    private JSpinner testField;
    private JPanel resultPanel;

    public UI() throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        UIManager.put("Button.font", new FontUIResource(new Font("Dialog", Font.BOLD, 18)));
        UIManager.put("ProgressBar.font", new FontUIResource(new Font("Dialog", Font.BOLD, 18)));
        neuralNetwork.init();
        convolutionalNeuralNetwork.init();
    }

    public void initUI() throws Exception {
        // create main frame
        mainFrame = createMainFrame();

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        addTopPanel();

        drawAndDigitPredictionPanel = new JPanel(new GridLayout());
        addDrawArea();
        addActionPanel();
        addPredictionArea();
        mainPanel.add(drawAndDigitPredictionPanel, BorderLayout.CENTER);

        addSignature();

        mainFrame.add(mainPanel, BorderLayout.CENTER);
        mainFrame.setVisible(true);

    }
// GUess the number
    private void addActionPanel() {
        JLabel gif = new JLabel(new ImageIcon("C:\\Users\\petko\\Documents\\GitHub\\DigitRecognizer\\src\\main\\java\\petkov\\ui\\neuralNet.gif"));
        gif.setPreferredSize(new Dimension(250, 250));
        gif.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton recognize = new JButton(new ImageIcon("C:\\Users\\petko\\Documents\\GitHub\\DigitRecognizer\\src\\main\\java\\petkov\\ui\\recognize.png"));

        recognize.setPreferredSize(new Dimension(250, 54));
        recognize.setBorderPainted(false);
        recognize.setOpaque(true);
        recognize.setBackground(Gcolor);
        recognize.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JButton recognizeCNN = new JButton(new ImageIcon("C:\\Users\\petko\\Documents\\GitHub\\DigitRecognizer\\src\\main\\java\\petkov\\ui\\recognizeCNN.png"));

        recognizeCNN.setPreferredSize(new Dimension(250, 54));
        recognizeCNN.setBorderPainted(false);
        recognizeCNN.setOpaque(true);
        recognizeCNN.setBackground(Gcolor);
        recognizeCNN.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        recognize.addActionListener(e -> {
            Image drawImage = drawArea.getImage();
            BufferedImage sbi = toBufferedImage(drawImage);
            Image scaled = scale(sbi);
            BufferedImage scaledBuffered = toBufferedImage(scaled);
            double[] scaledPixels = transformImageToOneDimensionalVector(scaledBuffered);
            LabeledImage labeledImage = new LabeledImage(0, scaledPixels);
            LabeledImage predict = neuralNetwork.predict(labeledImage);
            JLabel predictNumber = new JLabel("" + (int) predict.getLabel());
            predictNumber.setForeground(Color.WHITE);
            predictNumber.setFont(new Font("David Libre", Font.BOLD, 128));
            resultPanel.removeAll();
            resultPanel.add(predictNumber);
            resultPanel.updateUI();

        });

        recognizeCNN.addActionListener(e -> {
            Image drawImage = drawArea.getImage();
            BufferedImage sbi = toBufferedImage(drawImage);
            Image scaled = scale(sbi);
            BufferedImage scaledBuffered = toBufferedImage(scaled);
            double[] scaledPixels = transformImageToOneDimensionalVector(scaledBuffered);
            LabeledImage labeledImage = new LabeledImage(0, scaledPixels);
            int predict = convolutionalNeuralNetwork.predict(labeledImage);
            JLabel predictNumber = new JLabel("" + predict);
            predictNumber.setForeground(Color.WHITE);
            predictNumber.setFont(new Font("Courier New", Font.BOLD, 128));
            resultPanel.removeAll();
            resultPanel.add(predictNumber);
            resultPanel.updateUI();

        });
        JButton clear = new JButton(new ImageIcon("C:\\Users\\petko\\Documents\\GitHub\\DigitRecognizer\\src\\main\\java\\petkov\\ui\\clear.png"));
        
        clear.setPreferredSize(new Dimension(250, 54));
        clear.setBorderPainted(false);
        clear.setOpaque(true);
        clear.setBackground(Gcolor);
        clear.setAlignmentX(Component.CENTER_ALIGNMENT);
        clear.addActionListener(e -> {
            drawArea.setImage(null);
            drawArea.repaint();
            drawAndDigitPredictionPanel.updateUI();
        });
        JPanel actionPanel = new JPanel();
        		actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));
        actionPanel.setBackground(new java.awt.Color(102, 102, 102));
        actionPanel.add(gif);
        actionPanel.add(recognizeCNN);
        actionPanel.add(recognize);
        actionPanel.add(clear);
        drawAndDigitPredictionPanel.add(actionPanel);
    }
// Adding Draw Area
    private void addDrawArea() {

        drawArea = new DrawArea();

        drawAndDigitPredictionPanel.add(drawArea);
    }
   // Adding Prediction Area
    private void addPredictionArea() {
        resultPanel = new JPanel();
        resultPanel.setBackground(Color.gray);
        resultPanel.setLayout(new GridBagLayout());
        drawAndDigitPredictionPanel.add(resultPanel);
    }
// Training Panel
    private void addTopPanel() throws Exception{
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.setBackground(new java.awt.Color(102, 102, 102));
        ImageIcon train =new ImageIcon("C:\\Users\\petko\\Documents\\GitHub\\DigitRecognizer\\src\\main\\java\\petkov\\ui\\train.png");
        JButton trainNN = new JButton(train);
        trainNN.setPreferredSize(new Dimension(132, 54));
        trainNN.setBorderPainted(false);
        trainNN.setOpaque(true);
        trainNN.setBackground(Gcolor);
        trainNN.addActionListener(e -> {

            int i = JOptionPane.showConfirmDialog(mainFrame, "Are you sure this may take some time to train?");

            if (i == JOptionPane.OK_OPTION) {
                ProgressBar progressBar = new ProgressBar(mainFrame);
                SwingUtilities.invokeLater(() -> progressBar.showProgressBar("Training may take one or two minutes..."));
                Executors.newCachedThreadPool().submit(() -> {
                    try {
                        LOGGER.info("Start of training of the Neural Network");
                        neuralNetwork.train((Integer) trainField.getValue(), (Integer) testField.getValue());
                        LOGGER.info("End of training of the  Neural Network");
                    } finally {
                        progressBar.setVisible(false);
                    }
                });
            }
        });
        //JButton trainCNN = new JButton( new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/images/recognizeCNN.png")).getImage().getScaledInstance(100, 50, Image.SCALE_SMOOTH)));
        JButton trainCNN = new JButton(new ImageIcon("C:\\Users\\petko\\Documents\\GitHub\\DigitRecognizer\\src\\main\\java\\petkov\\ui\\trainCNN.png"));
        trainCNN.setPreferredSize(new Dimension(132, 54));
        trainCNN.setBorderPainted(false);
        trainCNN.setOpaque(true);
        trainCNN.setBackground(Gcolor);
        trainCNN.addActionListener(e -> {

            int i = JOptionPane.showConfirmDialog(mainFrame, "Are you sure, training requires >10GB memory and more than 1 hour?");

            if (i == JOptionPane.OK_OPTION) {
                ProgressBar progressBar = new ProgressBar(mainFrame);
                SwingUtilities.invokeLater(() -> progressBar.showProgressBar("Training may take a while..."));
                Executors.newCachedThreadPool().submit(() -> {
                    try {
                        LOGGER.info("Start of train Convolutional Neural Network");
                        convolutionalNeuralNetwork.train((Integer) trainField.getValue(), (Integer) testField.getValue());
                        LOGGER.info("End of train Convolutional Neural Network");
                    } catch (IOException e1) {
                        LOGGER.error("CNN not trained " + e1);
                        throw new RuntimeException(e1);
                    } finally {
                        progressBar.setVisible(false);
                    }
                });
            }
        });

        topPanel.add(trainCNN);
        topPanel.add(trainNN);
        JLabel tL = new JLabel("Training Data");
        tL.setForeground(Color.WHITE);
        tL.setFont(sansSerifBold);
        topPanel.add(tL);
        modelTrainSize = new SpinnerNumberModel(TRAIN_SIZE, 10000, 60000, 1000);
        trainField = new JSpinner(modelTrainSize);
        trainField.setFont(sansSerifBold);
        topPanel.add(trainField);

        JLabel ttL = new JLabel("Test Data");
        ttL.setForeground(Color.WHITE);
        ttL.setFont(sansSerifBold);
        topPanel.add(ttL);
        modelTestSize = new SpinnerNumberModel(TEST_SIZE, 1000, 10000, 500);
        testField = new JSpinner(modelTestSize);
        testField.setFont(sansSerifBold);
        topPanel.add(testField);

        mainPanel.add(topPanel, BorderLayout.NORTH);
    }

// We scale the image
    private static BufferedImage scale(BufferedImage imageToScale) {
        ResampleOp resizeOp = new ResampleOp(28, 28);
        resizeOp.setFilter(ResampleFilters.getLanczos3Filter());
        BufferedImage filter = resizeOp.filter(imageToScale, null);
        return filter;
    }

    private static BufferedImage toBufferedImage(Image img) {
        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

// We transform the image to a 1D vector
    private static double[] transformImageToOneDimensionalVector(BufferedImage img) {

        double[] imageGray = new double[28 * 28];
        int w = img.getWidth();
        int h = img.getHeight();
        int index = 0;
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                Color color = new Color(img.getRGB(j, i), true);
                int red = (color.getRed());
                int green = (color.getGreen());
                int blue = (color.getBlue());
                double v = 255 - (red + green + blue) / 3d;
                imageGray[index] = v;
                index++;
            }
        }
        return imageGray;
    }

// Main Frame
    private JFrame createMainFrame() {
        JFrame mainFrame = new JFrame();
        mainFrame.setTitle("Digit Recognizer");
        mainFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        mainFrame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                System.exit(0);
            }
        });
        ImageIcon imageIcon = new ImageIcon("icon.png");
        mainFrame.setIconImage(imageIcon.getImage());

        return mainFrame;
    }
// Adding our company name to the bottom
    private void addSignature() {
        JLabel signature = new JLabel("© OpenStratum", JLabel.HORIZONTAL);
        signature.setFont(new Font("Courier New", Font.ITALIC, 20));
        signature.setForeground(Color.WHITE);
        signature.setBackground(new java.awt.Color(102, 102, 102));
        signature.setOpaque(true);
        mainPanel.add(signature, BorderLayout.SOUTH);
    }

}