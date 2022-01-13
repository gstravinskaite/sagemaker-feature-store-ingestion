ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.15"

lazy val root = (project in file("."))
  .settings(
    name := "sagemaker-feature-store-ingestion",
    organization := "com.elsevier.recs"
  )

lazy val awsSdkVersion         = "1.12.31"
lazy val sparkVersion = "2.4.4"
lazy val hadoopVersion = "2.8.5"
lazy val jacksonVersion        = "2.12.3"
+3.6+

val commonsLangExclusions = Seq(
  // Spark relies on an older version of Apache Commons Lang which clashes with the DAL
  //  - Spark: org.apache.commons:commons-lang3:3.4
  //  - DAL:   org.apache.commons:commons-lang3:3.9
  // We exclude the DAL version and let it fall back to Spark's version
  ExclusionRule(organization = "org.apache.commons", name = "commons-lang3")
)

val hadoopExclusions = Seq(
  ExclusionRule(organization = "org.apache.hadoop"),
  ExclusionRule(organization = "commons-beanutils", name = "commons-beanutils")
)

val jacksonExclusions = Seq(
  // AWS SDK pulls in later versions of Jackson than Elastic4s can handle
  //    Elastic4s com.fasterxml.jackson.core:jackson-databind:2.10.0
  //    AWS       com.fasterxml.jackson.core:jackson-databind:2.12.3
  // We exclude the AWS version and fall back to Elastic4s
  ExclusionRule(organization = "com.fasterxml.jackson.core", name = "jackson-annotations"),
  ExclusionRule(organization = "com.fasterxml.jackson.core", name = "jackson-core"),
  ExclusionRule(organization = "com.fasterxml.jackson.core", name = "jackson-databind")
)

libraryDependencies ++= Seq(

  "com.amazonaws" % "aws-java-sdk-sagemakerruntime" % awsSdkVersion excludeAll (jacksonExclusions: _*),
  "com.amazonaws" % "aws-java-sdk-sagemaker"        % awsSdkVersion excludeAll (jacksonExclusions: _*),
  "com.amazonaws" % "aws-java-sdk-sts"              % awsSdkVersion excludeAll (jacksonExclusions: _*),
  "software.amazon.sagemaker.featurestore"          %% "sagemaker-feature-store-spark-sdk" % "1.0.0",
  "com.amazonaws" % "aws-java-sdk-sagemakerfeaturestoreruntime" % awsSdkVersion excludeAll (jacksonExclusions: _*),


  // Config
  "com.github.pureconfig" %% "pureconfig" % "0.12.3",
  "com.iheart" %% "ficus" % "1.4.1",

  // Spark
  "org.apache.spark"  %% "spark-core"                   % sparkVersion,  //% Provided excludeAll (hadoopExclusions: _*),
  "org.apache.spark"  %% "spark-sql"                    % sparkVersion,  //% Provided excludeAll (hadoopExclusions: _*),
  "org.apache.spark"  %% "spark-avro"                   % sparkVersion,  //% Provided excludeAll (hadoopExclusions: _*),

//  // Jackson
  "com.fasterxml.jackson.core"       %  "jackson-annotations"       % jacksonVersion,
  "com.fasterxml.jackson.core"       %  "jackson-core"              % jacksonVersion,
  "com.fasterxml.jackson.core"       %  "jackson-databind"          % jacksonVersion,
  "com.fasterxml.jackson.dataformat" %  "jackson-dataformat-cbor"   % jacksonVersion,
  "com.fasterxml.jackson.datatype"   %  "jackson-datatype-joda"     % jacksonVersion,
  "com.fasterxml.jackson.module"     %  "jackson-module-paranamer"  % jacksonVersion,
  "com.fasterxml.jackson.module"     %% "jackson-module-scala"      % jacksonVersion,

)


excludeDependencies ++= Seq(
  ExclusionRule("com.amazonaws", "aws-java-sdk-bundle")
)