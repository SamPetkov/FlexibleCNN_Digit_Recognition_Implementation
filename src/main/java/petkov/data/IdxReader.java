package petkov.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
/*
* Created by sam_petkov 13.05.2021
*/
// Here we implement a ldx data reader ( a quantized png file format)
public class IdxReader {

    private final static Logger LOGGER = LoggerFactory.getLogger(IdxReader.class);

    public static final String INPUT_IMAGE_PATH = "C:\\Users\\petko\\Documents\\GitHub\\DigitRecognizer\\resources\\train-images.idx3-ubyte";
    public static final String INPUT_LABEL_PATH = "C:\\Users\\petko\\Documents\\GitHub\\DigitRecognizer\\resources\\train-labels.idx1-ubyte";

    public static final String INPUT_IMAGE_PATH_TEST_DATA = "C:\\Users\\petko\\Documents\\GitHub\\DigitRecognizer\\resources\\t10k-images.idx3-ubyte";
    public static final String INPUT_LABEL_PATH_TEST_DATA = "C:\\Users\\petko\\Documents\\GitHub\\DigitRecognizer\\resources\\t10k-labels.idx1-ubyte";

    public static final int VECTOR_DIMENSION = 784; //square 28*28 as from data set -> array 784 items

    // Load Data
    public static List<LabeledImage> loadData(final int size) {
        return getLabeledImages(INPUT_IMAGE_PATH, INPUT_LABEL_PATH, size);
    }

    // Load the test data
    public static List<LabeledImage> loadTestData(final int size) {
        return getLabeledImages(INPUT_IMAGE_PATH_TEST_DATA, INPUT_LABEL_PATH_TEST_DATA, size);
    }

    private static List<LabeledImage> getLabeledImages(final String inputImagePath,
                                                       final String inputLabelPath,
                                                       final int amountOfDataSet) {

        final List<LabeledImage> labeledImageArrayList = new ArrayList<>(amountOfDataSet);

        try (FileInputStream inImage = new FileInputStream(inputImagePath);
             FileInputStream inLabel = new FileInputStream(inputLabelPath)) {

            // just skip the amount of a data(bytes)
            // see the test and description for dataset
            inImage.skip(16);
            inLabel.skip(8);
            LOGGER.debug("Available bytes in inputImage stream after read: " + inImage.available());
            LOGGER.debug("Available bytes in inputLabel stream after read: " + inLabel.available());

            //empty array for 784 pixels - the image from 28x28 pixels in a single row
            double[] imgPixels = new double[VECTOR_DIMENSION];

            LOGGER.info("Creating ADT filed with Labeled Images ...");
            long start = System.currentTimeMillis();
            for (int i = 0; i < amountOfDataSet; i++) {

                if (i % 1000 == 0) {
                    LOGGER.info("Number of images extracted: " + i);
                }
                //it fills the array of pixels
                for (int index = 0; index < VECTOR_DIMENSION; index++) {
                    imgPixels[index] = inImage.read();
                }
                //it creates a label for that
                int label = inLabel.read();
                //it creates a compound object and adds them to a list
                labeledImageArrayList.add(new LabeledImage(label, imgPixels));
            }
            LOGGER.info("Time to load LabeledImages in seconds: " + ((System.currentTimeMillis() - start) / 1000d));
        } catch (Exception e) {
            LOGGER.error("Smth went wrong: \n" + e);
            throw new RuntimeException(e);
        }

        return labeledImageArrayList;
    }

}