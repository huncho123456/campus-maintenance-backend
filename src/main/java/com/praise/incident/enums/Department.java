package com.praise.incident.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Department {
    @JsonProperty("Physical Planning and Development Unit")
    PPDU,

    @JsonProperty("Electrical Maintenance Unit")
    ELECTRICAL_MAINTENANCE,

    @JsonProperty("Plumbing and Water Maintenance Unit")
    PLUMBING_AND_WATER,

    @JsonProperty("Environmental Health and Sanitation Unit")
    ENVIRONMENTAL_SANITATION,

    @JsonProperty("ICT / Network Support Unit")
    ICT_NETWORK_SUPPORT,

    @JsonProperty("Hostel Maintenance Unit")
    HOSTEL_MAINTENANCE,

    @JsonProperty("Grounds and Drainage Maintenance Unit")
    GROUNDS_AND_DRAINAGE

}
