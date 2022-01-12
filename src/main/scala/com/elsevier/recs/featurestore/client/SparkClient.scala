package com.elsevier.recs.featurestore.client

import org.apache.log4j.{Level, LogManager}
import org.apache.spark.sql.SparkSession


trait SparkClient  {

  implicit var sparkSession: SparkSession = _

  sparkSession = SparkSession.builder()
    .master("local[2]")
    .appName(getClass.getSimpleName)
    .config("spark.driver.host", "127.0.0.1")
    .config("spark.ui.enabled", "false")
    .getOrCreate()

  LogManager.getRootLogger.setLevel(Level.ERROR)

  sparkSession.sql("set spark.sql.shuffle.partitions=4")
}
