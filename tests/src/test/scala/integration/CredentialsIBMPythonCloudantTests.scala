/*
 * Copyright 2017 IBM Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package integration

import common.TestHelpers
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import common.WskTestHelpers
import common.WskProps
import java.io.File
import spray.json._
import common.TestUtils
import common.rest.WskRest

@RunWith(classOf[JUnitRunner])
class CredentialsIBMPythonCloudantTests extends TestHelpers with WskTestHelpers {

  implicit val wskprops: WskProps = WskProps()
  var defaultKind = Some("python-jessie:3")
  val wsk = new WskRest
  val datdir = "tests/dat/cloudant/"
  var creds = TestUtils.getVCAPcredentials("cloudantNoSQLDB")
  val actionName = "testCloudantService"
  val actionFileName = "testCloudantService.py"

  it should "Test whether or not cloudant database is reachable using cloudant python package" in withAssetCleaner(
    wskprops) { (wp, assetHelper) =>
    val file = Some(new File(datdir, actionFileName).toString())

    assetHelper.withCleaner(wsk.action, actionName) { (action, _) =>
      action.create(
        actionName,
        file,
        main = Some("main"),
        kind = defaultKind,
        parameters = Map(
          "username" -> JsString(creds.get("username")),
          "password" -> JsString(creds.get("password")),
          "host" -> JsString(creds.get("host"))))
    }

    withActivation(wsk.activation, wsk.action.invoke(actionName)) { activation =>
      val response = activation.response
      response.result.get.fields.get("error") shouldBe empty
      response.result.get.fields.get("lastname") should be(Some(JsString("Queue")))
    }

  }

}
