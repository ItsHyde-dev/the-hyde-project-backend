package com.theHydeProject.components.widgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
import com.theHydeProject.utils.ResponseBuilder;
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
    Users user = userRepo.findById(userId).get();
    List<WidgetData> widgets = widgetDataRepo.findAllByUserId(userId);

    Map<Long, List<WidgetData>> widgetGroups = new HashMap<>();

    for (WidgetData widget : widgets) {
      if (widget.getLinkId() == null) {
        if (!widgetGroups.containsKey(widget.getId()))
          widgetGroups.put(widget.getId(), new ArrayList<>());

        widgetGroups.get(widget.getId()).add(widget);
      } else {
        if (!widgetGroups.containsKey(widget.getLinkId()))
          widgetGroups.put(widget.getLinkId(), new ArrayList<>());

        widgetGroups.get(widget.getLinkId()).add(widget);
      }
    }

    return response
        .builder()
        .message("Successfully Fetched Widgets")
        .addToBody("widgetGroups", widgetGroups)
        .addToBody("layouts", user.getLayouts())
        .build();
  }

  @GetMapping("/getWidgetTypes")
  public ResponseEntity<ResponseBody> getWidgetTypes() {
    List<Widgets> widgetTypes = widgetRepo.findAll();

    List<Map<String, String>> responseWidgets = new ArrayList<>();

    for (Widgets widgetType : widgetTypes) {
      Map<String, String> widgetTypesJson = new HashMap<>();
      widgetTypesJson.put("id", widgetType.getId().toString());
      widgetTypesJson.put("name", widgetType.getName());
      widgetTypesJson.put("type", widgetType.getType().toString());

      responseWidgets.add(widgetTypesJson);
    }

    return response.builder()
        .message("Successfully fetched widget types")
        .addToBody("widgets", responseWidgets)
        .build();

  }

  @PostMapping("/updateLayout")
  public ResponseEntity<ResponseBody> updateLayout(@RequestBody Map<String, Object> body,
      HttpServletRequest request) {
    // update the data in the database
    Long userId = jwtUtil.getUserId(request);

    Users user = userRepo.findById(userId).get();
    String layouts = body.get("layouts").toString();

    user.setLayouts(layouts);
    userRepo.save(user);

    System.out.println("Saved layouts for the user");

    return response.builder().status(HttpStatus.OK).build();
  }

  @PostMapping("/createWidget")
  public ResponseEntity<ResponseBody> createWidget(@Valid @RequestBody CreateWidgetDto body,
      HttpServletRequest request) {

    Long userId = jwtUtil.getUserId(request);

    Users user = userRepo.findById(userId).get();

    System.out.println("body: " + body.toString());

    Optional<Widgets> widget = widgetRepo.findById(Long.parseLong(body.widgetId));
    if (!widget.isPresent())
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Widget not found");

    WidgetData newWidget = new WidgetData();
    newWidget.setWidget(widget.get());

    if (body.data != null && !body.data.isEmpty())
      newWidget.setData(body.data);
    else
      newWidget.setData("{}");

    newWidget.setUser(user);

    if (body.linkWidgetId != null && body.linkWidgetId.trim() != "") {
      System.out.println("linkWidgetId: " + body.linkWidgetId);
      newWidget.setLinkId(Long.parseLong(body.linkWidgetId));
    }

    if (body.name == null || body.name.isBlank()) {
      body.name = newWidget.getWidget().getName() + " Widget";
    }

    newWidget.setName(body.name);

    System.out.println("newWidget: " + newWidget.toString());

    widgetDataRepo.save(newWidget);
    System.out.println("Created widget data");

    return response.builder()
        .status(HttpStatus.OK)
        .message("Successfully Created Widget")
        .build();

  }

  @PostMapping("/update")
  public ResponseEntity<ResponseBody> updateData(
      @Valid @RequestBody UpdateWidgetDto body,
      HttpServletRequest request) {

    WidgetData widget = widgetDataRepo.findById(Long.parseLong(body.widgetId.toString())).get();
    // Long userId = jwtUtil.getUserId(request);

    if (body.data != null) {
      widget.setData(body.data.toString());
      widgetDataRepo.save(widget);
    }

    return response.builder().status(HttpStatus.OK).build();

  }

  @GetMapping("/getData/{widgetId}")
  public ResponseEntity<ResponseBody> getData(
      HttpServletRequest request,
      @RequestParam("widgetId") Long widgetId) {

    Long userId = jwtUtil.getUserId(request);
    WidgetData data = widgetDataRepo.findByUserIdAndWidgetId(userId, widgetId);

    ResponseBuilder res = response.builder();
    res.addToBody("widgetData", data);

    return res
        .message("successfully fetched widget data")
        .build();
  }

  @PostMapping("/delete")
  public ResponseEntity<ResponseBody> deleteWidget(
      HttpServletRequest request,
      @RequestBody Map<String, Object> body) {

    Long widgetId = Long.parseLong(body.get("widgetId").toString());
    Long userId = jwtUtil.getUserId(request);

    widgetDataRepo.deleteByUserIdAndWidgetId(userId, widgetId);

    return response.builder().build();
  }

  @PostMapping("/rename")
  public ResponseEntity<ResponseBody> renameWidget(
      HttpServletRequest request,
      @RequestBody Map<String, Object> body) {

    Long widgetId = Long.parseLong(body.get("widgetId").toString());
    String name = body.get("widgetName").toString();
    Long userId = jwtUtil.getUserId(request);

    widgetDataRepo.updateNameByUserIdAndWidgetId(userId, widgetId, name);

    return response.builder().build();
  }

}
