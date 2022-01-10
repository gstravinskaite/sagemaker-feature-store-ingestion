package com.elsevier.recs.featurestore

import com.typesafe.config.{Config, ConfigFactory}
import net.ceedubs.ficus.readers.ArbitraryTypeReader.arbitraryTypeValueReader

case class SageMakerConfig(region: String)

trait ConfigComponent {

  import net.ceedubs.ficus.Ficus._

  val getConfigName: String = "featurestore-local"

  private lazy val localConfig: Config = ConfigFactory.load(getConfigName)

//  lazy val secretsManagerStore: SecretsStore = new AWSSecretsManagerStore
//  private lazy val remoteConfig = new RemoteConfig(secretsManagerStore)

  lazy val sageMakerConfig: SageMakerConfig = localConfig.as[SageMakerConfig]("sagemaker")
}

case object AWSConfig extends ConfigComponent