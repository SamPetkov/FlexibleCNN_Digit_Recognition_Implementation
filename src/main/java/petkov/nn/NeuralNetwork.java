package petkov.nn;

/**
 * Created by sam_petkov 21.05.2021
 */

import org.apache.spark.ml.classification.MultilayerPerceptronClassificationModel;
import org.apache.spark.ml.classification.MultilayerPerceptronClassifier;
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import petkov.data.IdxReader;
import petkov.data.LabeledImage;

import java.io.IOException;
import java.util.List;
// SImple Neural Network
public class NeuralNetwork {

    private final static Logger LOGGER = LoggerFactory.getLogger(NeuralNetwork.class);

    private SparkSession sparkSession;
    private MultilayerPerceptronClassificationModel model;
// Initialization
    public void init() {
        initSparkSession();
        if (model == null) {
            LOGGER.info("Loading the Neural Network from saved model ... ");
            model = MultilayerPerceptronClassificationModel.load("C:\\Users\\petko\\Documents\\GitHub\\DigitRecognizer\\resources\\nnTrainedModels\\ModelWith60000");
            LOGGER.info("Loading from saved model is done");
        }
    }
// Training
    public void train(Integer trainData, Integer testFieldValue) {

        initSparkSession();

        List<LabeledImage> labeledImages = IdxReader.loadData(trainData);
        List<LabeledImage> testLabeledImages = IdxReader.loadTestData(testFieldValue);

        Dataset<Row> train = sparkSession.createDataFrame(labeledImages, LabeledImage.class).checkpoint();
        Dataset<Row> test = sparkSession.createDataFrame(testLabeledImages, LabeledImage.class).checkpoint();

        int[] layers = new int[]{784, 128, 64, 10};

        MultilayerPerceptronClassifier trainer = new MultilayerPerceptronClassifier()
                .setLayers(layers)
                .setBlockSize(128)
                .setSeed(1234L)
                .setMaxIter(100);

        model = trainer.fit(train);

        evalOnTest(test);
        evalOnTest(train);
    }
// Accuracy
    private void evalOnTest(Dataset<Row> test) {
        Dataset<Row> result = model.transform(test);
        Dataset<Row> predictionAndLabels = result.select("prediction", "label");
        MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator()
                .setMetricName("accuracy");

        LOGGER.info("Test set accuracy = " + evaluator.evaluate(predictionAndLabels));
    }
// Apache Class for building data frames
    private void initSparkSession() {
        if (sparkSession == null) {
            sparkSession = SparkSession.builder()
                    .master("local[*]")
                    .appName("Digit Recognizer")
                    .getOrCreate();
        }

        sparkSession.sparkContext().setCheckpointDir("checkPoint");
    }
// Predict the number
    public LabeledImage predict(LabeledImage labeledImage) {
        double predict = model.predict(labeledImage.getFeatures());
        labeledImage.setLabel(predict);
        return labeledImage;
    }
}
