package nerd.tuxmobil.fahrplan.congress.schedule.observables

import androidx.annotation.LayoutRes
import com.google.common.truth.Truth.assertThat
import info.metadude.android.eventfahrplan.commons.temporal.Moment
import nerd.tuxmobil.fahrplan.congress.R
import nerd.tuxmobil.fahrplan.congress.models.Session
import nerd.tuxmobil.fahrplan.congress.schedule.Conference
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.threeten.bp.ZoneOffset
import java.util.Locale
import java.util.TimeZone

class TimeTextViewParameterTest {

    private companion object {
        const val NORMALIZED_BOX_HEIGHT = 34 // Pixel 2 portrait mode
    }

    private val systemTimezone = TimeZone.getDefault()
    private val systemLocale = Locale.getDefault()

    @BeforeEach
    fun setUp() {
        Locale.setDefault(Locale("de", "DE"))
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"))
    }

    @AfterEach
    fun resetSystemDefaults() {
        Locale.setDefault(systemLocale)
        TimeZone.setDefault(systemTimezone)
    }

    @Test
    fun `parametersOf returns four view parameters without -now- view parameter if the session happened yesterday`() {
        val moment = Moment.ofEpochMilli(1582963200000L) // February 29, 2020 08:00:00 AM GMT
        val nowMoment = moment.plusDays(1)
        val parameters = parametersOf(nowMoment, moment, 60)
        assertThat(parameters.size).isEqualTo(4)
        parameters[0].assert(R.layout.time_layout, "08:00")
        parameters[1].assert(R.layout.time_layout, "08:15")
        parameters[2].assert(R.layout.time_layout, "08:30")
        parameters[3].assert(R.layout.time_layout, "08:45")
    }

    @Test
    fun `parametersOf returns four view parameters without -now- view parameter if the session happened at the same date last month`() {
        val momentInFebruary = Moment.ofEpochMilli(1582963200000L) // February 29, 2020 08:00:00 AM GMT
        val momentInMarch = Moment.ofEpochMilli(1585468800000L) // March 29, 2020 08:00:00 AM GMT
        val nowMoment = momentInMarch.plusMinutes(30)
        val parameters = parametersOf(nowMoment, momentInFebruary, 60)
        assertThat(parameters.size).isEqualTo(4)
        parameters[0].assert(R.layout.time_layout, "08:00")
        parameters[1].assert(R.layout.time_layout, "08:15")
        parameters[2].assert(R.layout.time_layout, "08:30")
        parameters[3].assert(R.layout.time_layout, "08:45")
    }

    @Test
    fun `parametersOf returns four view parameters including one -now- view parameter if the session happens now`() {
        val moment = Moment.ofEpochMilli(1582963200000L) // February 29, 2020 08:00:00 AM GMT
        val nowMoment = moment.plusMinutes(30)
        val parameters = parametersOf(nowMoment, moment, 60)
        assertThat(parameters.size).isEqualTo(4)
        parameters[0].assert(R.layout.time_layout, "08:00")
        parameters[1].assert(R.layout.time_layout, "08:15")
        parameters[2].assert(R.layout.time_layout_now, "08:30")
        parameters[3].assert(R.layout.time_layout, "08:45")
    }

    @Test
    fun `parametersOf returns four view parameters for a session crossing the intra-day limit if the session happened yesterday`() {
        val moment = Moment.ofEpochMilli(1583019000000L) // February 29, 2020 11:30:00 PM GMT
        val nowMoment = moment.plusDays(1)
        val parameters = parametersOf(nowMoment, moment, 60)
        assertThat(parameters.size).isEqualTo(4)
        parameters[0].assert(R.layout.time_layout, "23:30")
        parameters[1].assert(R.layout.time_layout, "23:45")
        parameters[2].assert(R.layout.time_layout, "00:00")
        parameters[3].assert(R.layout.time_layout, "00:15")
    }

