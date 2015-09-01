package de.chrgroth.smartcron.util;

import java.time.temporal.ChronoUnit;

import org.junit.Assert;
import org.junit.Test;

public class ChronoUnitUtilsTest {

    @Test(expected = IllegalArgumentException.class)
    public void foreverToMillis() {
        ChronoUnitUtils.toMillis(1, ChronoUnit.FOREVER);
    }

    @Test(expected = IllegalArgumentException.class)
    public void erasToMillis() {
        ChronoUnitUtils.toMillis(1, ChronoUnit.ERAS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void milleniaToMillis() {
        ChronoUnitUtils.toMillis(1, ChronoUnit.MILLENNIA);
    }

    @Test(expected = IllegalArgumentException.class)
    public void centuriesToMillis() {
        ChronoUnitUtils.toMillis(1, ChronoUnit.CENTURIES);
    }

    @Test(expected = IllegalArgumentException.class)
    public void decadesToMillis() {
        ChronoUnitUtils.toMillis(1, ChronoUnit.DECADES);
    }

    @Test(expected = IllegalArgumentException.class)
    public void yearsToMillis() {
        ChronoUnitUtils.toMillis(1, ChronoUnit.YEARS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void monthsToMillis() {
        ChronoUnitUtils.toMillis(1, ChronoUnit.MONTHS);
    }

    @Test
    public void weeksToMillis() {
        Assert.assertEquals(7 * 24 * 60 * 60 * 1000, ChronoUnitUtils.toMillis(1, ChronoUnit.WEEKS));
    }

    @Test
    public void daysToMillis() {
        Assert.assertEquals(24 * 60 * 60 * 1000, ChronoUnitUtils.toMillis(1, ChronoUnit.DAYS));
    }

    @Test
    public void halfDaysToMillis() {
        Assert.assertEquals(12 * 60 * 60 * 1000, ChronoUnitUtils.toMillis(1, ChronoUnit.HALF_DAYS));
    }

    @Test
    public void hoursToMillis() {
        Assert.assertEquals(60 * 60 * 1000, ChronoUnitUtils.toMillis(1, ChronoUnit.HOURS));
    }

    @Test
    public void minutesToMillis() {
        Assert.assertEquals(60 * 1000, ChronoUnitUtils.toMillis(1, ChronoUnit.MINUTES));
    }

    @Test
    public void secondsToMillis() {
        Assert.assertEquals(1000, ChronoUnitUtils.toMillis(1, ChronoUnit.SECONDS));
    }

    @Test
    public void millisToMillis() {
        Assert.assertEquals(1, ChronoUnitUtils.toMillis(1, ChronoUnit.MILLIS));
    }

    @Test(expected = IllegalArgumentException.class)
    public void microsToMillis() {
        ChronoUnitUtils.toMillis(1, ChronoUnit.MICROS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nanosToMillis() {
        ChronoUnitUtils.toMillis(1, ChronoUnit.NANOS);
    }
}
