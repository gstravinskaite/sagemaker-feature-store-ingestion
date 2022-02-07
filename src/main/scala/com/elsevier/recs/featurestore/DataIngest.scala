package com.elsevier.recs.featurestore
import com.elsevier.recs.featurestore.client.SparkClient
import org.apache.spark.sql
import org.apache.spark.sql.{Dataset, Row}
import org.apache.spark.sql.functions.{col, column, current_timestamp, date_format, exp, explode, expr, when}
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}
import software.amazon.sagemaker.featurestore.sparksdk.FeatureStoreManager
import org.apache.spark

import scala.collection.immutable.Nil.distinct


case object DataIngest extends SparkClient {
  def main(args: Array[String]): Unit = {
    val filepath = System.getProperty("user.dir") + "/src/main/resources/af-prod.parquet"
    //ingestStaticData()
//    val fullData = sparkSession.read.parquet(filepath)
//    println(fullData.show())
//    println(fullData.printSchema())

    val fullData = sparkSession.read.parquet(filepath)


    val neededData = fullData.selectExpr(
      "numPublications", "numPubsThisYear", "numPubsLast5Year", "id as scopusId", "email",
      "citationHIndex as hIndex", "pubYearFirst", "affiliations.id as affiliationId", "affiliations.country as countries",
      "affiliations.parentId as affilParentId"
    )
    import neededData.sparkSession.implicits._
    // Now need to remove duplicates from affils
    val t = neededData.selectExpr("countries").as[Seq[String]].map(e=>e.distinct).collect()

    println(t)



//    val deduplicatedData = neededData.withColumn("countriesZ", t.col("country"))
//    println(deduplicatedData.show(20, false))
  }


  def readAndProcessLambdaJson(filepath: String): sql.DataFrame = {
    val fullData = sparkSession.read.json(filepath)

    println(s"Size:${fullData.count()}")

    val neededData = fullData.selectExpr(
      "numPublications", "numPubsThisYear", "numPubsLast5Year", "givenName", "surname", "id", "email",
    )
//    val neededData = fullData.selectExpr(
//      "numPublications", "numPubsThisYear", "numPubsLast5Year", "name", "surname", "scopusId", "email",
//    ).withColumnRenamed("name", "givenName")
//      .withColumnRenamed("scopusId","id")

    val timestampedData = neededData.withColumn(
      "EventTime", date_format(current_timestamp(), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"))

    timestampedData
  }

  def ingestStaticData(): Unit = {

    val data = List(
      sql.Row(100 , 50, 100, "Gabriele", "Stravinskaite", "888888", "gabby@vilnius.com"),
      sql.Row(110 , 40, 110, "Jon", "Snow", "777777", "jon@wall.com"),
      sql.Row(90, 30, 200, "Harry", "Potter", "666666", "harry@scotland.com")
    )

    val schema = StructType(
      List(StructField("numPublications", IntegerType),
        StructField("numPubsThisYear", IntegerType),
        StructField("numPubsLast5Year", IntegerType),
        StructField("givenName", StringType),
        StructField("surname", StringType),
        StructField("id", StringType),
        StructField("email", StringType),
      ))

    val df = sparkSession.createDataFrame(sparkSession.sparkContext.parallelize(data), schema)

    val timestampedData = df.withColumn(
      "EventTime", date_format(current_timestamp(), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"))

    ingestDataToFeatureStore(timestampedData)

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
