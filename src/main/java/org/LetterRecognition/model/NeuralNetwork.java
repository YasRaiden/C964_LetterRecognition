package org.LetterRecognition.model;

import org.datavec.api.io.labels.ParentPathLabelGenerator;
import org.datavec.api.split.FileSplit;
import org.datavec.image.loader.NativeImageLoader;
import org.datavec.image.recordreader.ImageRecordReader;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.*;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class NeuralNetwork {
    private static final Logger log = LoggerFactory.getLogger(NeuralNetwork.class);
    public static MultiLayerNetwork model;

    // Setup default image information
    private static final int height = 28;
    private static final int width = 28;
    private static final int channels = 1;

    // Random number to randomize dataset
    private static final int seed = 323;
    private static final Random randNum = new Random(seed);


    private static final int numEpochs = 1;
    private static final int batchSize = 128;

    // Total number of possibilities, 26 lower case letters and 26 uppercase letters
    private static final int outputNum = 52;

    // Specify file paths for training and test data
    public static final File trainData = new File("digit/training");
    public static final File testData = new File("digit/testing");

    public static final List<String> letterLabels = Arrays.asList(
            "41", "42", "43", "44", "45", "46", "47", "48", "49", "4a", "4b", "4c",
            "4d", "4e", "4f", "50", "51", "52", "53", "54", "55", "56", "57", "58",
            "59", "5a", "61", "62", "63", "64", "65", "66", "67", "68", "69", "6a",
            "6b", "6c", "6d", "6e", "6f", "70", "71", "72", "73", "74", "75", "76",
            "77", "78", "79", "7a");

    /** Method parses given directory for files to be used in model
     * @param dataFile root file directory to be used.
     * @return dataset of listed files that have been preprocessed
     * @throws IOException
     */
    public static DataSetIterator parseDirectory(File dataFile)  {
        FileSplit parseData = new FileSplit(dataFile, NativeImageLoader.ALLOWED_FORMATS, randNum);
        ParentPathLabelGenerator imgLabel = new ParentPathLabelGenerator();
        ImageRecordReader recordReader = new ImageRecordReader(height,width,channels,imgLabel);

        try {
            recordReader.initialize(parseData);
        } catch (IOException e) {
            log.error("Issue parsing directory for model " + e);
        }
        DataSetIterator dataSetIterator = new RecordReaderDataSetIterator(recordReader,batchSize,1,outputNum);

        // Set pixel value to 0 or 1
        DataNormalization scalar = new ImagePreProcessingScaler(0,1);
        scalar.fit(dataSetIterator);
        dataSetIterator.setPreProcessor(scalar);

        return dataSetIterator;
    }

    /** Method is used to add a single item to NeuralNetwork
     * @param dataFile file to be learned
     * @param indexes label indexes
     * @param iterations number of epochs
     * @throws IOException
     */
    public static void learnItem(File dataFile, int[] indexes, int iterations) throws IOException {
        log.info("###### TRAIN MODEL");
        for(int i = 0; i < iterations; i++){
            model.fit(normalizeImage(dataFile), indexes);
        }

    }

    /** Method builds model to train data against setup uses Convolutional Neural Network
     * @return model configuration
     */
    public static MultiLayerNetwork buildModel() {
        log.info("###### BUILD MODEL");
        // Convolutional Neural Network Setup (LeNet-5)
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .weightInit(WeightInit.XAVIER)
                .l2(1e-3)
                .updater(new Adam(1e-3))
                .list()
                .layer(new ConvolutionLayer.Builder()
                        .nIn(channels)
                        .nOut(20)
                        .kernelSize(5,5)
                        .stride(1,1)
                        .activation(Activation.IDENTITY)
                        .build())
                .layer(new SubsamplingLayer.Builder()
                        .poolingType(PoolingType.MAX)
                        .kernelSize(2,2)
                        .stride(2,2)
                        .build())
                .layer(new ConvolutionLayer.Builder()
                        .nOut(50)
                        .kernelSize(5,5)
                        .stride(1,1)
                        .activation(Activation.IDENTITY)
                        .build())
                .layer(new SubsamplingLayer.Builder()
                        .poolingType(PoolingType.MAX)
                        .kernelSize(2,2)
                        .stride(2,2)
                        .build())
                .layer(new DenseLayer.Builder()
                        .nOut(500)
                        .activation(Activation.RELU)
                        .build())
                .layer(new OutputLayer.Builder()
                        .nIn(500)
                        .nOut(outputNum)
                        .lossFunction(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .activation(Activation.SOFTMAX)
                        .build())
                .setInputType(InputType.convolutionalFlat(height,width,channels))
                .build();


        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();
        return model;
    }

    /** Method trains model with given dataset
     * @param model NeuralNetwork model configuration
     * @param dataSetIterator pulled dataset that has been normalized
     * @param testDataSet dataset to use to test against model while training
     * @return trained model
     */
    public static MultiLayerNetwork trainModel(MultiLayerNetwork model, DataSetIterator dataSetIterator, DataSetIterator testDataSet) {
        log.info("###### TRAIN MODEL");
        //model.setListeners(new ScoreIterationListener(10), new EvaluativeListener(testDataSet, 1, InvocationType.EPOCH_END));
        model.fit(dataSetIterator, numEpochs);
        return model;
    }

    /** Method is used to evaluate model again test dataset. Similar testing processes occurs while training
     * @param model NeuralNetwork model configuration that has been trained
     * @param dataSetIterator dataset to test against model
     * @throws IOException handles exceptions
     */
    public static void evaluateModel(MultiLayerNetwork model, DataSetIterator dataSetIterator) throws IOException {
        log.info("###### EVALUATE MODEL");

        Evaluation eval = new Evaluation(outputNum);

        while(dataSetIterator.hasNext()){
            DataSet next = dataSetIterator.next();
            INDArray output = model.output(next.getFeatures());
            eval.eval(next.getLabels(),output);
        }

        log.info(eval.stats());
    }

    /** Method is used to save model to current directory
     * @param model trained model to be saved
     * @throws IOException handles exceptions
     */
    public static void saveModel(MultiLayerNetwork model) {
       log.info("###### SAVE MODEL");
        try {
            model.save(new File("Saved_Model.zip"), true);
        } catch (IOException e) {
            log.info("Error Saving Model: " + e);
        }
    }

    /** Method is used to load model from current directory
     * @throws IOException handles exceptions
     */
    public static void loadModel() {
        log.info("###### LOAD MODEL");
        try {
            model = ModelSerializer.restoreMultiLayerNetwork(new File("Saved_Model.zip"));
        } catch (IOException e) {
            log.info("Error Loading Model: " + e);
        }
    }

    /** Method is used to normalize data in buffered image for better predictions
     * @param image buffered image to be normalized
     * @return INDArray of normalized image
     */
    public static INDArray normalizeImage(BufferedImage image)  {
        NativeImageLoader loader = new NativeImageLoader(height, width, channels);
        INDArray indArray = null;
        try {
            indArray = loader.asMatrix(image);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        DataNormalization scalar = new ImagePreProcessingScaler(0,1);
        scalar.transform(indArray);
        return indArray;
    }

    /** Method is used to normalize data in file for better predictions
     * @param file to be utilized for normalization
     * @return INDArray of normalized image
     */
    public static INDArray normalizeImage(File file) throws IOException {
        NativeImageLoader loader = new NativeImageLoader(height, width, channels);
        INDArray image = loader.asMatrix(file);
        DataNormalization scalar = new ImagePreProcessingScaler(0,1);
        scalar.transform(image);
        return image;
    }
}
