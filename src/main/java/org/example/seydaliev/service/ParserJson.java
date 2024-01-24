package org.example.seydaliev.service;

import com.google.gson.Gson;
import org.example.seydaliev.model.Ticket;
import org.example.seydaliev.model.Tickets;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public abstract class ParserJson {

    public static String readJson(String pathToJson) {
        try {
            return Files.readString(Path.of(pathToJson)).trim().replaceFirst("\ufeff", "");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Ticket> jsonToTickets(String json) {
        Tickets ticketsCollections = new Gson().fromJson(json, Tickets.class);
        return ticketsCollections.getTickets();
    }


}