    @Test
    fun `parametersOf returns four view parameters including one -now- view parameter for a session crossing the intra-day limit`() {
        val moment = Moment.ofEpochMilli(1583019000000L) // February 29, 2020 11:30:00 PM GMT
        val nowMoment = moment.plusMinutes(45) // March 1, 2020 00:15:00 AM GMT
        val parameters = parametersOf(nowMoment, moment, 60)
        assertThat(parameters.size).isEqualTo(4)
        parameters[0].assert(R.layout.time_layout, "23:30")
        parameters[1].assert(R.layout.time_layout, "23:45")
        parameters[2].assert(R.layout.time_layout, "00:00")
        parameters[3].assert(R.layout.time_layout_now, "00:15")
    }

    @Test
    fun `parametersOf returns 20 view parameters for a session crossing the daylight saving time start`() {
        val moment = Moment.ofEpochMilli(1616889600000L) // March 28, 2021 12:00:00 AM GMT
        val nowMoment = moment.plusDays(1) // March 29, 2021 12:00:00 AM GMT
        val parameters = parametersOf(nowMoment, moment, 300)
        assertThat(parameters.size).isEqualTo(20)
        parameters[0].assert(R.layout.time_layout, "00:00")
        parameters[2].assert(R.layout.time_layout, "00:30")
        parameters[4].assert(R.layout.time_layout, "01:00")
        parameters[6].assert(R.layout.time_layout, "01:30")
        parameters[8].assert(R.layout.time_layout, "02:00") // Clock turns to 03:00 summer time, currently not supported, see Conference class
        parameters[10].assert(R.layout.time_layout, "02:30")
        parameters[12].assert(R.layout.time_layout, "03:00")
        parameters[14].assert(R.layout.time_layout, "03:30")
        parameters[16].assert(R.layout.time_layout, "04:00")
    }

    @Test
    fun `parametersOf returns 20 view parameters for a session crossing the daylight saving time end`() {
        val moment = Moment.ofEpochMilli(1635638400000L) // October 31, 2021 12:00:00 AM GMT
        val nowMoment = moment.plusDays(1) // November 1, 2021 12:00:00 AM GMT
        val parameters = parametersOf(nowMoment, moment, 300)
        assertThat(parameters.size).isEqualTo(20)
        parameters[0].assert(R.layout.time_layout, "00:00")
        parameters[2].assert(R.layout.time_layout, "00:30")
        parameters[4].assert(R.layout.time_layout, "01:00")
        parameters[6].assert(R.layout.time_layout, "01:30")
        parameters[8].assert(R.layout.time_layout, "02:00")
        parameters[10].assert(R.layout.time_layout, "02:30")
        parameters[12].assert(R.layout.time_layout, "03:00") // Clock turns to 02:00 winter time, currently not supported, see Conference class
        parameters[14].assert(R.layout.time_layout, "03:30")
        parameters[16].assert(R.layout.time_layout, "04:00")
    }

    private fun parametersOf(nowMoment: Moment, moment: Moment, duration: Int): List<TimeTextViewParameter> {
        val session = createSession(moment, duration)
        val conference = Conference.ofSessions(listOf(session))
        return TimeTextViewParameter.parametersOf(nowMoment, conference, NORMALIZED_BOX_HEIGHT, useDeviceTimeZone = false)
    }

    private fun TimeTextViewParameter.assert(@LayoutRes layout: Int, titleText: String) {
        assertThat(this.layout).isEqualTo(layout)
        assertThat(this.height).isEqualTo(102)
        assertThat(this.titleText).isEqualTo(titleText)
    }

    private fun createSession(moment: Moment, duration: Int = 60) = Session(
        guid = "11111111-1111-1111-1111-111111111111",
        dayIndex = 0,
        dateText = moment.toZonedDateTime(ZoneOffset.UTC).toLocalDate().toString(),
        dateUTC = moment.toMilliseconds(),
        startTime = moment.minuteOfDay,
        duration = duration,
        roomName = "Main hall",
    )

}