package petkov.cnn;

import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator;
import org.deeplearning4j.earlystopping.scorecalc.ScoreCalculator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by sam_petkov 15.05.2021.
 */
/// We create an instance of the Accuracy Calculator class
public class AccuracyCalculator implements ScoreCalculator<MultiLayerNetwork> {

    private static final Logger log = LoggerFactory.getLogger(AccuracyCalculator.class);

    private final MnistDataSetIterator dataSetIterator;

    public AccuracyCalculator(MnistDataSetIterator dataSetIterator) {
        this.dataSetIterator = dataSetIterator;
    }

    int i = 0;

    // We get the accuracy of our Neural Network
    @Override
    public double calculateScore(MultiLayerNetwork network) {
        Evaluation evaluate = network.evaluate(dataSetIterator);
        double accuracy = evaluate.accuracy();
        log.error("Accuracy " + i++ + " " + accuracy);
        return 1 - evaluate.accuracy();
    }
}
