package ru.radzvbov.rpbo.tutoring.controller;

import ru.radzvbov.rpbo.tutoring.domain.Course;
import ru.radzvbov.rpbo.tutoring.service.CourseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public List<Course> getAll() {
        return courseService.getAll();
    }

    @GetMapping("/{id}")
    public Course getById(@PathVariable long id) {
        return courseService.getById(id);
    }

    @PostMapping
    public Course create(@RequestBody Course course) {
        return courseService.create(course);
    }

    @PutMapping("/{id}")
    public Course update(@PathVariable long id, @RequestBody Course course) {
        return courseService.update(id, course);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable long id) {
        return courseService.delete(id) ? "Deleted" : "Not found";
    }
}