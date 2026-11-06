package com.de200319.mvc.controller;

import com.de200319.mvc.dto.DeviceDTO;
import com.de200319.mvc.entity.Device;
import com.de200319.mvc.service.CategoryService;
import com.de200319.mvc.service.DeviceService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/")
public class DeviceController {

    private final DeviceService deviceService;
    private final CategoryService categoryService;

    public DeviceController(DeviceService deviceService, CategoryService categoryService) {
        this.deviceService = deviceService;
        this.categoryService = categoryService;
    }

    private Map<String, String> statusOptions() {
        Map<String, String> statuses = new LinkedHashMap<>();
        statuses.put("AC", "AC-Active");
        statuses.put("IN", "IN-Inactive");
        statuses.put("DR", "DR-Draft");
        return statuses;
    }

    // Q1. Smart Device List (screen 1)
    @GetMapping
    public String list(@RequestParam(value = "deviceName", required = false) String deviceName, Model model) {
        String keyword = deviceName == null ? null : deviceName.trim();
        List<Device> devices = (keyword == null || keyword.isEmpty())
                ? deviceService.getAllDevices()
                : deviceService.searchByName(keyword);
        model.addAttribute("devices", devices);
        model.addAttribute("keyword", keyword);
        return "device-list";
    }

    // Q2 - Delete function (Screen 2 confirm handled in device-list.html via modal)
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            deviceService.deleteDevice(id);
            redirectAttributes.addFlashAttribute("successMessage", "Deleted successfully");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Device not found");
        }
        return "redirect:/";
    }

    // Q3.1 - Add New Device (Screen 3), blank form
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("DeviceDTO", new DeviceDTO());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("statuses", statusOptions());
        return "device-add";
    }

    // Q3.2 - Save
    @PostMapping("/add")
    public String save(@Valid @ModelAttribute("DeviceDTO") DeviceDTO deviceForm,
                       BindingResult bindingResult, Model model) {

        // Q1.5 / Q3 Screen Definition - trim leading/trailing spaces before validating
        if (deviceForm.getDeviceCode() != null) {
            deviceForm.setDeviceCode(deviceForm.getDeviceCode().trim());
        }
        if (deviceForm.getDeviceName() != null) {
            deviceForm.setDeviceName(deviceForm.getDeviceName().trim());
        }

        deviceService.validateBusinessRules(deviceForm, bindingResult);

        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("statuses", statusOptions());

        if (bindingResult.hasErrors()) {
            return "device-add";
        }

        deviceService.createDevice(deviceForm);

        // Q3.2.a - clear the form and show success message
        model.addAttribute("DeviceDTO", new DeviceDTO());
        model.addAttribute("successMessage", "Created new device successfully");
        return "device-add";
    }

    // Q4 - View Device Details (Screen 4)
    @GetMapping("/view/{id}")
    public String view(@PathVariable("id") Integer id, Model model) {
        Device device = deviceService.getById(id);
        if (device == null) {
            model.addAttribute("errorMessage", "Device not found");
        } else {
            model.addAttribute("device", device);
        }
        return "device-view";
    }
}