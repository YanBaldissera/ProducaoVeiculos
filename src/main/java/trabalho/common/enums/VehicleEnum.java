package trabalho.common.enums;

public enum VehicleEnum {
    SUV("SUV"),
    SEDAN("Sedan");

    private String description;

    VehicleEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}
