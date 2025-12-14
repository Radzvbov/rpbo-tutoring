package ru.radzvbov.rpbo.tutoring.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.radzvbov.rpbo.tutoring.domain.Course;
import ru.radzvbov.rpbo.tutoring.repository.CourseRepository;

import java.util.List;

@Service
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    // GET /api/courses
    public List<Course> getAll() {
        return courseRepository.findAll();
    }

    // GET /api/courses/{id}
    public Course getById(long id) {
        return courseRepository.findById(id).orElse(null);
    }

    // POST /api/courses
    public Course create(Course course) {
        // id и createdAt выставятся в БД (IDENTITY + DEFAULT CURRENT_TIMESTAMP)
        return courseRepository.save(course);
    }

    // PUT /api/courses/{id}
    public Course update(long id, Course updated) {
        return courseRepository.findById(id)
                .map(existing -> {
                    existing.setTitle(updated.getTitle());
                    existing.setDescription(updated.getDescription());
                    existing.setSubject(updated.getSubject());
                    existing.setPrice(updated.getPrice());
                    existing.setTutorId(updated.getTutorId());
                    return courseRepository.save(existing);
                })
                .orElse(null);
    }

    // DELETE /api/courses/{id}
    public boolean delete(long id) {
        if (!courseRepository.existsById(id)) {
            return false;
        }
        courseRepository.deleteById(id);
        return true;
    }
}