package org.example.gestionproduit.util;

public class MapBridge {
    private String selectedLocation = "";

    public void onLocationSelected(String coords) {
        System.out.println("📍 Selected coordinates: " + coords);
        this.selectedLocation = coords;
    }

    public String getSelectedLocation() {
        return selectedLocation;
    }
}
