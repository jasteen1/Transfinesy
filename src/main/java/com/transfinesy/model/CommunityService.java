package com.transfinesy.model;

import java.time.LocalDate;

/**
 * Represents a community service record in the system.
 * 
 * This class models community service hours rendered by students, which can be
 * converted to monetary credits that reduce their outstanding balance.
 * 
 * Key Features:
 * - Tracks hours rendered (hoursRendered)
 * - Converts hours to credit amount (creditAmount)
 * - Conversion rate: Typically 1 hour = ₱50 (configurable)
 * - Includes description of service performed
 * - Linked to a specific student (studID)
 * 
 * Credit Conversion:
 * - Hours are converted to credits when recorded
 * - Credits are applied to student ledger as negative transactions
 * - Formula: creditAmount = hoursRendered × hourlyRate
 * 
 * Usage:
 * - Students can render community service to offset fines
 * - Service credits reduce outstanding balance
 * - Tracked separately from regular payments
 * 
 * @author transFINESy Development Team
 */
public class CommunityService {
    private String serviceID;
    private String studID;
    private int hoursRendered;
    private double creditAmount;
    private LocalDate date;
    private String description; // Description/type of service performed

    public CommunityService() {
    }

    public CommunityService(String serviceID, String studID, int hoursRendered, double creditAmount, LocalDate date) {
        this.serviceID = serviceID;
        this.studID = studID;
        this.hoursRendered = hoursRendered;
        this.creditAmount = creditAmount;
        this.date = date;
    }

    public CommunityService(String serviceID, String studID, int hoursRendered, double creditAmount, LocalDate date, String description) {
        this.serviceID = serviceID;
        this.studID = studID;
        this.hoursRendered = hoursRendered;
        this.creditAmount = creditAmount;
        this.date = date;
        this.description = description;
    }

    public String getServiceID() {
        return serviceID;
    }

    public void setServiceID(String serviceID) {
        this.serviceID = serviceID;
    }

    public String getStudID() {
        return studID;
    }

    public void setStudID(String studID) {
        this.studID = studID;
    }

    public int getHoursRendered() {
        return hoursRendered;
    }

    public void setHoursRendered(int hoursRendered) {
        this.hoursRendered = hoursRendered;
    }

    public double getCreditAmount() {
        return creditAmount;
    }

    public void setCreditAmount(double creditAmount) {
        this.creditAmount = creditAmount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "CommunityService{" +
                "serviceID='" + serviceID + '\'' +
                ", studID='" + studID + '\'' +
                ", hoursRendered=" + hoursRendered +
                ", creditAmount=" + creditAmount +
                ", date=" + date +
                '}';
    }
}

