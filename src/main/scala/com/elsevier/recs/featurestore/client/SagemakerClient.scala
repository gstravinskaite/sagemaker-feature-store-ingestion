package com.elsevier.recs.featurestore.client

import com.amazonaws.services.sagemaker.model.{CreateFeatureGroupRequest, CreateFeatureGroupResult, DeleteFeatureGroupRequest, DeleteFeatureGroupResult, ListFeatureGroupsRequest}
import com.elsevier.recs.featurestore.{ConfigComponent, SageMakerConfig}
import com.amazonaws.services.sagemaker.{AbstractAmazonSageMaker, AmazonSageMakerClientBuilder}



trait SageMakerClient{
 // def listFeatureGroups : String
}


class SageMakerClientImpl extends ConfigComponent with SageMakerClient {

  val client = AmazonSageMakerClientBuilder.standard().withRegion(sageMakerConfig.region).build()

  def listFeatureGroups: String = {
    val request = new ListFeatureGroupsRequest()

    val result = client.listFeatureGroups(request).withFeatureGroupSummaries().toString

    result
  }

  def createFS(request: CreateFeatureGroupRequest): CreateFeatureGroupResult = {
    val result = client.createFeatureGroup(request)

    result
  }

  def deleteFS(name:String): DeleteFeatureGroupResult = {
    val request = new DeleteFeatureGroupRequest()
    request.setFeatureGroupName(name)
    val result = client.deleteFeatureGroup(request)

    result
  }

}

object listFeatureGroups extends ConfigComponent with SparkClient {
  def main(args: Array[String]): Unit = {
    val client = new SageMakerClientImpl()
//    println(client.listFeatureGroups)

    println(client.deleteFS("number-publications-2022-01-11"))

  }
}



