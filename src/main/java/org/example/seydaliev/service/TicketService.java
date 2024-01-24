package org.example.seydaliev.service;

import org.example.seydaliev.model.Ticket;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TicketService {
    private static final String VVO = "VVO";
    private static final String TLV = "TLV";
    private static final String VLADIVOSTOK_ZONE = "+10";
    private static final String TEL_AVIV_ZONE = "+3";
    public static final String DEFAULT_ZONE = "+0";

    public static ZonedDateTime getTime(String formattedDate, String formattedTime, String airport) {

        ZoneId zone = ZoneOffset.of(getOffset(airport));
        String unitedDate = formattedDate + "T" + formattedTime;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy'T'H:mm").withZone(zone);

        return ZonedDateTime.parse(unitedDate, formatter);
    }

    public static String getOffset(String airport) {

        return switch (airport) {
            case (VVO) -> VLADIVOSTOK_ZONE;
            case (TLV) -> TEL_AVIV_ZONE;
            default -> DEFAULT_ZONE;
        };
    }


    public static Duration getFlyTime(Ticket ticket) {
        ZonedDateTime departureTime = getTime(
                ticket.departure_date(),
                ticket.departure_time(),
                ticket.origin());
        ZonedDateTime arrivalTime = getTime(
                ticket.arrival_date(),
                ticket.arrival_time(),
                ticket.destination());

        return Duration.between(departureTime, arrivalTime);
    }


    public static Map<String, Duration> getMinFlyTime(List<Ticket> tickets) {
        Map<String, Duration> minFlyByCarriers = new HashMap<>();

        for (Ticket ticket : tickets) {

            String originAirport = ticket.origin();
            String destinationAirport = ticket.destination();


            if (originAirport.equals(VVO) && destinationAirport.equals(TLV)) {
                String carrier = ticket.carrier();
                Duration duration = getFlyTime(ticket);
                int price = ticket.price();

                if (minFlyByCarriers.containsKey(carrier)) {
                    if (minFlyByCarriers.get(carrier).compareTo(duration) > 0) {
                        minFlyByCarriers.put(carrier, duration);
                    }
                } else {
                    minFlyByCarriers.put(carrier, duration);
                }
            }
        }
        return minFlyByCarriers;
    }


    public static Double getAveragePrice(List<Ticket> tickets) {
        List<Ticket> vvoAndTlvTickets = getAllTickets(tickets);
        return vvoAndTlvTickets.stream()
                .collect(Collectors.averagingInt(Ticket::price));
    }

    public static Double getMedianPrice(List<Ticket> tickets) {
        List<Ticket> ticketList = getAllTickets(tickets);

        double median = ticketList.get(ticketList.size() / 2).price();
        if (ticketList.size() % 2 == 0) {
            return (median + ticketList.get(ticketList.size() / 2 - 1).price()) / 2;
        }

        return median;
    }

    public static List<Ticket> getAllTickets(List<Ticket> tickets) {
        return tickets.stream()
                .filter(ticket -> ticket.origin().equals(VVO))
                .filter(ticket -> ticket.destination().equals(TLV))
                .sorted(Comparator.comparingDouble(Ticket::price))
                .toList();
    }

    public static String getResult(List<Ticket> tickets) {

        StringBuffer result = new StringBuffer();

        Map<String, Duration> minFlyByCarriers = getMinFlyTime(tickets);

        minFlyByCarriers.forEach((k, v) -> {
            result.append("Минимальное время полета между городами Владивосток и Тель-Авив для авиаперевозчика ");
            result.append(k);
            result.append(" cоставляет ");
            result.append(v.toHours());
            result.append(" ч ");
            result.append(v.toMinutes() % 60);
            result.append(" мин. \n");
        });

        Double averagePrice = getAveragePrice(tickets);
        Double medianPrice = getMedianPrice(tickets);

        result.append("\n");
        result.append("Разница между средней ценой и медианой для полета между городами Владивосток и Тель-Авив - ");
        result.append(averagePrice - medianPrice);

        return result.toString();
    }
}
