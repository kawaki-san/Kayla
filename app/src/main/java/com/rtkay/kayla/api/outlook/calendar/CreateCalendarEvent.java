package com.rtkay.kayla.api.outlook.calendar;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.Scanner;

public class CreateCalendarEvent {
    private static void createEvent(String accessToken, String timeZone, Scanner input) {
        DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("M/d/yyyy h:mm a");

        // Prompt for subject
        String subject = "";
        while (subject.isBlank()) {
            System.out.print("Subject (required): ");
            subject = input.nextLine();
        }

        // Prompt for start date/time
        LocalDateTime start = null;
        while (start == null) {
            System.out.print("Start (mm/dd/yyyy hh:mm AM/PM): ");
            String date = input.nextLine();

            try {
                start = LocalDateTime.parse(date, inputFormat);
            } catch (DateTimeParseException dtp) {
                System.out.println("Invalid input, try again.");
            }
        }

        // Prompt for end date/time
        LocalDateTime end = null;
        while (end == null) {
            System.out.print("End (mm/dd/yyyy hh:mm AM/PM): ");
            String date = input.nextLine();

            try {
                end = LocalDateTime.parse(date, inputFormat);
            } catch (DateTimeParseException dtp) {
                System.out.println("Invalid input, try again.");
            }

            if (end.isBefore(start)) {
                System.out.println("End time must be after start time.");
                end = null;
            }
        }

        // Prompt for attendees
        HashSet<String> attendees = new HashSet<String>();
        System.out.print("Would you like to add attendees? (y/n): ");
        if (input.nextLine().trim().toLowerCase().startsWith("y")) {
            String attendee = "";
            do {
                System.out.print("Enter an email address (leave blank to finalize the list): ");
                attendee = input.nextLine();

                if (!attendee.isBlank()) {
                    attendees.add(attendee);
                }
            } while (!attendee.isBlank());
        }

        // Prompt for body
        String body = null;
        System.out.print("Would you like to add a body? (y/n): ");
        if (input.nextLine().trim().toLowerCase().startsWith("y")) {
            System.out.print("Enter a body: ");
            body = input.nextLine();
        }

        // Confirm input
        System.out.println();
        System.out.println("New event:");
        System.out.println("Subject: " + subject);
        System.out.println("Start: " + start.format(inputFormat));
        System.out.println("End: " + end.format(inputFormat));
        System.out.println("Attendees: " + (attendees.size() > 0 ? attendees.toString() : "NONE"));
        System.out.println("Body: " + (body == null ? "NONE" : body));

        System.out.print("Is this correct? (y/n): ");
        if (input.nextLine().trim().toLowerCase().startsWith("y")) {
            Graph.createEvent(accessToken, timeZone, subject, start, end, attendees, body);
            System.out.println("Event created.");
        } else {
            System.out.println("Canceling.");
        }

        System.out.println();
    }
}