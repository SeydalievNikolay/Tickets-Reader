package org.example.seydaliev;
import lombok.SneakyThrows;
import org.example.seydaliev.model.Ticket;
import org.example.seydaliev.service.TicketService;
import org.example.seydaliev.service.ParserJson;

import java.util.List;

public class Main {
    public static final String PATH = "src/main/resources/tickets.json";
    @SneakyThrows
    public static void main(String[] args) {

        String json = ParserJson.readJson(PATH);

        List<Ticket> tickets = ParserJson.jsonToTickets(json);

        System.out.println(TicketService.getResult(tickets));

    }
}
