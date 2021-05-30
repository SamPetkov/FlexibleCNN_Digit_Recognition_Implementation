package petkov;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import petkov.ui.ProgressBar;
import petkov.ui.UI;

import javax.swing.*;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.Exception;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

/**
 * Created by sam_petkov on 22.05.2021.
 */
public class Run  {
    // We use the logger to check for any problems in such big of a program
    private final static Logger LOGGER = LoggerFactory.getLogger(Run.class);
    private static JFrame mainFrame = new JFrame();
    // Main class with loading bar
    public static void main(String[] args) throws Exception {

        LOGGER.info("Application is starting ... ");

        setHadoopHomeEnvironmentVariable();
        ProgressBar progressBar = new ProgressBar(mainFrame, true);
        progressBar.showProgressBar("OpenStratum is starting");
        UI ui = new UI();
        Executors.newCachedThreadPool().submit(()->{
            try {
                ui.initUI();
            } catch( Exception ex) {
            	LOGGER.info("BAD");
            } finally {
                progressBar.setVisible(false);
                mainFrame.dispose();
            }
        });
    }

    // We implement the Hadoop Apache framework to be able to deal with such a large quantity of data
    private static void setHadoopHomeEnvironmentVariable() throws Exception {
        HashMap<String, String> hadoopEnvSetUp = new HashMap<>();
        hadoopEnvSetUp.put("HADOOP_HOME", new File("C:\\Users\\petko\\Documents\\GitHub\\DigitRecognizer\\resources\\winutils-master\\hadoop-2.8.1").getAbsolutePath());
        Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
        Field theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment");
        theEnvironmentField.setAccessible(true);
        Map<String, String> env = (Map<String, String>) theEnvironmentField.get(null);
        env.clear();
        env.putAll(hadoopEnvSetUp);
        Field theCaseInsensitiveEnvironmentField = processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment");
        theCaseInsensitiveEnvironmentField.setAccessible(true);
        Map<String, String> cienv = (Map<String, String>) theCaseInsensitiveEnvironmentField.get(null);
        cienv.clear();
        cienv.putAll(hadoopEnvSetUp);
    }
}
