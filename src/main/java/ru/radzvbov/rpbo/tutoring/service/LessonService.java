package ru.radzvbov.rpbo.tutoring.service;

import org.springframework.stereotype.Service;
import ru.radzvbov.rpbo.tutoring.domain.Lesson;
import ru.radzvbov.rpbo.tutoring.enums.LessonStatus;
import ru.radzvbov.rpbo.tutoring.repository.LessonRepository;

import java.time.Instant;
import java.util.List;

@Service
public class LessonService {

    private final LessonRepository lessonRepository;

    public LessonService(LessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
    }

    public List<Lesson> getAll() {
        return lessonRepository.findAll();
    }

    public List<Lesson> getByCourseId(long courseId) {
        return lessonRepository.findByCourseId(courseId);
    }

    public Lesson getById(long id) {
        return lessonRepository.findById(id).orElse(null);
    }

    public Lesson create(Lesson lesson) {
        lesson.setId(null);
        if (lesson.getStartTime() == null) {
            lesson.setStartTime(Instant.now());
        }
        if (lesson.getStatus() == null) {
            lesson.setStatus(LessonStatus.PLANNED);
        }
        return lessonRepository.save(lesson);
    }

    public Lesson update(long id, Lesson updated) {
        return lessonRepository.findById(id)
                .map(existing -> {
                    existing.setCourseId(updated.getCourseId());
                    existing.setTutorId(updated.getTutorId());
                    existing.setTopic(updated.getTopic());
                    if (updated.getStartTime() != null) {
                        existing.setStartTime(updated.getStartTime());
                    }
                    existing.setDurationMinutes(updated.getDurationMinutes());
                    if (updated.getStatus() != null) {
                        existing.setStatus(updated.getStatus());
                    }
                    return lessonRepository.save(existing);
                })
                .orElse(null);
    }

    public boolean delete(long id) {
        if (!lessonRepository.existsById(id)) return false;
        lessonRepository.deleteById(id);
        return true;
    }
}