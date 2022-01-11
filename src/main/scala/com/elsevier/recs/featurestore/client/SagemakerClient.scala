package com.elsevier.recs.featurestore.client

import com.amazonaws.services.sagemaker.model.{CreateFeatureGroupRequest, CreateFeatureGroupResult, DeleteFeatureGroupRequest, DeleteFeatureGroupResult, ListFeatureGroupsRequest}
import com.elsevier.recs.featurestore.ConfigComponent
import com.amazonaws.services.sagemaker.{AmazonSageMaker, AmazonSageMakerClientBuilder}
import com.amazonaws.services.sagemakerfeaturestoreruntime.model.GetRecordRequest
import com.amazonaws.services.sagemakerfeaturestoreruntime.{AmazonSageMakerFeatureStoreRuntime, AmazonSageMakerFeatureStoreRuntimeClientBuilder}




trait SageMakerClient[A]{
  def client: A
}


case class SageMakerClientImpl() extends ConfigComponent with SageMakerClient[AmazonSageMaker] {

  val client = AmazonSageMakerClientBuilder.standard().withRegion(sageMakerConfig.region).build()

  def listFeatureGroups: String = {
    val request = new ListFeatureGroupsRequest()

    val result = client.listFeatureGroups(request).withFeatureGroupSummaries().toString

    result
  }

   def runCreateFeatureGroup(request: CreateFeatureGroupRequest): CreateFeatureGroupResult = {
    val result = client.createFeatureGroup(request)

    result
  }

  def runDeleteFeatureGroup(name:String): DeleteFeatureGroupResult = {
    val request = new DeleteFeatureGroupRequest()
    request.setFeatureGroupName(name)
    val result = client.deleteFeatureGroup(request)

    result
  }
}


case object FeatureStoreClient extends SageMakerClient[AmazonSageMakerFeatureStoreRuntime] with ConfigComponent {
  val client = AmazonSageMakerFeatureStoreRuntimeClientBuilder.standard().withRegion(sageMakerConfig.region).build()

  def getDataFromFeatureStore(featureGroupName: String, id: String): String = {
    val request = new GetRecordRequest()
    request.setFeatureGroupName(featureGroupName)
    request.setRecordIdentifierValueAsString(id)

    val response = client.getRecord(request).toString

    response
  }
}
object SageMakerClient extends ConfigComponent {
  def main(args: Array[String]): Unit = {
    println(FeatureStoreClient.getDataFromFeatureStore("number-publications-2022-01-11", "10038929200"))
  }
}



