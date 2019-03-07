
package ca.aeso.capacitymarket;

import static ca.aeso.capacitymarket.ucap.UcapResultDetail.*;
import static org.junit.Assert.*;

import ca.aeso.capacitymarket.ucap.*;
import com.google.protobuf.Message;
import com.google.protobuf.Timestamp;
import org.junit.*;
import ca.aeso.type.YearMonthDay;
import com.google.protobuf.util.JsonFormat;

import static ca.aeso.capacitymarket.ucap.UcapResultDetail.CalculationMethod.AVAILABLITY_FACTOR;
import static ca.aeso.capacitymarket.ucap.UcapResultDetail.CalculationMethod.CAPACITY_FACTOR;

public class SampleMessagesTest {


    @Test
    public void displaySampleUcapCalculationResults() throws Exception{

        YearMonthDay date_2019_11_01 = createDate(2019, 11, 01);
        YearMonthDay date_2020_10_31 = createDate(2020, 10, 31);
        long millis = System.currentTimeMillis();
        Timestamp now = Timestamp.newBuilder()
            .setSeconds(millis / 1000)
            .build();

        ObligationPeriod period = ObligationPeriod.newBuilder()
                .setBegin(date_2019_11_01)
                .setEnd(date_2020_10_31)
                .setHistoricalBegin(createDate(2013, 11, 01))
                .setHistoricalEnd(createDate(2018, 10, 31))
                .setAuctionType(ObligationPeriod.AuctionType.BASE_AUCTION)
                .build();

        UcapResults ucapResults = UcapResults.newBuilder()
                .setObligationPeriod(period)
                .setAudit(Audit.newBuilder()
                        .setApprovedBy("Staff Member")
                        .setApprovedOn(now)
                        .setExportedOn(now)
                        .build())
                .addDetails(createUcapResultDetail("BIG", 97, 103, 99, AVAILABLITY_FACTOR,
                        0.88f, 120))
                .addDetails(createUcapResultDetail( "ABC", 50, 80, 61, CAPACITY_FACTOR,
                        0.99f, 100))
                .build();


        String jsonOutput = JsonFormat.printer().print(ucapResults);

        // Rehydrate from json
        UcapResultsOrBuilder builder = UcapResults.newBuilder();
        JsonFormat.parser().merge(jsonOutput, (Message.Builder) builder);
        UcapResults rehydratedUcapResults = ((UcapResults.Builder) builder).build();
        assertSameUcapResults(ucapResults, rehydratedUcapResults);

        System.out.println("UcapResults:" + System.lineSeparator() +
                jsonOutput);

    }

    private void assertSameUcapResults(UcapResults expected, UcapResults actual) {

        assertEquals(expected.getObligationPeriod().getBegin(), actual.getObligationPeriod().getBegin());
        assertEquals(expected.getObligationPeriod().getEnd(), actual.getObligationPeriod().getEnd());
        assertEquals(expected.getDetailsCount(), actual.getDetailsCount());
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

    private static UcapResultDetail createUcapResultDetail(String assetName, int lower, int upper, int value,
                                   CalculationMethod calcMethod, float performanceFactor, int maxCapacity) {

        return newBuilder()
                .setAssetShortName(assetName)
                .setUcapLower(lower)
                .setUcapUpper(upper)
                .setUcapPoint(value)
                .setCalculationMethod(calcMethod)
                .setPerformanceFactor(performanceFactor)
                .setMaximumCapacityInMw(maxCapacity)
                .build();
    }

}
