package google

import com.typesafe.config.Config
import play.api.ConfigLoader

case class AnalyticsConfig(applicationName: String, clientSecretJson: String, redirectUrl: String)
object AnalyticsConfig {

  implicit val configLoader: ConfigLoader[AnalyticsConfig] = new ConfigLoader[AnalyticsConfig] {
    def load(rootConfig: Config, path: String): AnalyticsConfig = {
      val config = rootConfig.getConfig(path)
      AnalyticsConfig(
        applicationName = config.getString("applicationName"),
        clientSecretJson = config.getString("clientSecretJson"),
        redirectUrl = config.getString("redirectUrl")
      )
    }
  }
}

