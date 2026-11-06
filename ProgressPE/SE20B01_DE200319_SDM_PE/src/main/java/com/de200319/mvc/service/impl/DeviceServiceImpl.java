package com.de200319.mvc.service.impl;

import com.de200319.mvc.dto.DeviceDTO;
import com.de200319.mvc.entity.Category;
import com.de200319.mvc.entity.Device;
import com.de200319.mvc.repository.CategoryRepository;
import com.de200319.mvc.repository.DeviceRepository;
import com.de200319.mvc.service.DeviceService;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@Service
public class DeviceServiceImpl implements DeviceService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DeviceRepository deviceRepository;
    private final CategoryRepository categoryRepository;

    public DeviceServiceImpl(DeviceRepository deviceRepository, CategoryRepository categoryRepository) {
        this.deviceRepository = deviceRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Device> getAllDevices() {
        return deviceRepository.findAllByOrderByInstallationDateDesc(); // gọi phương thức tìm kiếm tất cả thiết bị và sắp xếp giảm dần theo InstallationDate[cite: 7]
    }

    @Override
    public List<Device> searchByName(String keyword) {
        return deviceRepository.findByDeviceNameContainingIgnoreCaseOrderByInstallationDateDesc(keyword); // gọi phương thức tìm kiếm theo tên thiết bị và sắp xếp giảm dần theo InstallationDate[cite: 7]
    }

    @Override
    public Device getById(Integer id) {
        return deviceRepository.findById(id).orElse(null); // gọi phương thức tìm kiếm thiết bị theo id, nếu không tìm thấy trả về null[cite: 7]
    }

    @Override
    public void validateBusinessRules(DeviceDTO deviceDTO, BindingResult bindingResult) {
        // Device code duy nhất (unique), check format đã valid[cite: 7]
        if (deviceDTO.getDeviceCode() != null && !deviceDTO.getDeviceCode().isBlank() && !bindingResult.hasFieldErrors("deviceCode")) {
            Optional<Device> existing = deviceRepository.findByDeviceCode(deviceDTO.getDeviceCode());
            if (existing.isPresent()) {
                bindingResult.addError(new FieldError("deviceDTO", "deviceCode", "Device Code already exists"));
            }
        }

        // Category must exist in the database[cite: 7]
        if (deviceDTO.getCategoryId() != null) {
            if (!categoryRepository.existsById(deviceDTO.getCategoryId())) {
                bindingResult.addError(new FieldError("deviceDTO", "categoryId", "Selected category does not exist"));
            }
        }

        // Installation date: dd/MM/yyyy format + Ngay cai dat la ngay trong tuong lai[cite: 7]
        if (deviceDTO.getInstallationDate() != null && !deviceDTO.getInstallationDate().isBlank()
                && !bindingResult.hasFieldErrors("installationDate")) {
            try {
                LocalDate installationDate = LocalDate.parse(deviceDTO.getInstallationDate().trim(), DATE_FORMATTER);
                if (!installationDate.isAfter(LocalDate.now())) {
                    bindingResult.addError(new FieldError("deviceDTO", "installationDate", "Installation date must be a future date"));
                }
            } catch (DateTimeParseException e) {
                bindingResult.addError(new FieldError("deviceDTO", "installationDate", "Invalid date format. Please use dd/MM/yyyy"));
            }
        }
    }

    @Override
    public void createDevice(DeviceDTO deviceDTO) {
        Category category = categoryRepository.findById(deviceDTO.getCategoryId()).orElseThrow(
                () -> new IllegalArgumentException("Category not found with id: " + deviceDTO.getCategoryId())
        );

        Device device = new Device();
        device.setDeviceCode(deviceDTO.getDeviceCode().trim());
        device.setDeviceName(deviceDTO.getDeviceName().trim());
        device.setPrice(deviceDTO.getPrice());
        device.setWarrantyMonths(deviceDTO.getWarrantyMonths());
        device.setInstallationDate(LocalDate.parse(deviceDTO.getInstallationDate().trim(), DATE_FORMATTER));
        device.setCategory(category);
        device.setStatus(deviceDTO.getStatus());

        deviceRepository.save(device);
    }

    @Override
    public void deleteDevice(Integer id) {
        Device device = deviceRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Device not found with id: " + id)
        );
        // Q2.2 - delete a device; throws IllegalStateException when device status is Active (AC)[cite: 7]
        if ("AC".equalsIgnoreCase(device.getStatus())) {
            throw new IllegalStateException("Cannot delete an active device");
        }
        deviceRepository.deleteById(id);
    }
}