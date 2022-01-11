package com.elsevier.recs.featurestore

import com.amazonaws.services.sagemaker.AmazonSageMaker

import java.time.LocalDate
import com.amazonaws.services.sagemaker.model.{CreateFeatureGroupRequest, FeatureDefinition, OfflineStoreConfig, OnlineStoreConfig, S3StorageConfig}
import com.elsevier.recs.featurestore.client.SageMakerClientImpl

import collection.JavaConverters._
import collection.mutable._
import scala.collection.convert.ImplicitConversions.`collection AsScalaIterable`

case class FD(fName: String, fType:String) extends FeatureDefinition {

}
class CreateFeatureStore extends SageMakerClientImpl {

  val now = LocalDate.now()
  val FEATURE_GROUP_NAME="number-publications-" + now.toString



  def buildsRequest(): CreateFeatureGroupRequest = {
    val request = new CreateFeatureGroupRequest()
    request.setFeatureGroupName(FEATURE_GROUP_NAME)
    request.setEventTimeFeatureName("EventTime")
    request.setRecordIdentifierFeatureName("id")

    val offlineStoreConf = new OfflineStoreConfig()
    val onlineStoreCOnf = new OnlineStoreConfig()
    onlineStoreCOnf.setEnableOnlineStore(true)

    val s3Config = new S3StorageConfig()
    s3Config.setS3Uri("s3://com-elsevier-recs-dev-reviewers/SDPR-5746")
    offlineStoreConf.setS3StorageConfig(s3Config)


    request.setOfflineStoreConfig(offlineStoreConf)
    request.setOnlineStoreConfig(onlineStoreCOnf)
    request.setRoleArn("arn:aws:iam::975165675840:role/dev_feature_store_experiment")

    val FDList = buildFeatureDefinitions()
    request.setFeatureDefinitions(FDList)


    request
  }


  def buildFeatureDefinitions(): java.util.Collection[FeatureDefinition] = {

    val numPublications = new FeatureDefinition
    numPublications.setFeatureType("Integral")
    numPublications.setFeatureName("numPublications")

    val numPubsThisYear = new FeatureDefinition
    numPubsThisYear.setFeatureType("Integral")
    numPubsThisYear.setFeatureName("numPubsThisYear")

    val numPubsLast5Year = new FeatureDefinition
    numPubsLast5Year.setFeatureType("Integral")
    numPubsLast5Year.setFeatureName("numPubsLast5Year")

    val givenName = new FeatureDefinition
    givenName.setFeatureType("String")
    givenName.setFeatureName("givenName")

    val surname = new FeatureDefinition
    surname.setFeatureType("String")
    surname.setFeatureName("surname")

    val id = new FeatureDefinition
    id.setFeatureType("String")
    id.setFeatureName("id")

    val email = new FeatureDefinition
    email.setFeatureType("String")
    email.setFeatureName("email")

    val EventTime = new FeatureDefinition
    EventTime.setFeatureType("String")
    EventTime.setFeatureName("EventTime")


    val FDList: java.util.Collection[FeatureDefinition] = ArrayBuffer(
      numPublications, numPubsThisYear, numPubsLast5Year, givenName, surname, id, email, EventTime
    ).asJava

    FDList
  }


  def createTest(request: CreateFeatureGroupRequest): String = {
   val response = runCreateFeatureGroup(request)

    response.toString
  }


}


object CreateFeatureStore{
  def main(args: Array[String]): Unit = {
    val test = new CreateFeatureStore
    val build = test.buildsRequest()
    println(test.createTest(build))

  }
}