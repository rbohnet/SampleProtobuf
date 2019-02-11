
package ca.aeso.capacitymarket;

import static org.junit.Assert.*;

import ca.aeso.capacitymarket.ucap.UcapCalculationResultsOrBuilder;
import com.google.protobuf.Message;
import org.junit.*;
import ca.aeso.capacitymarket.ucap.Ucap;
import ca.aeso.capacitymarket.ucap.UcapCalculationResults;
import ca.aeso.type.YearMonthDay;
import com.google.protobuf.util.JsonFormat;

import static ca.aeso.capacitymarket.ucap.Ucap.CalculationMethod.AVAILABLITY_FACTOR;
import static ca.aeso.capacitymarket.ucap.Ucap.CalculationMethod.CAPACITY_FACTOR;
import static ca.aeso.capacitymarket.ucap.UcapCalculationResults.AuctionType.BASE_AUCTION;

public class SampleMessagesTest {


    @Test
    public void displaySampleUcapCalculationResults() throws Exception{

        YearMonthDay date_2019_11_01 = createDate(2019, 11, 01);
        YearMonthDay date_2020_10_31 = createDate(2020, 10, 31);

        ObligationPeriod period = ObligationPeriod.newBuilder()
                .setIdentifier("2019 / 2020")
                .setBegin(date_2019_11_01)
                .setEnd(date_2020_10_31)
                .build();

        UcapCalculationResults ucapResults = UcapCalculationResults.newBuilder()
                .setObligationPeriod(period)
                .setAuctionType(BASE_AUCTION)
                .addUcap(createUcap("522", "BIG", 97, 103, 99, AVAILABLITY_FACTOR,
                        0.88f, 120))
                .addUcap(createUcap("101", "ABC", 50, 80, 61, CAPACITY_FACTOR,
                        0.99f, 100))
                .build();


        String jsonOutput = JsonFormat.printer().print(ucapResults);

        // Rehydrate from json
        UcapCalculationResultsOrBuilder builder = UcapCalculationResults.newBuilder();
        JsonFormat.parser().merge(jsonOutput, (Message.Builder) builder);
        UcapCalculationResults rehydratedUcapResults = ((UcapCalculationResults.Builder) builder).build();
        assertSameUcapCalculateResults(ucapResults, rehydratedUcapResults);

        System.out.println("UcapCalculationResults:" + System.lineSeparator() +
                jsonOutput);

    }

    @Test
    public void displaySampleSettlementInvervalExclusions() throws Exception{

    }

    private void assertSameUcapCalculateResults(UcapCalculationResults expected, UcapCalculationResults actual) {

        assertEquals(expected.getObligationPeriod().getBegin(), actual.getObligationPeriod().getBegin());
        assertEquals(expected.getObligationPeriod().getEnd(), actual.getObligationPeriod().getEnd());
        assertEquals(expected.getAuctionTypeValue(), actual.getAuctionTypeValue());
        assertEquals(expected.getUcapCount(), actual.getUcapCount());
        // TODO: validate rest

    }

    private static YearMonthDay createDate(int year, int month, int day) {

        //TODO: validate year: 1-9999, month: 1-12, day: 1-31
        return YearMonthDay.newBuilder()
                .setYear(year)
                .setMonth(month)
                .setDay(day)
                .build();
    }

    private static ObligationPeriod createObligationPeriod(String identifer, YearMonthDay begin, YearMonthDay end) {
        return ObligationPeriod.newBuilder()
                .setIdentifier(identifer)
                .setBegin(begin)
                .setEnd(end)
                .build();
    }

    private static Ucap createUcap(String assetId, String assetName, int lower, int upper, int value,
                                   Ucap.CalculationMethod calcMethod, float performanceFactor, int maxCapacity) {


        return Ucap.newBuilder()
                .setCapacityAssetId(assetId)
                .setCapacityAssetName(assetName)
                .setLower(lower)
                .setUpper(upper)
                .setCalculatedValue(value)
                .setCalculationMethod(calcMethod)
                .setPerformanceFactor(performanceFactor)
                .setMaximumCapacityInMw(maxCapacity)
                .build();
    }

}
