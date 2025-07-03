package com.alerts.system;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;

public class MissingPersonService {
    private static ObservableList<MissingPerson> missingPersons = FXCollections.observableArrayList();

    // Static initializer block to add some sample data on application startup
    static {
        missingPersons.add(new MissingPerson(
                "Alice Wonderland",
                10,
                "Female",
                "Wonderland Park",
                LocalDate.of(2023, 10, 20),
                "White Rabbit",
                "555-111-2222",
                "Wearing a blue dress and a white apron. Has blonde hair.",
                "/com/alerts/system/placeholder_profile.jpg", // Example path for a placeholder
                "Missing"
        ));
        missingPersons.add(new MissingPerson(
                "Bob The Builder",
                45,
                "Male",
                "Construction Site, Downtown",
                LocalDate.of(2024, 1, 5),
                "Wendy",
                "555-333-4444",
                "Wearing a yellow hard hat and orange overalls.",
                "/com/alerts/system/placeholder_profile.jpg",
                "Missing"
        ));
        missingPersons.add(new MissingPerson(
                "Charlie Chaplin",
                88,
                "Male",
                "Old Film Studio",
                LocalDate.of(2022, 7, 1),
                "Usher",
                "555-555-6666",
                "Known for his small mustache, bowler hat, and cane.",
                "/com/alerts/system/placeholder_profile.jpg",
                "Found" // Example of a 'found' person
        ));
    }

    public static ObservableList<MissingPerson> getAllMissingPersons() {
        return missingPersons;
    }

    public static void addMissingPerson(MissingPerson person) {
        missingPersons.add(person);
        // In a production app, you would log this to a proper logging framework
    }

    public static void removeMissingPerson(MissingPerson person) {
        missingPersons.remove(person);
        // In a production app, you would log this to a proper logging framework
    }

    // You could add methods for searching, updating, etc.
    public static ObservableList<MissingPerson> searchMissingPersons(String query) {
        ObservableList<MissingPerson> filteredList = FXCollections.observableArrayList();
        String lowerCaseQuery = query.toLowerCase();
        for (MissingPerson person : missingPersons) {
            if (person.getName().toLowerCase().contains(lowerCaseQuery) ||
                    person.getLastSeenLocation().toLowerCase().contains(lowerCaseQuery) ||
                    person.getDescription().toLowerCase().contains(lowerCaseQuery)) {
                filteredList.add(person);
            }
        }
        return filteredList;
    }
}