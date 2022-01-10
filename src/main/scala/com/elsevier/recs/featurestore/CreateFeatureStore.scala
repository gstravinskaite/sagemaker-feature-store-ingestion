package com.elsevier.recs.featurestore

import java.time.LocalDate
import com.amazonaws.services.sagemaker.model.{CreateFeatureGroupRequest, FeatureDefinition, FeatureGroup}
import com.elsevier.recs.featurestore.client.{SageMakerClientImpl, SparkClient}

class CreateFeatureStore extends SageMakerClientImpl {

  val now = LocalDate.now()
  val FEATURE_GROUP_NAME="number-publications-" + now.toString



  def buildsRequest(): CreateFeatureGroupRequest = {
    val request = new CreateFeatureGroupRequest()
    request.setFeatureGroupName(FEATURE_GROUP_NAME)
    request.setEventTimeFeatureName("EventTime")
    request.setRecordIdentifierFeatureName("id")




    request
  }

  def createTest(request: CreateFeatureGroupRequest): String = {
   val response = createFS(request)

    response.toString
  }


}


object CreateFeatureStore{
  def main(args: Array[String]): Unit = {
//    val test = new CreateFeatureStore
//    val build = test.buildsRequest()
//    println(test.createTest(build))

    println(FD().test)
  }
}