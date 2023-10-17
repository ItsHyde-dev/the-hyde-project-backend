package com.theHydeProject.components.widgets;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.theHydeProject.components.widgets.dto.CreateWidgetDto;
import com.theHydeProject.components.widgets.dto.UpdateWidgetDto;
import com.theHydeProject.models.Users;
import com.theHydeProject.models.WidgetData;
import com.theHydeProject.models.Widgets;
import com.theHydeProject.repositories.UserRepository;
import com.theHydeProject.repositories.WidgetDataRepository;
import com.theHydeProject.repositories.WidgetRepository;
import com.theHydeProject.utils.JwtUtil;
import com.theHydeProject.utils.ResponseBody;
import com.theHydeProject.utils.ResponseBuilderFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/widgets")
public class WidgetController {

    @Autowired
    ResponseBuilderFactory response;
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    WidgetRepository widgetRepo;
    @Autowired
    UserRepository userRepo;
    @Autowired
    WidgetDataRepository widgetDataRepo;

    @GetMapping("/getWidgets")
    public ResponseEntity<ResponseBody> getWidgets(HttpServletRequest request) {

        Long userId = jwtUtil.getUserId(request);
        List<WidgetData> widgets = widgetDataRepo.findAllByUser_Id(userId);

        return response
                .builder()
                .status(HttpStatus.OK)
                .message("Successfully Fetched Widgets")
                .addToBody("widgets", widgets)
                .build();
    }

    @GetMapping("/getWidgetTypes")
    public ResponseEntity<ResponseBody> getWidgetTypes() {
        List<Widgets> widgetTypes = widgetRepo.findAll();

        return response.builder()
                .status(HttpStatus.OK)
                .message("Successfully fetched widget types")
                .addToBody("widgets", widgetTypes)
                .build();

    }

    public void updatePosition(
            Long userId,
            Long widgetId,
            int newPosition) {

        int currentPosition = widgetDataRepo.findCurrentPosition(widgetId);
        int maxPossiblePosition = widgetDataRepo.findMaxPossiblePosition(userId);

        WidgetData widget = widgetDataRepo.findById(widgetId).get();
        if (newPosition < 0 || newPosition > maxPossiblePosition)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Position");

        if (currentPosition == newPosition)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Old and new position cannot be the same");

        this.handleShiftElements(currentPosition, newPosition);

        widget.setPosition(newPosition);
        widgetDataRepo.save(widget);

    }

    private void handleShiftElements(int currentPosition, int newPosition) {
        if (currentPosition < newPosition)
            widgetDataRepo.incrementRange(currentPosition, newPosition, -1);
        if (currentPosition > newPosition)
            widgetDataRepo.incrementRange(currentPosition, newPosition, 1);
    }

    @PostMapping("/createWidget")
    public ResponseEntity<ResponseBody> createWidget(@Valid @RequestBody CreateWidgetDto body) {

        Users user = userRepo.findById(body.userId).get();
        Widgets widget = widgetRepo.findById(body.widgetId).get();

        WidgetData newWidget = new WidgetData(widget, body.data, body.position, user);

        widgetDataRepo.save(newWidget);

        return response.builder()
                .status(HttpStatus.OK)
                .message("Successfully Created Widget")
                .build();

    }

    @PostMapping("/update")
    public ResponseEntity<ResponseBody> updateData(
            HttpServletRequest request,
            @Valid @RequestBody UpdateWidgetDto body) {

        WidgetData widget = widgetDataRepo.findById(body.widgetId).get();
        Long userId = jwtUtil.getUserId(request);

        if (body.data != null) {
            widget.setData(body.data);
            widgetDataRepo.save(widget);
        }
        if (body.position != null)
            this.updatePosition(userId, body.widgetId, body.position);

        return response.builder().status(HttpStatus.OK).build();

    }

}
