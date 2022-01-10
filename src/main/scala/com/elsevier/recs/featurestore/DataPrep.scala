package com.elsevier.recs.featurestore
import com.elsevier.recs.featurestore.client.SparkClient
import org.apache.spark.sql.functions.current_timestamp



class DataPrep extends SparkClient{

  def readAndProcessData(filepath: String): Unit = {

    val fullData = sparkSession.read.parquet(filepath)
    // EventTime is a needed field for feature groups
    val neededData = fullData.selectExpr(
      "numPublications", "numPubsThisYear", "numPubsLast5Year", "givenName", "surname", "id", "email",
      s"${current_timestamp()} as EventTime"
    )
    neededData.show()
  }
}

object DataPrep {

  def main(args: Array[String]): Unit = {
    val test = new DataPrep
    val filePath = System.getProperty("user.dir") + "/src/main/resources/data/part-00000-5f001ccd-45f5-42fc-abf3-615f6fcbe4f6-c000.snappy.parquet"
    println(test.readAndProcessData(filePath))
  }
}