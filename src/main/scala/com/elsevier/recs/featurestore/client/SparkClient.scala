package com.elsevier.recs.featurestore.client

import org.apache.log4j.{Level, LogManager}
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession


trait SparkClient {

  implicit var sparkSession: SparkSession = _

  val registerKryoClasses = Array[Class[_]]()

  sparkSession = SparkSession.builder()
    .appName("FeatureStoreIngestion")
//    .master("local[2]")
//    .config("spark.driver.host", "127.0.0.1")
//    .config("spark.ui.enabled", "false")
    .config(
      new SparkConf()
        .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
        .registerKryoClasses(registerKryoClasses)
    )
    .getOrCreate()

  LogManager.getRootLogger.setLevel(Level.ERROR)

  sparkSession.sql("set spark.sql.shuffle.partitions=4")
}

