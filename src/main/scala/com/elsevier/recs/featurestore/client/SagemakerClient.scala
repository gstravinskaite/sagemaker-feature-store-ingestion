package com.elsevier.recs.featurestore.client

import com.amazonaws.services.sagemaker.model.CreateFeatureGroupResult
import com.elsevier.recs.featurestore.{ConfigComponent, SageMakerConfig}
import com.amazonaws.services.sagemaker.{AbstractAmazonSageMaker, AmazonSageMakerClientBuilder}
import com.amazonaws.services.sagemaker.model.{CreateFeatureGroupRequest, CreateFeatureGroupResult, ListFeatureGroupsRequest}



trait SageMakerClient{
 // def listFeatureGroups : String
}


class SageMakerClientImpl extends ConfigComponent with SageMakerClient {

  val client = AmazonSageMakerClientBuilder.standard().withRegion(sageMakerConfig.region).build()

//  def listFeatureGroups: String = {
//    val request = new ListFeatureGroupsRequest()
//
//    val result = client.listFeatureGroups(request).withFeatureGroupSummaries().toString
//
//    result
//  }

  def createFS(request: CreateFeatureGroupRequest): CreateFeatureGroupResult = {
    val result = client.createFeatureGroup(request)

    //val result = client.createFeatureGroup(request) //createFeatureGroup(request)
    result
  }

}

//object listFeatureGroups extends ConfigComponent with SparkClient {
//  def main(args: Array[String]): Unit = {
////    val client = new SageMakerClientImpl(sageMakerConfig)
////    println(client.listFeatureGroups)
//
//    val spark = sparkSession
//    val logFile = "/Users/stravinskaiteg/Documents/recommenders/experiments/sagemaker-feature-store-ingestion/README.md" // Should be some file on your system
//    val logData = spark.read.textFile(logFile).cache()
//    val numAs = logData.filter(line => line.contains("a")).count()
//    val numBs = logData.filter(line => line.contains("b")).count()
//    println(s"Lines with a: $numAs, Lines with b: $numBs")
//    spark.stop()
//  }
//}
//


