package forms.$routeFile$

import forms.behaviours.FormBehaviours
import models.{Field, Invalid, Required, $className$}

class $className$FormProviderSpec extends FormBehaviours {

  val validData: Map[String, String] = Map(
    "value" -> $className$.options.head.value
  )

  val form = new $className$FormProvider()()

  "$className$ form" must {

    behave like questionForm[$className$]($className$.values.head)

    behave like formWithOptionField(
      Field(
        "value",
        Required -> "$className;format="decap"$.error.required",
        Invalid -> "error.invalid"),
      $className$.options.toSeq.map(_.value): _*)
  }
}
