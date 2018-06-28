package com.vaibhav.trasher.classifier.tensorflow

import android.content.res.AssetManager
import com.vaibhav.trasher.classifier.COLOR_CHANNELS
import com.vaibhav.trasher.classifier.Classifier
import com.vaibhav.trasher.utils.getLabels
import org.tensorflow.contrib.android.TensorFlowInferenceInterface

object ImageClassifierFactory {

    fun create(
            assetManager: AssetManager,
            graphFilePath: String,
            labelsFilePath: String,
            imageSize: Int,
            inputName: String,
            outputName: String
    ): Classifier {

        val labels = getLabels(assetManager, labelsFilePath)

        return ImageClassifier(
                inputName,
                outputName,
                imageSize.toLong(),
                labels,
                IntArray(imageSize * imageSize),
                FloatArray(imageSize * imageSize * COLOR_CHANNELS),
                FloatArray(labels.size),
                TensorFlowInferenceInterface(assetManager, graphFilePath)
        )
    }
}