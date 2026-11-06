package com.de200319.mvc.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * DeviceDTO backing the "Add New Device" form
 * Installation date is kept as a raw String (dd/MM/yyyy) so the format
 * and "must be a future date" business rule can be validated with a
 * custom, field-specific error message.
 */
public class DeviceDTO {

    @NotBlank(message = "Device Code is required")
    @Pattern(regexp = "^[A-Z]{3}[0-9]{3}$",
            message = "Device Code must start with 3 uppercase letters followed by 3 digits (e.g., DVC101)")
    private String deviceCode;

    @NotBlank(message = "Device Name is required")
    @Size(max = 200, message = "Device Name must not exceed 200 characters")
    private String deviceName;

    @NotNull(message = "Category is required")
    private Integer categoryId;

    // DB column devices.price is decimal(10) i.e. no decimal places; SQL Server rounds any
    // fractional input on insert, so whole numbers are recommended.
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be a number greater than 0")
    private BigDecimal price;

    @NotNull(message = "Warranty is required")
    @Min(value = 1, message = "Warranty must be a positive integer between 1 and 60")
    @Max(value = 60, message = "Warranty must be a positive integer between 1 and 60")
    private Integer warrantyMonths;

    @NotBlank(message = "Installation Date is required")
    private String installationDate;

    @NotBlank(message = "Status is required")
    private String status;

    public DeviceDTO() {
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getWarrantyMonths() {
        return warrantyMonths;
    }

    public void setWarrantyMonths(Integer warrantyMonths) {
        this.warrantyMonths = warrantyMonths;
    }

    public String getInstallationDate() {
        return installationDate;
    }

    public void setInstallationDate(String installationDate) {
        this.installationDate = installationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}