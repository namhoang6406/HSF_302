package com.de200319.mvc.service;

import com.de200319.mvc.dto.DeviceDTO;
import com.de200319.mvc.entity.Device;
import org.springframework.validation.BindingResult;

import java.util.List;

public interface DeviceService {
    // Q1.1 - list ordered by Installation Date descending
    List<Device> getAllDevices();

    // Q1.2 - wildcard search by device name, ordered by Installation Date
    List<Device> searchByName(String keyword);

    // Q4 - view a single device, returns null when not found
    Device getById(Integer id);

    // Q3.2 - business-rule validation not covered by Bean Validation annotations
    // (unique Device Code, existing Category, Installation Date format + future date)
    void validateBusinessRules(DeviceDTO deviceDTO, BindingResult bindingResult);

    // Q3.2.a - persist a new device
    void createDevice(DeviceDTO deviceDTO);

    // Q2.2 - delete a device; throws IllegalStateException when device status is Active (AC)
    void deleteDevice(Integer id);
}