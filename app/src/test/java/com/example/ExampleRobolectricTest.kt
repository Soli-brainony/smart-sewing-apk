package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.BodyMeasurements
import com.example.data.model.GarmentType
import com.example.data.model.UnitSystem
import com.example.data.pattern.PatternEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("SewSmart AI", appName)
  }

  @Test
  fun `test pattern calculation generates geometric pieces`() {
    val measurements = BodyMeasurements(
      unitSystem = UnitSystem.INCHES,
      bust = 36f,
      waist = 28f,
      hip = 40f
    )

    val (pieces, breakdowns) = PatternEngine.calculatePattern(
      measurements = measurements,
      garmentType = GarmentType.DRESS,
      customEaseInches = 1.5f,
      seamAllowanceInches = 0.5f
    )

    assertTrue("Should generate at least 4 pattern pieces", pieces.size >= 4)
    val frontBodice = pieces.find { it.name == "Front Bodice" }
    assertTrue("Should contain Front Bodice", frontBodice != null)
    assertTrue("Front bodice width should be positive", frontBodice!!.widthInches > 0)
    assertTrue("Breakdowns should not be empty", breakdowns.isNotEmpty())
  }
}
