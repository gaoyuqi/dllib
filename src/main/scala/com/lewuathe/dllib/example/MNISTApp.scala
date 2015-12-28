package com.lewuathe.dllib.example

import com.lewuathe.dllib.Model
import com.lewuathe.dllib.form.Form
import com.lewuathe.dllib.layer.{ClassificationLayer, FullConnectedLayer}
import com.lewuathe.dllib.network.Network
import com.lewuathe.dllib.solver.MultiLayerPerceptron
import org.apache.spark.SparkContext
import org.apache.spark.sql.{SQLContext, DataFrame}

class MNISTApp {
  def createMNISTDataset(path: String, sc: SparkContext): DataFrame = {
    val dataset = MNIST(path)
    MNIST.asDF(dataset, sc, 2000)
  }

  def submit(sc: SparkContext) = {
    val sqlContext = new SQLContext(sc)
    val df = createMNISTDataset("/tmp/", sc)

    val nn3Form = new Form(Array(
      new FullConnectedLayer(100, 784),
      new ClassificationLayer(10, 100)
    ))

    val nn3Model = Model(nn3Form)
    val nn3 = Network(nn3Model, nn3Form)

    val multilayerPerceptron = new MultiLayerPerceptron("MNIST", nn3)
    val model = multilayerPerceptron.fit(df)

    val result = model.transform(df)

    result.filter("label = prediction").count()
  }
}