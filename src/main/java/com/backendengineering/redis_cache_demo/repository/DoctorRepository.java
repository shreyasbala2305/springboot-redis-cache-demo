
package com.backendengineering.redis_cache_demo.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.backendengineering.redis_cache_demo.model.Doctor;

@Repository
public class DoctorRepository {

    private final Map<Long, Doctor> doctors = new ConcurrentHashMap<>();

    public DoctorRepository() {
        doctors.put(101L,
                new Doctor(101L, "Dr. Sharma", "Cardiology", 800));

        doctors.put(102L,
                new Doctor(102L, "Dr. Mehta", "Dermatology", 600));

        doctors.put(103L,
                new Doctor(103L, "Dr. Patil", "Neurology", 1000));
    }

    public Doctor findById(Long id) {
        return doctors.get(id);
    }

    public void update(Doctor doctor) {
        doctors.put(doctor.getId(), doctor);
    }

    public int count() {
        return doctors.size();
    }
}
