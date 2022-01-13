package com.elsevier.recs.featurestore
import com.elsevier.recs.featurestore.client.SparkClient
import org.apache.spark.sql
import org.apache.spark.sql.functions.{current_timestamp, date_format}
import software.amazon.sagemaker.featurestore.sparksdk.FeatureStoreManager


case object DataIngest extends SparkClient {

  def main(args: Array[String]): Unit = {
    val filePath = System.getProperty("user.dir") + "/src/main/resources/data/part-00000-5f001ccd-45f5-42fc-abf3-615f6fcbe4f6-c000.snappy.parquet"
    val jsonPath = System.getProperty("user.dir") + "/src/main/resources/ABREP_2019_204__bigrams_logistic-regression.json"
    val data = readAndProcessLambdaJson(jsonPath)
    println(ingestDataToFeatureStore(data))
  }


  def readAndProcessLambdaJson(filepath: String): sql.DataFrame = {
    val fullData = sparkSession.read.json(filepath)

    val neededData = fullData.selectExpr(
      "numPublications", "numPubsThisYear", "numPubsLast5Year", "name", "surname", "scopusId", "email",
    ).withColumnRenamed("name", "givenName")
      .withColumnRenamed("scopusId","id")

    val timestampedData = neededData.withColumn(
      "EventTime", date_format(current_timestamp(), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"))

    timestampedData
  }

  def readAndProcessData(filepath: String): sql.DataFrame = {

    val fullData = sparkSession.read.parquet(filepath)
    // EventTime is a needed field for feature groups
    val neededData = fullData.selectExpr(
      "numPublications", "numPubsThisYear", "numPubsLast5Year", "givenName", "surname", "id", "email",
    )
    val timestampedData = neededData.withColumn(
      "EventTime", date_format(current_timestamp(), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"))

    timestampedData.printSchema()
    println(timestampedData.count())
    timestampedData
  }

  def ingestDataToFeatureStore(inputData: sql.DataFrame): Unit = {
    val featureStoreManager = new FeatureStoreManager()

    val featureGroupArn = "arn:aws:sagemaker:us-east-1:975165675840:feature-group/number-publications-2022-01-11"

    featureStoreManager.ingestData(inputData, featureGroupArn, directOfflineStore = false)

  }

}
