
package com.backendengineering.redis_cache_demo.model;

public class Doctor {

    private Long id;
    private String name;
    private String specialization;
    private double consultationFee;

    public Doctor() {
    }

    public Doctor(Long id, String name, String specialization,
                  double consultationFee) {
        this.id = id;
        this.name = name;
        this.specialization = specialization;
        this.consultationFee = consultationFee;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public double getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(double consultationFee) {
        this.consultationFee = consultationFee;
    }
}
