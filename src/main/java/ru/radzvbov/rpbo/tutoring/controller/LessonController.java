package ru.radzvbov.rpbo.tutoring.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.radzvbov.rpbo.tutoring.domain.Lesson;
import ru.radzvbov.rpbo.tutoring.service.LessonService;

import java.util.List;

@RestController
@RequestMapping("/api/lessons")
public class LessonController {

    private final LessonService lessonService;

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    // GET /api/lessons?courseId=1
    @GetMapping
    public List<Lesson> getAll(@RequestParam(required = false) Long courseId) {
        if (courseId != null) {
            return lessonService.getByCourseId(courseId);
        }
        return lessonService.getAll();
    }

    // GET /api/lessons/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Lesson> getById(@PathVariable long id) {
        Lesson lesson = lessonService.getById(id);
        if (lesson == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(lesson);
    }

    // POST /api/lessons
    @PostMapping
    public Lesson create(@RequestBody Lesson lesson) {
        return lessonService.create(lesson);
    }

    // PUT /api/lessons/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Lesson> update(@PathVariable long id,
                                         @RequestBody Lesson updated) {
        Lesson result = lessonService.update(id, updated);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    // DELETE /api/lessons/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        boolean deleted = lessonService.delete(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}